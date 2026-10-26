package com.udb.citas.util;

import com.udb.citas.model.Especialidad;

import java.util.ArrayList;
import java.util.List;

// Reglas de validacion de la especialidad en el servidor. El Servlet solo la invoca.
// Que el nombre no se repita lo consulta el Servlet con EspecialidadDAO.existeNombre().
public class EspecialidadValidator {

    /** Limpia los datos del bean y los valida. Devuelve la lista de mensajes (vacia si todo esta bien). */
    public List<String> validar(Especialidad esp) {
        esp.setNombre(limpiar(esp.getNombre()));
        esp.setDescripcion(limpiar(esp.getDescripcion()));

        List<String> errores = new ArrayList<>();
        if (esp.getNombre().isEmpty()) {
            errores.add("El nombre es obligatorio.");
        } else if (esp.getNombre().length() > 80) {
            errores.add("El nombre no puede pasar de 80 caracteres.");
        }
        if (esp.getDescripcion().length() > 255) {
            errores.add("La descripción no puede pasar de 255 caracteres.");
        }
        return errores;
    }

    private static String limpiar(String s) {
        return s == null ? "" : s.trim();
    }
}
