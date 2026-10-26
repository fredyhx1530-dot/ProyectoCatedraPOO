package com.udb.citas.util;

import com.udb.citas.model.Cita;
import com.udb.citas.model.Medico;
import com.udb.citas.model.Paciente;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Reglas de validacion de la cita en el servidor. El Servlet solo la invoca.
// El choque de horario del medico lo consulta el Servlet con CitaDAO.existeConflicto().
public class CitaValidator {

    public static final List<String> ESTADOS =
            Collections.unmodifiableList(Arrays.asList("PENDIENTE", "CONFIRMADA", "ATENDIDA", "CANCELADA"));

    // horario de atencion y duracion de cada espacio (si cambian, se ajusta aqui y en validacionesCita.js)
    private static final LocalTime APERTURA = LocalTime.of(7, 0);
    private static final LocalTime CIERRE = LocalTime.of(18, 0);
    private static final int MINUTOS_POR_ESPACIO = 30;

    /**
     * Limpia y valida. Llena fecha y hora del bean si vienen bien escritas.
     * @param paciente el paciente elegido, o null si no existe
     * @param medico el medico elegido, o null si no existe
     * @return campo -> mensaje (vacio si todo esta bien)
     */
    public Map<String, String> validar(Cita c, String fechaTxt, String horaTxt, Paciente paciente, Medico medico) {
        Map<String, String> e = new LinkedHashMap<>();
        c.setEstado(c.getEstado() == null ? "" : c.getEstado().trim().toUpperCase());
        c.setMotivo(c.getMotivo() == null ? "" : c.getMotivo().trim());
        fechaTxt = fechaTxt == null ? "" : fechaTxt.trim();
        horaTxt = horaTxt == null ? "" : horaTxt.trim();

        if (paciente == null) e.put("idPaciente", "Selecciona un paciente válido.");
        if (medico == null) e.put("idMedico", "Selecciona un médico válido.");

        if (!ESTADOS.contains(c.getEstado())) e.put("estado", "Selecciona un estado válido.");
        boolean agendada = "PENDIENTE".equals(c.getEstado()) || "CONFIRMADA".equals(c.getEstado());

        if (medico != null && !medico.isActivo() && agendada) {
            e.put("idMedico", "El médico seleccionado está inactivo y no puede recibir citas.");
        }

        if (fechaTxt.isEmpty()) {
            e.put("fecha", "La fecha es obligatoria.");
        } else {
            try {
                LocalDate fecha = LocalDate.parse(fechaTxt);
                c.setFecha(fecha);
                if (agendada && fecha.isBefore(LocalDate.now())) {
                    e.put("fecha", "No se puede agendar una cita en una fecha pasada.");
                }
            } catch (DateTimeParseException ex) {
                e.put("fecha", "La fecha no es válida.");
            }
        }

        if (horaTxt.isEmpty()) {
            e.put("hora", "La hora es obligatoria.");
        } else {
            try {
                LocalTime hora = LocalTime.parse(horaTxt);
                c.setHora(hora);
                if (hora.isBefore(APERTURA) || hora.isAfter(CIERRE)) {
                    e.put("hora", "El horario de atención es de 07:00 a 18:00.");
                } else if (hora.getMinute() % MINUTOS_POR_ESPACIO != 0) {
                    e.put("hora", "Las citas se agendan en espacios de 30 minutos (ej. 08:00, 08:30).");
                }
            } catch (DateTimeParseException ex) {
                e.put("hora", "La hora no es válida.");
            }
        }

        if (c.getMotivo().length() > 255) e.put("motivo", "El motivo no puede pasar de 255 caracteres.");
        return e;
    }
}
