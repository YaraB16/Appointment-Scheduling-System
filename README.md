
<!-- PROJECT HEADER -->

<br />
<div align="center">

<h3 align="center">📅 Appointment Scheduling System</h3>

<p align="center">
  A clean, modular, and rule-based system for managing appointments with validation and notification support.
  <br />
</p>

![Java](https://img.shields.io/badge/Java-21-blue)
![Maven](https://img.shields.io/badge/Maven-Build-red)
![Tests](https://img.shields.io/badge/Tests-JUnit5-green)
![Architecture](https://img.shields.io/badge/Architecture-Layered-orange)

</div>

---

## 📚 Table of Contents

* [About The Project](#about-the-project)
* [Key Features](#-key-features)
* [What Makes This Project Unique](#-what-makes-this-project-unique)
* [Architecture](#architecture)
* [Built With](#built-with)
* [Getting Started](#getting-started)
* [Usage](#usage)
* [Data Structure](#data-structure)
* [Testing & Quality](#testing--quality)
* [Future Improvements](#future-improvements)
* [Authors](#-authors)

---

## 📖 About The Project

The **Appointment Scheduling System** is a Java-based application designed to efficiently manage appointments while enforcing strict business rules such as time validation and conflict prevention.

The system is built using **clean layered architecture**, making it highly maintainable, testable, and scalable.

---

## ✨ Key Features

* Create and manage appointments
* Prevent overlapping appointments (time conflict validation)
* Business rule enforcement (validation layer)
* Notification system integration
* Modular and clean architecture
* Unit testing support
* Code coverage tracking

---

## 🏆 What Makes This Project Unique

* 🔹 **Separation of Concerns** → each layer has a clear responsibility
* 🔹 **Scalable Design** → easy to extend (API, database, UI)
* 🔹 **Testable Architecture** → services can be tested independently
* 🔹 **Clean Code Practices** → readable and maintainable structure
* 🔹 **Real-world Logic** → handles scheduling constraints like real systems

---

## 🏛️ Architecture

The system follows a **Layered Architecture**:

| Layer      | Responsibility                       |
| ---------- | ------------------------------------ |
| Domain     | Core business entities (Appointment) |
| Service    | Business logic & validation          |
| Repository | Data handling and storage            |
| Value      | Structured immutable data            |
| Util       | Helper and utility functions         |

### 🔹 Design Principles

* Low coupling
* High cohesion
* Modularity
* Maintainability

---

## ⚙️ Built With

* Java (JDK 21)
* Maven (Build & Dependency Management)
* JUnit 5 (Unit Testing)
* Mockito (Mocking for Tests)
* JaCoCo (Code Coverage)
* Jakarta Mail (Notification System Support)

---

## 🚀 Getting Started

### 🔧 Installation

```bash
git clone <your-repo-link>
cd Appointment-Scheduling-System
mvn clean install
```

### ▶️ Run the Project

```bash
mvn exec:java
```

---

## 🧩 Usage

### Example Workflow:

1. Create an appointment request
2. System checks:

   * Time availability
   * Business constraints
3. If valid → appointment is saved
4. Notification is triggered
5. Confirmation is returned

---

## 📊 Data Structure

### 📁 Internal Representation

* Data is managed using **Java objects (no external database yet)**
### 🔹 Example Variables

### Core Entity: `Appointment`

| Field | Type | Description |
|---|---|---|
| `id` | `String (UUID)` | Auto-generated unique identifier |
| `slot` | `TimeSlot` | Immutable start & end `LocalDateTime` |
| `type` | `AppointmentType` | Category/type of the appointment |
| `status` | `AppointmentStatus` | `AVAILABLE` / `CONFIRMED` / `CANCELLED` |
| `participants` | `List<User>` | Confirmed attendees (defensive copy) |
---

## 🧪 Testing & Quality

Run tests:

```bash
mvn test
```

Generate coverage report:

```bash
mvn jacoco:report
```

### ✔ Quality Highlights

* Unit testing using **JUnit 5**
* Mocking with **Mockito**
* Code coverage using **JaCoCo**
* Focus on validating edge cases and business rules

---

## 📬 Authors

This project was developed as part of the Software Engineering course by:

* Reem Ghannam
* Yara Barghothey
* Nour Marei
<table>
  <tr>
    <td align="center">
      <a href="https://github.com/nourame">
        <img src="https://github.com/nourame.png" width="100" style="border-radius:50%;"/><br/>
        <sub><b>نور حسن محمد زياد مرعي</b></sub><br/>
        <sub><a href="https://github.com/nourame">@nourame</a></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/reemgha">
        <img src="https://github.com/reemgha.png" width="100" style="border-radius:50%;"/><br/>
        <sub><b>ريم نجيب توفيق غنام</b></sub><br/>
        <sub><a href="https://github.com/reemgha">@reemgha</a></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/YaraBarghothey">
        <img src="https://github.com/YaraBarghothey.png" width="100" style="border-radius:50%;"/><br/>
        <sub><b>يارا سائد حاتم برغوث</b></sub><br/>
        <sub><a href="https://github.com/YaraBarghothey">@YaraBarghothey</a></sub>
      </a>
    </td>
  </tr>
</table>
