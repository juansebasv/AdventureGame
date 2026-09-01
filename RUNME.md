# RUNME — Levantar Adventure Game en local con Docker

Guía paso a paso. **Todo corre en contenedores**: no necesitas Java, Gradle ni
PostgreSQL instalados. No se hace ninguna llamada a internet en tiempo de ejecución.

Tiempo estimado la primera vez: ~5 min (descarga de imágenes + build de Gradle).

---

## 0. Requisitos previos

Necesitas **Docker Engine** con el plugin **Docker Compose v2**.

Comprobación:

```bash
docker --version
# Docker version 24.x o superior

docker compose version
# Docker Compose version v2.x  (con espacio: "docker compose", no "docker-compose")

docker info | grep -i 'server version'
# debe responder sin error -> el daemon está arrancado
```

Si `docker` requiere `sudo`, añádete al grupo `docker` una sola vez y reinicia sesión:

```bash
sudo usermod -aG docker "$USER"     # cerrar sesión y volver a entrar
```

---

## 1. Situarte en la raíz del proyecto

```bash
cd "/ruta/a/AdventureGame"
ls docker-compose.yml Dockerfile
# ambos ficheros deben existir
```

---

## 2. Qué se va a levantar

`docker-compose.yml` define tres servicios en la red `adventure-net`:

| Servicio | Contenedor | Imagen | Puerto host | Rol |
|---|---|---|---|---|
| `db` | `adventure-db` | `postgres:15-alpine` | `5432` | PostgreSQL. En el **primer arranque** ejecuta `docker/db/init/*.sql`: crea el esquema y carga la historia de ejemplo + 3 scores |
| `altiria-mock` | `adventure-altiria-mock` | `hashicorp/http-echo` | — | Sustituye al proveedor de SMS. Responde `200 OK` a todo |
| `app` | `adventure-app` | se **construye** con `Dockerfile` | `9697` | La API Spring Boot |

El `Dockerfile` compila el JAR en una etapa `gradle:6.9.4-jdk11` y lo ejecuta en
una imagen `eclipse-temurin:11-jre` como usuario no root.

---

## 3. Construir la imagen de la aplicación

```bash
docker compose build
```

- La primera vez descarga la imagen de Gradle y todas las dependencias (varios
  minutos). Las siguientes son casi instantáneas gracias a la caché de capas.
- Debe terminar con `BUILD SUCCESSFUL` y `=> naming to docker.io/library/...app`.

---

## 4. Levantar todo

```bash
docker compose up -d
```

Orden de arranque (lo gestiona `depends_on` + healthcheck):

1. `db` arranca y ejecuta los scripts de `docker/db/init/`.
2. Cuando `db` está *healthy* y `altiria-mock` está *up*, arranca `app`.

Comprobar estado:

```bash
docker compose ps
```

Espera a ver algo como:

```
NAME                     STATUS
adventure-altiria-mock   Up
adventure-app            Up (healthy)          <- puede tardar ~45s en pasar a healthy
adventure-db             Up (healthy)
```

Seguir el arranque de la API:

```bash
docker compose logs -f app
```

Busca la línea:

```
Started CanivalesApplication in X seconds
```

Sal de los logs con `Ctrl-C` (la app sigue corriendo).

---

## 5. Verificar que funciona

### 5.1 Health check

```bash
curl -s http://localhost:9697/actuator/health
# {"status":"UP"}
```

### 5.2 Obtener el nodo inicial de la historia

```bash
curl -s -X POST http://localhost:9697/app/adventure/1
```

Esperado (HTTP 200):

```json
{
  "id": 1,
  "description": "Despiertas tras el accidente de una avioneta en plena selva amazonica. ...",
  "opt_1_text": "Seguir el sonido de los tambores",
  "opt_2_text": "Caminar hacia el rio que se oye al oeste",
  "opt_3_text": "Quedarte junto a los restos de la avioneta y encender una fogata",
  "opt_1_value": 2,
  "opt_2_value": 3,
  "opt_3_value": 4
}
```

