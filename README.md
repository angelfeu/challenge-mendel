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
Hoy es posible registrar una transacción con un `parent_id` que no corresponde a ninguna transacción existente. El sistema no lo rechaza y el cálculo del sum sigue siendo correcto, pero semánticamente se estaría creando una relación rota. Una mejora sería validar la existencia del padre al momento de la creación.

**Validación del monto**
El campo `amount` no tiene restricción de valor positivo. Dependiendo de las reglas de negocio, podría tener sentido rechazar montos negativos o iguales a cero.

## Correr localmente

Requiere Java 25 instalado.

```bash
./gradlew bootRun
```

La aplicación levanta en `http://localhost:8080`.

## Correr los tests

```bash
./gradlew test
```

Incluye tests unitarios por capa (controller, service, repository) y un test de integración que valida el escenario completo del enunciado.

## Correr con Docker

Requiere Docker instalado y corriendo.

```bash
docker build -t mendel-challenge .
docker run -p 8080:8080 mendel-challenge
```

El Dockerfile usa un build multi-stage: el primer stage resuelve y cachea dependencias, el segundo compila el jar, y el tercero genera la imagen final basada en `eclipse-temurin:25-jre-alpine` para mantener el peso mínimo. La aplicación corre con un usuario sin privilegios de root.
