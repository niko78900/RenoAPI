HomeReno API Documentation
==========================
Base URL: http://localhost:8080

Purpose and Scope
- This backend exists to power the HomeReno frontend with persistent data and workflow behavior.
- It is intentionally simple and optimized for development speed and clarity over production hardening.

Architecture at a Glance
- Spring Boot REST API with a controller -> service -> repository structure.
- MongoDB document storage for projects, contractors, tasks, and images.
- CORS is enabled for http://localhost:4200 (Angular frontend).

Project Structure
- controller: REST endpoints, request parsing, response shaping.
- service: business rules, cross-entity updates, and orchestration.
- repository: Spring Data MongoDB repository interfaces.
- entity: MongoDB document models.
- controller/dto: response-only DTOs (ProjectResponse).

Data Model and Relationships
Project (collection: projects)
- Fields: id, name, budget, contractorId, address, latitude, longitude, progress, number_of_workers, finished, taskIds[], ETA.
- Relationship: projectId links to tasks; contractorId links to contractors.
- Coordinates are optional and can be set on create or patch.
- finished is an explicit boolean flag, separate from progress.

Contractor (collection: contractors)
- Fields: id, fullName, price, expertise (JUNIOR | APPRENTICE | SENIOR).

Task (collection: tasks)
- Fields: id, projectId, name, status (NOT_STARTED | WORKING | FINISHED | CANCELED).

Image (collection: images)
- Stored entity only; no endpoints currently expose images.

Source of Truth for Tasks
- Project.taskIds is the authoritative list of tasks for a project.
- Tasks should always belong to a project; projectId is required for task create/update.
- Creating/updating/deleting tasks via /api/tasks updates Project.taskIds accordingly.
- Creating a task via /api/projects/{projectId}/tasks also writes the task and links it.
- Deleting a project does not currently delete its tasks; those tasks remain in the tasks collection.

Field Naming (Request vs Response)
- Project requests use the Project entity field names:
  - contractor (not contractorId)
  - number_of_workers (snake case)
  - ETA (represented as eta in most clients and docs)
- Project responses use ProjectResponse:
  - contractorId and contractorName
  - numberOfWorkers (camel case)
  - eta (lower case)
- Task uses projectId in both requests and responses.

Derived and Automatic Behavior
- number_of_workers is recalculated when budget is patched: (budget / 2) / 1500.
- On API create, number_of_workers is whatever the client supplies (no server default).
- finished is updated only by the dedicated finished endpoint.
- Latitude/longitude patches are optional; omitted values do not overwrite existing coordinates.
- Progress is intended to be 0-100 but is not validated by the API.

Error Handling and Status Codes
- 400: missing or invalid fields on task create/update or project patch endpoints.
- 404: resource not found on most controllers that catch exceptions.
- 204: no content when a contractor has no projects.
- Some endpoints do not catch runtime exceptions and may return 500 if the ID is invalid:
  - GET /api/projects/timeline/{id}
  - DELETE /api/projects/{id}/tasks/{taskId}

API Endpoints
Project API
- GET /api/projects
  - Returns List<ProjectResponse>.
- GET /api/projects/{id}
  - 200 with ProjectResponse, or 404.
- GET /api/projects/adr/{address}
  - Exact address match, 200 or 404.
- GET /api/projects/name/{name}
  - Exact name match, 200 or 404.
- GET /api/projects/contractor/{contractorId}
  - 200 with list or 204 if none.
- GET /api/projects/timeline/{id}
  - Returns a summary map with string keys and values.
  - Example response:
    {
      "ID": "PROJECT_ID",
      "Name": "Home Renovation #1",
      "Progress": "45 %",
      "Task List": ["TASK_ID_1", "TASK_ID_2"],
      "Estimated Time to finish: ": "3 Months"
    }
- GET /api/projects/{id}/finished
  - Returns { "finished": true|false }.
