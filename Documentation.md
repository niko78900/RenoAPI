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
    Num_of_workers = Assigned upon creation using the formula (budget / 2) / 1500 to give an estimation on how many workers are needed per project (it can be changed through the front end or in console).
    Geolocation = Location in coordinates which are going to be used for the map interface front end if possible.

    Task:
    ID = randomly generated ID
    projectId = Used as a way to assign task to a specific project
    Status = Status of set task. Can be either not started, working, finished or canceled. It can never have two states.

Behaviour:
    General: 
    Creation should be a simple intuitive procedure. User selects the option to create a project and is prompted by a new window. The user is then prompted to enter basic information such as the name of the project, the budget and the address. He will then be prompted to conitnue with the finer details and will be shown an estimation for the number of workers, a pre assigned geolcation (depending on the address and also displayed as a pin on the map) and the option to pick a contractor (Depending on the price), contractors outside of the price range will be greyed out (there should be a formula where there is recommended contractors that can be assigned based of the total budget). After inputting all of this information, the user will press next and in a new window will be asked to review the project details and finally click "submit", which then will be sent to the database, saved and the website will refresh to reflect the changes on the map.
    Task progression will have 4 states as stated in the task class and will also have a loading bar style progression bar with a % done based on the completed tasks out of the total tasks assigned. This loading bar can change if more tasks are added.

    Contractor:
    Assigned via ID to a projects object and having his salary deducted from the budget. The contractors expertise dictates the speed of the projects progress and overall quality of the finished product.
    Through the front end when creating a project, there should a window popup (with some sort of animation) showing the names of each contractor, their expertise and their salary. 

    Project:
    Can be created and modified through the front end. Administrators, creators of the project and contractors can change the status of the project.

    Tasks:
    Will be displayed to the user with their name, status and also the assigned project of it (you can navigate through that window to the actual project overview).


