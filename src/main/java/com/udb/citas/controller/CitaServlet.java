package com.udb.citas.controller;

import com.udb.citas.dao.CitaDAO;
import com.udb.citas.dao.MedicoDAO;
import com.udb.citas.dao.PacienteDAO;
import com.udb.citas.model.Cita;
import com.udb.citas.model.Medico;
import com.udb.citas.model.Paciente;
import com.udb.citas.util.CitaValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

// Controlador de citas: recibe, invoca al Modelo (DAO / validador) y hace forward o redirect.
// Rutas: GET /citas?accion=listar|nuevo|editar (listar acepta &fecha=&estado=)  POST accion=guardar|eliminar
@WebServlet("/citas")
public class CitaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String LISTA = "/WEB-INF/views/citas/lista.jsp";
    private static final String FORM = "/WEB-INF/views/citas/form.jsp";

    private final transient CitaDAO citaDAO = new CitaDAO();
    private final transient PacienteDAO pacienteDAO = new PacienteDAO();
    private final transient MedicoDAO medicoDAO = new MedicoDAO();
    private final transient CitaValidator validador = new CitaValidator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");
        try {
            if ("nuevo".equals(accion)) {
                Cita c = new Cita();
                c.setEstado("PENDIENTE");
                mostrarFormulario(req, resp, c, new LinkedHashMap<String, String>(), "", "");
            } else if ("editar".equals(accion)) {
                Cita c = citaDAO.buscarPorId(aEntero(req.getParameter("id")));
                if (c == null) {
                    flash(req, "error", "La cita no existe.");
                    resp.sendRedirect(req.getContextPath() + "/citas");
                } else {
                    mostrarFormulario(req, resp, c, new LinkedHashMap<String, String>(), "", "");
                }
            } else {
                listar(req, resp);
            }
        } catch (SQLException ex) {
            throw new ServletException("Error al consultar citas", ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");
        try {
            if ("guardar".equals(accion)) {
                guardar(req, resp);
            } else if ("eliminar".equals(accion)) {
                eliminar(req, resp);
            } else {
                resp.sendRedirect(req.getContextPath() + "/citas");
            }
        } catch (SQLException ex) {
            throw new ServletException("Error al procesar la cita", ex);
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        String fechaTxt = valor(req, "fecha");
        String estado = valor(req, "estado").toUpperCase();
        LocalDate fecha = null;
        if (!fechaTxt.isEmpty()) {
            try {
                fecha = LocalDate.parse(fechaTxt);
            } catch (DateTimeParseException ex) {
                req.setAttribute("errorFiltro", "La fecha del filtro no es válida.");
            }
        }
        if (!CitaValidator.ESTADOS.contains(estado)) estado = "";

        req.setAttribute("lista", citaDAO.listar(fecha, estado));
        req.setAttribute("estados", CitaValidator.ESTADOS);
        req.setAttribute("filtroFecha", fechaTxt);
        req.setAttribute("filtroEstado", estado);
        req.getRequestDispatcher(LISTA).forward(req, resp);
    }

    private void guardar(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        Cita c = new Cita();
        c.setId(aEntero(req.getParameter("id")));
        c.setIdPaciente(aEntero(req.getParameter("idPaciente")));
        c.setIdMedico(aEntero(req.getParameter("idMedico")));
        c.setEstado(req.getParameter("estado"));
        c.setMotivo(req.getParameter("motivo"));
        String fechaTxt = valor(req, "fecha");
        String horaTxt = valor(req, "hora");

        Paciente paciente = c.getIdPaciente() > 0 ? pacienteDAO.buscarPorId(c.getIdPaciente()) : null;
        Medico medico = c.getIdMedico() > 0 ? medicoDAO.buscarPorId(c.getIdMedico()) : null;
        Map<String, String> errores = validador.validar(c, fechaTxt, horaTxt, paciente, medico);

        // un medico no puede tener dos citas a la misma fecha y hora
        if (errores.isEmpty() && citaDAO.existeConflicto(c.getIdMedico(), c.getFecha(), c.getHora(), c.getId())) {
            errores.put("hora", "El médico ya tiene una cita el " + c.getFecha() + " a las " + c.getHora()
                    + ". Elige otra hora u otro médico.");
        }
        if (!errores.isEmpty()) {
            mostrarFormulario(req, resp, c, errores, fechaTxt, horaTxt);
            return;
        }

        try {
            if (c.getId() == 0) {
                citaDAO.insertar(c);
                flash(req, "ok", "Cita registrada correctamente.");
            } else if (citaDAO.actualizar(c) > 0) {
                flash(req, "ok", "Cita actualizada correctamente.");
            } else {
                flash(req, "error", "La cita ya no existe; no se pudo actualizar.");
            }
            resp.sendRedirect(req.getContextPath() + "/citas");
        } catch (SQLException ex) {
            // otro usuario pudo tomar la misma hora justo antes (UNIQUE id_medico, fecha, hora)
            if (ex.getSQLState() != null && ex.getSQLState().startsWith("23")) {
                errores.put("hora", "Esa hora acaba de ser ocupada o hay un dato no válido. Elige otra hora.");
                mostrarFormulario(req, resp, c, errores, fechaTxt, horaTxt);
            } else {
                throw ex;
            }
        }
    }

    private void eliminar(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int id = aEntero(req.getParameter("id"));
        if (id > 0 && citaDAO.eliminar(id) > 0) {
            flash(req, "ok", "Cita eliminada correctamente.");
        } else {
            flash(req, "error", "La cita no existe o ya fue eliminada.");
        }
        resp.sendRedirect(req.getContextPath() + "/citas");
    }

    // ---------------------------------------------------------------- apoyo

    private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp, Cita cita,
                                   Map<String, String> errores, String fechaIngresada, String horaIngresada)
            throws SQLException, ServletException, IOException {
        req.setAttribute("cita", cita);
        req.setAttribute("errores", errores);
        req.setAttribute("fechaIngresada", fechaIngresada);
        req.setAttribute("horaIngresada", horaIngresada);
        req.setAttribute("pacientes", pacienteDAO.listar(""));
        req.setAttribute("medicos", medicoDAO.listar());
        req.setAttribute("estados", CitaValidator.ESTADOS);
        req.getRequestDispatcher(FORM).forward(req, resp);
    }

    private String valor(HttpServletRequest req, String campo) {
        String v = req.getParameter(campo);
        return v == null ? "" : v.trim();
    }

    private int aEntero(String s) {
        try {
            return (s == null || s.trim().isEmpty()) ? 0 : Integer.parseInt(s.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private void flash(HttpServletRequest req, String tipo, String msg) {
        req.getSession().setAttribute("flashTipo", tipo);
        req.getSession().setAttribute("flashMsg", msg);
    }
}
