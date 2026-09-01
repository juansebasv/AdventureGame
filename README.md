# Adventure Game (`canivales`)

API REST de un juego de aventura de texto tipo *"elige tu propia aventura"*
ambientado en la selva. El servidor entrega los nodos de la historia y persiste
los tiempos (*scores*) de cada partida; opcionalmente notifica al jugador por SMS.

Este repositorio contiene **únicamente el backend**. No hay frontend ni assets: la
interfaz la implementa una aplicación cliente que consume estos endpoints.

> Para levantarlo en local paso a paso, ver **[RUNME.md](RUNME.md)**.

---

## 1. Stack tecnológico

| Área | Tecnología | Versión |
|---|---|---|
| Lenguaje | Java | 11 (nivel de fuente `11`) |
| Framework | Spring Boot | `2.4.13` |
| | Spring Framework | `5.3.13` (transitiva) |
| Web | Spring MVC + Tomcat embebido | Tomcat `9.0.55` |
| Persistencia | Spring Data JPA + Hibernate ORM | Hibernate `5.4.32` |
| Pool de conexiones | HikariCP | (gestionada por Boot) |
| Base de datos | PostgreSQL | 15 (contenedor); driver JDBC `42.2.24` |
| Validación | Bean Validation (Hibernate Validator) | (gestionada por Boot) |
| Documentación API | springdoc-openapi (OpenAPI 3 + Swagger UI) | `1.5.13` |
| Observabilidad | Spring Boot Actuator (`/actuator/health`, `/actuator/info`) | (gestionada por Boot) |
| Cliente HTTP (SMS) | Apache HttpClient | `4.5.14` |
| Utilidades de código | Lombok | (gestionada por Boot) |
| Build | Gradle (wrapper) | `6.9.4` |
| Tests | JUnit 5 + Spring Boot Test + H2 en memoria | (gestionadas por Boot) |
| Contenedores | Docker + Docker Compose v2 | — |

> **Nota sobre versiones:** Gradle 6.9 / Spring Boot 2.4 / springfox 2.9.2 **no
> son compatibles con JDK 17+**. El build y el runtime usan **JDK 11** (dentro de
> Docker no hace falta instalarlo en la máquina).

---

## 2. Arquitectura

Arquitectura en capas con separación de responsabilidades y dependencias apuntando
siempre hacia el dominio:

```
HTTP  ─▶  controller  ─▶  service  ─▶  repository  ─▶  PostgreSQL
                │             │
                │             ├─▶  mapper        (entidad  ⇄  DTO)
                │             └─▶  sms.SmsGateway (puerto de salida)
                │                      ├─ AltiriaSmsGateway  (adaptador HTTP real)
                │                      └─ NoOpSmsGateway      (sin efectos)
                └─▶  GlobalExceptionHandler  (errores ─▶ HTTP homogéneo)

config  ─▶  SmsProperties (@ConfigurationProperties)  ·  TimeConfig (Clock)
```

Principios aplicados:

- **Inyección por constructor** (campos `final`, sin `@Autowired` en atributos).
- **DTOs de entrada y de salida separados** del modelo de persistencia
  (`SaveScoreRequest` ≠ `ScoreDto` ≠ `Score`).
- **Puerto/adaptador** para el SMS (`SmsGateway`): el servicio no conoce Altiria ni
  HTTP. La implementación se elige por configuración (`@ConditionalOnProperty`).
- **Manejo de errores centralizado** con `@RestControllerAdvice`; los controladores
  no llevan `try/catch`.
- **Sin *magic numbers* / *magic strings*:** rutas, parámetros del protocolo Altiria,
  límites de validación, formatos de fecha y textos viven en constantes o en
  `application.yml`.
- **Configuración y secretos fuera del código**, vía `@ConfigurationProperties` y
  variables de entorno.
- **`Clock` inyectable** para que la lógica temporal sea testeable.

