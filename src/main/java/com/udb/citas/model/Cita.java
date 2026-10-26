package com.udb.citas.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

// JavaBean de la tabla cita. Los ultimos 4 atributos solo se llenan cuando la consulta hace JOIN (listado y reporte).
public class Cita implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int idPaciente;
    private int idMedico;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;
    private String motivo;

    // datos que vienen del JOIN con paciente, medico y especialidad
    private String pacienteNombre;
    private String pacienteDui;
    private String medicoNombre;
    private String especialidades;

    public Cita() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getPacienteNombre() { return pacienteNombre; }
    public void setPacienteNombre(String pacienteNombre) { this.pacienteNombre = pacienteNombre; }

    public String getPacienteDui() { return pacienteDui; }
    public void setPacienteDui(String pacienteDui) { this.pacienteDui = pacienteDui; }

    public String getMedicoNombre() { return medicoNombre; }
    public void setMedicoNombre(String medicoNombre) { this.medicoNombre = medicoNombre; }

    public String getEspecialidades() { return especialidades; }
    public void setEspecialidades(String especialidades) { this.especialidades = especialidades; }
}
