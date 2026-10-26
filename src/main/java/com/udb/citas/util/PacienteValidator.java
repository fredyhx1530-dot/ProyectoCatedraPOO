package com.udb.citas.util;

import com.udb.citas.model.Paciente;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// Reglas de validacion del paciente en el servidor. El Servlet solo la invoca.
// Que el DUI no se repita lo consulta el Servlet con PacienteDAO.duiExiste().
public class PacienteValidator {

    private static final Pattern NOMBRE = Pattern.compile("(?U)[\\p{L} .'-]{2,60}");
    private static final Pattern DUI = Pattern.compile("\\d{8}-\\d");
    private static final Pattern TELEFONO = Pattern.compile("\\d{4}-\\d{4}");
    private static final Pattern CORREO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    /**
     * Limpia los datos del bean y los valida. Si la fecha de nacimiento viene bien escrita, la guarda en el bean.
     * @return lista de mensajes (vacia si todo esta bien)
     */
    public List<String> validar(Paciente p, String fechaNacimientoTxt) {
        p.setNombre(limpiar(p.getNombre()));
        p.setApellido(limpiar(p.getApellido()));
        p.setDui(limpiar(p.getDui()));
        p.setTelefono(limpiar(p.getTelefono()));
        p.setEmail(limpiar(p.getEmail()));
        p.setDireccion(limpiar(p.getDireccion()));
        String fecha = limpiar(fechaNacimientoTxt);

        List<String> errores = new ArrayList<>();
        if (!NOMBRE.matcher(p.getNombre()).matches()) errores.add("El nombre debe tener entre 2 y 60 letras.");
        if (!NOMBRE.matcher(p.getApellido()).matches()) errores.add("El apellido debe tener entre 2 y 60 letras.");
        if (!DUI.matcher(p.getDui()).matches()) errores.add("El DUI debe tener el formato 00000000-0.");
        if (!p.getTelefono().isEmpty() && !TELEFONO.matcher(p.getTelefono()).matches()) {
            errores.add("El teléfono debe tener el formato 0000-0000.");
        }
        if (!p.getEmail().isEmpty() && (p.getEmail().length() > 100 || !CORREO.matcher(p.getEmail()).matches())) {
            errores.add("Escribe un correo válido.");
        }
        if (p.getDireccion().length() > 200) errores.add("La dirección no debe superar 200 caracteres.");

        if (!fecha.isEmpty()) {
            try {
                LocalDate fechaNac = LocalDate.parse(fecha);
                if (fechaNac.isAfter(LocalDate.now())) {
                    errores.add("La fecha de nacimiento no puede ser futura.");
                } else {
                    p.setFechaNacimiento(Date.valueOf(fechaNac));
                }
            } catch (DateTimeParseException e) {
                errores.add("La fecha de nacimiento no es válida.");
            }
        }
        if (p.getId() < 0) errores.add("El identificador no es válido.");
        return errores;
    }

    private static String limpiar(String s) {
        return s == null ? "" : s.trim();
    }
}
