package com.xvclemente.dnd.ms2.kafka.consumer;

import com.xvclemente.dnd.dtos.events.AventuraCreadaEvent;
import com.xvclemente.dnd.ms2.service.RosterService;
// Importar el futuro ParticipantEventProducer y ParticipantesListosParaAventuraEvent
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.xvclemente.dnd.ms2.model.Personaje;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class AdventureEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdventureEventConsumer.class);
    private final RosterService rosterService;
    // Inyectar el ParticipantEventProducer más adelante

    @Autowired
    public AdventureEventConsumer(RosterService rosterService /*, ParticipantEventProducer participantEventProducer */) {
        this.rosterService = rosterService;
        // this.participantEventProducer = participantEventProducer;
    }

    @KafkaListener(topics = "${app.kafka.topic.aventuras-creadas}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleAventuraCreadaEvent(AventuraCreadaEvent event) {
        LOGGER.info("Evento AventuraCreadaEvent recibido: {}", event.getAdventureId());
        LOGGER.info("Detalles: Tipo={}, Entorno={}, Encuentros={}, Recompensa={}",
            event.getChallengeType(), event.getEnvironment(), event.getNumEncounters(), event.getGoldRewardTier());

        // Lógica para que PJs/ENs decidan unirse
        List<Personaje> pjsQueSeUnen = rosterService.getPersonajesQueSeUnen(event.getChallengeType(), event.getEnvironment());
        // List<Enemigo> ensQueSeUnen = rosterService.getEnemigosQueSeUnen(event.getEnvironment()); // Lógica similar

        LOGGER.info("PJs que se unen a la aventura {}: {}", event.getAdventureId(), 
            pjsQueSeUnen.stream().map(Personaje::getNombre).collect(Collectors.toList()));

        // TODO Próximo Paso: Crear y enviar ParticipantesListosParaAventuraEvent
        // List<String> pjIds = pjsQueSeUnen.stream().map(Personaje::getId).collect(Collectors.toList());
        // List<String> enIds = ...
        // ParticipantesListosParaAventuraEvent participantesEvent = new ParticipantesListosParaAventuraEvent(event.getAdventureId(), pjIds, enIds);
        // participantEventProducer.sendParticipantesListosEvent(participantesEvent);
    }
}