# 🏢 UrbanLife – Smart Gated Community Management System

UrbanLife is a modern **Full Stack Community Management System** designed to simplify the management of gated communities, apartments, and residential societies.

The application enables administrators, residents, security personnel, and staff to efficiently manage daily community operations through a secure, role-based platform.

---

# 🚀 Features

## 👤 User & Role Management

- User Registration
- Secure Login
- Role-based Access Control
- Profile Management
- Active / Inactive Users

Roles Supported:

- SUPER_ADMIN
- ADMIN
- RESIDENT
- SECURITY
- STAFF

---

## 🏘 Community Management

- Create Communities
- Manage Blocks
- Manage Flats
- Resident Assignment

---

## 👨‍👩‍👧 Resident Management

- Add Residents
- Update Resident Details
- Search Residents
- View Resident Information

---

## 🚗 Vehicle Management

- Register Vehicles
- View Resident Vehicles
- Update Vehicle Details
- Vehicle Status Management

---

## 👥 Visitor Management

- Visitor Registration
- Visitor Approval
- Check-In / Check-Out
- Visitor History

---

## 🚘 Parking Management

- Parking Slot Management
- Vehicle Parking Allocation
- Available Slot Tracking

---

## 🛠 Complaint Management

- Raise Complaints
- Complaint Tracking
- Assign Complaints
- Status Updates

---

## 💳 Maintenance Management

- Generate Maintenance Bills
- Record Payments
- Payment History

---

## 🏢 Amenity Management

- Create Amenities
- Book Amenities
- Approve / Reject Bookings

---

## 📢 Notice Management

- Publish Notices
- Notice Read Tracking

---

## 🎉 Event Management

- Community Events
- Event Registration

---

## 🗳 Poll & Voting

- Create Polls
- Resident Voting
- Poll Results

---

## 📦 Parcel Management

- Parcel Entry
- Parcel Collection Tracking

---

## 🚨 Emergency Management

- Emergency Reporting
- Emergency Updates
- Emergency Status Tracking

---

## 🔔 Notification System

- Community Notifications
- Complaint Notifications
- Visitor Notifications
- Emergency Notifications

---

## 📊 Dashboard

Dashboard includes:

- Total Communities
- Blocks
- Flats
- Residents
- Visitors
- Complaints
- Maintenance Collection
- Parking Statistics
- Emergency Reports

---

# 🛠 Tech Stack

## Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- Maven

## Frontend

- React
- Bootstrap / CSS
- Axios

## Database

- MySQL

## Authentication

- Spring Security
- HTTP Basic Authentication
- BCrypt Password Encryption

## API

- RESTful APIs

## Deployment

- AWS EC2
- AWS RDS
- Vercel

---

# 📂 Project Structure

```
UrbanLife
│
├── Backend (Spring Boot)
│   ├── Controller
│   ├── Service
│   ├── Repository
│   ├── Entity
│   ├── DTO
│   ├── Security
│   ├── Config
│   └── Exception
│
└── Frontend (React)
    ├── Components
    ├── Pages
    ├── Services
    ├── Assets
    └── Routing
```

---

# 🔐 Security

The application uses Spring Security with:

- HTTP Basic Authentication
- BCrypt Password Encryption
- Role-Based Authorization
- User Status Validation
- Method-Level Security

---

# 📡 REST APIs

Major API Modules:

- Role APIs
- User APIs
- Community APIs
- Block APIs
- Flat APIs
- Resident APIs
- Vehicle APIs
- Visitor APIs
- Complaint APIs
- Parking APIs
- Maintenance APIs
- Amenity APIs
- Event APIs
- Notice APIs
- Poll APIs
- Parcel APIs
- Emergency APIs
- Notification APIs
- Dashboard APIs

---

# 🚀 Getting Started

## Clone Repository

```bash
git clone https://github.com/yourusername/urbanlife.git
```

## Backend

```bash
cd backend

mvn clean install

mvn spring-boot:run
```

## Frontend

```bash
cd frontend

npm install

npm start
```

---

# 🗄 Database Configuration

Update the following properties in:

```
application.properties
```

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/urbanlife

spring.datasource.username=your_username

spring.datasource.password=your_password
```

---

# Future Enhancements

- JWT Authentication
- Email Notifications
- SMS Notifications
- Payment Gateway Integration
- Cloud Storage
- Docker
- Kubernetes
- CI/CD Pipeline
- Microservices Architecture

---

# Author

**Akash Muddapu**

Java Full Stack Developer

- Java
- Spring Boot
- Spring Security
- Hibernate
- REST APIs
- React
- MySQL
- AWS

---

⭐ If you found this project helpful, consider giving it a Star.
