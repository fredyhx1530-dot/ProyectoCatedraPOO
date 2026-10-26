package com.udb.citas.controller;

import com.udb.citas.dao.UsuarioDAO;
import com.udb.citas.model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // si ya inicio sesion no tiene caso mostrar el login otra vez
        HttpSession sesion = req.getSession(false);
        if (sesion != null && sesion.getAttribute("usuario") != null) {
            resp.sendRedirect(req.getContextPath() + "/inicio.jsp");
            return;
        }
        if (req.getParameter("salio") != null) {
            req.setAttribute("info", "Cerraste sesión correctamente.");
        }
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        username = username == null ? "" : username.trim();
        password = password == null ? "" : password;

        if (username.isEmpty() || password.isEmpty()) {
            volverConError(req, resp, "Escribe tu usuario y contraseña.", username);
            return;
        }

        try {
            Usuario u = usuarioDAO.validar(username, password);
            if (u == null) {
                volverConError(req, resp, "Usuario o contraseña incorrectos.", username);
                return;
            }
            // sesion nueva para evitar que reusen una anterior
            HttpSession vieja = req.getSession(false);
            if (vieja != null) {
                vieja.invalidate();
            }
            HttpSession sesion = req.getSession(true);
            sesion.setAttribute("usuario", u);
            sesion.setMaxInactiveInterval(30 * 60);
            resp.sendRedirect(req.getContextPath() + "/inicio.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
            volverConError(req, resp, "No se pudo conectar con la base de datos.", username);
        }
    }

    private void volverConError(HttpServletRequest req, HttpServletResponse resp, String msg, String username)
            throws ServletException, IOException {
        req.setAttribute("error", msg);
        req.setAttribute("username", username);
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }
}
