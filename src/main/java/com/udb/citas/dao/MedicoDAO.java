package com.udb.citas.dao;

import com.udb.citas.model.Especialidad;
import com.udb.citas.model.Medico;
import com.udb.citas.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Todo el JDBC de medicos. La relacion N:M (medico_especialidad) se guarda en una transaccion.
public class MedicoDAO {

    // JOIN medico - medico_especialidad - especialidad (LEFT: tambien trae medicos sin especialidad)
    private static final String SELECT_BASE =
          "SELECT m.id, m.nombre, m.apellido, m.jvpm, m.telefono, m.email, m.activo, "
        + "       e.id AS esp_id, e.nombre AS esp_nombre, e.descripcion AS esp_descripcion "
        + "FROM medico m "
        + "LEFT JOIN medico_especialidad me ON me.id_medico = m.id "
        + "LEFT JOIN especialidad e ON e.id = me.id_especialidad ";

    /**
     * Lista medicos con filtros opcionales.
     * @param texto busca en nombre, apellido o JVPM ("" = sin filtro)
     * @param idEspecialidad solo medicos con esa especialidad (0 = sin filtro); cada medico trae todas sus especialidades
     */
    public List<Medico> buscar(String texto, int idEspecialidad) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_BASE);
        List<Object> params = new ArrayList<>();
        List<String> condiciones = new ArrayList<>();

        if (texto != null && !texto.trim().isEmpty()) {
            condiciones.add("(CONCAT(m.nombre, ' ', m.apellido) LIKE ? OR m.jvpm LIKE ?)");
            String patron = "%" + escaparLike(texto.trim()) + "%";
            params.add(patron);
            params.add(patron);
        }
        if (idEspecialidad > 0) {
            condiciones.add("m.id IN (SELECT id_medico FROM medico_especialidad WHERE id_especialidad = ?)");
            params.add(idEspecialidad);
        }
        if (!condiciones.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", condiciones)).append(' ');
        }
        sql.append("ORDER BY m.nombre, m.apellido, e.nombre");

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                return agrupar(rs);
            }
        }
    }

    public List<Medico> listar() throws SQLException {
        return buscar(null, 0);
    }

    public Medico buscarPorId(int id) throws SQLException {
        String sql = SELECT_BASE + "WHERE m.id = ? ORDER BY e.nombre";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                List<Medico> r = agrupar(rs);
                return r.isEmpty() ? null : r.get(0);
            }
        }
    }

    // idExcluir = el medico que se esta editando (0 si es nuevo)
    public boolean existeJvpm(String jvpm, int idExcluir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM medico WHERE jvpm = ? AND id <> ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, jvpm);
            ps.setInt(2, idExcluir);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /** Inserta el medico y sus especialidades en una sola transaccion. */
    public void insertar(Medico m) throws SQLException {
        String sql = "INSERT INTO medico (nombre, apellido, jvpm, telefono, email, activo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    llenar(ps, m);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) m.setId(keys.getInt(1));
                    }
                }
                insertarEspecialidades(con, m.getId(), m.getEspecialidades());
                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    /** Actualiza el medico y reemplaza sus especialidades (transaccion). false si el medico ya no existe. */
    public boolean actualizar(Medico m) throws SQLException {
        String sql = "UPDATE medico SET nombre = ?, apellido = ?, jvpm = ?, telefono = ?, email = ?, activo = ? WHERE id = ?";
        try (Connection con = ConexionDB.getConnection()) {
            con.setAutoCommit(false);
            try {
                int filas;
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    llenar(ps, m);
                    ps.setInt(7, m.getId());
                    filas = ps.executeUpdate();
                }
                if (filas == 0) {
                    con.rollback();
                    return false;
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM medico_especialidad WHERE id_medico = ?")) {
                    ps.setInt(1, m.getId());
                    ps.executeUpdate();
                }
                insertarEspecialidades(con, m.getId(), m.getEspecialidades());
                con.commit();
                return true;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    /** Elimina el medico (medico_especialidad se borra por CASCADE). Falla si tiene citas (FK RESTRICT). */
    public boolean eliminar(int id) throws SQLException {
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM medico WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ---------------------------------------------------------------- privados

    private void llenar(PreparedStatement ps, Medico m) throws SQLException {
        ps.setString(1, m.getNombre());
        ps.setString(2, m.getApellido());
        ps.setString(3, m.getJvpm());
        ps.setString(4, vacioANull(m.getTelefono()));
        ps.setString(5, vacioANull(m.getEmail()));
        ps.setBoolean(6, m.isActivo());
    }

    private void insertarEspecialidades(Connection con, int idMedico, List<Especialidad> lista) throws SQLException {
        if (lista == null || lista.isEmpty()) return;
        String sql = "INSERT INTO medico_especialidad (id_medico, id_especialidad) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Especialidad e : lista) {
                ps.setInt(1, idMedico);
                ps.setInt(2, e.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // el JOIN trae una fila por (medico, especialidad): aqui se agrupan en un Medico con su lista
    private List<Medico> agrupar(ResultSet rs) throws SQLException {
        Map<Integer, Medico> porId = new LinkedHashMap<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            Medico m = porId.get(id);
            if (m == null) {
                m = new Medico();
                m.setId(id);
                m.setNombre(rs.getString("nombre"));
                m.setApellido(rs.getString("apellido"));
                m.setJvpm(rs.getString("jvpm"));
                m.setTelefono(rs.getString("telefono"));
                m.setEmail(rs.getString("email"));
                m.setActivo(rs.getBoolean("activo"));
                porId.put(id, m);
            }
            int idEsp = rs.getInt("esp_id");
            if (!rs.wasNull()) {
                m.getEspecialidades().add(new Especialidad(idEsp, rs.getString("esp_nombre"), rs.getString("esp_descripcion")));
            }
        }
        return new ArrayList<>(porId.values());
    }

    private static String vacioANull(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }

    // para buscar % _ \ de forma literal dentro de LIKE
    private static String escaparLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
