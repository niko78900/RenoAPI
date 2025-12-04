Renovation Project API 
Description: Platform for management of projects. Assign contractors, get amount of workers calculated and ability to upload progress and finished photos of said project. Creation of projects with their details, choosing of contractors (with a vast array of different contractors with different skills and prices). Backend API in java, Database used is MongoDB, Frontend is angular.

Entities:
Contractor:
ID = randomly generated ID
Fullname = Name
Price per Project = price
Expertise = One of 3 levels for a contractor: junior, apprentice or senior.


Project:
ID = randomly generated ID
Name = Name of project
Budget = Budget for the project. It gets calculated by trying to subtract the price of the contractor FIRST (if not possible return an error message) and then assigned to the project.
Address = Location of project
Progress = Progress in days left
Num_of_workers = Assigned upon creation using the formula (budget / 2) / 1500 to give an estimation on how many workers are needed per project (it can be changed through the front end).
Geolocation = Location in coordinates which are going to be used for the map interface front end if possible.

Task:
ID = randomly generated ID
projectId = Used as a way to assign task to a specific project
Status = Status of set task. Can be either not started, working, finished or canceled. It can never have two states.
