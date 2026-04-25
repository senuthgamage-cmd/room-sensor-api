# room-sensor-api

> **5COSC022W Client-Server Architectures Coursework 2025/26**  
> University of Westminster · JAX-RS (Jersey 3.x / Jakarta EE) · Grizzly Embedded Server

---

## API Overview

A RESTful HTTP API for managing campus rooms and IoT sensors. Built with JAX-RS (Jersey) on an embedded Grizzly HTTP server — no external servlet container required.

| Resource | Base Path |
|---|---|
| Discovery | `GET /api/v1` |
| Rooms | `/api/v1/rooms` |
| Sensors | `/api/v1/sensors` |
| Sensor Readings | `/api/v1/sensors/{id}/readings` |

### Full Endpoint List

```
GET    /api/v1                          – Discovery / API metadata
GET    /api/v1/rooms                    – List all rooms
POST   /api/v1/rooms                    – Create a room
GET    /api/v1/rooms/{id}               – Get a room by ID
PUT    /api/v1/rooms/{id}               – Update a room
DELETE /api/v1/rooms/{id}               – Delete a room (blocked if sensors assigned)
GET    /api/v1/sensors                  – List all sensors (optional ?type= filter)
POST   /api/v1/sensors                  – Create a sensor (validates roomId)
GET    /api/v1/sensors/{id}             – Get a sensor by ID
GET    /api/v1/sensors/{id}/readings    – List all readings for a sensor
POST   /api/v1/sensors/{id}/readings    – Append a new reading
```

### Data Models

**Room** — `id`, `name`, `floor`

**Sensor** — `id`, `type`, `roomId`, `readings[]`

**Reading** — `id`, `value`, `timestamp`

---

## Tech Stack

- **Java 21**
- **JAX-RS** via Jersey 3.x (Jakarta EE namespace — `jakarta.ws.rs.*`)
- **Grizzly 2** embedded HTTP server
- **Jackson** for JSON serialisation
- **Maven** build tool
- **In-memory storage** only (`Collections.synchronizedMap(new HashMap<>())`) — no database

---

## Project Structure

```
room-sensor-api/
├── pom.xml
└── src/main/java/com/example/roomsensor/
    ├── Main.java                                   # Entry point, Grizzly server
    ├── RoomSensorApplication.java                  # ResourceConfig bootstrap
    ├── api/
    │   ├── ApiRootResource.java                    # GET /api/v1
    │   ├── RoomResource.java                       # /api/v1/rooms
    │   ├── SensorResource.java                     # /api/v1/sensors + sub-resource locator
    │   └── SensorReadingsResource.java             # /api/v1/sensors/{id}/readings
    ├── domain/
    │   ├── Room.java
    │   ├── Sensor.java
    │   └── Reading.java
    ├── dto/
    │   ├── CreateRoomRequest.java
    │   ├── UpdateRoomRequest.java
    │   ├── CreateSensorRequest.java
    │   ├── CreateReadingRequest.java
    │   └── ErrorResponse.java
    ├── exception/
    │   ├── ConflictException.java                  → 409
    │   ├── ConflictExceptionMapper.java
    │   ├── ForbiddenOperationException.java        → 403
    │   ├── ForbiddenOperationExceptionMapper.java
    │   ├── UnprocessableEntityException.java       → 422
    │   ├── UnprocessableEntityExceptionMapper.java
    │   ├── JsonProcessingExceptionMapper.java      → 400
    │   ├── WebApplicationExceptionMapper.java      → passthrough
    │   └── GlobalExceptionMapper.java              → 500
    ├── filter/
    │   ├── RequestLoggingFilter.java
    │   └── ResponseLoggingFilter.java
    └── store/
        └── InMemoryStore.java                      # Singleton store
```

---

## Build & Run

### Prerequisites

- Java 21+ (`java -version`)
- Maven 3.8+ (`mvn -version`)

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/senuthgamage-cmd/room-sensor-api.git
cd room-sensor-api

# 2. Build the fat executable JAR
mvn clean package -q

# 3. Run the server
java -jar target/room-sensor-api-1.0-SNAPSHOT.jar
```

The server starts on **http://localhost:8080**.  
The API root is **http://localhost:8080/api/v1**.

Press **Enter** in the terminal to stop the server.

---

## Sample curl Commands

### 1 · Discovery — GET /api/v1

```bash
curl -s http://localhost:8080/api/v1
```

Expected response (`200 OK`):
```json
{
  "name": "room-sensor-api",
  "status": "ok",
  "endpoints": ["/api/v1/rooms", "/api/v1/sensors"]
}
```

---

### 2 · Create a Room — POST /api/v1/rooms

```bash
curl -s -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"LIB-301\",\"name\":\"Library Quiet Study\",\"floor\":\"3\"}"
```

Expected response (`201 Created`):
```json
{
  "id": "LIB-301",
  "name": "Library Quiet Study",
  "floor": "3"
}
```

---

### 3 · Create a Sensor — POST /api/v1/sensors

```bash
curl -s -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"TEMP-001\",\"type\":\"Temperature\",\"roomId\":\"LIB-301\"}"
```

Expected response (`201 Created`):
```json
{
  "id": "TEMP-001",
  "type": "Temperature",
  "roomId": "LIB-301",
  "readings": []
}
```

---

### 4 · Filter Sensors by Type — GET /api/v1/sensors?type=Temperature

```bash
curl -s "http://localhost:8080/api/v1/sensors?type=Temperature"
```

Returns only sensors where `type` equals `"Temperature"` (case-insensitive).

---

### 5 · Post a Sensor Reading — POST /api/v1/sensors/TEMP-001/readings

```bash
curl -s -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d "{\"value\":21.5}"
```

Expected response (`201 Created`):
```json
{
  "id": "a3f1c2d4-...",
  "value": 21.5,
  "timestamp": "2025-04-24T10:30:00Z"
}
```

---

### 6 · List Readings for a Sensor — GET /api/v1/sensors/TEMP-001/readings

```bash
curl -s http://localhost:8080/api/v1/sensors/TEMP-001/readings
```

---

### 7 · Delete a Room with Sensors — triggers 403

```bash
curl -s -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