### Estructura del proyecto

```
src/main/java/co/com/adventure
├── CanivalesApplication.java        Arranque (@SpringBootApplication + @ConfigurationPropertiesScan)
├── config/
│   ├── OpenApiConfig.java           Metadatos OpenAPI 3 (springdoc)
│   ├── SmsProperties.java           Binding de 'adventure.sms.*' (URL, credenciales, timeouts)
│   └── TimeConfig.java              Bean Clock con zona America/Bogota
├── controller/
│   ├── AdventureController.java     Los 3 endpoints REST
│   ├── AdventureRoutes.java         Constantes de rutas
│   └── GlobalExceptionHandler.java  Excepciones ─▶ respuestas HTTP + StatusCodeDto
├── dto/
│   ├── SaveScoreRequest.java        Entrada de saveScore (con Bean Validation)
│   ├── OptionsDto.java              Salida de un nodo (@JsonProperty conserva el JSON)
│   ├── ScoreDto.java               Salida de un score
│   └── StatusCodeDto.java           Cuerpo genérico { "message": ... }
├── exception/
│   ├── OptionNotFoundException.java ─▶ HTTP 404
│   └── SmsDeliveryException.java    ─▶ HTTP 502 (si se propagara)
├── mapper/
│   ├── OptionMapper.java
│   └── ScoreMapper.java             Normaliza el nombre (guardado en minúsculas)
├── model/
│   ├── Options.java                 @Entity tabla OPTIONS
│   └── Score.java                   @Entity tabla SCORES
├── repository/
│   ├── OptionsRepository.java       CrudRepository<Options, Integer>
│   └── ScoreRepository.java         CrudRepository<Score, Integer>
├── service/
│   ├── OptionsService.java  / OptionsServiceImpl.java
│   └── ScoreService.java    / ScoreServiceImpl.java
└── sms/
    ├── SmsGateway.java              Puerto de salida
    ├── AltiriaSmsGateway.java       Adaptador HTTP (activo si adventure.sms.enabled=true)
    ├── NoOpSmsGateway.java          Adaptador inerte (activo en caso contrario)
    ├── ScoreSmsMessageBuilder.java  Renderiza el texto desde la plantilla
    └── AltiriaApiParams.java        Constantes del protocolo Altiria

src/main/resources/application.yml   Configuración (con placeholders ${ENV:default})
src/test/resources/application-test.yml  Perfil 'test' con H2 en memoria

docker/db/init/*.sql                 Esquema + datos de ejemplo (historia y scores)
Dockerfile                           Build multi-etapa (Gradle 6.9.4/JDK11 ─▶ JRE 11)
docker-compose.yml                   db + altiria-mock + app
```

---

## 3. Modelo de datos

### Tabla `OPTIONS` — nodos de la historia

| Columna | Tipo | Descripción |
|---|---|---|
| `id` | `integer` PK | Id del nodo (asignado de forma explícita en los datos semilla) |
| `description` | `text` | Texto narrativo mostrado al jugador |
| `opt_1_text` / `opt_2_text` / `opt_3_text` | `text` | Etiqueta de cada opción |
| `opt_1_value` / `opt_2_value` / `opt_3_value` | `integer` | Id del **siguiente** nodo. `0` ⇒ la opción no existe / es un final |

Una partida es un recorrido por esta tabla: el cliente pide el nodo `id`, muestra
las opciones con `opt_N_value != 0`, y al elegir una vuelve a pedir ese `id`.

### Tabla `SCORES` — resultados de partidas

| Columna | Tipo | Descripción |
|---|---|---|
| `id` | `serial` PK | Autogenerado |
| `name` | `varchar` | Nombre del jugador (se guarda en minúsculas) |
| `s_hour` / `s_minute` / `s_second` | `integer` | Tiempo empleado en completar el reto |
| `s_timestamp` | `timestamp` | Instante de registro (zona `America/Bogota`) |
| `cellphone` | `varchar` | Celular del jugador (destino del SMS) |

