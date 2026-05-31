# Pawsy ~ Pet Adoption System
## Business Requirements and MVP Features

### I. Business Domain Overview

Pawsy is designed to connect **animal shelters** with **individual adopters**. The system
facilitates browsing, requesting, adopting pets, scheduling appointments to meet with the pets and interacting with the shelter through a simple and user-friendly platform. The Monolithic backend is powered by Java Spring Boot, with a relational database to store all persistent data, while the frontend uses simple Thymeleaf views. The Microservices rewrite uses three Java Spring Boot Services, with a main gateway that still uses Thymeleaf views for the frontend.

### II. Business Requirements

| Nr. | Requirement | Description |
| --: | ----------- | ----------- |
|  1. | Shelter Registration | The system must allow shelters to register their details (name, contact information, and location) to manage adoptable pets. |
|  2. | Pet Listing Management | Shelters must be able to add, update, and remove pets from their listings. |
|  3. | View Available Pets | Users must be able to view all pets currently available for adoption, with filters such as species, age, shelter etc. |
|  4. | Adoption Request Submission | Users must be able to submit adoption requests for specific pets. |
|  5. | Adoption Request Management | Shelters must be able to view, approve, or reject adoption requests linked to their pets. |
|  6. | Appointment Scheduling | Users must be able to schedule an appointment to meet and find out more about the pets. |
|  7. | Review System | Users must be able to leave reviews for shelters they interacted with (e.g., rating and comment). |
|  8. | Pet Status Tracking | The system must automatically update the pet's status (Available, Adopted, Awaiting) based on adoption outcomes. |
|  9. | Shelter Appointments | Shelters must be able to view their upcoming appointments to prepare for their visitors. |
| 10. | Adopter Registration | Adopters must be able to register their details (first name, last name, address information and contact information) to adopt managed pets. |
| 11. | Admin Dashboard | The system must allow an administrator to overview the application's current status. |
| 12. | Admin Moderation | The administrator must be allowed to moderate user generated content such as user reviews. |

### III. Features
### Feature 1 ~ Pet Management

**Requirements:** 2., 3. & 8. \
**Description:** Shelters can create, edit, and remove pet listings, including details such as name, species, age, description, current status and sex. Users can browse and filter available pets. Users can view statistics about the total registered pets and total adopted pets. Shelters can also update a pet's information and status. \
**Actions:**
  * Add a new pet. (**POST** /api/shelters/{id}/pets)
  * Delete a registered pet. (**DELETE** /api/pets/{id})
  * Update a registered pet. (**PUT** /api/pets/{id})
  * Get (a page) pages of all pets with possible filtering. (**GET** /api/pets)
  * Get (a page) pages of all pets by a particular shelter. (**GET** /api/shelters/{id}/pets or /api/shelters/by-manager/{manager}/pets)
  * Get a particular pet. (**GET** /api/pets/{id})
  * Get pet statistics. (**GET** /api/pets/stats)
  * Filter by shelter id, species, name, sex.
  * Search by name & use paging and sorting.

### Feature 2 ~ Adoption Requests

**Requirements:** 4., 5., 8. & 10. \
**Description:** Users can send requests to adopt pets. Shelters can view and manage these requests, approving or rejecting them. \
**Actions:**
  * Submit an adoption request for a pet. (**POST** /api/adoptions/for-pet/{petId}/at/{shelterId}/for-user/{username})
  * Delete an adoption request. (**DELETE** /api/adoptions/{id})
  * Update an adoption request's status (Approved or Rejected). (**PUT** /api/adoptions/{id})
  * Approve an adoption request. (**POST** /api/adoptions/{id}/approve)
  * Reject an adoption request. (**POST** /api/adoptions/{id}/reject)
  * Get a particular adoption request. (**GET** /api/adoptions/{id})
  * Get a list of all adoption requests with possible filtering. (**GET** /api/adoptions)
  * Get a shelter's adoption requests. (**GET** /api/adoptions/by-shelter/{id})
  * Get an adopter's adoption requests. (**GET** /api/adoptions/by-user/{username})
  * Get adoption statistics. (**GET** /api/adoptions/stats)
  * Filter by adopter id, pet id and status.

### Feature 3 ~ Appointment Scheduling

**Requirements:** 6., 9. & 10. \
**Description:** Users can schedule appointments to meet and find out more about their favourite pets. A pet can only be booked for an appointment once per day. \
**Actions:**
  * Submit an appointment for a pet. (**POST** /api/appointments/for-pet/{petId}/at/{shelterId}/for-user/{username})
  * Delete an appointment. (**DELETE** /api/appointments/{id})
  * Update an appointment's date and status (Done or Cancelled). (**PUT** /api/appointments/{id})
  * Get a particular appointment. (**GET** /api/appointments/{id})
  * Get a list of all appointments with possible filtering. (**GET** /api/appointments)
  * Get an adopter's appointments. (**GET** /api/adopters/{id}/appointments)
  * Get booked dates for a pet. (**GET** /api/appointments/by-pet/{petId}/booked)
  * Filter by adopter id, pet id and date range.

### Feature 4 ~ Shelter Management

