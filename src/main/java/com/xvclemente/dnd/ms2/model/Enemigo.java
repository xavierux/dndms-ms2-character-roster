package com.xvclemente.dnd.ms2.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Enemigo {
    private String id;
    private String nombre;
    private String tipoEntorno; // Entorno donde suele aparecer

    private int hpBase = 50;
    private int ataqueBase = 8;
    private int defensaBase = 3;

    private int hpActual;
    private int ataqueActual;
    private int defensaActual;
    private int victorias = 0; // Los enemigos también pueden tener victorias
    private boolean vivo = true;

    public Enemigo(String id, String nombre, String tipoEntorno, int hpBase, int ataqueBase, int defensaBase) {
        this.id = id;
        this.nombre = nombre;
        this.tipoEntorno = tipoEntorno;
        this.hpBase = hpBase;
        this.ataqueBase = ataqueBase;
        this.defensaBase = defensaBase;
        resetStats();
    }

    public void resetStats() {
        this.hpActual = this.hpBase;
        this.ataqueActual = this.ataqueBase;
        this.defensaActual = this.defensaBase;
        this.vivo = true;
    }

     public void aplicarBonificacionVictoria() {
        if (this.vivo) {
            this.ataqueActual = (int) (this.ataqueActual * 1.10);
            this.defensaActual = (int) (this.defensaActual * 1.10);
            this.hpActual = (int) (this.hpActual * 1.10);
            this.victorias++;
        }
    }

    public void marcarComoDerrotado() {
        this.hpActual = 0;
        this.vivo = false;
    }
}