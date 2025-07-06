# D&D Microservices - MS2: Character Roster (`dndms-ms2-character-roster`)

## Propósito
Este microservicio es responsable de gestionar el elenco de Personajes Jugadores (PJs) y Enemigos (ENs). Mantiene sus estadísticas, estado y preferencias, y procesa los eventos del sistema para actualizar su progresión y determinar su participación en las aventuras.

## Responsabilidades Clave
* **Gestión del Roster:** Mantiene un registro persistente de PJs y ENs en DynamoDB, incluyendo sus estadísticas, estado (vivo/muerto), oro y victorias.
* **Selección de Participantes:** Al recibir una nueva aventura, evalúa el roster de PJs y ENs vivos para determinar quiénes participarán basándose en sus preferencias.
* **Progresión de Personajes:** Consume los resultados de los combates para aplicar bonificaciones por victoria y actualizar el estado de los derrotados.
* **Gestión de Recompensas:** Consume los resultados de las aventuras finalizadas para otorgar recompensas de oro a los PJs victoriosos.
* **Regeneración Automática (Respawn):** Detecta cuándo todos los enemigos han sido derrotados y los regenera automáticamente para asegurar la continuidad de la simulación.

---
## Stack Tecnológico
* **Lenguaje/Framework:** Java 17, Spring Boot 3.3.0
* **Gestión de Dependencias:** Maven
* **Comunicación de Eventos:** Spring Kafka (Productor y Consumidor) para Apache Kafka.
* **Base de Datos:** Amazon DynamoDB para persistencia de datos, a través de Spring Cloud AWS.
* **DTOs Compartidos:** Consumidos como un Git Submodule desde el repositorio `dndms-event-dtos` (ubicado en `shared-dtos-module`).
* **Contenerización:** Docker.

---
## Arquitectura de Eventos

#### Eventos Publicados
* `ParticipantesListosParaAventuraEvent` (al topic: `participantes-topic`): Publicado después de seleccionar a los PJs y ENs para una nueva aventura, enriquecido con sus estadísticas de combate.

#### Eventos Consumidos
* `AventuraCreadaEvent` (del topic: `aventuras-topic`): Para iniciar la selección de participantes.
* `ResultadoCombateIndividualEvent` (del topic: `combate-resultados-topic`): Para actualizar las stats y el estado de los combatientes.
* `AventuraFinalizadaEvent` (del topic: `aventura-finalizada-topic`): Para otorgar el oro a los PJs ganadores.

---
## API Endpoints
La API se expone bajo la ruta base `/api/v1/roster`.

* `POST /init`: Puebla la base de datos con un conjunto de PJs y ENs de ejemplo. No duplicará los datos si ya existen.
* `POST /reset`: Resetea todos los PJs y ENs a su estado y estadísticas base. Útil para iniciar una nueva simulación completa.
* `GET /characters`: Devuelve una lista de todos los personajes.
* `GET /characters/{id}`: Devuelve los detalles de un personaje específico por su ID.
* `GET /enemies`: Devuelve una lista de todos los enemigos.
* `GET /enemies/{id}`: Devuelve los detalles de un enemigo específico por su ID.

---
## Configuración Local
Las configuraciones se encuentran en `src/main/resources/application.properties` y se anulan para el entorno Docker con `application-docker.properties`.

* **Puerto del Servidor:** 8082
* **Kafka Bootstrap (local):** `localhost:9092`
* **Kafka Bootstrap (Docker):** `kafka:29092`
* **DynamoDB Endpoint (local):** `http://localhost:8000`
* **DynamoDB Endpoint (Docker):** `http://dynamo-local:8000`
* **Nombres de Tablas:** `dndms-personajes`, `dndms-enemigos`

---
## Cómo Construir y Ejecutar

### Ejecutar con Docker Compose (Recomendado)
Este microservicio está diseñado para ser orquestado por el archivo `docker-compose.yml` principal ubicado en el repositorio de `dndms-ms1-adventure-forge`.

1.  Asegúrate de que la definición para `dndms-ms2-character-roster-app` esté presente y correcta en el `docker-compose.yml`.
2.  Desde la raíz del proyecto `dndms-ms1-adventure-forge`, ejecuta:
    ```bash
    # El flag --build es importante si has hecho cambios en el código de MS2
    docker-compose up -d --build
    ```

### Ejecutar Manualmente (para depuración)
1.  **Asegúrate de que los submódulos Git estén actualizados:**
    ```bash
    git submodule update --init --recursive
    ```
2.  **Levanta la infraestructura** (Kafka, DynamoDB) usando `docker-compose` desde el proyecto MS1.
3.  **Ejecuta la aplicación** desde la raíz de este proyecto (`dndms-ms2-character-roster`):
    ```bash
    mvn spring-boot:run
    ```