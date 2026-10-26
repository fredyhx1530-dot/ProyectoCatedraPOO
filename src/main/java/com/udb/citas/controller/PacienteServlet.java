package com.udb.citas.controller;

import com.udb.citas.dao.PacienteDAO;
import com.udb.citas.model.Paciente;
import com.udb.citas.util.PacienteValidator;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

// Recibe las acciones del formulario y usa PacienteDAO para guardar los datos.
@WebServlet("/pacientes")
public class PacienteServlet extends HttpServlet {
    private final PacienteDAO dao = new PacienteDAO();
    private final PacienteValidator validador = new PacienteValidator();
    private static final String LISTA = "/WEB-INF/views/pacientes/lista.jsp";
    private static final String FORM = "/WEB-INF/views/pacientes/form.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");
        try {
            if ("nuevo".equals(accion)) {
                req.setAttribute("paciente", new Paciente());
                req.getRequestDispatcher(FORM).forward(req, resp);
            } else if ("editar".equals(accion)) {
                int id = leerId(req.getParameter("id"));
                Paciente p = id > 0 ? dao.buscarPorId(id) : null;
                if (p == null) {
                    mensaje(req, "error", "No se encontró el paciente.");
                    resp.sendRedirect(req.getContextPath() + "/pacientes");
                } else {
                    req.setAttribute("paciente", p);
                    req.getRequestDispatcher(FORM).forward(req, resp);
                }
            } else {
                req.setAttribute("busqueda", req.getParameter("busqueda"));
                req.setAttribute("lista", dao.listar(req.getParameter("busqueda")));
                req.getRequestDispatcher(LISTA).forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException("Error al consultar pacientes", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");
        try {
            if ("guardar".equals(accion)) guardar(req, resp);
            else if ("eliminar".equals(accion)) eliminar(req, resp);
            else resp.sendRedirect(req.getContextPath() + "/pacientes");
        } catch (SQLException e) {
            throw new ServletException("Error al procesar paciente", e);
        }
    }

    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        Paciente p = new Paciente();
        p.setId(leerId(req.getParameter("id")));
        if (req.getParameter("id") == null) p.setId(-1);
        p.setNombre(valor(req, "nombre"));
        p.setApellido(valor(req, "apellido"));
        p.setDui(valor(req, "dui"));
        p.setTelefono(valor(req, "telefono"));
        p.setEmail(valor(req, "email"));
        p.setDireccion(valor(req, "direccion"));

        String fecha = valor(req, "fechaNacimiento");

        // las reglas estan en PacienteValidator; aqui solo se consulta al DAO lo que depende de la base de datos
        List<String> errores = validador.validar(p, fecha);
        if (errores.isEmpty() && dao.duiExiste(p.getDui(), p.getId())) errores.add("Ya existe un paciente con ese DUI.");
        if (errores.isEmpty() && p.getId() > 0 && dao.buscarPorId(p.getId()) == null) errores.add("El paciente ya no existe.");

        if (!errores.isEmpty()) {
            req.setAttribute("errores", errores);
            req.setAttribute("paciente", p);
            req.setAttribute("fechaIngresada", fecha);
            req.getRequestDispatcher(FORM).forward(req, resp);
            return;
        }
        try {
            if (p.getId() == 0) dao.insertar(p);
            else if (dao.actualizar(p) == 0) {
                mensaje(req, "error", "No se pudo actualizar el paciente.");
                resp.sendRedirect(req.getContextPath() + "/pacientes");
                return;
            }
            mensaje(req, "ok", "Paciente guardado correctamente.");
            resp.sendRedirect(req.getContextPath() + "/pacientes");
        } catch (SQLException e) {
            // Tambien se controla el DUI repetido si otro usuario lo registro al mismo tiempo.
            if (e.getErrorCode() == 1062 || "23000".equals(e.getSQLState())) {
                errores.add("No se pudo guardar: el DUI ya existe o hay un dato duplicado.");
                req.setAttribute("errores", errores);
                req.setAttribute("paciente", p);
                req.setAttribute("fechaIngresada", fecha);
                req.getRequestDispatcher(FORM).forward(req, resp);
            } else throw e;
        }
    }

    private void eliminar(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int id = leerId(req.getParameter("id"));
        if (id <= 0) mensaje(req, "error", "Identificador no válido.");
        else {
            try {
                if (dao.eliminar(id) == 0) mensaje(req, "error", "El paciente ya no existe.");
                else mensaje(req, "ok", "Paciente eliminado correctamente.");
            } catch (SQLException e) {
                // Una cita existente impide eliminar el paciente por la llave foranea.
                if (e.getErrorCode() == 1451) mensaje(req, "error", "No se puede eliminar porque el paciente tiene citas registradas.");
                else throw e;
            }
        }
        resp.sendRedirect(req.getContextPath() + "/pacientes");
    }

    private String valor(HttpServletRequest req, String campo) {
        String dato = req.getParameter(campo);
        return dato == null ? "" : dato.trim();
    }

    private int leerId(String valor) {
        try { return valor == null || valor.isEmpty() ? 0 : Integer.parseInt(valor); }
        catch (NumberFormatException e) { return -1; }
    }

    private void mensaje(HttpServletRequest req, String tipo, String texto) {
        req.getSession().setAttribute("flashTipo", tipo);
        req.getSession().setAttribute("flashMsg", texto);
    }
}