Expected response (`403 Forbidden`) — room still has sensors assigned:
```json
{
  "status": 403,
  "error": "Cannot delete room with assigned sensors: LIB-301"
}
```

---

### 8 · Create Sensor with invalid roomId — triggers 422

```bash
curl -s -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"CO2-999\",\"type\":\"CO2\",\"roomId\":\"DOES-NOT-EXIST\"}"
```

Expected response (`422 Unprocessable Entity`):
```json
{
  "status": 422,
  "error": "Invalid roomId: DOES-NOT-EXIST"
}
```

---

## Report: Answers to Coursework Questions

---

### Part 1 — Service Architecture & Setup

**Q1.1: Explain the default lifecycle of a JAX-RS resource class. How does this impact in-memory data management?**

By default, JAX-RS uses a **per-request lifecycle**: the runtime creates a brand-new instance of each resource class for every incoming HTTP request and discards it once the response is sent. This means instance fields on a resource class cannot safely hold shared state — data written through one request's instance would be invisible to the next.

To address this, all shared state in this project is held in `InMemoryStore`, a static singleton. Its maps are created with `Collections.synchronizedMap(new HashMap<>())`, which wraps every individual map operation in a `synchronized` block, preventing data corruption from concurrent reads and writes across request threads. Resource classes obtain a reference to this singleton at construction time via `InMemoryStore.getInstance()`, so every per-request instance operates on the same underlying data without any risk of losing writes or observing stale state.

---

**Q1.2: Why is HATEOAS considered a hallmark of advanced RESTful design? How does it benefit client developers?**

HATEOAS (Hypermedia As The Engine Of Application State) means that every API response embeds navigable links to related resources, so clients can discover and traverse the API dynamically rather than relying on hard-coded URLs. It represents Richardson Maturity Model Level 3 — the highest level of REST maturity.

Benefits for client developers include:

- **Resilience to URI changes** — clients that follow links rather than hard-coding paths continue to work when a resource path is refactored, requiring no client-side update.
- **Discoverability** — a developer can start at the root (`/api/v1`) and explore the full API by following embedded links, reducing dependence on external documentation.
- **Reduced coupling** — the client depends only on stable link-relation names (e.g. `"rooms"`, `"sensors"`) rather than specific paths, letting the server and client evolve independently.

---

### Part 2 — Room Management

**Q2.1: What are the implications of returning only IDs versus full room objects when listing rooms?**

Returning **only IDs** minimises payload size and network bandwidth, which matters for large collections. The downside is that a client needing full room detail must make N+1 HTTP requests — one to list IDs, then one per room — increasing latency and server load.

Returning **full objects** is convenient for clients that need all metadata immediately and avoids extra round-trips. However, for large collections it wastes bandwidth and forces serialisation of all fields even when the client only needs a subset.

A practical middle ground is a **summary projection** — return only `id` and `name` in the list response, and expose full detail via `GET /rooms/{id}`. This balances bandwidth efficiency with usability.

---

**Q2.2: Is DELETE idempotent in your implementation? Justify by describing what happens on repeated calls.**

Yes, DELETE is idempotent in this implementation. HTTP idempotency means repeated calls produce the same **server state**, not necessarily the same response code.

- **First call**: the room exists and has no sensors assigned → it is removed → `204 No Content` is returned.
- **Second and subsequent calls**: the room no longer exists → `NotFoundException` is thrown → `WebApplicationExceptionMapper` returns `404 Not Found`.

In both cases the resulting server state is identical — the room is absent. This is consistent with the HTTP/1.1 specification, which explicitly classifies DELETE as idempotent. The differing status codes are expected and do not violate idempotency.

---

### Part 3 — Sensor Operations & Linking

**Q3.1: What happens if a client sends data in a format other than `application/json` to a `@Consumes(APPLICATION_JSON)` endpoint?**

JAX-RS performs content-type negotiation **before** the resource method body executes. If the incoming `Content-Type` header does not match the declared `@Consumes` media type, the framework automatically rejects the request with **HTTP 415 Unsupported Media Type** — the method is never invoked. In this project, the `WebApplicationExceptionMapper` intercepts that JAX-RS exception and serialises it into a consistent JSON `ErrorResponse`, ensuring even the rejection itself is returned as valid JSON rather than an HTML error page.

