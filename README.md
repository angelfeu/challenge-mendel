# Mendel Java Code Challenge

Servicio RESTful desarrollado en Spring Boot que permite registrar transacciones en memoria y consultarlas por tipo o calcular su suma transitiva a través de relaciones padre-hijo.

## Tecnologías

- Java 25
- Spring Boot 4.0.5
- Gradle 9
- JUnit 5 + Mockito
- Docker (multi-stage build)

## Decisiones de diseño

El proyecto está estructurado en capas bien definidas (controller, service, repository) siguiendo los principios SOLID. Cada capa tiene una responsabilidad única y se comunica a través de interfaces, lo que facilita el testing y permite intercambiar implementaciones sin afectar al resto del sistema.

El almacenamiento es en memoria mediante un `ConcurrentHashMap`, que garantiza acceso seguro en entornos concurrentes sin necesidad de sincronización explícita. Al ser un bean singleton de Spring, el estado persiste durante todo el ciclo de vida de la aplicación.

El monto de las transacciones se maneja con `BigDecimal` en lugar de `double` para evitar errores de precisión en operaciones financieras.

El desarrollo se realizó aplicando TDD: primero el test, luego la implementación, de forma incremental por endpoint y por capa.

## Endpoints

### Crear transacción

```
PUT /transactions/{transactionId}
```

Body:

```json
{
  "amount": 5000,
  "type": "cars",
  "parent_id": 10
}
```

`parent_id` es opcional. Respuesta:

```json
{ "status": "ok" }
```

### Obtener IDs por tipo

```
GET /transactions/types/{type}
```

Respuesta:

```json
[10, 11]
```

### Suma transitiva

```
GET /transactions/sum/{transactionId}
```

Devuelve la suma del monto de la transacción indicada más todos sus descendientes vinculados transitivamente por `parent_id`. Respuesta:

```json
{ "sum": 20000 }
```

### Ejemplo del enunciado

```
PUT /transactions/10  { "amount": 5000,  "type": "cars" }
PUT /transactions/11  { "amount": 10000, "type": "shopping", "parent_id": 10 }
PUT /transactions/12  { "amount": 5000,  "type": "shopping", "parent_id": 11 }

GET /transactions/types/cars   => [10]
GET /transactions/sum/10       => { "sum": 20000 }
GET /transactions/sum/11       => { "sum": 15000 }
```

## Consideraciones y mejoras identificadas

Durante el desarrollo se identificaron puntos que en un sistema productivo requerirían atención adicional. Se documentan aquí como parte del análisis honesto de la solución.

**Diseño del endpoint de creación**
El enunciado especifica `PUT /transactions/{id}` para crear transacciones. Sin embargo, REST establece que `PUT` es una operación idempotente orientada a actualizaciones sobre un recurso conocido. La forma semánticamente correcta sería exponer un `POST /transactions` para la creación y reservar `PUT /transactions/{id}` para modificaciones. La implementación actual respeta el contrato del enunciado pero vale la pena tenerlo en cuenta en un diseño de API real.

**Referencias circulares**
Si una transacción A declara como padre a B y B declara como padre a A, el cálculo de la suma transitiva entraría en un loop infinito. El enunciado no contempla este caso, pero se decidió implementar la detección al momento del `PUT`: si el `parent_id` genera un ciclo en la cadena de ancestros, la operación retorna un `400 Bad Request`.

**Validación de `parent_id`**
Se valida la existencia del padre al momento del `PUT`: si el `parent_id` no corresponde a una transacción registrada, la operación retorna un `404 Not Found`.

**Validación del monto**
El campo `amount` no tiene restricción de valor positivo. Dependiendo de las reglas de negocio, podría tener sentido rechazar montos negativos o iguales a cero.

**CI/CD y caché de dependencias**
Se tuvo en cuenta la incorporación de un pipeline de GitHub Actions para ejecutar los tests automáticamente en cada push, junto con estrategias de caché de dependencias Gradle para reducir tiempos de build. Para el alcance de este MVP se consideró overkilling: el Dockerfile ya resuelve el build reproducible y los tests se corren localmente antes de commitear.

## Seguridad

Todos los endpoints de la API requieren el header `X-Api-Key`. Las rutas de Swagger, OpenAPI y Actuator quedan excluidas para facilitar la exploración y el monitoreo sin fricción.

```
X-Api-Key: <valor>
```

El valor se configura mediante la variable de entorno `API_KEY`. Si no se define, el sistema usa `secret` como valor por defecto (solo para desarrollo local).

En Swagger UI el botón **Authorize** permite ingresar la key antes de ejecutar los endpoints.

## Correr localmente

Requiere Java 25 instalado.

```bash
./gradlew bootRun
```

Para usar una key distinta al default:

```bash
API_KEY=mi-clave ./gradlew bootRun
```

La aplicación levanta en `http://localhost:8080`.

| Recurso | URL |
|---|---|
| UI | `http://localhost:8080` |
| API | `http://localhost:8080/transactions` |
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| OpenAPI JSON | `http://localhost:8080/api-docs` |
| Health | `http://localhost:8080/actuator/health` |
| Metrics | `http://localhost:8080/actuator/metrics` |

### UI de prueba

La aplicación incluye una interfaz web en `http://localhost:8080` para probar los tres endpoints sin necesidad de curl ni Postman. Permite ingresar el `X-Api-Key` directamente en el header de la página, visualiza el status code, el tiempo de respuesta y el JSON formateado de cada llamada.

## Correr los tests

```bash
./gradlew test
```

Incluye tests unitarios por capa (controller, service, repository) y un test de integración que valida el escenario completo del enunciado.

## Correr con Docker

Requiere Docker instalado y corriendo.

```bash
docker build -t mendel-challenge .
docker run -p 8080:8080 -e API_KEY=mi-clave mendel-challenge
```

El Dockerfile usa un build multi-stage: el primer stage resuelve y cachea dependencias, el segundo compila el jar, y el tercero genera la imagen final basada en `eclipse-temurin:25-jre-alpine` para mantener el peso mínimo. La aplicación corre con un usuario sin privilegios de root.
