# NextQuest

NextQuest is a full-stack application for discovering games and managing a personal game library. The project is currently under development and is being built incrementally with a Java/Spring Boot API and a React/TypeScript client.

## Current features

- User registration with password hashing using BCrypt
- Authentication with signed JWTs
- Stateless authorization with Spring Security
- Public game catalog API
- Authenticated personal library API
- React pages for registration and login
- Authentication state stored in `sessionStorage`
- Protected frontend route for the user's library
- Unit and integration tests for the backend

The catalog and library interfaces are still being implemented on the frontend.

## Roadmap

- [ ] Add the main navigation and logout flow
- [ ] Build the game catalog interface
- [ ] Connect the personal library page to the backend
- [ ] Add and update games in the user's library
- [ ] Improve frontend error handling
- [ ] Integrate with the Steam Web API

## Technologies

### Backend

- Java 25
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Spring Security
- PostgreSQL
- Maven Wrapper
- JUnit and H2 for tests

### Frontend

- React 19
- TypeScript 6
- Vite 8
- React Router 8
- ESLint
- Fetch API

## Project structure

```text
NextQuest/
|-- backend/    Spring Boot REST API
|-- frontend/   React and TypeScript client
`-- README.md
```

## Requirements

- JDK 25
- PostgreSQL
- Node.js `^20.19.0` or `>=22.12.0`
- npm

## Running locally

### 1. Create the database

Create a PostgreSQL database named `nextquest`. The application currently connects with the `postgres` user:

```sql
CREATE DATABASE nextquest;
```

### 2. Configure the backend environment

The backend requires two environment variables:

- `DB_PASSWORD`: password for the local PostgreSQL `postgres` user
- `JWT_SECRET`: Base64-encoded secret containing at least 32 bytes

In PowerShell, define them for the current terminal session:

```powershell
$env:DB_PASSWORD = 'your_postgres_password'

$jwtBytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($jwtBytes)
$env:JWT_SECRET = [Convert]::ToBase64String($jwtBytes)
$rng.Dispose()
```

Do not commit real credentials or JWT secrets to the repository.

### 3. Start the backend

From the project root on Windows:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

On Linux or macOS:

```bash
cd backend
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8081`.

### 4. Start the frontend

Open another terminal from the project root:

```bash
cd frontend
npm ci
npm run dev
```

The application will be available at `http://localhost:5173`.

## Frontend routes

| Route | Access | Description |
| --- | --- | --- |
| `/` | Public | Home page |
| `/register` | Public | Account registration |
| `/login` | Public | Authentication |
| `/library` | Authenticated | Personal library |

## API overview

| Method | Endpoint | Access | Description |
| --- | --- | --- | --- |
| `POST` | `/api/users` | Public | Create an account |
| `POST` | `/api/auth/login` | Public | Authenticate and receive a JWT |
| `GET` | `/api/games` | Public | List games |
| `GET` | `/api/games/{id}` | Public | Find a game by ID |
| `POST` | `/api/games` | Authenticated | Create a game |
| `GET` | `/api/library` | Authenticated | List the authenticated user's library |
| `POST` | `/api/library` | Authenticated | Add a game to the library |
| `GET` | `/api/library/{entryId}` | Authenticated | Find a library entry |
| `PATCH` | `/api/library/{entryId}` | Authenticated | Update a library entry |
| `DELETE` | `/api/library/{entryId}` | Authenticated | Remove a library entry |

Authenticated endpoints expect the token in the HTTP header:

```http
Authorization: Bearer <token>
```

## Checks and tests

Run the backend test suite:

```powershell
cd backend
.\mvnw.cmd test
```

Check and build the frontend:

```bash
cd frontend
npm run lint
npm run build
```
