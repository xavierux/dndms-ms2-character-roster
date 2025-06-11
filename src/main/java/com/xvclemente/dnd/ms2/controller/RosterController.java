package com.xvclemente.dnd.ms2.controller;

import com.xvclemente.dnd.ms2.model.Enemigo;
import com.xvclemente.dnd.ms2.model.Personaje;
import com.xvclemente.dnd.ms2.service.RosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/roster")
public class RosterController {

    private final RosterService rosterService;

    @Autowired
    public RosterController(RosterService rosterService) {
        this.rosterService = rosterService;
    }

    /**
     * Endpoint para poblar la base de datos con PJs y ENs de ejemplo.
     * Es seguro llamarlo múltiples veces, ya que no duplicará los datos si ya existen.
     */
    @PostMapping("/init")
    public ResponseEntity<String> initializeRoster() {
        String message = rosterService.initializeRoster();
        return ResponseEntity.ok(message);
    }

    /**
     * Obtiene una lista de todos los personajes en el roster.
     */
    @GetMapping("/characters")
    public ResponseEntity<List<Personaje>> getAllCharacters() {
        return ResponseEntity.ok(rosterService.findAllPersonajes());
    }

    /**
     * Obtiene un personaje específico por su ID.
     */
    @GetMapping("/characters/{id}")
    public ResponseEntity<Personaje> getCharacterById(@PathVariable String id) {
        Optional<Personaje> personaje = rosterService.findPersonajeById(id);
        return personaje.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Obtiene una lista de todos los enemigos en el roster.
     */
    @GetMapping("/enemies")
    public ResponseEntity<List<Enemigo>> getAllEnemies() {
        return ResponseEntity.ok(rosterService.findAllEnemigos());
    }

    /**
     * Obtiene un enemigo específico por su ID.
     */
    @GetMapping("/enemies/{id}")
    public ResponseEntity<Enemigo> getEnemyById(@PathVariable String id) {
        Optional<Enemigo> enemigo = rosterService.findEnemigoById(id);
        return enemigo.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Resetea todos los PJs y ENs a su estado y estadísticas base.
     * Útil para iniciar una nueva simulación completa.
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetRoster() {
        String message = rosterService.resetAllRosters();
        return ResponseEntity.ok(message);
    }
}