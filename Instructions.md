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

Create new project: Assign budget, Assign contractor, Assign # of workers, Assign address
Things that will generate(Tasks List, Milestones, Estimation for finishing, Photo Gallery)

Update said project: Give it a list of tasks

Read Project: Project ID, Project Name, Assigned contractor, Assigned budget, Progress %, Estimation for finishing. 