- POST /api/projects
  - Creates a project. Example body:
    {
      "name": "Home Renovation #2",
      "budget": 75000,
      "contractor": "CONTRACTOR_ID_HERE",
      "address": "123 Main Street",
      "latitude": 41.9982,
      "longitude": 21.4254,
      "progress": 0,
      "finished": false,
      "number_of_workers": 20,
      "taskIds": [],
      "eta": 4
    }
- DELETE /api/projects/{id}
  - Deletes a project (no task cascade).
- PATCH /api/projects/{id}/contractor
  - Body: { "contractorId": "NEW_CONTRACTOR_ID", "latitude": 41.9982, "longitude": 21.4254 }
- PATCH /api/projects/{id}/contractor/remove
  - Clears contractorId.
- PATCH /api/projects/{id}/address
  - Body: { "address": "456 Updated Avenue", "latitude": 41.9982, "longitude": 21.4254 }
- PATCH /api/projects/{id}/name
  - Body: { "name": "Updated Project Name", "latitude": 41.9982, "longitude": 21.4254 }
- PATCH /api/projects/{id}/budget
  - Body: { "budget": 82000, "latitude": 41.9982, "longitude": 21.4254 }
- PATCH /api/projects/{id}/workers
  - Body: { "workers": 18, "latitude": 41.9982, "longitude": 21.4254 }
- PATCH /api/projects/{id}/progress
  - Body: { "progress": 65, "latitude": 41.9982, "longitude": 21.4254 }
- PATCH /api/projects/{id}/eta
  - Body: { "eta": 3, "latitude": 41.9982, "longitude": 21.4254 }
- PATCH /api/projects/{id}/finished
  - Body: { "finished": true }
- POST /api/projects/{projectId}/tasks
  - Creates a task and links it to the project.
- DELETE /api/projects/{projectId}/tasks/{taskId}
  - Removes a task from the project and deletes it.

ProjectResponse Shape
- id, name, address, latitude, longitude, budget, progress, finished,
  numberOfWorkers, contractorId, contractorName, taskIds, eta
- contractorName is derived by lookup; if contractorId is missing or invalid, it is null.

Contractor API
- GET /api/contractors
- GET /api/contractors/{id}
- GET /api/contractors/search/{name}
  - Case-insensitive "contains" search on fullName.
- GET /api/contractors/expertise/{level}
- GET /api/contractors/expertise
  - Returns the expertise enum values.
- POST /api/contractors
  - Example body:
    {
      "fullName": "Jane Builder",
      "price": 950,
      "expertise": "SENIOR"
    }
- PUT /api/contractors/{id}
  - Replaces fields on the existing contractor.
- DELETE /api/contractors/{id}

Task API
- GET /api/tasks
- GET /api/tasks/{id}
- GET /api/tasks/project/{projectId}
  - Fetches tasks by projectId from the tasks collection.
- GET /api/tasks/statuses
  - Returns the task status enum values.
- POST /api/tasks
  - Requires projectId and links the task to the project.
  - 200 on success, 400 for missing projectId, 404 if projectId not found.
  - Example body:
    {
      "name": "Pour concrete",
      "projectId": "PROJECT_ID_HERE",
      "status": "NOT_STARTED"
    }
- PUT /api/tasks/{id}
  - Replaces the task fields and can move the task to a different projectId.
  - 200 on success, 400 for missing projectId, 404 if task or project not found.
- DELETE /api/tasks/{id}
  - 204 on success, 404 if not found.
  - Also removes the task ID from the owning project.

Seed Data (Development Only)
- DataInitializer clears all collections and inserts sample contractors, projects, and tasks.
- It runs on application startup and provides consistent data for frontend development.

Configuration
- HomeReno/src/main/resources/application.properties
  - spring.application.name=HomeReno
  - server.port=8080
  - spring.data.mongodb.uri=mongodb://localhost:27017/HomeReno
- Start the server with:
  - mvn spring-boot:run

Security
- An ApiKeyFilter exists but is fully commented out and not active.
- To enable API key checks, the filter must be uncommented and api.key configured.

Testing
- Only a basic Spring context load test exists.
- No integration or controller tests are currently included.

Related Docs
- Documentation/API_Testing_Guide.txt includes ready-to-use request bodies and quick testing steps.
