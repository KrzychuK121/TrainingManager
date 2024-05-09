# TrainingManager

## Quick description
It's a web application using Spring Boot v.3.2.2 technology with MVC, REST API, Jpa Repository (Hibernate) etc. The application gives CRUD operations on exercises and trainings, using views (Thymeleaf templates) or with REST API (no authentication). There is also "train" option that is displaying only the list of exercises for now. But in the future there will be option to control the training process.

## Main technologies
* Spring Boot
* Security (accounts)
* Jpa Repository (Hibernate)
* Thymeleaf templates
* Flyway migrations (SQL and Java  files)

## How to use
This project was created using **Spring Boot v.3.2.2**. Please make sure you have everything what is necessary to run the project.

## How to solve problems
* **Migrations**: It might happen that new commits might change migration order (seeding java migrations invoked after table structure changes). In that case it is recommended to remove all migrations from *"flyway_schema_history"* and drop all tables except *"flyway_schema_history"*. Eventually, you can try to remove few migrations and data/tables connected with them.  

### Step by step:
1. Clone the repository
2. Open pom.xml file as project (recommended using IntelliJ 2023)
3. Run the application and test it using default accounts

## Default accounts
There are few default accounts. These accounts allows you to test full potential of the application.

### Administrator account
**login:** Administrator<br>
**password:** Adm1nP@ss<br>

### User accounts
**User**
**login:** Uzytkownik<br>
**password:** UserP@ssw0rd<br>
