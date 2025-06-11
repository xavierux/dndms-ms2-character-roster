package com.xvclemente.dnd.ms2.service;

import com.xvclemente.dnd.dtos.events.AventuraFinalizadaEvent;
import com.xvclemente.dnd.ms2.model.Enemigo;
import com.xvclemente.dnd.ms2.model.Personaje;
import com.xvclemente.dnd.ms2.repository.EnemigoRepository; // IMPORTAR REPO
import com.xvclemente.dnd.ms2.repository.PersonajeRepository; // IMPORTAR REPO

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RosterService {

    private final PersonajeRepository personajeRepository;
    private final EnemigoRepository enemigoRepository;

    public RosterService(PersonajeRepository personajeRepository, EnemigoRepository enemigoRepository) {
        this.personajeRepository = personajeRepository;
        this.enemigoRepository = enemigoRepository;
    }

    @PostConstruct
    public void init() {
        // La creación de tablas ahora se hace en DynamoDbInitializer
        log.info("RosterService inicializado. La población inicial se puede gestionar por separado.");
    }

    public String initializeRoster() {
        // Verificar si ya hay datos para no duplicar
        if (!personajeRepository.findAll().isEmpty() || !enemigoRepository.findAll().isEmpty()) {
            String message = "El roster ya contiene datos. No se realizó la inicialización.";
            log.warn(message);
            return message;
        }

        // Añadir PJs de ejemplo
        Personaje p1 = new Personaje("pj_aragorn", "Aragorn", "proteger", "bosque encantado", 120, 15, 8);
        Personaje p2 = new Personaje("pj_legolas", "Legolas", "investigar", "cualquiera", 100, 12, 6);
        Personaje p3 = new Personaje("pj_gimli", "Gimli", "eliminar", "cueva oscura", 150, 18, 10);
        Personaje p4 = new Personaje("pj_frodo", "Frodo", "escoltar", "cualquiera", 80, 8, 4);
        personajeRepository.save(p1);
        personajeRepository.save(p2);
        personajeRepository.save(p3);
        personajeRepository.save(p4);

        // Añadir ENs de ejemplo
        Enemigo e1 = new Enemigo("en_goblin_1", "Goblin Lancero", "cueva oscura", 30, 7, 2);
        Enemigo e2 = new Enemigo("en_orco_1", "Orco Brutal", "ruina olvidada", 80, 12, 5);
        Enemigo e3 = new Enemigo("en_lobo_1", "Lobo Temible", "bosque encantado", 45, 10, 3);
        Enemigo e4 = new Enemigo("en_bandido_1", "Bandido Común", "taberna sospechosa", 50, 9, 4);
        enemigoRepository.save(e1);
        enemigoRepository.save(e2);
        enemigoRepository.save(e3);
        enemigoRepository.save(e4);

        String message = "Roster inicializado con 4 PJs y 4 ENs en DynamoDB.";
        log.info(message);
        return message;
    }    

    public Optional<Personaje> findPersonajeById(String id) {
        return personajeRepository.findById(id);
    }

    public List<Personaje> findAllPersonajes() {
        return personajeRepository.findAll();
    }
    
    public Optional<Enemigo> findEnemigoById(String id) {
        return enemigoRepository.findById(id);
    }
    
    public List<Enemigo> findAllEnemigos() {
        return enemigoRepository.findAll();
    }

    public List<String> getIdsPersonajesQueSeUnen(String challengeType, String environment) {
        return personajeRepository.findAll().stream() // Ojo: findAll() puede ser lento con muchos datos
            .filter(Personaje::isVivo)
            .filter(p -> p.getTipoAventuraPreferida().equalsIgnoreCase("todas") ||
                         p.getTipoAventuraPreferida().equalsIgnoreCase(challengeType) ||
                         p.getEntornoPreferido().equalsIgnoreCase("cualquiera") ||
                         p.getEntornoPreferido().equalsIgnoreCase(environment))
            .map(Personaje::getId)
            .collect(Collectors.toList());
    }

    public List<String> getIdsEnemigosParaEntorno(String environment, int numEncounters) {
        return enemigoRepository.findAll().stream() // Ojo: findAll() puede ser lento con muchos datos
            .filter(Enemigo::isVivo)
            .filter(e -> e.getTipoEntorno().equalsIgnoreCase(environment) || e.getTipoEntorno().equalsIgnoreCase("cualquiera"))
            .limit(numEncounters)
            .map(Enemigo::getId)
            .collect(Collectors.toList());
    }

    public void procesarVictoria(String winnerId, String winnerType) {
        if ("PJ".equalsIgnoreCase(winnerType)) {
            findPersonajeById(winnerId).ifPresent(pj -> {
                log.info("PROCESANDO VICTORIA para [{}]. Estado ANTES: Victorias={}, Ataque={}, HP={}", 
                        pj.getNombre(), pj.getVictorias(), pj.getAtaqueActual(), pj.getHpActual());
                pj.aplicarBonificacionVictoria();
                log.info("ESTADO EN MEMORIA ACTUALIZADO para [{}]. Estado AHORA: Victorias={}, Ataque={}, HP={}",
                        pj.getNombre(), pj.getVictorias(), pj.getAtaqueActual(), pj.getHpActual());
                personajeRepository.save(pj); // GUARDAR CAMBIOS EN DYNAMODB
                log.info("PJ {} [{}] guardado en DynamoDB después de la victoria.", pj.getNombre(), pj.getId());
            });
        } else if ("EN".equalsIgnoreCase(winnerType)) {
            findEnemigoById(winnerId).ifPresent(en -> {
                en.aplicarBonificacionVictoria();
                enemigoRepository.save(en); // GUARDAR CAMBIOS EN DYNAMODB
                log.info("EN {} [{}] recibió bonificación y fue guardado. Victorias: {}", en.getNombre(), en.getId(), en.getVictorias());
            });
        }
    }

    public void procesarDerrota(String loserId, String loserType) {
        if ("PJ".equalsIgnoreCase(loserType)) {
            findPersonajeById(loserId).ifPresent(pj -> {
                pj.marcarComoDerrotado();
                personajeRepository.save(pj); // GUARDAR CAMBIOS EN DYNAMODB
                log.info("PJ {} [{}] fue derrotado y guardado.", pj.getNombre(), pj.getId());
            });
        } else if ("EN".equalsIgnoreCase(loserType)) {
            findEnemigoById(loserId).ifPresent(en -> {
                en.marcarComoDerrotado();
                enemigoRepository.save(en); // GUARDAR CAMBIOS EN DYNAMODB
                log.info("EN {} [{}] fue derrotado y guardado.", en.getNombre(), en.getId());
            });
        }
    }

    public void savePersonaje(Personaje p) {
        personajeRepository.save(p);
    }

    public void otorgarRecompensa(AventuraFinalizadaEvent event) {
        if ("PJs VICTORIOSOS".equalsIgnoreCase(event.getResultadoAventura()) && event.getPjsGanadoresIds() != null) {
            for (String pjId : event.getPjsGanadoresIds()) {
                findPersonajeById(pjId).ifPresent(pj -> {
                    pj.agregarOro(event.getOroGanadoPorPJ());
                    personajeRepository.save(pj); // Guardar el cambio en DynamoDB
                    log.info("MS2: Otorgados {} de oro a PJ {}. Total actual: {}", event.getOroGanadoPorPJ(), pj.getNombre(), pj.getOro());
                });
            }
        }
    }

    public String resetAllRosters() {
        // Obtenemos todos los PJs y ENs de la base de datos
        List<Personaje> todosLosPjs = personajeRepository.findAll();
        List<Enemigo> todosLosEns = enemigoRepository.findAll();

        // Reseteamos las estadísticas de cada uno
        todosLosPjs.forEach(pj -> {
            pj.resetStats(); // El método que ya tenías en Personaje.java
            pj.setVictorias(0); // Reseteamos también las victorias y el oro
            pj.setOro(0);
            personajeRepository.save(pj); // Guardamos el estado reseteado
        });

        todosLosEns.forEach(en -> {
            en.resetStats(); // El método que ya tenías en Enemigo.java
            en.setVictorias(0);
            enemigoRepository.save(en);
        });

        String message = String.format("Roster reseteado: %d PJs y %d ENs restaurados a su estado inicial.", todosLosPjs.size(), todosLosEns.size());
        log.info(message);
        return message;
    }
}