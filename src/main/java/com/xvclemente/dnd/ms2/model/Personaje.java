package com.xvclemente.dnd.ms2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Personaje {
    private String id;
    private String nombre;
    private String tipoAventuraPreferida; // ej: "investigar", "todas"
    private String entornoPreferido; // ej: "ruina olvidada", "cualquiera"
}