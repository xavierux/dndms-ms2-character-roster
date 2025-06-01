package com.xvclemente.dnd.ms2.kafka.consumer;

import com.xvclemente.dnd.dtos.events.AventuraCreadaEvent;
import com.xvclemente.dnd.dtos.events.ParticipantesListosParaAventuraEvent;
import com.xvclemente.dnd.ms2.kafka.producer.ParticipantEventProducer;
import com.xvclemente.dnd.ms2.service.RosterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.List;

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

    @KafkaListener(topics = "${app.kafka.topic.aventuras-creadas}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleAventuraCreadaEvent(AventuraCreadaEvent event) {
        LOGGER.info("Evento AventuraCreadaEvent recibido: {}", event.getAdventureId());
        LOGGER.info("Detalles: Tipo={}, Entorno={}, Encuentros={}, Recompensa={}",
            event.getChallengeType(), event.getEnvironment(), event.getNumEncounters(), event.getGoldRewardTier());

        List<String> pjIdsQueSeUnen = rosterService.getIdsPersonajesQueSeUnen(event.getChallengeType(), event.getEnvironment());
        List<String> enIdsParaAventura = rosterService.getIdsEnemigosParaEntorno(event.getEnvironment(), event.getNumEncounters());

        LOGGER.info("PJs (IDs) que se unen a la aventura {}: {}", event.getAdventureId(), pjIdsQueSeUnen);
        LOGGER.info("Enemigos (IDs) para la aventura {}: {}", event.getAdventureId(), enIdsParaAventura);

        // Crear y enviar ParticipantesListosParaAventuraEvent
        ParticipantesListosParaAventuraEvent participantesEvent = new ParticipantesListosParaAventuraEvent(
                event.getAdventureId(),
                pjIdsQueSeUnen,
                enIdsParaAventura
        );
        participantEventProducer.sendParticipantesListosEvent(participantesEvent);
    }
}