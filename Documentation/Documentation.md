HomeReno API Documentation
==========================
Base URL: http://localhost:8080

Overview
- Manage renovation projects end-to-end: create/update projects, assign contractors, track tasks, and mark completion.
- Stack: Spring Boot (Java) with MongoDB; Angular frontend; CORS allows http://localhost:4200.

Domain Model
- Project: id, name, budget, contractorId (nullable), address, latitude (nullable), longitude (nullable), progress (0-100), finished (boolean), number_of_workers, taskIds[], eta (months).
  - Progress/finished: progress is percentage; finished is an explicit flag with dedicated endpoints.
  - Workers: default on create is (budget / 2) / 1500; can be patched manually.
  - Coordinates: optional on create/patch; omit latitude/longitude to keep existing values.
- Contractor: id, fullName, price (per project), expertise (JUNIOR | APPRENTICE | SENIOR).
- Task: id, name, projectId, status (NOT_STARTED | WORKING | FINISHED | CANCELED).

Project API (key routes)
- GET /api/projects - list all projects.
- GET /api/projects/{id} - fetch full project.
- GET /api/projects/adr/{address} - fetch by address.
- GET /api/projects/name/{name} - fetch by name.
- GET /api/projects/contractor/{contractorId} - list projects for a contractor.
- GET /api/projects/timeline/{id} - returns progress, ETA, and task list summary.
- GET /api/projects/{id}/finished - returns only { "finished": true|false } for lightweight frontend checks.
- POST /api/projects - create project (see body templates in API_Testing_Guide.txt).
- PATCH /api/projects/{id}/contractor - set contractorId (and optional lat/long).
- PATCH /api/projects/{id}/contractor/remove - clear contractor.
- PATCH /api/projects/{id}/address | /name | /budget | /workers | /progress | /eta - update specific fields.
- PATCH /api/projects/{id}/finished - update only the finished flag.
- POST /api/projects/{projectId}/tasks - create a task and link it to the project.
- DELETE /api/projects/{projectId}/tasks/{taskId} - remove a linked task.

Contractor API
- GET /api/contractors
- GET /api/contractors/{id}
- GET /api/contractors/search/{name}
- GET /api/contractors/expertise/{level}
- GET /api/contractors/expertise - list enum values.
- POST /api/contractors - create.
- PUT /api/contractors/{id} - replace.
- DELETE /api/contractors/{id}

Task API
- GET /api/tasks
- GET /api/tasks/{id}
- GET /api/tasks/project/{projectId}
- GET /api/tasks/statuses - list enum values.
- POST /api/tasks - create.
- PUT /api/tasks/{id} - replace.
- DELETE /api/tasks/{id}

Response Shape: Project
Fields returned by ProjectResponse:
- id, name, address, latitude, longitude, budget, progress, finished, numberOfWorkers, contractorId, contractorName (nullable), taskIds[], eta.

Usage Notes
- Use the dedicated finished endpoints when the frontend only needs the completion flag.
- Latitude/longitude are nullable; omit them from PATCH bodies to avoid overwriting existing coordinates.
- See Documentation/API_Testing_Guide.txt for request body templates and quick testing steps.
