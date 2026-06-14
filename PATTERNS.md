# Architectural Design Patterns Used

Although Pawsy was migrated from a monolithic application to a microservices architecture using Spring Boot and Kubernetes, several enterprise integration patterns were intentionally adopted to improve separation of concerns, scalability, and maintainability.

---

## Backend-for-Frontend (BFF)

### Description

The Backend-for-Frontend (BFF) pattern introduces a dedicated application that serves as the single entry point for the user interface. Instead of the browser communicating directly with multiple microservices, all requests pass through a specialized frontend-oriented backend.

### How Pawsy Uses It

`pawsy` acts as the application's BFF:

* Handles Spring Security authentication and user sessions
* Renders all Thymeleaf templates
* Performs API composition by aggregating data from multiple services
* Converts REST responses into view models for the UI
* Provides a single endpoint for browser interactions

### Architecture

```
Browser → Pawsy (BFF) → User Service
                      → Pet Service
                      → Adoption Service
```

### Benefits

* The browser remains unaware of internal service topology.
* UI requirements are isolated from backend services.
* Backend services can evolve independently of the frontend.
* Authentication logic exists in a single location.

---

## Edge Authentication Pattern

### Description

The Edge Authentication pattern centralizes authentication at the system boundary. Internal services do not authenticate users directly and instead trust requests originating from the edge application.

### How Pawsy Uses It

Authentication is performed exclusively by `pawsy` using Spring Security:

* User login and logout are handled by Pawsy
* Sessions are stored only in Pawsy
* Backend services are not directly exposed to users

### Architecture

```
Browser → Pawsy (Authentication) → Internal Services
```

### Benefits

* Security configuration exists in only one application.
* Backend services remain simple and focused on business logic.
* No JWT or OAuth server is required for internal communication.
* Internal services cannot be directly accessed from outside the cluster.

---

## API Composition Pattern

### Description

API Composition occurs when an application gathers information from multiple services and combines it into a single response.

### How Pawsy Uses It

Some views require information owned by different services.

Example:

* `Pet Service` owns `Shelter`
* `User Service` owns `User`
* `Shelter` stores only `manager` (`Username`)

To display a shelter page:

1. Pawsy requests the shelter from Pet Service.
2. Pawsy extracts `manager's username`.
3. Pawsy requests manager details from User Service.
4. Pawsy combines the responses into a single view model.
5. Thymeleaf renders the final page.

### Architecture

```
Pawsy
├── GET Shelter → Pet Service
└── GET Manager → User Service
```

### Benefits

* Services remain independent.
* Cross-database joins are avoided.
* Each service owns its own domain model.
* Complex UI requirements can be satisfied without coupling services together.

---

## Database per Service Pattern

### Description

The Database per Service pattern states that every microservice owns its own persistence layer and is solely responsible for its data.

### How Pawsy Uses It

User Service

* Owns user accounts and authentication data
* Uses `{USER_DB_NAME}`

Pet Service

* Owns pets, shelters, and related entities
* Uses `{PET_DB_NAME}`

Adoption Service

* Owns adoption requests and workflows
* Uses `{ADOPTION_DB_NAME}`

Services communicate through REST APIs rather than directly accessing another service's database.

### Architecture

```
User Service → {USER_DB_NAME}

Pet Service → {PET_DB_NAME}

Adoption Service → {ADOPTION_DB_NAME}
```

### Benefits

* Strong service boundaries and ownership.
* Independent deployment and schema evolution.
* Services can be scaled individually.
* Prevents tight coupling through shared databases.
* Encourages communication through explicit APIs rather than direct data access.

---

## Pattern Interaction in Pawsy

```
Browser
↓
Pawsy (BFF + Edge Authentication)
↓
API Composition Layer
↓
User Service → Own Database
Pet Service → Own Database
Adoption Service → Own Database
```

Together, these patterns provide a simple but production-oriented architecture in which authentication is centralized, services remain autonomous, data ownership is clearly defined, and the frontend can aggregate information from multiple services without introducing direct dependencies between microservices.
