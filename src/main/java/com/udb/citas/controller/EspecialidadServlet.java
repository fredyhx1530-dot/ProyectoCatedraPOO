package com.udb.citas.controller;

import com.udb.citas.dao.EspecialidadDAO;
import com.udb.citas.model.Especialidad;
import com.udb.citas.util.EspecialidadValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/especialidades")
public class EspecialidadServlet extends HttpServlet {

    private static final String LISTA = "/WEB-INF/views/especialidades/lista.jsp";
    private static final String FORM = "/WEB-INF/views/especialidades/form.jsp";

    private final EspecialidadDAO dao = new EspecialidadDAO();
    private final EspecialidadValidator validador = new EspecialidadValidator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String accion = req.getParameter("accion");
        if (accion == null) accion = "listar";

        try {
            switch (accion) {
                case "nuevo":
                    req.setAttribute("esp", new Especialidad());
                    req.getRequestDispatcher(FORM).forward(req, resp);
                    break;

                case "editar":
                    Especialidad e = dao.buscarPorId(leerId(req));
                    if (e == null) {
                        flash(req, "error", "La especialidad no existe.");
                        resp.sendRedirect(req.getContextPath() + "/especialidades");
                        return;
                    }
                    req.setAttribute("esp", e);
                    req.getRequestDispatcher(FORM).forward(req, resp);
                    break;

                default:
                    req.setAttribute("lista", dao.listar());
                    req.getRequestDispatcher(LISTA).forward(req, resp);
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String accion = req.getParameter("accion");
        try {
            if ("guardar".equals(accion)) {
                guardar(req, resp);
            } else if ("eliminar".equals(accion)) {
                eliminar(req, resp);
            } else {
                resp.sendRedirect(req.getContextPath() + "/especialidades");
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    private void guardar(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        Especialidad esp = new Especialidad();
        esp.setId(leerId(req));
        esp.setNombre(req.getParameter("nombre"));
        esp.setDescripcion(req.getParameter("descripcion"));

        // las reglas estan en EspecialidadValidator; aqui solo se consulta al DAO si el nombre ya existe
        List<String> errores = validador.validar(esp);
        if (errores.isEmpty() && dao.existeNombre(esp.getNombre(), esp.getId())) {
            errores.add("Ya existe una especialidad con ese nombre.");
        }

        if (!errores.isEmpty()) {
            req.setAttribute("errores", errores);
            req.setAttribute("esp", esp);
            req.getRequestDispatcher(FORM).forward(req, resp);
            return;
        }

        if (esp.getId() == 0) {
            dao.insertar(esp);
            flash(req, "ok", "Especialidad creada correctamente.");
        } else {
            dao.actualizar(esp);
            flash(req, "ok", "Especialidad actualizada correctamente.");
        }
        resp.sendRedirect(req.getContextPath() + "/especialidades");
    }

    private void eliminar(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
        int id = leerId(req);
        int medicos = dao.contarMedicos(id);
        if (medicos > 0) {
            flash(req, "error", "No se puede eliminar, hay " + medicos + " médico(s) con esta especialidad.");
        } else {
            dao.eliminar(id);
            flash(req, "ok", "Especialidad eliminada.");
        }
        resp.sendRedirect(req.getContextPath() + "/especialidades");
    }

    // mensajes que sobreviven al redirect, el header los muestra y los borra
    private void flash(HttpServletRequest req, String tipo, String msg) {
        req.getSession().setAttribute("flashTipo", tipo);
        req.getSession().setAttribute("flashMsg", msg);
    }

    private int leerId(HttpServletRequest req) {
        try {
            return Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
