package com.xvclemente.dnd.ms2.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@NoArgsConstructor
@DynamoDbBean
public class Personaje {
    private String id;
    private String nombre;
    private List<String> tiposAventuraPreferidos = new ArrayList<>();
    private List<String> entornosPreferidos = new ArrayList<>();

    private int hpBase = 100;
    private int ataqueBase = 10;
    private int defensaBase = 5;

    private int hpActual;
    private int ataqueActual;
    private int defensaActual;
    private int victorias = 0;
    private boolean vivo = true;
    private int oro = 0;


    public Personaje(String id, String nombre, List<String> tiposAventura, List<String> entornos, int hpBase, int ataqueBase, int defensaBase) {
        this.id = id;
        this.nombre = nombre;
        this.tiposAventuraPreferidos = tiposAventura;
        this.entornosPreferidos = entornos;
        this.hpBase = hpBase;
        this.ataqueBase = ataqueBase;
        this.defensaBase = defensaBase;
        resetStats();
    }

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public com.xvclemente.dnd.dtos.events.CombatantStatsDto getStatsDto() {
        return new com.xvclemente.dnd.dtos.events.CombatantStatsDto(this.getNombre(), this.getHpActual(), this.getAtaqueActual(), this.getDefensaActual());
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

    public void agregarOro(int cantidad) {
        this.oro += cantidad;
    }
}