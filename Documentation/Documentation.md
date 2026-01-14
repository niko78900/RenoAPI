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
- Fields: id, ownerId, name, budget, contractorId, address, latitude, longitude, progress, number_of_workers, finished, taskIds[], imageIds[], ETA.
- Relationship: projectId links to tasks; contractorId links to contractors.
- Coordinates are optional and can be set on create or patch.
- finished is an explicit boolean flag, separate from progress.
- ownerId is set by the server and is not accepted on requests.

Contractor (collection: contractors)
- Fields: id, fullName, price, expertise (JUNIOR | APPRENTICE | SENIOR).
  - fullName is required and non-blank.
  - price must be >= 0.
  - expertise is required.

Task (collection: tasks)
- Fields: id, projectId, name, status (NOT_STARTED | WORKING | FINISHED | CANCELED).

Image (collection: images)
- Fields: id, projectId, url, description, uploadedAt, uploadedBy.
- url stores the public path to the file (for local uploads, /uploads/<filename>).

User (collection: users)
- Fields: id, username, passwordHash (BCrypt), role (USER | ADMIN), enabled, createdAt.
- enabled=false means the account is pending admin approval.

Source of Truth for Tasks
- Project.taskIds is the authoritative list of tasks for a project.
- Tasks should always belong to a project; projectId is required for task create/update.
- Creating/updating/deleting tasks via /api/tasks updates Project.taskIds accordingly.
- Creating a task via /api/projects/{projectId}/tasks also writes the task and links it.
- taskIds provided on project create are ignored.
- Deleting a project deletes its tasks.

