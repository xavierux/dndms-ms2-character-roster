package com.xvclemente.dnd.ms2.kafka.producer;

import com.xvclemente.dnd.dtos.events.ParticipantesListosParaAventuraEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class ParticipantEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParticipantEventProducer.class);

    @Value("${app.kafka.topic.participantes-listos}")
    private String topicParticipantesListos;

    private final KafkaTemplate<String, ParticipantesListosParaAventuraEvent> kafkaTemplate;

    @Autowired
    public ParticipantEventProducer(KafkaTemplate<String, ParticipantesListosParaAventuraEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendParticipantesListosEvent(ParticipantesListosParaAventuraEvent event) {
        LOGGER.info("Intentando enviar ParticipantesListosParaAventuraEvent para adventureId: {}", event.getAdventureId());
        CompletableFuture<SendResult<String, ParticipantesListosParaAventuraEvent>> future =
                kafkaTemplate.send(topicParticipantesListos, event.getAdventureId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                LOGGER.info("ParticipantesListosParaAventuraEvent enviado con éxito [adventureId={}] a topic [{}] con offset [{}]",
                        event.getAdventureId(), topicParticipantesListos, result.getRecordMetadata().offset());
            } else {
                LOGGER.error("Error al enviar ParticipantesListosParaAventuraEvent [adventureId={}] a topic [{}]: {}",
                        event.getAdventureId(), topicParticipantesListos, ex.getMessage());
            }
        });
    }
}