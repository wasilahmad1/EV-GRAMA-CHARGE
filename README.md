# EV-GRAMA CHARGE

## Project Overview

EV-GRAMA CHARGE is a native Android-based smart electric vehicle charging platform developed to address accessibility challenges in EV charging infrastructure through a community-driven approach. The application creates a shared charging ecosystem where EV travellers can discover nearby charging hosts, request charging access, and manage charging sessions efficiently.

The system is designed to bridge the gap between EV users and locally available charging resources by enabling individuals to share charging points within their community. The platform focuses on usability, accessibility, and scalable system design while providing an organized workflow for both travellers and charging hosts.

This project has been developed as a major internship project with emphasis on software architecture, modular implementation, structured navigation, maintainability, and real-world applicability.

---

## Problem Statement

Limited charging infrastructure and uneven availability of charging stations continue to be major barriers to electric vehicle adoption. EV-GRAMA CHARGE addresses this challenge by introducing a decentralized charging-sharing model where users can locate nearby charging hosts and access charging facilities based on availability and booking requests.

The platform aims to improve charging accessibility while promoting efficient utilization of existing charging resources.

---

## Objectives

- Provide a community-driven EV charging ecosystem
- Enable discovery of nearby charging hosts
- Facilitate charging slot requests and booking management
- Support role-based workflows for Travellers and Hosts
- Improve charging accessibility through local resource sharing
- Provide scalable architecture for future enhancements

---

## Core Features

### Authentication & User Access
- Mobile login workflow
- Role-based user onboarding
- Traveller and Host profile management
- Session handling and navigation flow

### Traveller Module
- Browse nearby charging hosts
- View charging host details
- Request charging sessions
- Track booking and charging status
- Charging range and charging-time estimation

### Host Module
- Host dashboard for managing requests
- Availability management system
- Booking approval workflow
- Profile and charging details management

### Booking & Session Workflow
- Booking request generation
- Request acceptance and status updates
- Session monitoring flow
- Post-session review mechanism

### Rating & Review System
- User feedback collection
- Review submission
- Rating management system

---

## Technical Implementation

The application is developed as a native Android project using Kotlin and follows structured Android development practices. The implementation emphasizes modularity, maintainability, and separation of responsibilities.

The project adopts a layered design approach that separates business logic, presentation logic, and data handling components for improved scalability and easier future expansion.

The application structure includes:

- Domain models
- Navigation components
- Screen-based UI modules
- Theme and design components
- Booking and session models
- User and host management modules

---

## Architecture

The system is designed using a layered architecture structure:

```text
Presentation Layer
        ↓
Business Logic Layer
        ↓
Domain Layer
        ↓
Data Layer
