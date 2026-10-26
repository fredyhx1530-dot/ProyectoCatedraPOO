package com.udb.citas.util;

import com.udb.citas.model.Medico;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

// Reglas de validacion del medico en el servidor. El Servlet solo la invoca.
public class MedicoValidator {

    // acepta "JVPM-10234" o solo "10234"; se guarda siempre como JVPM-#####
    private static final Pattern JVPM = Pattern.compile("(?i)(JVPM-)?(\\d{3,10})");
    private static final Pattern NOMBRE = Pattern.compile("(?U)[\\p{L}][\\p{L} .'-]{1,59}");
    private static final Pattern CORREO = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+");
    private static final Pattern TELEFONO = Pattern.compile("\\d{4}-\\d{4}");

    /** Limpia los datos del bean y los valida. Devuelve campo -> mensaje (vacio si todo esta bien). */
    public Map<String, String> validar(Medico m) {
        m.setNombre(limpiar(m.getNombre()).replaceAll("\\s+", " "));
        m.setApellido(limpiar(m.getApellido()).replaceAll("\\s+", " "));
        m.setJvpm(limpiar(m.getJvpm()));
        m.setTelefono(limpiar(m.getTelefono()));
        m.setEmail(limpiar(m.getEmail()).toLowerCase());

        Map<String, String> e = new LinkedHashMap<>();

        if (m.getJvpm().isEmpty()) {
            e.put("jvpm", "El número de JVPM es obligatorio.");
        } else if (!JVPM.matcher(m.getJvpm()).matches()) {
            e.put("jvpm", "El JVPM debe ser como JVPM-12345 (de 3 a 10 dígitos).");
        } else {
            m.setJvpm("JVPM-" + m.getJvpm().replaceAll("(?i)^JVPM-", ""));
        }

        if (!NOMBRE.matcher(m.getNombre()).matches()) {
            e.put("nombre", "El nombre es obligatorio (2 a 60 letras).");
        }
        if (!NOMBRE.matcher(m.getApellido()).matches()) {
            e.put("apellido", "El apellido es obligatorio (2 a 60 letras).");
        }
        if (!m.getEmail().isEmpty() && (m.getEmail().length() > 100 || !CORREO.matcher(m.getEmail()).matches())) {
            e.put("email", "Escribe un correo válido (ej. nombre@clinica.com).");
        }
        if (!m.getTelefono().isEmpty() && !TELEFONO.matcher(m.getTelefono()).matches()) {
            e.put("telefono", "El teléfono debe tener el formato 0000-0000.");
        }
        if (m.getEspecialidades() == null || m.getEspecialidades().isEmpty()) {
            e.put("especialidades", "Selecciona al menos una especialidad.");
        }
        return e;
    }

    private static String limpiar(String s) {
        return s == null ? "" : s.trim();
    }
}
