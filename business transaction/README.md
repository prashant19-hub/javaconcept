# Business Dashboard

Java Spring Boot business dashboard with PostgreSQL and Docker.

## Features
- Add transaction
- View all transactions
- Delete transaction
- Business report
- PostgreSQL database using Docker

## Run the application with Docker

From the project root, copy `.env.example` to `.env`, set a strong `DB_PASSWORD`,
then start the application:

```bash
Copy-Item .env.example .env
# Edit .env and set DB_PASSWORD to a strong, private value.
docker compose up --build
```

Do not commit `.env`; it contains local database credentials.
This starts PostgreSQL and the Spring Boot API. The API waits for PostgreSQL's
health check before starting.

Open the API at http://localhost:8080. Transaction endpoints are under
`/api/transactions`.

To start only PostgreSQL and run the app from your IDE, use
`docker compose up -d postgres`, then run the Spring Boot application normally.
PostgreSQL is published on port `15432` by default to avoid conflicts with other
local database containers; set `DB_PORT` to change it.