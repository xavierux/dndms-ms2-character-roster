package com.xvclemente.dnd.ms2.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enemigo {
    private String id;
    private String nombre;
    private String tipoEntorno; // Entorno donde suele aparecer
    // Stats básicas más adelante
}