El esquema se versiona en [`docker/db/init/01-schema.sql`](docker/db/init/01-schema.sql);
la aplicación **no** lo genera (`spring.jpa.hibernate.ddl-auto=none`).

---

## 4. Endpoints

Base URL local: `http://localhost:9697` · Prefijo: `/app/adventure`

### `POST /app/adventure/{id}` — obtener un nodo

| | |
|---|---|
| Path param | `id` (entero) — nodo a recuperar |
| `200` | `OptionsDto` con la descripción y las 3 opciones |
| `400` | `{id}` no es un número |
| `404` | no existe un nodo con ese `id` |

```bash
curl -X POST http://localhost:9697/app/adventure/1
```
```json
{
  "id": 1,
  "description": "Despiertas tras el accidente de una avioneta ...",
  "opt_1_text": "Seguir el sonido de los tambores",
  "opt_2_text": "Caminar hacia el rio que se oye al oeste",
  "opt_3_text": "Quedarte junto a los restos de la avioneta y encender una fogata",
  "opt_1_value": 2,
  "opt_2_value": 3,
  "opt_3_value": 4
}
```

### `POST /app/adventure/saveScore` — guardar resultado (+ SMS)

| | |
|---|---|
| Body | `SaveScoreRequest` (JSON) |
| `201` | `{ "message": "saved" }` |
| `400` | body inválido (detalle campo a campo en `message`) |

Reglas de validación: `name` no vacío; `hour ≥ 0`; `minute`/`second` en `[0, 59]`;
`cellphone` = 7 a 15 dígitos.

```bash
curl -X POST http://localhost:9697/app/adventure/saveScore \
  -H 'Content-Type: application/json' \
  -d '{"name":"Carlos","hour":0,"minute":7,"second":3,"cellphone":"3212664870"}'
```

Tras persistir, se intenta enviar un SMS de felicitación. **El fallo del SMS no
revierte el guardado**: se registra un `WARN` y la respuesta sigue siendo `201`.

### `GET /app/adventure/scores` — listar resultados

```bash
curl -s http://localhost:9697/app/adventure/scores
```
```json
[
  { "id": 1, "name": "Sebastian", "hour": 0, "minute": 12, "second": 45,
    "timestamp": "2026-08-30T14:03:00", "cellphone": "3001234567" }
]
```

### Documentación y observabilidad

| Recurso | URL |
|---|---|
| Swagger UI | `http://localhost:9697/swagger-ui.html` |
| OpenAPI 3 JSON | `http://localhost:9697/v3/api-docs` |
| Health check | `http://localhost:9697/actuator/health` |

---

## 5. Configuración

Todos los parámetros tienen un valor por defecto apto para desarrollo y se pueden
sobrescribir por variable de entorno.

| Variable de entorno | Propiedad | Default | Descripción |
|---|---|---|---|
| `SERVER_PORT` | `server.port` | `9697` | Puerto HTTP |
| `DB_URL` | `spring.datasource.url` | `jdbc:postgresql://localhost:5432/adventure` | JDBC URL |
| `DB_USERNAME` | `spring.datasource.username` | `adventure` | Usuario BD |
| `DB_PASSWORD` | `spring.datasource.password` | `adventure` | Contraseña BD |
| `JPA_DDL_AUTO` | `spring.jpa.hibernate.ddl-auto` | `none` | Estrategia DDL de Hibernate |
| `JPA_SHOW_SQL` | `spring.jpa.show-sql` | `true` | Log de SQL |
| `SMS_ENABLED` | `adventure.sms.enabled` | `false` | `true` ⇒ envía SMS real; `false` ⇒ `NoOpSmsGateway` |
| `SMS_URL` | `adventure.sms.url` | `http://www.altiria.net/api/http` | Endpoint del proveedor |
| `SMS_LOGIN` / `SMS_PASSWORD` | `adventure.sms.login` / `.password` | *(vacío)* | Credenciales del proveedor |
| `SMS_SENDER_COUNTRY_CODE` | `adventure.sms.sender-country-code` | `57` | Prefijo país que se antepone al celular |
| `SMS_CONNECT_TIMEOUT` / `SMS_SOCKET_TIMEOUT` | `adventure.sms.connect-timeout` / `.socket-timeout` | `5s` / `60s` | Timeouts del cliente HTTP |

