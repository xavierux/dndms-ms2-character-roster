# D&D Microservices - MS2: Character Roster (`dndms-ms2-character-roster`)

## Propósito
Este microservicio es responsable de gestionar el elenco de Personajes Jugadores (PJs) y Enemigos (ENs), sus estadísticas, preferencias, y su decisión de participar en las aventuras generadas. También procesa los resultados de los combates para actualizar su estado y progresión.

## Responsabilidades Clave
- Mantener un registro persistente de PJs y ENs (stats, estado: vivo/muerto, preferencias, oro, victorias).
- Evaluar las aventuras generadas y determinar qué PJs/ENs "deciden" participar.
- Publicar la lista de participantes para una aventura.
- Actualizar las estadísticas y el estado de PJs/ENs basado en los resultados de los combates.

## Tecnologías
- Java, Spring Boot
- Spring Kafka (Productor y Consumidor)
- Amazon DynamoDB (para persistencia - planeado)
- DTOs compartidos vía Git Submodule (`dndms-event-dtos` referenciado en `shared-dtos-module`)

## Eventos Publicados
- `ParticipantesListosParaAventuraEvent` (al topic: `participantes-topic`)

## Eventos Consumidos
- `AventuraCreadaEvent` (del topic: `aventuras-topic`)
- `ResultadoCombateIndividualEvent` (del topic: `combate-resultados-topic`)

## API Endpoints (Preliminar)
- `GET /api/v1/characters/{id}`: Obtener detalles de un PJ.
- `GET /api/v1/enemies/{id}`: Obtener detalles de un EN.
- `POST /api/v1/characters`: Crear un nuevo PJ.
- `POST /api/v1/enemies`: Crear un nuevo EN.
*(Estos se definirán e implementarán más adelante)*

## Cómo Construir y Ejecutar Localmente
1. Asegúrate de que los submódulos Git estén inicializados y actualizados:
   `git submodule init`
   `git submodule update --remote`
2. Construye con Maven:
   `mvn clean package`
3. Ejecuta la aplicación (requiere Kafka y potencialmente DynamoDB Local corriendo):
   `java -jar target/dndms-ms2-character-roster-0.0.1-SNAPSHOT.jar`