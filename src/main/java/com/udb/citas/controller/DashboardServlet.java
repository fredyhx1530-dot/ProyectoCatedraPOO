package com.udb.citas.controller;

import com.udb.citas.dao.DashboardDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private final DashboardDAO dao = new DashboardDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("totalPacientes", dao.contar("paciente"));
            req.setAttribute("totalMedicos", dao.contar("medico"));
            req.setAttribute("totalCitas", dao.contar("cita"));
            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("No se pudieron cargar los contadores", e);
        }
    }
}