La plantilla del SMS (`adventure.sms.message-template`) admite los marcadores
`{name}`, `{score}` y `{date}`.

---

## 6. Conexiones y dependencias externas

| Dependencia | En producción (original) | En local (este repo) |
|---|---|---|
| **PostgreSQL** | Heroku (`ec2-3-214-3-162…:5432`) — **fuera de servicio** | Contenedor `postgres:15-alpine` |
| **Pasarela SMS (Altiria)** | `http://www.altiria.net/api/http` con credenciales embebidas | Contenedor mock (`hashicorp/http-echo`) que responde `OK`; o `SMS_ENABLED=false` |

En local no se realiza **ninguna** llamada a internet.

---

## 7. Cambios respecto a la versión original

Refactor orientado a *Clean Code* / *Clean Architecture* preservando el contrato
HTTP. Cambios de comportamiento **intencionados**:

| Antes | Ahora | Motivo |
|---|---|---|
| `id` no numérico → `404` con cuerpo vacío | `400` con `{ "message": ... }` | Semántica REST correcta (petición malformada) |
| `404` con cuerpo vacío | `404` con `{ "message": "No existe ... id N" }` | Respuestas de error homogéneas |
| `saveScore` con body inválido → se persistía basura | `400` con detalle de validación | Bean Validation en `SaveScoreRequest` |
| JSON mal formado → `500` | `400` "Cuerpo de la petición ilegible o mal formado" | Handler de `HttpMessageNotReadableException` |
| Fallo de SMS → `saveScore` devolvía `404` | Se persiste igual, `201`, `WARN` en log | El score no debe perderse por una notificación |
| `timestamp` truncado a medianoche (`Date` → `"…T00:00:00.000+00:00"`) | `LocalDateTime` con hora real (`"…T18:55:04"`) | `java.time`; sin *round-trip* con `SimpleDateFormat` |
| Credenciales de Altiria y BD en el código / `application.yml` | En `@ConfigurationProperties` + variables de entorno | Secretos fuera del binario |
| Spring Boot `2.4.7-SNAPSHOT` | `2.4.13` (release) | El repo de snapshots es efímero |
| springfox `2.9.2` (Swagger 2) | springdoc-openapi `1.5.13` (OpenAPI 3) | springfox está descontinuado; su Swagger UI tenía roto el botón *Try it out* con Spring 5.3 y documentaba los endpoints de Actuator |
| `sourceCompatibility 1.8` | `11` | Alinear con el JDK de build/runtime |

Otros: se eliminaron dependencias sin uso (`junit:4.12`, `commons-httpclient:3.1`,
`httpcore` explícito); se añadió `actuator` y H2 para tests offline; `httpclient`
`4.5` → `4.5.14`; clases `*Imp` renombradas a `*Impl`; `SwaggerConfig` (springfox)
→ `OpenApiConfig` (springdoc).

---

## 8. Tests

```bash
./gradlew test          # usa H2 en memoria (perfil 'test'), no requiere Docker
```

| Clase | Cobertura |
|---|---|
| `CanivalesApplicationTests` | Arranque del contexto de Spring (todos los beans se cablean) |
| `AdventureApiIntegrationTest` | Extremo a extremo sobre los 3 endpoints con servidor real (`RANDOM_PORT`) y H2: nodo OK / 404 / 400, guardar score + normalización de nombre + listar, validación de body y JSON mal formado |
