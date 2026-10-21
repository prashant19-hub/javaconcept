# CompanyMS

A standalone Spring Boot employee, attendance and payroll management project.
It lives in `demo\CompanyMS` and does not require changes to the sibling `demo\src` project.

## Requirements

- JDK 25 (latest LTS)
- Docker Desktop with Docker Compose (or a local PostgreSQL 16 instance)

## Run from the beginning

Open PowerShell and run these commands:

```powershell
cd C:\Users\prash\java\demo\CompanyMS
docker compose up -d
docker compose ps
.\mvnw.cmd spring-boot:run
```

Wait until `companyms-db` is healthy before starting Spring Boot. The app runs at
`http://localhost:8081/companyms/dashboard`; the earlier project can continue using
its own port and database.
The CompanyMS PostgreSQL container is isolated from the other project databases and
is published on host port `5435` (container port `5432`).

### Connect DBeaver

Create a new **PostgreSQL** connection in DBeaver and enter:

| Setting | Value |
| --- | --- |
| Host | `localhost` |
| Port | `5435` |
| Database | `companyms` |
| Username | `companyms` |
| Password | `companyms` |

Select **Test Connection**, install the PostgreSQL driver if DBeaver asks, then finish.
Refresh `companyms` → `Schemas` → `public` → `Tables` to see
`company_employees`, `company_attendance` and `company_payroll`. These are local-demo
credentials for this isolated container only; change them for any shared deployment.

To stop the app, press `Ctrl+C`. To stop the database without removing its saved data:

```powershell
docker compose down
```

The PostgreSQL data is stored in the `companyms-postgres-data` Docker volume. Do not
run `docker compose down -v` unless you intentionally want to erase that database.

### Use an existing PostgreSQL server

Set connection values before starting Maven. These are PowerShell examples:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5435/companyms"
$env:DB_USERNAME = "your_database_user"
$env:DB_PASSWORD = "your_database_password"
.\mvnw.cmd spring-boot:run
```

The defaults are for the isolated local Docker database only. Use environment variables
or a secrets manager for non-local environments; do not commit real credentials.

## Pages

- Dashboard: `http://localhost:8081/companyms/dashboard`
- Employees: `http://localhost:8081/companyms/employees`
- Add employee: `http://localhost:8081/companyms/employees/add`
- Attendance: `http://localhost:8081/companyms/attendance`
- Payroll: `http://localhost:8081/companyms/payroll`

## Employee REST API

Base URL: `http://localhost:8081/api/companyms/employees`

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/companyms/employees` | List employees; optional `?search=engineering` |
| GET | `/api/companyms/employees/{id}` | Get one employee |
| POST | `/api/companyms/employees` | Create an employee |
| PUT | `/api/companyms/employees/{id}` | Update an employee |
| PUT | `/api/companyms/employees/{id}/transfer?department=Engineering` | Transfer department |
| DELETE | `/api/companyms/employees/{id}` | Delete an employee without attendance/payroll history |

Example request body:

```json
{
  "name": "Asha Sharma",
  "email": "asha@example.com",
  "phone": "9876543210",
  "designation": "Software Engineer",
  "department": "Engineering",
  "joinDate": "2025-01-15",
  "salary": 65000.00,
  "experienceYears": 2,
  "status": "ACTIVE"
}
```

Validation errors return HTTP 400 with field messages. Missing records return HTTP 404,
and duplicate or conflicting data returns HTTP 409. Payroll net salary is calculated
as basic salary plus bonus minus deduction. Attendance and payroll records are linked
to a valid employee ID and retain the employee name for historical display.

## Run tests

```powershell
.\mvnw.cmd test
```

## Project layout

```text
src\main\java\com\prashant\companyms\
  controller\    Web controllers and Employee REST API
  exception\     API errors and domain exceptions
  model\         JPA entities and validation
  repository\    Spring Data repositories
  service\       Business rules and payroll calculations
src\main\resources\
  templates\     Thymeleaf pages
  static\css\    Dashboard styles
  static\js\     Data-driven dashboard charts
  application.properties
```
