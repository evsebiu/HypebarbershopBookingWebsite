# HypebarbershopBookingWebsite
Temp website link : https://hypebarbershopbookingwebsite.onrender.com

> ✂️ NextGen Barbershop Management System
Overview
A robust, full-stack booking platform engineered with Java Spring Boot, designed to streamline the scheduling process for modern barbershops. This application bridges the gap between client convenience and business efficiency, featuring a public-facing landing page for seamless reservations and a secured administrative dashboard for real-time business management.

The system supports multi-barber scheduling with distinct service catalogs, pricing tiers, and service durations, all governed by a custom-built conflict-resolution algorithm to ensure optimal time management.

Key Features
1. Client-Facing Portal (Public)
Dynamic Landing Page: A responsive interface showcasing the barbershop's identity, location, and operational hours.

Multi-Barber Selection: Clients can browse profiles for two distinct barbers, each with their own unique portfolio of services, prices, and time durations.

Real-Time Availability Engine: The booking interface queries the database in real-time, allowing clients to select specific time slots. The system instantly verifies barber availability, preventing double-booking.

Guest Checkout Flow: Frictionless booking experience requiring only Name, Email, and Phone Number—no client account registration needed.

2. Intelligent Scheduling Logic (Backend Core)
Advanced Overlap Detection: Implemented a custom algorithmic logic that validates potential appointments against existing records.

Logic: (NewStart < ExistingEnd) && (ExistingStart < NewEnd)

Dynamic Duration Calculation: Automatically calculates the appointment end-time based on the specific service selected (e.g., a 30-minute fade vs. a 60-minute full service), ensuring the schedule remains accurate to the minute.

3. Administrative Dashboard (Secured)
Role-Based Access Control (RBAC): Secured via Spring Security, ensuring only authorized staff can access sensitive business data.

Service & Price Management: Barbers have full CRUD (Create, Read, Update, Delete) capabilities to modify their service offerings and pricing directly through the UI, without needing code changes.

Appointment Oversight: A centralized view for barbers to manage their daily agenda.

4. Automated Notification Service
Email Integration: Built-in email triggers that handle the communication lifecycle:

Client: Instant booking confirmation with appointment details.

Barber: Immediate "New Appointment" alerts to keep staff synced with their schedule.

🛠 Technical Stack
Backend: Java SDK 25, Spring Boot 3.x

Security: Spring Security (Authentication & Authorization)

Database: PostgreSQL / MySQL (Relational Data Persistence)

ORM: Hibernate / Spring Data JPA

Notifications: JavaMailSender

Architecture: RESTful API Design, MVC Pattern

💡 Why This Project?
This application solves the classic "Double Booking Problem" using a strict server-side validation layer. Unlike basic CRUD apps, it handles temporal logic, managing time slots dynamically based on variable service durations. It demonstrates a complete software lifecycle from public access to secured administrative control.

![License](https://img.shields.io/badge/license-MIT-green) ![Version](https://img.shields.io/badge/version-1.0.0-blue) ![Language](https://img.shields.io/badge/language-Java-yellow) ![Framework](https://img.shields.io/badge/framework-Springboot-orange) ![GitHub](https://img.shields.io/badge/GitHub-evsebiu/HypebarbershopBookingWebsite-black?logo=github) ![Build Status](https://img.shields.io/github/actions/workflow/status/evsebiu/HypebarbershopBookingWebsite/ci.yml?branch=main)

## ℹ️ Project Information

- **👤 Author:** evsebiu
- **📦 Version:** 1.0.0
- **📄 License:** MIT
- **📂 Repository:** [https://github.com/evsebiu/HypebarbershopBookingWebsite](https://github.com/evsebiu/HypebarbershopBookingWebsite)
- **🏷️ Keywords:** barbershop, booking, microservice, bookingwebsite, admindashboard

