HomeReno API
============

Purpose
- Development backend that powers the HomeReno frontend with projects, contractors, and tasks.
- Designed for local use.

Tech Stack
- Java (Spring Boot)
- MongoDB
- Maven

Requirements
- Java 24 (matches the `pom.xml` target version)
- Maven 3.9+
- MongoDB running locally

MongoDB
- Default URI: `mongodb://localhost:27017/HomeReno`
- Update in `HomeReno/src/main/resources/application.properties` if needed.

How to Run
1) Start MongoDB locally.
2) From the repo root:
   - `cd HomeReno`
   - `mvn spring-boot:run`
3) API base URL: `http://localhost:8080`

Seed Data
- On startup, the database is cleared and re-seeded with sample data.
- This behavior is controlled by `HomeReno/src/main/java/com/example/HomeReno/config/DataInitializer.java`.

API Docs and Testing
- Full documentation: `Documentation/Documentation.md`
- Quick testing guide: `Documentation/API_Testing_Guide.txt`

Notes
- CORS is enabled for `http://localhost:4200` (Angular default).
- Tests can be run with `mvn test`.
