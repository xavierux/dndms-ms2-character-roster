package com.xvclemente.dnd.ms2.kafka.consumer;

import com.xvclemente.dnd.dtos.events.AventuraFinalizadaEvent;
import com.xvclemente.dnd.dtos.events.ResultadoCombateIndividualEvent;
import com.xvclemente.dnd.ms2.service.RosterService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CombatResultConsumer {

    private final RosterService rosterService;

    @Autowired
    public CombatResultConsumer(RosterService rosterService) {
        this.rosterService = rosterService;
    }

    @KafkaListener(topics = "${app.kafka.topic.combate-resultados}",
                   groupId = "${spring.kafka.consumer.group-id}",
                   containerFactory = "resultadoCombateIndividualEventKafkaListenerContainerFactory")
    public void handleResultadoCombateIndividual(@Payload ResultadoCombateIndividualEvent event) {
        log.info("MS2: ResultadoCombateIndividualEvent recibido...");
        rosterService.procesarResultadoCombate(event);
    }

    @KafkaListener(topics = "${app.kafka.topic.aventura-finalizada}", // Asegúrate de tener esta propiedad en application.properties
                   groupId = "${spring.kafka.consumer.group-id}",
                   containerFactory = "aventuraFinalizadaEventKafkaListenerContainerFactory") // Apunta a la nueva factory
    public void handleAventuraFinalizada(@Payload AventuraFinalizadaEvent event) {
        log.info("MS2: AventuraFinalizadaEvent recibido para adventureId: {}, Resultado: {}",
                event.getAdventureId(), event.getResultadoAventura());
        rosterService.otorgarRecompensa(event);
    }
}