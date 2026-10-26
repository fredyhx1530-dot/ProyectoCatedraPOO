package com.udb.citas.dao;

import com.udb.citas.model.Paciente;
import com.udb.citas.util.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Aqui se hacen las consultas a la tabla paciente.
public class PacienteDAO {
    private static final String CAMPOS = "id, nombre, apellido, dui, telefono, email, fecha_nacimiento, direccion";

    public List<Paciente> listar(String busqueda) throws SQLException {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT " + CAMPOS + " FROM paciente WHERE CONCAT(nombre, ' ', apellido) LIKE ? OR dui LIKE ? ORDER BY nombre, apellido";
        try (Connection con = ConexionDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            String texto = "%" + (busqueda == null ? "" : busqueda.trim()) + "%";
            ps.setString(1, texto);
            ps.setString(2, texto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) pacientes.add(mapear(rs));
            }
        }
        return pacientes;
    }

    public Paciente buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + CAMPOS + " FROM paciente WHERE id = ?";
        try (Connection con = ConexionDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public boolean duiExiste(String dui, int idActual) throws SQLException {
        String sql = "SELECT id FROM paciente WHERE dui = ? AND id <> ?";
        try (Connection con = ConexionDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dui);
            ps.setInt(2, idActual);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public void insertar(Paciente p) throws SQLException {
        String sql = "INSERT INTO paciente (nombre, apellido, dui, telefono, email, fecha_nacimiento, direccion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            llenarDatos(ps, p);
            ps.executeUpdate();
        }
    }

    public int actualizar(Paciente p) throws SQLException {
        String sql = "UPDATE paciente SET nombre=?, apellido=?, dui=?, telefono=?, email=?, fecha_nacimiento=?, direccion=? WHERE id=?";
        try (Connection con = ConexionDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            llenarDatos(ps, p);
            ps.setInt(8, p.getId());
            return ps.executeUpdate();
        }
    }

    public int eliminar(int id) throws SQLException {
        try (Connection con = ConexionDB.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM paciente WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    private void llenarDatos(PreparedStatement ps, Paciente p) throws SQLException {
        ps.setString(1, p.getNombre());
        ps.setString(2, p.getApellido());
        ps.setString(3, p.getDui());
        ps.setString(4, p.getTelefono());
        ps.setString(5, p.getEmail());
        ps.setDate(6, p.getFechaNacimiento());
        ps.setString(7, p.getDireccion());
    }

    private Paciente mapear(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setApellido(rs.getString("apellido"));
        p.setDui(rs.getString("dui"));
        p.setTelefono(rs.getString("telefono"));
        p.setEmail(rs.getString("email"));
        p.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
        p.setDireccion(rs.getString("direccion"));
        return p;
    }
}
