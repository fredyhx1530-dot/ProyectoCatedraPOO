package com.udb.citas.controller;

import com.udb.citas.dao.EspecialidadDAO;
import com.udb.citas.dao.MedicoDAO;
import com.udb.citas.model.Especialidad;
import com.udb.citas.model.Medico;
import com.udb.citas.util.MedicoValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

// Controlador de medicos: recibe, invoca al Modelo (DAO / validador) y hace forward o redirect.
// No genera HTML ni contiene SQL. Rutas: GET /medicos?accion=listar|nuevo|editar  POST accion=guardar|eliminar
@WebServlet("/medicos")
public class MedicoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(MedicoServlet.class.getName());

    private static final String LISTA = "/WEB-INF/views/medicos/lista.jsp";
    private static final String FORM = "/WEB-INF/views/medicos/form.jsp";

    private final transient MedicoDAO medicoDAO = new MedicoDAO();
    private final transient EspecialidadDAO especialidadDAO = new EspecialidadDAO();
    private final transient MedicoValidator validador = new MedicoValidator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");
        if ("nuevo".equals(accion)) {
            mostrarFormulario(req, resp, new Medico(), new LinkedHashMap<String, String>());
        } else if ("editar".equals(accion)) {
            editar(req, resp);
        } else {
            listar(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");
        if ("guardar".equals(accion)) {
            guardar(req, resp);
        } else if ("eliminar".equals(accion)) {
            eliminar(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/medicos");
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String texto = req.getParameter("q");
        int idEspecialidad = aEntero(req.getParameter("idEspecialidad"));
        List<Medico> medicos = new ArrayList<>();
        List<Especialidad> especialidades = new ArrayList<>();
        try {
            especialidades = especialidadDAO.listar();
            medicos = medicoDAO.buscar(texto, idEspecialidad);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error al listar medicos", ex);
            req.setAttribute("errorBD", "No se pudo consultar la base de datos. Intenta de nuevo.");
        }
        req.setAttribute("medicos", medicos);
        req.setAttribute("especialidades", especialidades);
        req.setAttribute("filtroTexto", texto == null ? "" : texto);
        req.setAttribute("filtroEspecialidad", idEspecialidad);
        req.getRequestDispatcher(LISTA).forward(req, resp);
    }

    private void editar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Medico m = medicoDAO.buscarPorId(aEntero(req.getParameter("id")));
            if (m == null) {
                flash(req, "error", "El médico solicitado no existe.");
                resp.sendRedirect(req.getContextPath() + "/medicos");
                return;
            }
            mostrarFormulario(req, resp, m, new LinkedHashMap<String, String>());
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error al cargar medico", ex);
            flash(req, "error", "No se pudo consultar la base de datos.");
            resp.sendRedirect(req.getContextPath() + "/medicos");
        }
    }

    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Medico m = leerFormulario(req);
        Map<String, String> errores = validador.validar(m);
        try {
            if (errores.isEmpty() && medicoDAO.existeJvpm(m.getJvpm(), m.getId())) {
                errores.put("jvpm", "Ya existe un médico con ese número de JVPM.");
            }
            if (!errores.isEmpty()) {
                mostrarFormulario(req, resp, m, errores);
                return;
            }
            if (m.getId() == 0) {
                medicoDAO.insertar(m);
                flash(req, "ok", "Médico registrado correctamente.");
            } else if (medicoDAO.actualizar(m)) {
                flash(req, "ok", "Médico actualizado correctamente.");
            } else {
                flash(req, "error", "El médico ya no existe; no se pudo actualizar.");
            }
            resp.sendRedirect(req.getContextPath() + "/medicos");
        } catch (SQLException ex) {
            if (esViolacionDeIntegridad(ex)) {
                errores.put("general", "No se pudo guardar: el JVPM ya existe o una especialidad ya no es válida.");
            } else {
                LOG.log(Level.SEVERE, "Error al guardar medico", ex);
                errores.put("general", "No se pudo guardar el médico por un error de base de datos.");
            }
            mostrarFormulario(req, resp, m, errores);
        }
    }

    private void eliminar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = aEntero(req.getParameter("id"));
        try {
            if (medicoDAO.eliminar(id)) {
                flash(req, "ok", "Médico eliminado correctamente.");
            } else {
                flash(req, "error", "El médico no existe o ya fue eliminado.");
            }
        } catch (SQLException ex) {
            if (esViolacionDeIntegridad(ex)) {
                flash(req, "error", "No se puede eliminar: el médico tiene citas registradas.");
            } else {
                LOG.log(Level.SEVERE, "Error al eliminar medico", ex);
                flash(req, "error", "No se pudo eliminar el médico por un error de base de datos.");
            }
        }
        resp.sendRedirect(req.getContextPath() + "/medicos");
    }

    // ---------------------------------------------------------------- apoyo

    private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp, Medico medico,
                                   Map<String, String> errores) throws ServletException, IOException {
        List<Especialidad> especialidades = new ArrayList<>();
        try {
            especialidades = especialidadDAO.listar();
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "Error al listar especialidades", ex);
            errores.put("general", "No se pudo cargar la lista de especialidades.");
        }
        Set<Integer> seleccionadas = new LinkedHashSet<>();
        for (Especialidad e : medico.getEspecialidades()) {
            seleccionadas.add(e.getId());
        }
        req.setAttribute("medico", medico);
        req.setAttribute("errores", errores);
        req.setAttribute("especialidades", especialidades);
        req.setAttribute("idsSeleccionados", seleccionadas);
        req.getRequestDispatcher(FORM).forward(req, resp);
    }

    // copia los parametros al bean (sin validar: eso lo hace MedicoValidator)
    private Medico leerFormulario(HttpServletRequest req) {
        Medico m = new Medico();
        m.setId(aEntero(req.getParameter("id")));
        m.setNombre(req.getParameter("nombre"));
        m.setApellido(req.getParameter("apellido"));
        m.setJvpm(req.getParameter("jvpm"));
        m.setTelefono(req.getParameter("telefono"));
        m.setEmail(req.getParameter("email"));
        m.setActivo(req.getParameter("activo") != null);

        Set<Integer> ids = new LinkedHashSet<>();
        String[] marcadas = req.getParameterValues("idEspecialidad");
        if (marcadas != null) {
            for (String s : marcadas) {
                int id = aEntero(s);
                if (id > 0) ids.add(id);
            }
        }
        List<Especialidad> lista = new ArrayList<>();
        for (int id : ids) {
            Especialidad e = new Especialidad();
            e.setId(id);
            lista.add(e);
        }
        m.setEspecialidades(lista);
        return m;
    }

    private static boolean esViolacionDeIntegridad(SQLException ex) {
        return ex.getSQLState() != null && ex.getSQLState().startsWith("23");
    }

    private static int aEntero(String s) {
        try {
            return (s == null || s.trim().isEmpty()) ? 0 : Integer.parseInt(s.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    // mismo mecanismo de mensajes del layout (header.jsp los muestra y los borra)
    private static void flash(HttpServletRequest req, String tipo, String msg) {
        req.getSession().setAttribute("flashTipo", tipo);
        req.getSession().setAttribute("flashMsg", msg);
    }
}
