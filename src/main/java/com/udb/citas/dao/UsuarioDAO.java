package com.udb.citas.dao;

import com.udb.citas.model.Usuario;
import com.udb.citas.util.ConexionDB;
import com.udb.citas.util.Hash;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    // devuelve el usuario si las credenciales estan bien, si no null
    public Usuario validar(String username, String password) throws SQLException {
        String sql = "SELECT id, username, nombre_completo, rol, activo FROM usuario "
                   + "WHERE username = ? AND password = ? AND activo = 1";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, Hash.sha256(password));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setNombreCompleto(rs.getString("nombre_completo"));
                    u.setRol(rs.getString("rol"));
                    u.setActivo(rs.getBoolean("activo"));
                    return u;
                }
            }
        }
        return null;
    }
}
