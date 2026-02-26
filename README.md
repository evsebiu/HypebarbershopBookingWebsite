Markdown

# ✂️ Hypebarbershop Booking Website

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen?style=for-the-badge&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Blue?style=for-the-badge&logo=postgresql)
![Hibernate](https://img.shields.io/badge/Hibernate-ORM-lightgrey?style=for-the-badge)

**Hypebarbershop** is a dynamic booking system and a comprehensive management platform designed to fully digitize a barbershop's daily operations. The project provides a multi-tenant environment at the dashboard level, allowing each barber to have complete control over their own schedule, clients, and the services they offer.

---

## ✨ Key Features

* **Dynamic Booking System (Smart Check-Overlap):** Advanced algorithm that prevents overlapping appointments in real-time by calculating availability based on the duration of each service and the specific working hours of the barber.
* **Personalized Dashboard & Data Isolation:** Each authenticated barber has access to their own workspace where they exclusively manage their appointments and active services.
* **Flexible Schedule Management:** Barbers can define their working days, time slots, and add exceptions (e.g., days off or holidays).
* **Full CRUD Control over Services:** Dedicated APIs for creating, updating, deleting (soft-delete), and viewing offered services (e.g., haircut, beard trim), with strict ownership validation.
* **Security & Role-Based Access Control (RBAC):** Authentication system based on Spring Security, with a clear distinction between `ROLE_ADMIN` (overview of all barbers) and `ROLE_BARBER` roles.
* **Reactive Calendar:** Real-time communication for confirming, canceling, or moving (drag & drop logic) appointments directly from the user interface.

---

## 🛠️ Tech Stack

* **Language:** Java 21
* **Core Framework:** Spring Boot (Web, Security, Validation)
* **Data Persistence:** Spring Data JPA & Hibernate
* **Database:** PostgreSQL
* **Architecture:** MVC (Model-View-Controller) and RESTful APIs.

---

## ⚙️ Installation & Setup

Follow these steps to run the project locally:

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/username/hypebarbershop.git](https://github.com/username/hypebarbershop.git)
   cd hypebarbershop
Configure the database:
Create a PostgreSQL database named barbershop_db.
Update the src/main/resources/application.properties file with your credentials:

Properties

spring.datasource.url=jdbc:postgresql://localhost:5432/barbershop_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
Build and run the application:

Bash

mvn clean install
mvn spring-boot:run
The application will start on http://localhost:8080.

📂 Project Structure
The application follows a clean, layered architecture:

Controller/: REST endpoints (ServiceDetailsControllerAPI.java) and MVC controllers (DashboardController.java, AppointmentWebController.java) handling the web interface.

Service/: Complex business logic layer (e.g., calculating available time slots, validating overlaps).

Repository/: Spring Data JPA interfaces for efficient database communication.

Model/: Contains Entities (Barber.java, Appointment.java), DTOs, and Mappers.

Exceptions/: Custom exception handlers for business logic errors (e.g., BarbershopDuplicateResource, BarbershopException).

💻 Usage Examples
Fetching available slots for an appointment:
The system automatically calculates free time slots based on the service duration and existing appointments.

HTTP

GET /api/appointments/available-slots?barberId=1&serviceId=2&date=2026-03-01
Adding a new service (Only for the authenticated barber):
Security logic prevents a barber from deleting or modifying another colleague's services.

HTTP

POST /api/services
Content-Type: application/json

{
  "serviceName": "Skin Fade & Beard Trim",
  "price": 80.0,
  "duration": 45
}
🚀 Roadmap (Future Improvements)
[ ] Email/SMS notification integration for clients (appointment confirmation/cancellation).

[ ] Online payment processing via Stripe.

[ ] Containerization using Docker & Docker Compose for a faster setup.

[ ] Review and rating system for barbers.

🤝 Contributing
Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are greatly appreciated.

Fork the Project

Create your Feature Branch (git checkout -b feature/AmazingFeature)

Commit your Changes (git commit -m 'Add some AmazingFeature')

Push to the Branch (git push origin feature/AmazingFeature)

Open a Pull Request
