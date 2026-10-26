package com.udb.citas.dao;

import com.udb.citas.model.Especialidad;
import com.udb.citas.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EspecialidadDAO {

    public List<Especialidad> listar() throws SQLException {
        List<Especialidad> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion FROM especialidad ORDER BY nombre";
        try (Connection con = ConexionDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Especialidad buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, descripcion FROM especialidad WHERE id = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public void insertar(Especialidad e) throws SQLException {
        String sql = "INSERT INTO especialidad (nombre, descripcion) VALUES (?, ?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getDescripcion());
            ps.executeUpdate();
        }
    }

    public void actualizar(Especialidad e) throws SQLException {
        String sql = "UPDATE especialidad SET nombre = ?, descripcion = ? WHERE id = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getDescripcion());
            ps.setInt(3, e.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM especialidad WHERE id = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // para no repetir nombres. idExcluir sirve al editar (0 si es nueva)
    public boolean existeNombre(String nombre, int idExcluir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM especialidad WHERE LOWER(nombre) = LOWER(?) AND id <> ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, idExcluir);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    // cuantos medicos usan esta especialidad (si hay, no se puede borrar)
    public int contarMedicos(int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM medico_especialidad WHERE id_especialidad = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private Especialidad mapear(ResultSet rs) throws SQLException {
        return new Especialidad(rs.getInt("id"), rs.getString("nombre"), rs.getString("descripcion"));
    }
}
