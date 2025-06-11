package com.xvclemente.dnd.ms2.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.xvclemente.dnd.ms2.model.Enemigo;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class EnemigoRepository {
    private final DynamoDbTable<Enemigo> enemigoTable;

    @Autowired
    public EnemigoRepository(DynamoDbEnhancedClient enhancedClient, 
                               @Value("${app.dynamodb.table-name.enemigos}") String tableName) {
        this.enemigoTable = enhancedClient.table(tableName, TableSchema.fromBean(Enemigo.class));
    }

    public void save(Enemigo enemigo) {
        enemigoTable.putItem(enemigo);
    }

    public Optional<Enemigo> findById(String id) {
        return Optional.ofNullable(enemigoTable.getItem(Key.builder().partitionValue(id).build()));
    }

    public List<Enemigo> findAll() {
        return enemigoTable.scan().items().stream().collect(Collectors.toList());
    }
}
