package com.udb.citas.dao;

import com.udb.citas.util.ConexionDB;
import java.sql.*;

// Cuenta los registros para la pagina de inicio.
public class DashboardDAO {
    public int contar(String tabla) throws SQLException {
        // Solo se permiten estas tablas, no se recibe el nombre desde el usuario.
        if (!tabla.equals("paciente") && !tabla.equals("medico") && !tabla.equals("cita")) {
            throw new IllegalArgumentException("Tabla no permitida");
        }
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM " + tabla);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