Recorre la historia siguiendo los `opt_N_value`:

```bash
curl -s -X POST http://localhost:9697/app/adventure/2   # rama "tambores"
curl -s -X POST http://localhost:9697/app/adventure/12  # final bueno
curl -s -X POST http://localhost:9697/app/adventure/6   # final malo
```

Casos de error:

```bash
curl -s -w '\n%{http_code}\n' -X POST http://localhost:9697/app/adventure/999
# {"message":"No existe ninguna opción con id 999"}
# 404

curl -s -w '\n%{http_code}\n' -X POST http://localhost:9697/app/adventure/abc
# {"message":"Parámetro 'id' inválido"}
# 400
```

### 5.3 Listar scores (datos de ejemplo)

```bash
curl -s http://localhost:9697/app/adventure/scores
```

Devuelve los 3 scores semilla (`Sebastian`, `Laura`, `Andres`).

### 5.4 Guardar un score (y disparar el SMS al mock)

```bash
curl -s -w '\n%{http_code}\n' -X POST http://localhost:9697/app/adventure/saveScore \
  -H 'Content-Type: application/json' \
  -d '{"name":"Carlos","hour":0,"minute":7,"second":3,"cellphone":"3212664870"}'
# {"message":"saved"}
# 201
```

Comprobar que el mock recibió la petición:

```bash
docker compose logs altiria-mock | tail -1
# ... "POST /api/http HTTP/1.1" 200 ...

docker compose logs app | grep -i "SMS aceptado"
# ... INFO SMS aceptado por el proveedor: OK
```

Validación de entrada:

```bash
curl -s -w '\n%{http_code}\n' -X POST http://localhost:9697/app/adventure/saveScore \
  -H 'Content-Type: application/json' \
  -d '{"name":"","minute":90,"cellphone":"abc"}'
# {"message":"name: must not be blank; minute: must be less than or equal to 59; cellphone: cellphone must contain between 7 and 15 digits"}
# 400
```

### 5.5 Swagger UI

Abre en el navegador: <http://localhost:9697/swagger-ui.html>

- Documentación OpenAPI 3 servida por **springdoc**. Spec JSON en
  <http://localhost:9697/v3/api-docs>.
- Solo aparecen los 3 endpoints del juego (sin ruido de Actuator).
- El botón **"Try it out" → "Execute"** funciona: lanza la petición real y muestra
  código de respuesta, cuerpo y `curl` equivalente.

---

## 6. Inspeccionar la base de datos

```bash
docker compose exec db psql -U adventure -d adventure
```

Dentro de `psql`:

```sql
\dt                                  -- tablas: options, scores
SELECT id, left(description, 50) FROM options ORDER BY id;
SELECT * FROM scores ORDER BY id;
\q
```

Conexión desde un cliente externo (DBeaver, IntelliJ, etc.):

```
Host: localhost   Puerto: 5432
Base: adventure   Usuario: adventure   Password: adventure
```

---

## 7. Modificar la historia de ejemplo

La historia vive en [`docker/db/init/02-seed-options.sql`](docker/db/init/02-seed-options.sql).
Los scripts de `docker/db/init/` **solo se ejecutan cuando la BD está vacía**. Para
aplicar cambios hay que recrear el volumen:

```bash
docker compose down -v      # borra el volumen de datos
docker compose up -d        # vuelve a levantar y re-siembra
```

Alternativa sin borrar todo: edita en caliente con `psql` (sección 6).

---

## 8. Enviar SMS reales (opcional)

Por defecto el `docker-compose.yml` apunta la app al **mock** (`SMS_URL=http://altiria-mock/api/http`).
Para usar un proveedor real necesitas una cuenta activa y **con saldo**, y cambiar
las variables de entorno del servicio `app` (o un `.env`):

```yaml
    environment:
      SMS_ENABLED: "true"
      SMS_URL: http://www.altiria.net/api/http
      SMS_LOGIN: tu-usuario
      SMS_PASSWORD: tu-clave
      SMS_SENDER_COUNTRY_CODE: "57"
```

