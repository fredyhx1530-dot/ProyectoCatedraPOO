package com.udb.citas.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// JavaBean de la tabla medico. La relacion N:M se guarda como lista de Especialidad.
public class Medico implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private String apellido;
    private String jvpm;
    private String telefono;
    private String email;
    private boolean activo = true;
    @SuppressWarnings("serial")
    private List<Especialidad> especialidades = new ArrayList<>();

    public Medico() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getJvpm() { return jvpm; }
    public void setJvpm(String jvpm) { this.jvpm = jvpm; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public List<Especialidad> getEspecialidades() { return especialidades; }
    public void setEspecialidades(List<Especialidad> especialidades) { this.especialidades = especialidades; }
}
