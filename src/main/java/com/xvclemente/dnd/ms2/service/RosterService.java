package com.xvclemente.dnd.ms2.service;

import com.xvclemente.dnd.ms2.model.Enemigo;
import com.xvclemente.dnd.ms2.model.Personaje;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class RosterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RosterService.class);

    private final Map<String, Personaje> personajesMap = new ConcurrentHashMap<>();
    private final Map<String, Enemigo> enemigosMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // Añadir PJs de ejemplo con stats
        Personaje p1 = new Personaje("pj_aragorn", "Aragorn", "proteger", "bosque encantado", 120, 15, 8);
        Personaje p2 = new Personaje("pj_legolas", "Legolas", "investigar", "cualquiera", 100, 12, 6);
        Personaje p3 = new Personaje("pj_gimli", "Gimli", "eliminar", "cueva oscura", 150, 18, 10);
        Personaje p4 = new Personaje("pj_frodo", "Frodo", "escoltar", "cualquiera", 80, 8, 4);
        personajesMap.put(p1.getId(), p1);
        personajesMap.put(p2.getId(), p2);
        personajesMap.put(p3.getId(), p3);
        personajesMap.put(p4.getId(), p4);

        // Añadir ENs de ejemplo con stats
        Enemigo e1 = new Enemigo("en_goblin_1", "Goblin Lancero", "cueva oscura", 30, 7, 2);
        Enemigo e2 = new Enemigo("en_orco_1", "Orco Brutal", "ruina olvidada", 80, 12, 5);
        Enemigo e3 = new Enemigo("en_lobo_1", "Lobo Temible", "bosque encantado", 45, 10, 3);
        Enemigo e4 = new Enemigo("en_bandido_1", "Bandido Común", "taberna sospechosa", 50, 9, 4);
        enemigosMap.put(e1.getId(), e1);
        enemigosMap.put(e2.getId(), e2);
        enemigosMap.put(e3.getId(), e3);
        enemigosMap.put(e4.getId(), e4);

        LOGGER.info("Roster inicializado con {} PJs y {} ENs.", personajesMap.size(), enemigosMap.size());
    }

    public Optional<Personaje> findPersonajeById(String id) {
        return Optional.ofNullable(personajesMap.get(id));
    }

    public Optional<Enemigo> findEnemigoById(String id) {
        return Optional.ofNullable(enemigosMap.get(id));
    }

    public List<String> getIdsPersonajesQueSeUnen(String challengeType, String environment) {
        return personajesMap.values().stream()
            .filter(Personaje::isVivo) // Solo PJs vivos pueden unirse
            .filter(p -> p.getTipoAventuraPreferida().equalsIgnoreCase("todas") ||
                         p.getTipoAventuraPreferida().equalsIgnoreCase(challengeType) ||
                         p.getEntornoPreferido().equalsIgnoreCase("cualquiera") ||
                         p.getEntornoPreferido().equalsIgnoreCase(environment))
            .map(Personaje::getId)
            .collect(Collectors.toList());
    }

    public List<String> getIdsEnemigosParaEntorno(String environment, int numEncounters) {
        return enemigosMap.values().stream()
            .filter(Enemigo::isVivo) // Solo ENs vivos pueden participar
            .filter(e -> e.getTipoEntorno().equalsIgnoreCase(environment) || e.getTipoEntorno().equalsIgnoreCase("cualquiera"))
            .limit(numEncounters)
            .map(Enemigo::getId)
            .collect(Collectors.toList());
    }

    // Nuevos métodos para actualizar después del combate
    public void procesarVictoria(String winnerId, String winnerType) {
        if ("PJ".equalsIgnoreCase(winnerType)) {
            findPersonajeById(winnerId).ifPresent(pj -> {
                pj.aplicarBonificacionVictoria();
                LOGGER.info("PJ {} [{}] recibió bonificación por victoria. Victorias: {}", pj.getNombre(), pj.getId(), pj.getVictorias());
            });
        } else if ("EN".equalsIgnoreCase(winnerType)) {
            findEnemigoById(winnerId).ifPresent(en -> {
                en.aplicarBonificacionVictoria();
                LOGGER.info("EN {} [{}] recibió bonificación por victoria. Victorias: {}", en.getNombre(), en.getId(), en.getVictorias());
            });
        }
    }

    public void procesarDerrota(String loserId, String loserType) {
        if ("PJ".equalsIgnoreCase(loserType)) {
            findPersonajeById(loserId).ifPresent(pj -> {
                pj.marcarComoDerrotado();
                LOGGER.info("PJ {} [{}] fue derrotado.", pj.getNombre(), pj.getId());
            });
        } else if ("EN".equalsIgnoreCase(loserType)) {
            findEnemigoById(loserId).ifPresent(en -> {
                en.marcarComoDerrotado();
                LOGGER.info("EN {} [{}] fue derrotado.", en.getNombre(), en.getId());
            });
        }
    }
     // Método para resetear PJs/ENs para una nueva simulación completa (opcional por ahora)
    public void resetAllRosters() {
        personajesMap.values().forEach(Personaje::resetStats);
        enemigosMap.values().forEach(Enemigo::resetStats);
        LOGGER.info("Todos los PJs y ENs han sido reseteados a sus stats base.");
    }
}