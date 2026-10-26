package com.udb.citas.dao;

import com.udb.citas.model.Cita;
import com.udb.citas.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Todo el JDBC de citas: CRUD, validacion de choque de horario y el reporte con JOIN.
public class CitaDAO {

    // JOIN cita - paciente - medico (para el listado y la edicion)
    private static final String SELECT_BASE =
          "SELECT c.id, c.id_paciente, c.id_medico, c.fecha, c.hora, c.estado, c.motivo, "
        + "       CONCAT(p.nombre, ' ', p.apellido) AS paciente, p.dui, "
        + "       CONCAT(m.nombre, ' ', m.apellido) AS medico, NULL AS especialidades "
        + "FROM cita c "
        + "JOIN paciente p ON p.id = c.id_paciente "
        + "JOIN medico m ON m.id = c.id_medico ";

    /** Listado con filtros opcionales por fecha y por estado (null o "" = sin filtro). */
    public List<Cita> listar(LocalDate fecha, String estado) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_BASE);
        List<Object> params = new ArrayList<>();
        List<String> condiciones = new ArrayList<>();
        if (fecha != null) {
            condiciones.add("c.fecha = ?");
            params.add(fecha);
        }
        if (estado != null && !estado.isEmpty()) {
            condiciones.add("c.estado = ?");
            params.add(estado);
        }
        if (!condiciones.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", condiciones)).append(' ');
        }
        sql.append("ORDER BY c.fecha, c.hora");
        return consultar(sql.toString(), params);
    }

    public Cita buscarPorId(int id) throws SQLException {
        List<Object> params = new ArrayList<>();
        params.add(id);
        List<Cita> r = consultar(SELECT_BASE + "WHERE c.id = ?", params);
        return r.isEmpty() ? null : r.get(0);
    }

    /**
     * REPORTE: JOIN de cita + paciente + medico + medico_especialidad + especialidad.
     * Un medico puede tener varias especialidades, por eso se agrupan con GROUP_CONCAT (una fila por cita).
     * Filtros opcionales: rango de fechas y estado.
     */
    public List<Cita> reporte(LocalDate desde, LocalDate hasta, String estado) throws SQLException {
        StringBuilder sql = new StringBuilder(
              "SELECT c.id, c.id_paciente, c.id_medico, c.fecha, c.hora, c.estado, c.motivo, "
            + "       CONCAT(p.nombre, ' ', p.apellido) AS paciente, p.dui, "
            + "       CONCAT(m.nombre, ' ', m.apellido) AS medico, "
            + "       GROUP_CONCAT(DISTINCT e.nombre ORDER BY e.nombre SEPARATOR ', ') AS especialidades "
            + "FROM cita c "
            + "JOIN paciente p ON p.id = c.id_paciente "
            + "JOIN medico m ON m.id = c.id_medico "
            + "LEFT JOIN medico_especialidad me ON me.id_medico = m.id "
            + "LEFT JOIN especialidad e ON e.id = me.id_especialidad ");
        List<Object> params = new ArrayList<>();
        List<String> condiciones = new ArrayList<>();
        if (desde != null) {
            condiciones.add("c.fecha >= ?");
            params.add(desde);
        }
        if (hasta != null) {
            condiciones.add("c.fecha <= ?");
            params.add(hasta);
        }
        if (estado != null && !estado.isEmpty()) {
            condiciones.add("c.estado = ?");
            params.add(estado);
        }
        if (!condiciones.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", condiciones)).append(' ');
        }
        sql.append("GROUP BY c.id, c.id_paciente, c.id_medico, c.fecha, c.hora, c.estado, c.motivo, ")
           .append("p.nombre, p.apellido, p.dui, m.nombre, m.apellido ")
           .append("ORDER BY c.fecha, c.hora");
        return consultar(sql.toString(), params);
    }

    /** true si el medico ya tiene OTRA cita en esa fecha y hora (idExcluir = la que se edita, 0 si es nueva). */
    public boolean existeConflicto(int idMedico, LocalDate fecha, LocalTime hora, int idExcluir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cita WHERE id_medico = ? AND fecha = ? AND hora = ? AND id <> ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setObject(2, fecha);
            ps.setObject(3, hora);
            ps.setInt(4, idExcluir);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void insertar(Cita c) throws SQLException {
        String sql = "INSERT INTO cita (id_paciente, id_medico, fecha, hora, estado, motivo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            llenar(ps, c);
            ps.executeUpdate();
        }
    }

    public int actualizar(Cita c) throws SQLException {
        String sql = "UPDATE cita SET id_paciente = ?, id_medico = ?, fecha = ?, hora = ?, estado = ?, motivo = ? WHERE id = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            llenar(ps, c);
            ps.setInt(7, c.getId());
            return ps.executeUpdate();
        }
    }

    public int eliminar(int id) throws SQLException {
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM cita WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    // ---------------------------------------------------------------- privados

    private List<Cita> consultar(String sql, List<Object> params) throws SQLException {
        List<Cita> lista = new ArrayList<>();
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    private void llenar(PreparedStatement ps, Cita c) throws SQLException {
        ps.setInt(1, c.getIdPaciente());
        ps.setInt(2, c.getIdMedico());
        ps.setObject(3, c.getFecha());
        ps.setObject(4, c.getHora());
        ps.setString(5, c.getEstado());
        ps.setString(6, c.getMotivo() == null || c.getMotivo().isEmpty() ? null : c.getMotivo());
    }

    private Cita mapear(ResultSet rs) throws SQLException {
        Cita c = new Cita();
        c.setId(rs.getInt("id"));
        c.setIdPaciente(rs.getInt("id_paciente"));
        c.setIdMedico(rs.getInt("id_medico"));
        c.setFecha(rs.getObject("fecha", LocalDate.class));
        c.setHora(rs.getObject("hora", LocalTime.class));
        c.setEstado(rs.getString("estado"));
        c.setMotivo(rs.getString("motivo"));
        c.setPacienteNombre(rs.getString("paciente"));
        c.setPacienteDui(rs.getString("dui"));
        c.setMedicoNombre(rs.getString("medico"));
        c.setEspecialidades(rs.getString("especialidades"));
        return c;
    }
}
