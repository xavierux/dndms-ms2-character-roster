package com.xvclemente.dnd.ms2.service;

import com.xvclemente.dnd.ms2.model.Enemigo;
import com.xvclemente.dnd.ms2.model.Personaje;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct; // Para Java 17+
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RosterService {
    private final List<Personaje> personajesDisponibles = new ArrayList<>();
    private final List<Enemigo> enemigosDisponibles = new ArrayList<>();

    @PostConstruct
    public void init() {
        // Añadir PJs de ejemplo
        personajesDisponibles.add(new Personaje("pj_aragorn", "Aragorn", "proteger", "bosque encantado"));
        personajesDisponibles.add(new Personaje("pj_legolas", "Legolas", "investigar", "cualquiera"));
        personajesDisponibles.add(new Personaje("pj_gimli", "Gimli", "eliminar", "cueva oscura"));
        personajesDisponibles.add(new Personaje("pj_frodo", "Frodo", "escoltar", "cualquiera"));


        // Añadir ENs de ejemplo
        enemigosDisponibles.add(new Enemigo("en_goblin_1", "Goblin Lancero", "cueva oscura"));
        enemigosDisponibles.add(new Enemigo("en_orco_1", "Orco Brutal", "ruina olvidada"));
        enemigosDisponibles.add(new Enemigo("en_lobo_1", "Lobo Temible", "bosque encantado"));
        enemigosDisponibles.add(new Enemigo("en_bandido_1", "Bandido Común", "taberna sospechosa"));
    }

    public List<String> getIdsPersonajesQueSeUnen(String challengeType, String environment) {
        return personajesDisponibles.stream()
            .filter(p -> p.getTipoAventuraPreferida().equalsIgnoreCase("todas") ||
                         p.getTipoAventuraPreferida().equalsIgnoreCase(challengeType) ||
                         p.getEntornoPreferido().equalsIgnoreCase("cualquiera") ||
                         p.getEntornoPreferido().equalsIgnoreCase(environment))
            .map(Personaje::getId) // Devolvemos solo los IDs
            .collect(Collectors.toList());
    }

    public List<String> getIdsEnemigosParaEntorno(String environment, int numEncounters) {
        // Lógica simple: filtra por entorno y toma una cantidad basada en numEncounters
        // Se puede hacer más complejo: diferentes tipos de enemigos, etc.
        return enemigosDisponibles.stream()
            .filter(e -> e.getTipoEntorno().equalsIgnoreCase(environment) || e.getTipoEntorno().equalsIgnoreCase("cualquiera"))
            .limit(numEncounters) // Tomamos un número de enemigos igual al número de encuentros para simplificar
            .map(Enemigo::getId)  // Devolvemos solo los IDs
            .collect(Collectors.toList());
    }
}