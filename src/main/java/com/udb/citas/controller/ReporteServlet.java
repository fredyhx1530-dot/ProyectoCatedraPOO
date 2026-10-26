package com.udb.citas.controller;

import com.udb.citas.dao.CitaDAO;
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

// Reporte de citas (JOIN cita + paciente + medico + especialidad) con filtros por rango de fechas y estado.
@WebServlet("/reportes")
public class ReporteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final transient CitaDAO citaDAO = new CitaDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String desdeTxt = limpiar(req.getParameter("desde"));
        String hastaTxt = limpiar(req.getParameter("hasta"));
        String estado = limpiar(req.getParameter("estado")).toUpperCase();
        if (!CitaValidator.ESTADOS.contains(estado)) estado = "";

        LocalDate desde = parsear(desdeTxt);
        LocalDate hasta = parsear(hastaTxt);
        if ((!desdeTxt.isEmpty() && desde == null) || (!hastaTxt.isEmpty() && hasta == null)) {
            req.setAttribute("errorFiltro", "Alguna de las fechas del filtro no es válida.");
            desde = null;
            hasta = null;
        } else if (desde != null && hasta != null && desde.isAfter(hasta)) {
            req.setAttribute("errorFiltro", "La fecha inicial no puede ser mayor que la final.");
            desde = null;
            hasta = null;
        }

        try {
            req.setAttribute("reporte", citaDAO.reporte(desde, hasta, estado));
        } catch (SQLException ex) {
            throw new ServletException("No se pudo generar el reporte", ex);
        }
        req.setAttribute("estados", CitaValidator.ESTADOS);
        req.setAttribute("filtroDesde", desdeTxt);
        req.setAttribute("filtroHasta", hastaTxt);
        req.setAttribute("filtroEstado", estado);
        req.getRequestDispatcher("/WEB-INF/views/reportes/citas.jsp").forward(req, resp);
    }

    private String limpiar(String s) {
        return s == null ? "" : s.trim();
    }

    private LocalDate parsear(String s) {
        if (s.isEmpty()) return null;
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