> Las credenciales que había en el código original (`Constants.java`) devuelven
> `ERROR errNum:020` (autenticación rechazada): la cuenta ya no es válida.

Para **desactivar** el SMS por completo (el score se guarda igual):

```yaml
    environment:
      SMS_ENABLED: "false"
```

---

## 9. Parámetros configurables al levantar

Variables admitidas por `docker-compose.yml` (exportadas o en un archivo `.env` en
la raíz):

| Variable | Default | Uso |
|---|---|---|
| `APP_PORT` | `9697` | Puerto de la API en el host |
| `DB_PORT` | `5432` | Puerto de PostgreSQL en el host |
| `POSTGRES_USER` / `POSTGRES_PASSWORD` / `POSTGRES_DB` | `adventure` | Credenciales de la BD |

Ejemplo (otro puerto para evitar choques):

```bash
APP_PORT=8080 DB_PORT=5433 docker compose up -d
curl -s http://localhost:8080/actuator/health
```

---

## 10. Ciclo de trabajo al cambiar código

```bash
# 1. editas src/...
docker compose up -d --build app     # reconstruye solo la app y la relanza
docker compose logs -f app           # verificas el arranque
```

Ejecutar los tests (no requiere Docker de la app; usa H2 en memoria):

```bash
./gradlew test
```

---

## 11. Parar y limpiar

```bash
docker compose stop            # pausa los contenedores (conserva datos)
docker compose start           # los reanuda

docker compose down            # elimina contenedores y red (CONSERVA el volumen de BD)
docker compose down -v         # + elimina el volumen  -> próximo 'up' re-siembra desde cero

docker image rm adventuregame-app   # (opcional) borra la imagen construida
```

---

## 12. Solución de problemas

| Síntoma | Causa / solución |
|---|---|
| `docker compose` → `command not found` | Tienes Compose v1. Usa el plugin v2 (`docker compose`, con espacio) o instala `docker-compose-plugin` |
| `Cannot connect to the Docker daemon` | El servicio Docker no está arrancado: `sudo systemctl start docker` |
| `Bind for 0.0.0.0:9697 failed: port is already allocated` | Otro proceso usa el 9697: `APP_PORT=8080 docker compose up -d` |
| `Bind for 0.0.0.0:5432 failed` | Ya tienes PostgreSQL local: `DB_PORT=5433 docker compose up -d` |
| `app` reinicia con `Connection refused` a `db:5432` | Arrancó antes que la BD. `docker compose restart app` (o `docker compose up -d` de nuevo) |
| Cambié un `.sql` de `docker/db/init/` y no se aplica | Esos scripts solo corren con la BD vacía: `docker compose down -v && docker compose up -d` |
| `app` no pasa a `(healthy)` | El *start-period* del healthcheck es 45s. Revisa `docker compose logs app`; debe decir `Started CanivalesApplication` |
| Build de Gradle falla resolviendo dependencias | Comprueba conexión a `repo1.maven.org`. El build necesita internet **solo al construir la imagen**, no al ejecutar |
| `saveScore` responde `502` | Solo posible con `SMS_ENABLED=true` y un `SMS_URL` real inalcanzable o credenciales inválidas. Con el mock no ocurre |
| Permisos raros en `build/` o `.gradle/` tras usar el contenedor | `docker run --rm -v "$PWD":/p -w /p gradle:6.9.4-jdk11 chown -R "$(id -u):$(id -g)" /p/build /p/.gradle` |

---

## 13. Resumen (TL;DR)

```bash
cd "/ruta/a/AdventureGame"
docker compose up -d --build
docker compose logs -f app          # esperar "Started CanivalesApplication"
curl -s http://localhost:9697/actuator/health
curl -s -X POST http://localhost:9697/app/adventure/1
curl -s http://localhost:9697/app/adventure/scores
# ... trabajar ...
docker compose down                 # (o 'down -v' para resetear la BD)
```
