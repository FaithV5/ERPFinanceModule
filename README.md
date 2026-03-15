# ERP Finance Module

## Overview
A simple ERP Finance Module built with Java and Supabase.

## Features
- User login
- Add/View/Update/Delete transactions
- Financial reports
- Income/Expense tracking

## Technologies Used
- Java Swing (GUI)
- Supabase (PostgreSQL database)

## Requirements
- Java 23 or higher
- PostgreSQL JDBC driver (included in lib/)


## Setup Instructions
1. Import project to Visual Studio
2. Add PostgreSQL JDBC driver to build path
3. Update database credentials in DatabaseConnection.java
4. Run Main.java

## Database Schema
- users (id, username, password, role)
- transactions (id, transaction_id, date, type, amount, description)
