# ✂️ HYPE BARBERSHOP - Booking Management System

> **A premium Full-Stack booking platform designed for a local barbershop.**
> *Luxury Dark Design | Robust Architecture | GDPR & Consumer Rights Compliant*

![Java](https://img.shields.io/badge/Java-17%2B-ed8b00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-6db33f?style=for-the-badge&logo=springboot&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Frontend-005f0f?style=for-the-badge&logo=thymeleaf&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-336791?style=for-the-badge&logo=postgresql&logoColor=white)
![Security](https://img.shields.io/badge/Spring_Security-RBAC-red?style=for-the-badge&logo=springsecurity&logoColor=white)

---

## 📖 About The Project

**Hype Barbershop** is a complete digital solution developed as a **Solo Full-Stack Project**. It modernizes the appointment process by replacing manual scheduling with an automated, secure, and elegant web application.

The project emphasizes **User Experience (UX)** through a responsive "Luxury Dark" interface and ensures **Data Integrity** via complex backend validation algorithms that prevent schedule conflicts in real-time.

---

## ✨ Key Features

### 🟢 Public Client Portal
* **Smart Booking Flow:** Interactive form with visual date/time selection.
* **Real-Time Availability:** The system checks barber availability and service duration instantly to prevent overlapping.
* **Barber Profiles:** Individual pages displaying portfolios, specific services, and dynamic pricing.
* **Legal Compliance:** Dedicated pages for GDPR, Terms & Conditions, and Consumer Rights (ANPC) with mandatory consent checkboxes.
* **Email Notifications:** Automatic confirmation emails sent to clients upon booking.

### 🛡️ Unified Dashboard (Secured)
Powered by **Spring Security** with Role-Based Access Control (`ROLE_ADMIN` vs `ROLE_BARBER`).

#### 1. Barber Panel
* **Dynamic Scheduler (New! ✨):**
    * Barbers have full control over their working hours.
    * **Visual Interface:** Uses interactive Sliders to set start/end times per day.
    * **"Magic Copy":** One-click feature to replicate the Monday schedule across the entire week.
    * **Instant Updates:** Changes are immediately reflected in the public booking calendar.
* **Appointment Management:** View, Edit, Cancel, or Complete appointments.
* **My Calendar:** A `Flatpickr` integrated view to visualize the daily agenda.

#### 2. Admin Panel
* **Team Management:** Full overview of all staff members.
* **Activity Toggle:** Admin-exclusive feature to Enable/Disable barbers (e.g., for vacations), automatically hiding their public profiles.

---

## 🛠️ Architecture & Tech Stack

### Backend (Java Spring Boot)
* **Core:** Spring MVC, Spring Data JPA, Validation API.
* **Security:** Form-based authentication, BCrypt password hashing, CSRF protection.
* **Business Logic:**
    * `AppointmentService`: Custom `checkForOverlaps` algorithm determining conflicts (`StartA < EndB` && `EndA > StartB`).
    * `BarberService`: Handles the dynamic `BarberSchedule` logic.
* **API:** REST Endpoints (`/api/appointments/slots`) for asynchronous communication with the frontend.

### Frontend
* **Template Engine:** Thymeleaf (Server-side rendering).
* **Styling:** CSS3 Custom Properties (Variables) for the Gold/Dark theme, Flexbox & Grid for responsiveness.
* **JavaScript:**
    * `Fetch API`: For dynamic slot loading without page refreshes.
    * `Flatpickr` & `noUiSlider`: Premium UI libraries for dates and time ranges.

---

## 📸 Preview (Screenshots)

| Mobile Booking | Dashboard Calendar | Dynamic Schedule |
|:---:|:---:|:---:|
| <img src="src/main/resources/static/images/image.png" width="200" alt="Mobile Booking"> | *Interactive daily agenda* | *Visual slider for work hours* |

*(Note: Images are demonstrative based on project assets)*

---

## 🚀 Installation & Setup

### Prerequisites
* Java 17 or higher
* PostgreSQL
* Maven

### Steps
1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/evsebiu/HypebarbershopBookingWebsite.git](https://github.com/evsebiu/HypebarbershopBookingWebsite.git)
    cd HypebarbershopBookingWebsite
    ```

2.  **Database Configuration:**
    Create a PostgreSQL database and update `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/your_db_name
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    ```

3.  **Run the Application:**
    ```bash
    ./mvnw spring-boot:run
    ```
    Access the app at: `http://localhost:8080`

---

## 🔮 Roadmap
* [ ] SMS Notifications (via Twilio).
* [ ] Financial Reports & Analytics in Dashboard.

---

## ⚖️ License & Rights

This project is the intellectual property of **CVL CONSTRUCTII IASI SRL**.
Compliant with GDPR regulations and local Consumer Protection laws.

---
*Developed with ❤️ by [@evsebiu]*
