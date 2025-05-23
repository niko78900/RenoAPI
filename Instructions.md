Home Renovation Project API 
Description: Manage renovation projects, tasks, contractors, budgets, and progress photos. 

Authentication: API key in header.

CRUD Method & Endpoint                                 Description

Create POST /projects                   Create a new renovation project with budget.
Read GET /projects/{id}/tasks           List all tasks and their statuses. 

Note: Students must define 10+ endpoints 
(e.g., assign contractor, upload photo, update budget, mark milestone, export timeline). 

Entities:
- Project entity (ID, Name, Assigned contractor, Budget(Double), Progress % (Double), Address(Location), photo gallery (List), Milestones (List), Export Function, Tasks(List), 
number of workers, Estimation for finishing)

- Tasks entity (Progress % (Double), Assigned contractor(String),ID, Name, Status (Done, Working, Not Started))

Create new project ( POST ): Assign budget, Assign contractor, Assign # of workers, Assign address
Things that will generate(Tasks List, Milestones, Estimation for finishing, Photo Gallery)

Update said project ( PUT ): add a new Task or give it a list of tasks

Read Project ( GET ): Project ID, Project Name, Assigned contractor, Assigned budget, Progress %, Estimation for finishing. DONE!

Upload photo ( POST ): Upload a photo to a specific project

Mark milestone ( PATCH ): Mark a milestone that was crossed

Export timeline ( GET ): Export the timeline i.e Task list, Estimated time of finishing, dates of each task and each milestone that has been finished/hit

Update budget ( PATCH ): Change the budget on a specific project

Update contractor ( PATCH ): Change the contractor assigned to the project

Delete Project ( DELETE ): Delete project

Change address ( PATCH ): Change the address

Audit Log ( GET ): Audit changes made by admin. Finishing of tasks etc.