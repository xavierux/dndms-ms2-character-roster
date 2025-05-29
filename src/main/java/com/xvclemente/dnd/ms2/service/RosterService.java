package com.xvclemente.dnd.ms2.service;

import com.xvclemente.dnd.ms2.model.Personaje;
// import com.xvclemente.dnd.ms2.model.Enemigo;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RosterService {
    private final List<Personaje> personajesDisponibles = new ArrayList<>();
    // private final List<Enemigo> enemigosDisponibles = new ArrayList<>(); // Similar para enemigos

    @PostConstruct 
    public void init() {
        // Añadir PJs de ejemplo
        personajesDisponibles.add(new Personaje("pj_aragorn", "Aragorn", "proteger", "bosque encantado"));
        personajesDisponibles.add(new Personaje("pj_legolas", "Legolas", "investigar", "cualquiera"));
        personajesDisponibles.add(new Personaje("pj_gimli", "Gimli", "eliminar", "cueva oscura"));
        // Añadir ENs de ejemplo...
    }

    public List<Personaje> getPersonajesQueSeUnen(String challengeType, String environment) {
        // Lógica simple de decisión: se unen si su tipo de aventura o entorno coincide, o si es "todas"/"cualquiera"
        return personajesDisponibles.stream()
            .filter(p -> p.getTipoAventuraPreferida().equalsIgnoreCase("todas") || 
                         p.getTipoAventuraPreferida().equalsIgnoreCase(challengeType) ||
                         p.getEntornoPreferido().equalsIgnoreCase("cualquiera") ||
                         p.getEntornoPreferido().equalsIgnoreCase(environment))
            .collect(Collectors.toList());
    }

    // Métodos para enemigos...
}