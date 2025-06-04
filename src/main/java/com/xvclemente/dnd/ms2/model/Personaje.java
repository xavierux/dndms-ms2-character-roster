package com.xvclemente.dnd.ms2.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Personaje {
    private String id;
    private String nombre;
    private String tipoAventuraPreferida; // ej: "investigar", "todas"
    private String entornoPreferido; // ej: "ruina olvidada", "cualquiera"

    private int hpBase = 100;
    private int ataqueBase = 10;
    private int defensaBase = 5;

    private int hpActual;
    private int ataqueActual;
    private int defensaActual;
    private int victorias = 0;
    private boolean vivo = true;

    public Personaje(String id, String nombre, String tipoAventuraPreferida, String entornoPreferido, int hpBase, int ataqueBase, int defensaBase) {
        this.id = id;
        this.nombre = nombre;
        this.tipoAventuraPreferida = tipoAventuraPreferida;
        this.entornoPreferido = entornoPreferido;
        this.hpBase = hpBase;
        this.ataqueBase = ataqueBase;
        this.defensaBase = defensaBase;
        resetStats(); // Inicializar stats actuales
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
            // El HP no suele aumentar así, pero para el bono de "fortaleza"
            this.hpActual = (int) (this.hpActual *1.10); // O quizás el hpBase aumenta y se resetea el actual
            this.victorias++;
        }
    }

    public void marcarComoDerrotado() {
        this.hpActual = 0;
        this.vivo = false;
    }
}