**Requirements:** 1., 2., 5. & 9. \
**Description:** Shelters can register, view, and manage their information as well as the pets under their care and the appointments they will organize. \
**Actions:**
  * Register a new shelter. (**POST** /api/shelters)
  * Delete a registered shelter. (**DELETE** /api/shelters/{id})
  * Update a registered shelter. (**PUT** /api/shelters/{id})
  * Get a list of all shelters with possible filtering. (**GET** /api/shelters)
  * Get a particular shelter. (**GET** /api/shelters/{id})
  * Get a particular shelter by manager. (**GET** /api/shelters/by-manager/{manager})
  * Get a shelter's reviews. (**GET** /api/shelters/{id}/reviews)
  * Get a list of a particular shelter's pets. (**GET** /api/shelters/{id}/pets)
  * Filter by location, paging & sorting.

### Feature 5 ~ Review System

**Requirements:** 1., 7. & 10. \
**Description:** Users can leave reviews for shelters they have interacted with, including ratings (between 1 and 5) and comments. \
**Actions:**
  * Submit a review for a shelter. (**POST** /api/reviews/for-user/{username}/at-shelter/{shelterId})
  * Delete a review. (**DELETE** /api/reviews/{id})
  * Update a review's rating or comment. (**PUT** /api/reviews/{id})
  * Get a particular review. (**GET** /api/reviews/{id})
  * Get a list of all reviews with possible filtering. (**GET** /api/reviews)
  * Get recent reviews. (**GET** /api/reviews/recent)
  * Filter by adopter id, shelter id, rating range.
  * Sort them by rating using `sort`.

### Feature 6 ~ Adopter Management

**Requirements:** 3., 4., 6. & 10. \
**Description:** Users can register, view, and manage their information as well as interacting with the available shelters and their pets through requests, appointments and reviews. \
**Actions:**
  * Add a new adopter. (**POST** /api/users)
  * Delete a registered adopter. (**DELETE** /api/users/{id})
  * Update a registered adopter. (**PUT** /api/users/{id})
  * Get a list of all adopters with possible filtering. (**GET** /api/users)
  * Get a particular adopter. (**GET** /api/users/{id})
  * Filter by first name and last name.

### Feature 7 ~ Admin Moderation

**Requirements:** 11. & 12. \
**Description:** Pawsy's administrator can view a summary of active users, pets & shelters, while being able to moderate users by deleting their reviews if they have been deemed inappropriate for our standards. \
**Actions:**
  * Get the overview statistics. (**GET** /api/admin/stats)
  * Delete a user's review. (**DELETE** /api/reviews/{id})
  * Get & moderate recent posted reviews. (**GET** /api/reviews/recent)

### IV. Entities
There are 7 entities:

* User
* Role
* Shelter
* **Pet**
* Appointment
* Review
* Adoption (*Request*)

with the following relationships:
![ERD](docs/diagrams/erd.png)

* 1 (*explicit*) Many to Many
* 1 One to One
* 7 One to Many / Many to One

### V. Architecture
### 1. Monolithic Pawsy

Monolithic Pawsy (*mono*) is a Spring Boot MVC web application for managing pets, shelters and adoption requests & multiple interactions with the pets.

### Overview

The application follows a layered architecture with domain-based modularization to allow an easier migration to micorservices:

Client (Browser) &mdash; Thymeleaf Views &mdash; Controllers (Spring MVC) &mdash; Services (Business Logic) &mdash; Repositories (Spring Data JPA) &mdash; Database (MySQL or H2)

### 2. Microservices Pawsy

Microservices Pawsy (*micro*) is a Spring Boot based web application separated into three main microservices: User Service (includes user features & admin features), Pet Service (includes pet features, shelter features & review features) and finally Adoption Service (includes adoption & appointment features). Besides these three microservices, the main application or gateway is another Java Spring Boot application that communicates with the services internally using REST APIs and presents to the user using simple Thymeleaf views in a MVC fashion.

### Overview

The main gateway still uses a MVC architecture relying on Controllers to handle requests and on Views to present the application to the user, while the Service & under layers have been moved into microservices.

Client (Browser) &mdash; Thymeleaf Views &mdash; Controllers (Spring MVC) &mdash; (REST) &mdash; Services (Distributed microservices) \
Microservice &mdash; Repository (Spring Data JPA) &mdash; Database (MySQL)

### VI. Setup
### IDE (Build System)

Preferably IntelliJ with Maven & JDK 21.

### Additional Tools

Docker (Desktop or CLI + Compose). *mono*
Kubernetes (Docker Desktop integrated or minikube). *micro*

### Instructions

* Choose a project (*mono* or *micro*).
* Create a dot env following the example ones or just the needed variables for **arch/** and for every service.
* Start every needed container:
  - For *mono*:
```bash
docker compose -f arch/docker/docker-compose.mono.yml up -d
```
* Open the chosen project in an IDE or just build with Maven (clean + package).
* Run the application from the IDE or directly using a Java Runtime.
  - For *micro*
* Create a *config-map* and *secrets* for holding .env in *k8s/config*.
* For local deployment, go into **micro** then:
* Go into each application's directory (*pawsy*, *pet-service*, *user-service* and *adoption-service*):
```bash
docker build -t pawsy .
```
 - (Change *pawsy* with each service name)
* Go back into **micro**:
```bash
kubectl apply -f k8s/config
kubectl apply -f k8s/mysql
kubectl apply -f k8s/deployments
kubectl apply -f k8s/services
```
 - Connect to the created mysql pod.
 - Create one database per service as configured in the config-map.
* The app should be available locally now.
