package com.xvclemente.dnd.ms2.model;

import java.util.ArrayList;
import java.util.List;

import com.xvclemente.dnd.dtos.events.CombatantStatsDto;

import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@NoArgsConstructor
@DynamoDbBean
public class Enemigo {
    private String id;
    private String nombre;
    private List<String> entornosPreferidos = new ArrayList<>();

    private int hpBase = 50;
    private int ataqueBase = 8;
    private int defensaBase = 3;

    private int hpActual;
    private int ataqueActual;
    private int defensaActual;
    private int victorias = 0; // Los enemigos también pueden tener victorias
    private boolean vivo = true;

    public Enemigo(String id, String nombre, List<String> entornos, int hpBase, int ataqueBase, int defensaBase) {
        this.id = id;
        this.nombre = nombre;
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

    /**
     * Método de conveniencia para crear un DTO con las stats actuales.
     */
    public CombatantStatsDto getStatsDto() {
        return new CombatantStatsDto(this.getNombre(), this.getHpActual(), this.getAtaqueActual(), this.getDefensaActual());
    }    
}