---

**Q3.2: Why is `@QueryParam` generally superior to a path segment for filtering (e.g. `/sensors?type=CO2` vs `/sensors/type/CO2`)?**

Using `@QueryParam` is the semantically correct approach for filtering because:

1. **Resource identity** — path segments define what a resource *is*. `/sensors/type/CO2` implies that `type/CO2` is itself a distinct resource, which is misleading; no such sub-resource exists.
2. **Optionality** — query parameters are naturally optional. The base collection `/sensors` remains fully accessible with no filter, and multiple filters compose cleanly: `?type=CO2&roomId=LIB-301`.
3. **REST convention** — REST distinguishes between the resource itself (path) and parameters that shape the representation returned (query). Filtering is a representation concern, not a resource-identity concern.
4. **Cacheability** — query strings are a well-understood HTTP convention that proxies, browsers, and CDNs handle correctly.

---

### Part 4 — Deep Nesting with Sub-Resources

**Q4.1: Discuss the architectural benefits of the Sub-Resource Locator pattern.**

In `SensorResource`, the method annotated `@Path("/{id}/readings")` carries no HTTP verb annotation, making it a **sub-resource locator**. Jersey calls this method to obtain a `SensorReadingsResource` instance and then continues path resolution within that class.

Benefits include:

- **Single Responsibility** — `SensorResource` manages sensor CRUD; `SensorReadingsResource` manages reading history. Each class has one clear purpose and stays focused.
- **Reduced complexity** — a monolithic controller accumulating every nested path would grow to hundreds of methods, becoming difficult to read, test, and extend. Delegation keeps each class manageable.
- **Context injection** — the locator method resolves the parent `Sensor` object and passes it directly into the sub-resource constructor, eliminating repeated lookups in every reading method.
- **Testability** — `SensorReadingsResource` can be unit-tested in isolation without standing up a full JAX-RS container.
- **Lazy resolution** — Jersey only instantiates the sub-resource class when the path segment actually matches, avoiding unnecessary object creation on unrelated requests.

---

### Part 5 — Error Handling, Exception Mapping & Logging

**Q5.2: Why is HTTP 422 more semantically accurate than 404 when a payload references a missing resource?**

`404 Not Found` means the **request URI itself** was not found on the server. In the case of `POST /api/v1/sensors` with an invalid `roomId`, the URI is perfectly valid and resolved correctly — the endpoint exists and accepted the request.

The problem is inside the **request body**: the `roomId` field references a room that does not exist in the system. The request is syntactically correct JSON, but it fails a **semantic business-logic constraint**. HTTP `422 Unprocessable Entity` is defined precisely for this scenario: the server understands the content type, the syntax is valid, but it cannot process the instructions due to a semantic error. Using 422 allows clients to distinguish "your URL is wrong" (404) from "your payload references something that doesn't exist" (422), enabling more precise error handling on the client side.

---

**Q5.4: From a cybersecurity standpoint, what are the risks of exposing Java stack traces to API consumers?**

Exposing raw stack traces is a serious **information disclosure** vulnerability. An attacker can extract:

1. **Framework and library versions** — package names reveal the exact technology stack. The attacker cross-references these against public CVE databases to find known exploits targeting those specific versions.
2. **Internal class structure and file paths** — fully-qualified class names and line numbers expose the application's architecture, aiding reverse-engineering of business logic and identification of injection points.
3. **Sensitive runtime data** — exception messages can contain SQL fragments, file system paths, internal hostnames, configuration values, or partial user data, all useful for reconnaissance.
4. **Error-based probing** — repeated stack traces from varied inputs let attackers map code paths and identify exploitable edge cases.

The `GlobalExceptionMapper` mitigates all of these by catching every `Throwable` and returning only a generic `{"status":500,"error":"Internal server error"}` to the consumer, while the full exception detail remains available in server logs for authorised operators — applying the principle of minimal information disclosure.

---

**Q5.5: Why use JAX-RS filters for logging rather than inserting log statements into every resource method?**

This project uses two dedicated filter classes — `RequestLoggingFilter` (implements `ContainerRequestFilter`) and `ResponseLoggingFilter` (implements `ContainerResponseFilter`) — rather than embedding log calls in each resource method. The advantages are:

1. **DRY (Don't Repeat Yourself)** — logging is written once and applies automatically to every existing and future endpoint without any additional code.
2. **Consistency** — every request and response is logged in exactly the same format. Manual insertion risks omissions or differing formats across methods.
3. **Separation of concerns** — resource methods remain focused on business logic. Observability is a cross-cutting infrastructure concern and belongs in dedicated classes.
4. **Ease of change** — updating the log format, adding structured logging, or directing output elsewhere requires modifying only the filter classes, not every resource method across the codebase.
5. **Completeness** — filters operate at the framework level and intercept every request, including those short-circuited by an exception mapper. A log statement inside a resource method body would be skipped entirely in such scenarios.
