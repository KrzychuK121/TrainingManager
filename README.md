# TrainingManager

## Quick description
It's a web application using Spring Boot v.3.2.2 technology with MVC, REST API, Jpa Repository (Hibernate) etc. The application gives CRUD operations on exercises and trainings, using views (Thymeleaf templates) or with REST API (no authentication). There is also "train" option that is displaying only the list of exercises for now. But in the future there will be option to control the training process.

## Main technologies
* **Backend:**
  * **Spring Boot**
  * **Spring Security - Tokens** (accounts)
  
* **Database:**
  * **PostgreSQL** (from docker compose)
  * **Spring Data - Jpa Repository** (Hibernate)
  * **Flyway migrations** (SQL and Java  files)

* **Frontend:**
  * **Thymeleaf templates**
  * **React**


## How to use
This project was created using **Spring Boot v.3.2.2**. Please make sure you have everything what is necessary to run the project.

### Setup database
You need `compose.yaml` file in project directory that contains Docker PostgreSQL server setup. It's all you need, because spring will look for this file and when he finds it, the container will be created and spring will connect to database automatically. But it will does not work if you don't have `Docker` installed on your machine.
 
Your database name is defined inside `.env` file in project directory, default is `POSTGRES_DB=local-database`. If you want other database name, you can change it before loading it. If you created container with other name of database and you want to run application with different database, then you have to create it manually first and change name of database in `.env` file.

### Step by step:
1. Clone the repository
2. Make sure you have `Docker` installed on your computer. If not, download it
3. Open pom.xml file as project (recommended using IntelliJ 2023)
4. Run the application and test it using default accounts

## How to solve migrations problems
* **Migrations**: It might happen that new commits might change migration order (seeding java migrations invoked after table structure changes). In that case it is recommended to remove all migrations from *"flyway_schema_history"* and drop all tables except *"flyway_schema_history"*. Eventually, you can try to remove few migrations and data/tables connected with them.

## Default accounts
There are few default accounts. These accounts allows you to test full potential of the application.

### Administrator account
**login:** Administrator<br>
**password:** Adm1nP@ss<br>

### User accounts
**User**
**login:** Uzytkownik<br>
**password:** UserP@ssw0rd<br>

## Plan for application:
* ~~Adding API + GUI for CRUD operations of:~~
    - ~~exercises~~
    - ~~trainings~~
    - ~~training schedule~~
    - ~~training routine~~
    - ~~training plan~~
    - ~~new user account (register action)~~
* Displaying training plans:
  - active (with done/upcomming info)
  - ~~all~~
  - history:
    - weekly
    - monthly
* Implementing filtering for:
  - trainings by:
    - owner (user's owned)
  - exercises by:
    - owner (user's owned)
  - training plans by:
    - owner (user's owned)
    - difficulty
    - practising body part
* ~~Moving from H2 Database to PostgreSQL database~~:
  - ~~changing migrations dialect for PostgreSQL~~
  - ~~setting up database environment (docker)~~
* BMI and BMR calculators
* Mobile version of the app with functionality:
  - current week infomration with "do todays training" option
  - notification about upcoming workout while app is working in the background
