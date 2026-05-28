# ✂️ Hypebarbershop Booking Website

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen?style=for-the-badge&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Blue?style=for-the-badge&logo=postgresql)
![Hibernate](https://img.shields.io/badge/Hibernate-ORM-lightgrey?style=for-the-badge)
![JavaScript](https://img.shields.io/badge/JavaScript-ES6-yellow?style=for-the-badge&logo=javascript)
![Docker](https://img.shields.io/badge/Docker-Blue?style=for-the-badge&logo=docker)

**NextGen Barbershop Management System**

This full-stack application is a streamlined booking solution engineered with Java Spring Boot, designed to modernize barbershop operations. It features a dual-interface architecture: a public landing page for frictionless client reservations and a secured administrative dashboard for business management.

## ✨ Key Features & Benefits

*   **Dual-Interface Architecture:** Separate public client booking portal and secure administrative dashboard.
*   **Frictionless Client Reservations:** Easy-to-use interface for clients to browse services, check availability, and book appointments.
*   **Comprehensive Business Management:** Tools within the administrative dashboard for managing bookings, services, staff, and client data.
*   **Modern Technology Stack:** Built with a robust Java Spring Boot backend, ensuring reliability, performance, and scalability.
*   **Containerized Deployment:** Docker support simplifies setup, deployment, and portability across different environments.
*   **Progressive Web App (PWA) Capabilities:** Enhances user experience with features like offline support and installability (from `sw.js`).
*   **Stylish & Responsive UI:** Modern design with custom CSS (`styles.css`) ensures an intuitive and engaging user experience on any device.
*   **Secure & Scalable:** Leverages Spring Security for robust authentication and authorization, backed by a PostgreSQL database for efficient data management.

## 🛠️ Prerequisites & Dependencies

Before you begin, ensure you have the following installed on your system:

*   **Java Development Kit (JDK) 21 or higher:** Required to compile and run the Java Spring Boot application.
    *   [Download JDK 21](https://www.oracle.com/java/technologies/downloads/)
*   **Apache Maven 3.x:** Used for project build and dependency management.
    *   [Download Maven](https://maven.apache.org/download.cgi)
*   **Docker Desktop:** For containerized deployment and running the application in isolated environments.
    *   [Download Docker Desktop](https://www.docker.com/products/docker-desktop)
*   **PostgreSQL Database:** The application uses PostgreSQL for data persistence. You will need access to a running PostgreSQL instance.
    *   [Download PostgreSQL](https://www.postgresql.org/download/)
*   **Git:** For cloning the repository.
    *   [Download Git](https://git-scm.com/downloads)

## 🚀 Installation & Setup

Follow these steps to get your development environment up and running.

### 1. Clone the Repository

```bash
git clone https://github.com/evsebiu/HypebarbershopBookingWebsite.git
cd HypebarbershopBookingWebsite
```

### 2. Configure Database

The application uses PostgreSQL. You'll need to create a database and configure the connection details.

1.  **Create a PostgreSQL database and user:**
    ```sql
    CREATE DATABASE hypebarbershop_db;
    CREATE USER hype_user WITH PASSWORD 'your_secure_password';
    GRANT ALL PRIVILEGES ON DATABASE hypebarbershop_db TO hype_user;
    ```
    *(Adjust database name, username, and password as needed.)*

2.  **Update application properties:**
    Create a file named `src/main/resources/application.properties` (or `application.yml`) and add your database credentials:

    ```properties
    # Database Configuration
    spring.datasource.url=jdbc:postgresql://localhost:5432/hypebarbershop_db
    spring.datasource.username=hype_user
    spring.datasource.password=your_secure_password

    # JPA and Hibernate Configuration
    spring.jpa.hibernate.ddl-auto=update # Use 'create' for initial schema generation, then 'update'
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
    ```

### 3. Build the Application

Use Maven to compile the project and package it into an executable JAR file. This will also run any configured tests.

```bash
mvn clean package -DskipTests
```

### 4. Run the Application

You have two main options to run the application:

#### Option A: Run Directly (Development)

```bash
java -jar target/HypebarbershopBookingWebsite-*.jar # Adjust filename if necessary
```

#### Option B: Run with Docker (Recommended for Production/Local Dev)

1.  **Ensure Docker Desktop is running** on your machine.
2.  **Build the Docker image:**
    ```bash
    docker build -t hypebarbershop-app .
    ```
3.  **Run the Docker container:**
    When running the container, you'll need to provide the database connection details as environment variables.
    ```bash
    docker run -p 8080:8080 \
      -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/hypebarbershop_db" \
      -e SPRING_DATASOURCE_USERNAME="hype_user" \
      -e SPRING_DATASOURCE_PASSWORD="your_secure_password" \
      hypebarbershop-app
    ```
    *Note: `host.docker.internal` allows the Docker container to connect to a PostgreSQL database running directly on your host machine. If your PostgreSQL is also in a Docker container, you'll need to set up a Docker network.*

The application will be accessible in your web browser at `http://localhost:8080`.

## 🌐 Usage

Once the application is running, you can access its features through the following interfaces:

*   **Public Booking Website:**
    Navigate to `http://localhost:8080` in your web browser. This is the client-facing portal where users can explore available services, view barber schedules, and book appointments seamlessly.

*   **Administrative Dashboard:**
    Access the secured administrative dashboard, typically at `http://localhost:8080/admin` (the exact path may vary based on `SecurityConfig.java`). This interface is for barbershop owners and staff to:
    *   Manage and confirm appointments.
    *   Add, update, or remove services.
    *   Manage staff profiles and availability.
    *   View client information and booking history.
    *   Configure business settings.

    Initial administrative login credentials are often set up during the application's first run via `DatabaseSeeder.java` or can be manually configured.

## ⚙️ Configuration Options

The application's behavior can be customized through various configuration properties, primarily located in `src/main/resources/application.properties` (or `application.yml`).

*   **Database Connection:**
    *   `spring.datasource.url`: The JDBC URL for your PostgreSQL database.
    *   `spring.datasource.username`: The username for accessing the database.
    *   `spring.datasource.password`: The password for the database user.
    *   `spring.jpa.hibernate.ddl-auto`: Controls Hibernate's schema generation strategy (`none`, `update`, `create`, `create-drop`). Use `update` for general development.

*   **Server Port:**
    *   `server.port`: The port on which the Spring Boot application listens (default is `8080`).

*   **Security Configuration:**
    *   The `src/main/java/com/hype/barbershop/Config/SecurityConfig.java` class contains the core security configurations, including authentication providers, authorization rules, and custom login/logout behaviors.
    *   User accounts and roles (e.g., for the administrative dashboard) are managed through this configuration, often seeded initially via `DatabaseSeeder.java` or a dedicated user management system.

*   **Static Resource Caching (PWA):**
    *   The `src/main/resources/static/sw.js` file configures the service worker, managing which static assets (CSS, JS, images) are cached for offline access, significantly improving performance for repeat visitors and enabling PWA features.

*   **Spring Boot Profiles:**
    You can use Spring profiles (e.g., `application-dev.properties`, `application-prod.properties`) to manage environment-specific configurations easily.

## 🤝 Contributing

We welcome contributions to the Hypebarbershop Booking Website! If you're interested in improving the project, please follow these guidelines:

1.  **Fork** the repository to your GitHub account.
2.  **Clone** your forked repository to your local machine.
3.  **Create a new branch** for your feature or bug fix:
    ```bash
    git checkout -b feature/your-feature-name
    ```
    or
    ```bash
    git checkout -b bugfix/issue-description
    ```
4.  **Make your changes**, ensuring they adhere to the project's coding style and best practices.
5.  **Write clear, concise commit messages** describing your changes.
6.  **Push your branch** to your forked repository.
7.  **Open a Pull Request** to the `main` branch of the original `evsebiu/HypebarbershopBookingWebsite` repository.
    *   Provide a detailed description of your changes.
    *   Reference any related issues or feature requests.

Please ensure your code passes all existing tests, and consider adding new tests for any new features or bug fixes.

## 📄 License

This project currently does not have a specified license. For specific terms of use or to discuss contribution terms, please contact the repository owner, [@evsebiu](https://github.com/evsebiu).

**Note:** It is highly recommended to add a license to open-source projects to clarify legal terms for users and contributors.

## 🙏 Acknowledgments

*   Built with [Spring Boot](https://spring.io/projects/spring-boot) for a robust and scalable backend.
*   Utilizes [PostgreSQL](https://www.postgresql.org/) as the reliable data store.
*   Leverages [Hibernate ORM](https://hibernate.org/) for powerful object-relational mapping.
*   Containerized using [Docker](https://www.docker.com/) for easy deployment and portability.
*   Frontend design and interactivity developed with modern CSS and JavaScript principles.
*   Typography enhanced with fonts from [Google Fonts](https://fonts.google.com/).
*   Special thanks to the open-source community for the tools and libraries that made this project possible.
