package com.xvclemente.dnd.ms2.repository;

import com.xvclemente.dnd.ms2.model.Personaje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PersonajeRepository {
    private final DynamoDbTable<Personaje> personajeTable;

    @Autowired
    public PersonajeRepository(DynamoDbEnhancedClient enhancedClient, 
                               @Value("${app.dynamodb.table-name.personajes}") String tableName) {
        this.personajeTable = enhancedClient.table(tableName, TableSchema.fromBean(Personaje.class));
    }

    public void save(Personaje personaje) {
        personajeTable.putItem(personaje);
    }

    public Optional<Personaje> findById(String id) {
        return Optional.ofNullable(personajeTable.getItem(Key.builder().partitionValue(id).build()));
    }

    public List<Personaje> findAll() {
        return personajeTable.scan().items().stream().collect(Collectors.toList());
    }
}