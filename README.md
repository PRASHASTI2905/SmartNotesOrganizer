📒 Smart Notes Organizer

🔷 Project Description
Smart Notes Organizer is a Java-based client-server application that allows users to manage their notes efficiently.
It supports features like adding, viewing, searching, updating, deleting, and filtering notes using a MySQL database.
This project uses:
- Java Swing for GUI
- Socket Programming for client-server communication
- JDBC for database connectivity

🔷 Features
- ➕ Add Notes
- 📖 View Notes
- ❌ Delete Notes
- ✏️ Update Notes
- 🔍 Search Notes
- 📂 Filter Notes by Subject
- 🌙 Dark Mode
- 📤 Export Notes to File

🔷 Technologies Used
- Java (Swing, AWT)
- Socket Programming
- MySQL
- JDBC

🔷 Project Structure
SmartNotesOrganizer/
│
├── ClientLogin.java
├── ClientDashboard.java
├── Server.java
├── database.sql
├── README.md

🔷 How to Run the Project
✅ Step 1: Start MySQL
Make sure MySQL server is running.
✅ Step 2: Setup Database
Run the following SQL:
CREATE DATABASE notesdb;
USE notesdb;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(50)
);

CREATE TABLE notes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subject VARCHAR(100),
    title VARCHAR(100),
    content TEXT,
    time VARCHAR(50),
    username VARCHAR(50)
);
✅ Step 3: Run the Server
Run:
Server.java
You should see:
Server Started...
✅ Step 4: Run the Client
Run:
ClientLogin.java
✅ Step 5: Use the Application
- Register a new user
- Login
- Start managing notes

🔷 Important Notes
- Make sure MySQL is running on port 3307 (or update in code if different)
- Database name should be notesdb
- Ensure JDBC driver is properly configured

🔷 Author
Developed by: Prashasti Rai

🔷 Future Improvements
- User-specific notes filtering
- Cloud storage integration
- Better UI design
- Password encryption
✨ This project demonstrates concepts of Java GUI, Networking, and Database Integration.
