# Star Awards

A recognition platform for Version 1: colleagues nominate each other for great work,
a coordinator reviews the nominations — with rule-based flags and an optional Claude
verdict alongside — and decides who gets a Star Award each quarter.

Built as the final project for the Version 1 Early Careers Academy.

## Stack

**Angular 22** → **Spring Boot** → **MongoDB Atlas**, deployed to Azure.

## Layout

| Path | What's there |
| --- | --- |
| [`frontend/`](frontend/README.md) | The Angular app — nomination form + reviewer dashboard |
| `backend/` | The Spring Boot API |
| `data/` | The mock nomination spreadsheet used to seed a fresh database |
| `docs/` | Requirements, stakeholder notes, user stories, PESTLE/VMOST |
| [`docs/diagrams/`](docs/diagrams/README.md) | UML class diagrams for the backend and frontend |

## Running it locally

Two servers, started independently — see [`frontend/README.md`](frontend/README.md)
for the Angular side.

```bash
# backend — needs JAVA_HOME set and MONGODB_URI exported from backend/.env
cd backend && ./mvnw spring-boot:run     # → localhost:8080

# frontend
cd frontend && npm install && npm start  # → localhost:4200
```

The dev server proxies `/api` to the backend, so run both together.

## Docs

Requirements, stakeholder specs, and planning docs live in [`docs/`](docs/).
Class diagrams for the domain model, API layer, service architecture, and
frontend are in [`docs/diagrams/`](docs/diagrams/README.md).