Source of Truth for Images
- Project.imageIds is the authoritative list of images for a project.
- Images should always belong to a project; projectId is required for image create/update.
- Creating/updating/deleting images via /api/images updates Project.imageIds accordingly.
- imageIds provided on project create are ignored.
- Deleting a project deletes its images.
- Uploads are stored on disk and served from /uploads/**.

Field Naming (Request vs Response)
- Project requests accept aliases:
  - contractor or contractorId
  - number_of_workers, numberOfWorkers, or workers
  - eta or ETA
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
- Progress is intended to be 0-100.
- ownerId is set from the authenticated user on project creation.

Validation
- budget must be >= 0.
- number_of_workers/workers must be >= 0.
- eta must be >= 0.
- progress must be between 0 and 100.
- latitude must be between -90 and 90.
- longitude must be between -180 and 180.
- images per project must be <= 50.
- uploaded files must decode as valid images (content type is not trusted).
- Invalid values return 400.

Error Handling and Status Codes
- 401: missing or invalid API key or JWT.
- 403: authenticated but not approved or lacks access to a project.
- 400: missing or invalid fields on task create/update or project patch endpoints.
- 400: invalid ranges/coordinates or task does not belong to project on DELETE /api/projects/{projectId}/tasks/{taskId}.
- 400: invalid contractor payloads (fullName/expertise/price).
- 400: missing projectId or url on image create/update.
- 400: missing projectId/file, invalid image, or limit exceeded on POST /api/images/upload.
- 404: resource not found on most controllers that catch exceptions.
- 404: contractor not found on project create/update.
- 404: project not found on DELETE /api/projects/{id}.
- 204: no content when a contractor has no projects.

API Endpoints
Auth API
- POST /api/auth/register
  - Body: { "username": "jane", "password": "secret" }
  - Creates a pending user (enabled=false) that requires admin approval.
- POST /api/auth/login
  - Body: { "username": "jane", "password": "secret" }
  - 200 returns { "token": "...", "tokenType": "Bearer", "username": "jane", "role": "USER" }.
  - 403 if the account is pending approval.

Admin User API (requires ADMIN)
- GET /api/admin/users/pending
  - Returns users waiting for approval.
- POST /api/admin/users/{id}/approve
  - Enables the user account.

Project API
- All project, task, and image operations enforce owner-or-admin access checks.
- GET /api/projects
  - Returns List<ProjectResponse>.
  - Regular users see only their own projects; admins see all projects.
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
  - 404 if project is not found.
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
- Accepts contractor or contractorId, number_of_workers or numberOfWorkers or workers, eta or ETA.
- taskIds and imageIds are ignored on create.
- 404 if contractorId is invalid.
- DELETE /api/projects/{id}
  - Deletes a project, its tasks, and its images.
  - 404 if project is not found.
- PATCH /api/projects/{id}/contractor
  - Body: { "contractorId": "NEW_CONTRACTOR_ID", "latitude": 41.9982, "longitude": 21.4254 }
  - contractor is also accepted in place of contractorId.
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
  - number_of_workers or numberOfWorkers are also accepted in place of workers.
- PATCH /api/projects/{id}/progress
  - Body: { "progress": 65, "latitude": 41.9982, "longitude": 21.4254 }
- PATCH /api/projects/{id}/eta
  - Body: { "eta": 3, "latitude": 41.9982, "longitude": 21.4254 }
  - ETA is also accepted in place of eta.
- PATCH /api/projects/{id}/finished
  - Body: { "finished": true }
- POST /api/projects/{projectId}/tasks
  - Creates a task and links it to the project.
- DELETE /api/projects/{projectId}/tasks/{taskId}
  - Removes a task from the project and deletes it.
  - 400 if the task does not belong to the project.

ProjectResponse Shape
- id, name, address, latitude, longitude, budget, progress, finished,
  numberOfWorkers, contractorId, contractorName, taskIds, imageIds, eta
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
  - 400 if fullName is blank, price is negative, or expertise is missing.
- PUT /api/contractors/{id}
  - Replaces fields on the existing contractor.
  - 400 if fullName is blank, price is negative, or expertise is missing.
- DELETE /api/contractors/{id}
  - Clears contractorId on any projects that referenced the contractor.

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

Image API
- GET /api/images
- GET /api/images/{id}
- GET /api/images/project/{projectId}
- POST /api/images
  - Requires projectId and url and links the image to the project.
  - 200 on success, 400 for missing projectId/url, 404 if projectId not found.
  - uploadedBy is set from the authenticated user.
  - Example body:
    {
      "projectId": "PROJECT_ID_HERE",
      "url": "https://example.com/image.jpg",
      "description": "Front elevation"
    }
- POST /api/images/upload (multipart/form-data)
  - Fields: projectId (required), file (required), description (optional).
  - 200 on success, 400 for missing projectId/file, invalid image, or limit exceeded, 404 if projectId not found.
- PUT /api/images/{id}
  - Replaces the image fields and can move the image to a different projectId.
  - 200 on success, 400 for missing projectId/url, 404 if image or project not found.
- DELETE /api/images/{id}
  - 204 on success, 404 if not found.
  - Also removes the image ID from the owning project.
- Max 50 images per project.

Seed Data (Development Only)
- DataInitializer clears projects, tasks, contractors, and users on startup.
- It runs on application startup and provides consistent data for frontend development.
- An admin user is created on every startup using seed.admin.username and seed.admin.password.

Configuration
- HomeReno/src/main/resources/application.properties
  - spring.application.name=HomeReno
  - server.port=8080
  - spring.data.mongodb.uri=mongodb://localhost:27017/HomeReno
  - security.jwt.secret=...
  - security.jwt.expiration-minutes=60
  - seed.admin.username=admin
  - seed.admin.password=admin123
  - storage.upload-dir=uploads
  - storage.url-prefix=/uploads/
- Start the server with:
  - mvn spring-boot:run

Security
- Most requests require both X-API-KEY and a JWT (Authorization: Bearer <token>).
- /api/auth/** requires the API key but does not require a JWT.
- The API key acts as an application-level access gate alongside user auth to simulate multi-tenant/client access in this demo.
- Default dev key: dev-local-key.
- Override via application.properties (api.key=YOUR_KEY) or an environment variable (API_KEY).
- If api.key is removed or blank, the API returns 500 on requests.
- Static file serving under /uploads/** is public.

Testing
- WebMvc tests cover API key enforcement and image endpoints.
- Service unit tests cover contractor and project validation.

Related Docs
- Documentation/API_Testing_Guide.txt includes ready-to-use request bodies and quick testing steps.
