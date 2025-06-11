package com.xvclemente.dnd.ms2.config;

import com.xvclemente.dnd.ms2.model.Enemigo;
import com.xvclemente.dnd.ms2.model.Personaje;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;

@Configuration
@Slf4j
@Profile("!test") // No ejecutar esto durante las pruebas de integración si no es necesario
public class DynamoDbInitializer {

    @Bean
    ApplicationRunner applicationRunner(DynamoDbClient dynamoDbClient,
                                        DynamoDbEnhancedClient enhancedClient,
                                        @Value("${app.dynamodb.table-name.personajes}") String personajesTableName,
                                        @Value("${app.dynamodb.table-name.enemigos}") String enemigosTableName) {
        return args -> {
            ListTablesResponse response = dynamoDbClient.listTables();
            if (!response.tableNames().contains(personajesTableName)) {
                log.info("Tabla '{}' no encontrada. Creándola...", personajesTableName);
                enhancedClient.table(personajesTableName, TableSchema.fromBean(Personaje.class)).createTable();
                log.info("Tabla '{}' creada.", personajesTableName);
            } else {
                log.info("Tabla '{}' ya existe.", personajesTableName);
            }

            if (!response.tableNames().contains(enemigosTableName)) {
                log.info("Tabla '{}' no encontrada. Creándola...", enemigosTableName);
                enhancedClient.table(enemigosTableName, TableSchema.fromBean(Enemigo.class)).createTable();
                log.info("Tabla '{}' creada.", enemigosTableName);
            } else {
                log.info("Tabla '{}' ya existe.", enemigosTableName);
            }
        };
    }
}