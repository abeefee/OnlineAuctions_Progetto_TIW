# Online Auctions Web Application - TIW Project 2022/23

*Puoi anche leggerlo in [Italiano](README.it.md)*

> **Academic Note:** This project was developed for the *Tecnologie Informatiche per il Web* course at Politecnico di Milano (Academic Year 2022/23), achieving a final grade of **27/30**.

### Project Description
The project consists of an online auction management platform developed in two distinct, complete web application versions:

* *Pure HTML Version*: A classic multi-page server-side rendered architecture using Java Servlets, JSPs, and JDBC, where navigation and state changes trigger full page reloads.

* *JavaScript / RIA (Rich Internet Application) Version*: A Single Page Application (SPA) architecture that extends the core functionalities using asynchronous server interactions (AJAX/Fetch) and client-side state management without full page reloads.

### Core Features & Requirements
* **User Roles & Authentication:** Secure login system ensuring strict role-based access control and parameter validation both client-side and server-side.
* **Selling Module (VENDO):** Allows users to create new articles (code, name, description, image, price) and bundle them into auctions with a starting price, minimum bid increment, and expiration date/time. Users can view active and closed auctions they created and close expired auctions manually.
* **Buying Module (ACQUISTO):** Provides keyword search filtering for open auctions, detailed view pages with bid histories sorted in reverse chronological order, and a bidding interface enforcing the minimum raise constraint. It also tracks won auctions.
* **RIA Persistence (JS Version):** Remembers the user's last visited state (e.g., VENDO vs. ACQUISTO and recently clicked open auctions) using client-side storage mechanisms lasting up to a month.

### Architecture & Technologies
* **Backend:** Java EE (Servlets, JavaBeans, DAO pattern for database separation, Custom Filters for security).
* **Frontend (Pure HTML):** HTML5, CSS3, JSP (JavaServer Pages).
* **Frontend (RIA):** Vanilla JavaScript, DOM manipulation, asynchronous communication.
* **IDE & Server:** Eclipse IDE (Dynamic Web Project structure), Apache Tomcat.


### Repository Structure
`pure-html/`: Contains the complete multi-page Java EE application (Servlets, DAOs, Beans, and JSPs) implementing server-side rendering.

`javascript/`: Contains the Single Page Application (SPA) version utilizing asynchronous AJAX/Fetch interactions and client-side state management.