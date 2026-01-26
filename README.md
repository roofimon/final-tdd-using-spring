# final-tdd-using-spring

A simple banking application demonstrating Test-Driven Development (TDD) with the Spring Framework. It models accounts and transfers, exposes lightweight HTTP endpoints, and includes comprehensive unit and integration tests.

## Overview
- Implements core domain types: `Account`, `TransferReceipt`, and exceptions for business rules.
- Service layer handles transfers and fees with pluggable `FeePolicy` implementations.
- Repository layer provides in-memory and JDBC-style implementations.
- Web layer uses Spring MVC to expose account lookup and transfer operations.
- In-memory HSQLDB is used for integration tests; schema and test data are provisioned via Spring.

## Tech Stack
- Java 21 (Temurin)
- Maven (WAR packaging)
- Spring Framework 5.3.x (MVC, JDBC, OXM)
- SLF4J logging
- HSQLDB (embedded, test-only)
- JUnit 4, Mockito

## Project Structure
- `src/main/java/com/bank/...` — Controllers, services, repositories, domain
- `src/main/resources/META-INF/spring` — Spring XML config, schema and test data
- `src/test/java/com/bank/...` — Unit and integration tests
- `src/webapp` — Minimal JSP and web descriptors

## Requirements
- Java 21
- Maven 3.9+

## Build & Test
```bash
# Run full test suite
mvn clean test

# Build WAR artifact
mvn clean package
```

## Run (Deployment)
This project packages as a WAR (`target/odtBank.war`). Deploy to a compliant Servlet container or use Docker.

### Option 1: Docker (Recommended)
Build and run the application in a containerized environment:

```bash
# Build Docker image
docker build -t odtbank:latest .

# Run container (port 8080)
docker run -d -p 8080:8080 --name odtbank odtbank:latest

# View logs
docker logs -f odtbank

# Stop container
docker stop odtbank
```

**Stack**: Java 21 (Temurin), Tomcat 9.0.89, Spring 5.3.36

### Option 2: Manual Deployment
Deploy `target/odtBank.war` to a compliant Servlet container (e.g., Tomcat 9 with Servlet 4 support). The legacy Jetty Maven plugin defined in `pom.xml` is not guaranteed to work on modern JDKs.

**Note**: Tomcat 10+ requires Jakarta Servlet API; this project uses `javax.servlet`, so Tomcat 9 is recommended.

## API Endpoints
Controller: `AccountController`
- GET `/odtBank/account/{id}` — Retrieve an account by ID
- GET `/odtBank/account/{srcId}/transfer/{amount}/to/{destId}` — Transfer `amount` from `srcId` to `destId`

Responses are simple JSON/XML depending on the configured message converters.

### Example Requests
```bash
# List account (replace {id} with actual ID from test-data.sql)
curl http://localhost:8080/odtBank/account/1

# Transfer funds
curl http://localhost:8080/odtBank/account/1/transfer/50/to/2
```

## Configuration
- Spring context and beans are defined in `src/main/resources/META-INF/spring/spring-context.xml`.
- Test data and schema are defined in `schema.sql` and `test-data.sql` under the same folder.

## Testing
The project includes 22 tests covering services, policies, controller behavior, and an integration test that provisions an embedded database:
- `DefaultTransferServiceTest`, `DefaultTimeServiceTest`, `FlatFeePolicyTest`, `VariableFeePolicyTest`, `CheckingTimeAdviceTest`
- `AccountControllerTest`
- `IntegrationITCase` (XML config-driven)

## Security & Maintenance
- Critical and high-severity CVEs have been remediated by upgrading Spring and migrating Jackson to FasterXML.
- See CVE details and actions in [CVE_FIX_SUMMARY.md](CVE_FIX_SUMMARY.md).
- Keep Spring 5.3.x and Jackson 2.17.x up to date with latest patch releases.

## Branch & Versioning
- Default development branch: `main` (rename from `master`).

## License
This repository does not declare a license. If you plan to distribute, add an appropriate LICENSE file.
