CREATE DATABASE IF NOT EXISTS notesdb;
USE notesdb;
CREATE TABLE notes ( 
	id INT AUTO_INCREMENT PRIMARY KEY,
    user VARCHAR(50),
    subject VARCHAR(50),
    title VARCHAR(100),
    content TEXT,
    time VARCHAR(50)
);
SHOW TABLES;
CREATE TABLE users(
	id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(100)
);
SELECT * FROM users;
INSERT INTO users(username, password)
VALUES ("tanvi", '123');
ALTER TABLE notes ADD COLUMN username VARCHAR(100);
DESCRIBE notes;