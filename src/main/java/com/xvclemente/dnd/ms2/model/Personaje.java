package com.xvclemente.dnd.ms2.model;

import lombok.Data;

@Data
public class Personaje {
    private String id;
    private String nombre;
    private String tipoAventuraPreferida; // ej: "investigar", "todas"
    private String entornoPreferido; // ej: "ruina olvidada", "cualquiera"

    public Personaje(String id, String nombre, String tipoAventuraPreferida, String entornoPreferido) {
        this.id = id;
        this.nombre = nombre;
        this.tipoAventuraPreferida = tipoAventuraPreferida;
        this.entornoPreferido = entornoPreferido;
    }
}