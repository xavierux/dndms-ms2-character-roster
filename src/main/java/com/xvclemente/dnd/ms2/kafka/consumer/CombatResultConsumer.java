package com.xvclemente.dnd.ms2.kafka.consumer;

import com.xvclemente.dnd.dtos.events.ResultadoCombateIndividualEvent;
import com.xvclemente.dnd.ms2.service.RosterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class CombatResultConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CombatResultConsumer.class);
    private final RosterService rosterService;

    @Autowired
    public CombatResultConsumer(RosterService rosterService) {
        this.rosterService = rosterService;
    }

    @KafkaListener(topics = "${app.kafka.topic.combate-resultados}",
                   groupId = "${spring.kafka.consumer.group-id}",
                   containerFactory = "resultadoCombateIndividualEventKafkaListenerContainerFactory")
    public void handleResultadoCombateIndividual(@Payload ResultadoCombateIndividualEvent event) {
        LOGGER.info("MS2: ResultadoCombateIndividualEvent recibido para adventureId: {}, Encuentro N°: {}",
                event.getAdventureId(), event.getEncounterNum());
        LOGGER.info("MS2: Ganador: {} (ID: {}), Perdedor: {} (ID: {})",
                event.getWinnerType(), event.getWinnerId(), event.getLoserType(), event.getLoserId());

        rosterService.procesarVictoria(event.getWinnerId(), event.getWinnerType());
        rosterService.procesarDerrota(event.getLoserId(), event.getLoserType());
    }
}