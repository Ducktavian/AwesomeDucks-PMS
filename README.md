# MotorPH Payroll System

Java Swing desktop application for payroll management. Built with Java 17, Maven, and MySQL.

---

## Requirements

- Java 17+
- Maven 3.6+ (or use the NetBeans bundled Maven)
- MySQL 8.0+
- NetBeans IDE (recommended) or any Java IDE

---

## Database Setup

### 1. Create the database

Run the main SQL schema in MySQL:

```sql
SOURCE src/main/resources/database/MotorPH_Payroll_Database.sql;
```

Or import it via MySQL Workbench / phpMyAdmin.

### 2. Configure your connection

Edit `src/main/resources/database.properties` with your local MySQL credentials:

```properties
db.url=jdbc:mysql://localhost:3306/motorph_payroll_db
db.user=root
db.password=your_password_here
```

> The default values in the file point to `localhost:3306` with user `root`. Change `db.password` to match your local MySQL setup.

---

## Running the App

### In NetBeans

1. Open the project (`File → Open Project`)
2. Right-click the project → `Run`

### Via Maven (terminal)

```bash
mvn compile exec:java
```

---

## Test Accounts

| Role     | Username               | Password |
|----------|------------------------|----------|
| Admin    | manueliii.garcia       | 10001M   |
| Finance  | roderick.alvaro        | 10010R   |
| IT       | eduard.hernandez       | 10005E   |
| HR       | brad.sanjose           | 10007B   |
| Employee | christian.mata         | 10016C   |

### Role permissions

#### Dashboard views
| Dashboard | Admin | HR | Finance | IT | Employee |
|-----------|-------|----|---------|----|----------|
| Admin     | ✓     |    |         |    |          |
| HR        | ✓     | ✓  |         |    |          |
| Finance   | ✓     |    | ✓       |    |          |
| IT        | ✓     |    |         | ✓  |          |
| Employee  | ✓     | ✓  | ✓       | ✓  | ✓        |

#### Employees
| Action            | Admin | HR | Finance | IT | Employee |
|-------------------|-------|----|---------|----|----------|
| View all          | ✓     | ✓  | ✓       | ✓  |          |
| Add / Update / Delete | ✓  | ✓  |         |    |          |
| View own only     |       |    |         |    | ✓        |

#### Payroll
| Action            | Admin | HR | Finance | IT | Employee |
|-------------------|-------|----|---------|----|----------|
| View all          | ✓     |    | ✓       |    |          |
| Add / Update / Delete | ✓  |   | ✓       |    |          |
| View own only     |       | ✓  |         | ✓  | ✓        |

#### Requests
| Action            | Admin | HR | Finance | IT | Employee |
|-------------------|-------|----|---------|----|----------|
| View all          | ✓     | ✓  |         |    |          |
| Add / Update / Delete | ✓  | ✓  |         |    |          |
| View own only     |       |    | ✓       | ✓  | ✓        |

#### Attendance
| Action            | Admin | HR | Finance | IT | Employee |
|-------------------|-------|----|---------|----|----------|
| View all          | ✓     | ✓  |         |    |          |
| Add               | ✓     | ✓  | ✓       | ✓  | ✓        |
| Update / Delete   | ✓     | ✓  |         |    |          |
| View own only     |       |    | ✓       | ✓  | ✓        |

#### User Accounts (Users tab)
| Action            | Admin | HR | Finance | IT | Employee |
|-------------------|-------|----|---------|----|----------|
| View & manage     | ✓     |    |         | ✓  |          |

> Sidebar tabs that a role cannot access are shown greyed out and are not clickable.
> The Employees tab is accessible to all roles; the panel restricts edits to Admin/HR and view scope to Finance/IT/Employee.

---

## Project Structure

```
src/
  main/
    java/com/motorph/
      dao/        - JDBC data access objects
      model/      - Domain models (Employee, Payslip, etc.)
      service/    - Business logic layer
      ui/         - Swing panels per feature
      util/       - AppContext, Session, helpers
    resources/
      database/   - SQL schema
      database.properties - DB connection config (fill in your credentials)
```
