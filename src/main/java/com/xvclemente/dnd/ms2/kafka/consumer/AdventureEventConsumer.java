package com.xvclemente.dnd.ms2.kafka.consumer;

import com.xvclemente.dnd.dtos.events.AventuraCreadaEvent;
import com.xvclemente.dnd.dtos.events.CombatantStatsDto; // Asegúrate de tener esta importación
import com.xvclemente.dnd.dtos.events.ParticipantesListosParaAventuraEvent;
import com.xvclemente.dnd.ms2.kafka.producer.ParticipantEventProducer;
import com.xvclemente.dnd.ms2.service.RosterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class AdventureEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdventureEventConsumer.class);
    private final RosterService rosterService;
    private final ParticipantEventProducer participantEventProducer;

    @Autowired
    public AdventureEventConsumer(RosterService rosterService, ParticipantEventProducer participantEventProducer) {
        this.rosterService = rosterService;
        this.participantEventProducer = participantEventProducer;
    }

    @KafkaListener(topics = "${app.kafka.topic.aventuras-creadas}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "aventuraCreadaEventKafkaListenerContainerFactory")
    public void handleAventuraCreadaEvent(AventuraCreadaEvent event) {
        LOGGER.info("Evento AventuraCreadaEvent recibido: {}", event.getAdventureId());
        LOGGER.info("Detalles: Tipo={}, Entorno={}, Encuentros={}, Recompensa={}",
            event.getChallengeType(), event.getEnvironment(), event.getNumEncounters(), event.getGoldRewardTier());

        // 1. OBTENER LOS MAPAS CON STATS DESDE EL SERVICIO
        Map<String, CombatantStatsDto> pjsQueSeUnenMap = rosterService.getParticipatingCharactersMap(event.getChallengeType(), event.getEnvironment());
        Map<String, CombatantStatsDto> ensParaAventuraMap = rosterService.getParticipatingEnemiesMap(event.getEnvironment(), event.getNumEncounters());

        LOGGER.info("PJs que se unen a la aventura {}: {}", event.getAdventureId(), pjsQueSeUnenMap.keySet());
        LOGGER.info("Enemigos para la aventura {}: {}", event.getAdventureId(), ensParaAventuraMap.keySet());

        // 2. CREAR EL EVENTO CON LA NUEVA ESTRUCTURA (USANDO LOS MAPAS)
        ParticipantesListosParaAventuraEvent participantesEvent = new ParticipantesListosParaAventuraEvent(
                event.getAdventureId(),
                pjsQueSeUnenMap,
                ensParaAventuraMap
        );
        
        // 3. ENVIAR EL EVENTO ENRIQUECIDO
        participantEventProducer.sendParticipantesListosEvent(participantesEvent);
    }
}