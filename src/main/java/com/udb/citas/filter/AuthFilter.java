package com.udb.citas.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // asi los acentos no se rompen en ningun formulario
        req.setCharacterEncoding("UTF-8");

        String path = req.getRequestURI().substring(req.getContextPath().length());
        boolean publico = path.equals("/") || path.equals("/index.jsp") || path.equals("/login")
                || path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/img/");

        HttpSession sesion = req.getSession(false);
        boolean logueado = sesion != null && sesion.getAttribute("usuario") != null;

        if (!publico && !logueado) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (!publico) {
            // para que al darle atras despues del logout no se vea la pagina vieja
            resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            resp.setHeader("Pragma", "no-cache");
            resp.setDateHeader("Expires", 0);
        }
        chain.doFilter(request, response);
    }
}
