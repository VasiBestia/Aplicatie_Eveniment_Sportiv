# 🏆 Sports Event Management Application

A complex web platform dedicated to organizing and managing sports tournaments, developed using **Java Spring Boot** and **Thymeleaf**. The application allows for complete administration of matches, teams, and live scores, featuring a secure authentication system via social networks.

---

## 📸 Visual Overview

### 1. Authentication System (OAuth2)
Users can quickly log in using **Google** or **Facebook** accounts, or via the standard method (username/password). The buttons are custom-styled to integrate seamlessly with the application theme.
![Login Page](img/loginscreenshot.png)

### 2. Administrator Dashboard
The main panel offers an overview of the competition, allowing quick scheduling of new matches and visualization of team statuses.
![Admin Dashboard](img/dashboardscreenshot.png)

### 3. Match Management & Team Elimination
Key functionality: The Admin can edit scores and, with a single click, **eliminate the losing team**. Eliminated teams are automatically excluded from selection lists for future matches to prevent errors.
![Match Editing and Elimination](img/paginademeciuriss.png)

---

## 🚀 Key Features

### 🔐 Security & Access
* **Social Login:** Full integration with **Google OAuth2** and **Facebook Login** APIs.
* **Data Security:** Standard user passwords are encrypted using **BCrypt**.
* **Role-Based Access:** Differentiated access for Administrators (edit/delete rights) and Standard Users (view-only rights).

### ⚙️ Business Logic (Knockout Tournament)
* **Elimination System:** Implemented specific backend logic that marks teams as `is_eliminated` in the SQL Server database.
* **Smart Validation:** When scheduling a new match, the interface loads **only** active teams, preventing human error in scheduling already eliminated teams.
* **Live Updates:** Score updates are instantly reflected in the rankings/standings.

### 💻 User Interface (UI/UX)
* **Responsive Design:** Based on **Bootstrap 4** (Customized SB Admin 2 Theme).
* **Dynamic Rendering:** Pages are rendered dynamically using **Thymeleaf**.
* **Fluid Navigation:** Seamless experience navigating between matches, teams, and user profiles.

---

## 🛠️ Technology Stack

| Category | Technologies |
| :--- | :--- |
| **Backend** | Java 17, Spring Boot 3.x, Spring Security |
| **Database** | Microsoft SQL Server (JDBC, JPA/Hibernate) |
| **Frontend** | Thymeleaf, HTML5, CSS3, JavaScript, Bootstrap 4 |
| **Build Tool** | Maven |
| **External APIs** | Google Identity Platform, Meta for Developers (Facebook Login) |

---

## 💻 Installation and Local Setup

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/your-username/Project-Name.git](https://github.com/your-username/Project-Name.git)
    ```

2.  **Database Configuration:**
    * Ensure you have SQL Server installed and running.
    * Create a database named `Eveniment_Sportiv`.
    * Configure `src/main/resources/application.properties` with your username and password.

3.  **Environment Variables Configuration (.env):**
    * For Facebook and Google login to work, create a `.env` file in the root directory or set the variables in your IDE:
    ```properties
    GOOGLE_CLIENT_ID=your_key_here
    GOOGLE_CLIENT_SECRET=your_secret_here
    FACEBOOK_CLIENT_ID=facebook_app_id
    FACEBOOK_CLIENT_SECRET=facebook_app_secret
    ```

4.  **Running the Application:**
    * Run the `EvenimentSportivApplication.java` class from IntelliJ/Eclipse.
    * Access the app at: `http://localhost:8080`.

---

## 👤 Author

Project realized and developed entirely by Vasilescu Alexandru Gabriel.

This project was developed as part of a bachelor's thesis, aiming to digitize the process of organizing sports competitions.

---

## 📄 License

All rights reserved © 2024 **[Your Name Here]**.
The source code is available for viewing for educational purposes.
