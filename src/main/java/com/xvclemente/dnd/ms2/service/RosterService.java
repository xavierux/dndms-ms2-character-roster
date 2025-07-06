package com.xvclemente.dnd.ms2.service;

import com.xvclemente.dnd.dtos.events.AventuraFinalizadaEvent;
import com.xvclemente.dnd.dtos.events.CombatantStatsDto;
import com.xvclemente.dnd.dtos.events.ResultadoCombateIndividualEvent;
import com.xvclemente.dnd.ms2.model.Enemigo;
import com.xvclemente.dnd.ms2.model.Personaje;
import com.xvclemente.dnd.ms2.repository.EnemigoRepository;
import com.xvclemente.dnd.ms2.repository.PersonajeRepository;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

        List<String> todosLosTiposDeAventura = Arrays.asList("investigar", "recuperar", "proteger", "escoltar", "eliminar");
        List<String> todosLosEntornos = Arrays.asList("ruina olvidada", "bosque encantado", "taberna sospechosa", "cueva oscura", "montaña nevada");


        // Añadir PJs de ejemplo
        Personaje p1 = new Personaje("pj_aragorn", "Aragorn", todosLosTiposDeAventura, todosLosEntornos, 120, 15, 8);
        Personaje p2 = new Personaje("pj_legolas", "Legolas", todosLosTiposDeAventura, todosLosEntornos, 100, 12, 6);
        Personaje p3 = new Personaje("pj_gimli", "Gimli", todosLosTiposDeAventura, todosLosEntornos, 150, 18, 10);
        Personaje p4 = new Personaje("pj_frodo", "Frodo", todosLosTiposDeAventura, todosLosEntornos, 80, 8, 4);
        personajeRepository.save(p1);
        personajeRepository.save(p2);
        personajeRepository.save(p3);
        personajeRepository.save(p4);

        // Añadir ENs de ejemplo
        Enemigo e1 = new Enemigo("en_goblin_1", "Goblin Lancero", todosLosEntornos, 30, 7, 2);
        Enemigo e2 = new Enemigo("en_orco_1", "Orco Brutal", todosLosEntornos, 80, 12, 5);
        Enemigo e3 = new Enemigo("en_lobo_1", "Lobo Temible", todosLosEntornos, 45, 10, 3);
        Enemigo e4 = new Enemigo("en_bandido_1", "Bandido Común", todosLosEntornos, 50, 9, 4);
        enemigoRepository.save(e1);
        enemigoRepository.save(e2);
        enemigoRepository.save(e3);
        enemigoRepository.save(e4);

        String message = "Roster inicializado con 4 PJs y 4 ENs en DynamoDB. Todos tienen todas las preferencias asignadas.";
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

    /**
     * Obtiene un mapa de los PJs que se unen a la aventura con sus stats.
     * Ahora comprueba si la preferencia está en la LISTA del personaje.
     */
    public Map<String, CombatantStatsDto> getParticipatingCharactersMap(String challengeType, String environment) {
        // Obtenemos todos los personajes vivos del repositorio
        List<Personaje> todosLosPjsVivos = personajeRepository.findAll().stream()
                .filter(Personaje::isVivo)
                .collect(Collectors.toList());

        // Si no hay PJs vivos, devolvemos un mapa vacío
        if (todosLosPjsVivos.isEmpty()) {
            return Collections.emptyMap();
        }

        // Filtramos por preferencias
        List<Personaje> pjsConPreferencia = todosLosPjsVivos.stream()
                .filter(p -> p.getTiposAventuraPreferidos().contains(challengeType) ||
                             p.getEntornosPreferidos().contains(environment))
                .collect(Collectors.toList());

        // Si nadie tiene preferencia, para cumplir tu último requisito, usamos todos los vivos
        if (pjsConPreferencia.isEmpty()) {
            log.warn("MS2: Ningún PJ vivo cumple las preferencias. Usando todos los PJs vivos para la selección.");
            pjsConPreferencia = todosLosPjsVivos;
        }
        
        // Barajamos y seleccionamos PJs para la aventura (podríamos limitarlo si quisiéramos)
        Collections.shuffle(pjsConPreferencia);
        return pjsConPreferencia.stream()
                .limit(4) // Limitemos a un máximo de 4 PJs por aventura, por ejemplo
                .collect(Collectors.toMap(
                    Personaje::getId,
                    Personaje::getStatsDto // Usamos el método helper que creamos
                ));
    }

    /**
     * Obtiene un mapa de los Enemigos para la aventura con sus stats.
     * Ahora comprueba si el entorno está en la LISTA del enemigo.
     */
    public Map<String, CombatantStatsDto> getParticipatingEnemiesMap(String environment, int numEncounters) {
        List<Enemigo> enemigosCandidatos = enemigoRepository.findAll().stream()
                .filter(Enemigo::isVivo)
                .filter(e -> e.getEntornosPreferidos().contains(environment))
                .collect(Collectors.toList());

        if (enemigosCandidatos.isEmpty()) {
             log.warn("MS2: Ningún enemigo vivo encaja en el entorno '{}'. Seleccionando enemigos al azar.", environment);
             enemigosCandidatos = enemigoRepository.findAll().stream().filter(Enemigo::isVivo).collect(Collectors.toList());
             if(enemigosCandidatos.isEmpty()) return Collections.emptyMap();
        }
        
        // Barajamos y tomamos la cantidad necesaria
        Collections.shuffle(enemigosCandidatos);
        return enemigosCandidatos.stream()
                .limit(numEncounters)
                .collect(Collectors.toMap(
                    Enemigo::getId,
                    Enemigo::getStatsDto // Usamos el método helper
                ));
    }

    public void procesarResultadoCombate(ResultadoCombateIndividualEvent event) {
        // Aplicar bonificación al ganador
        findPersonajeById(event.getWinnerId()).ifPresent(pj -> {
            pj.aplicarBonificacionVictoria();
            personajeRepository.save(pj);
            log.info("MS2: Bonificación de victoria aplicada a PJ {}", pj.getId());
        });
        findEnemigoById(event.getWinnerId()).ifPresent(en -> {
            en.aplicarBonificacionVictoria();
            enemigoRepository.save(en);
            log.info("MS2: Bonificación de victoria aplicada a EN {}", en.getId());
        });

        // Marcar al perdedor como no-vivo
        findPersonajeById(event.getLoserId()).ifPresent(pj -> {
            pj.marcarComoDerrotado();
            personajeRepository.save(pj);
            log.info("MS2: PJ {} marcado como derrotado.", pj.getId());
        });

        findEnemigoById(event.getLoserId()).ifPresent(en -> {
            en.marcarComoDerrotado();
            enemigoRepository.save(en);
            log.info("MS2: Enemigo {} marcado como derrotado.", en.getId());
            
            // --- LÓGICA DE RESPAWN IMPLEMENTADA AQUÍ ---
            // Justo después de confirmar que un enemigo ha caído,
            // comprobamos si era el último que quedaba.
            comprobarYRegenerarEnemigosSiEsNecesario();
        });
    }

    /**
     * Comprueba si quedan enemigos vivos. Si no queda ninguno, llama a resetEnemies.
     */
    private void comprobarYRegenerarEnemigosSiEsNecesario() {
        // Buscamos si existe AL MENOS UN enemigo que esté vivo
        boolean hayEnemigosVivos = enemigoRepository.findAll().stream().anyMatch(Enemigo::isVivo);

        if (!hayEnemigosVivos) {
            log.warn("!!! ÚLTIMO ENEMIGO DERROTADO !!! Iniciando protocolo de regeneración de enemigos...");
            resetEnemies();
        }
    }

    /**
     * Regenera (hace "respawn") de todos los enemigos, restaurando sus stats base.
     */
    private void resetEnemies() {
        List<Enemigo> todosLosEnemigos = enemigoRepository.findAll();
        todosLosEnemigos.forEach(enemigo -> {
            enemigo.resetStats();
            enemigoRepository.save(enemigo);
        });
        log.info(">>> RESPAWN DE ENEMIGOS COMPLETO <<< Todos los enemigos han sido restaurados.");
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
        long pjCount = personajeRepository.findAll().size();
        long enCount = enemigoRepository.findAll().size();
        resetCharacters();
        resetEnemies();
        String message = String.format("Roster reseteado: %d PJs y %d ENs restaurados a su estado inicial.", pjCount, enCount);
        log.info(message);
        return message;
    }

    // --- AÑADE ESTE MÉTODO SI NO LO TIENES ---
    public void resetCharacters() {
        List<Personaje> todosLosPersonajes = personajeRepository.findAll();
        todosLosPersonajes.forEach(personaje -> {
            personaje.resetStats();
            personaje.setVictorias(0);
            personaje.setOro(0);
            personajeRepository.save(personaje);
        });
        log.info(">>> RESPAWN DE PERSONAJES COMPLETO <<< Todos los personajes han sido restaurados.");
    }
}