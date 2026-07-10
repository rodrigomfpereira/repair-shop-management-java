# Desktop Management Application (Java Swing)

Desktop application built with Java Swing and backed by a MySQL database designed to manage repair shop workflows. This project was developed during the Computer Engineering degree, focusing on practicing the use of the Model-View-Controller (MVC) architecture and robust graphical user interface (GUI) development.

> **Note:** The application's interface is in Portuguese, as the project was originally developed for a Portuguese university course. Upon the first execution, if no configuration is found, the application will automatically launch a setup form to safely generate the required database access properties file.

This project demonstrates a classic desktop architecture featuring interactive Swing components, data validation, and persistent relational database storage.

## Features

- **MVC Architecture:** Clear separation of concerns between Model, View, and Controller layers.
- **Interactive GUI:** Built entirely with Java Swing components for intuitive repair shop management.
- **Smart Configuration:** Automated setup form that initializes the database configuration properties from scratch if missing.
- **Database Persistence:** Full integration with MySQL to manage clients, workers, repair orders and equipments.

## Project Structure

- **src/**: Java source files organized into `model`, `view`, and `controller` packages.
- **database/**: Contains the MySQL database creation script (`.sql`).

## Requirements

### Development & Execution
- Java Development Kit (JDK) 8 or higher
- Java Runtime Environment (JRE) to run the compiled application

### Database
- MySQL Server (running locally or remotely)

## Database Setup

Before running the application, you need to initialize the database schema:

1. Open your MySQL client or terminal.
2. Import and execute the script located in the database folder:
   ```sql
   SOURCE database/database_creation_script.sql;
   ```

## Execution

### Running via Release

1. Open the  
2. Download the compiled application.jar file.
3. Run the application via terminal (```java -jar application.jar```) or by double-clicking the file.


> **Note:** You could also compile and run the project from the source files, having only to clone the repository and then compiling / importing the `src` folder into your preferred IDE, and running the main class (```JanelaPrincipal```) inside the view package.