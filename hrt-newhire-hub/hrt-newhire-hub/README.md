# New Hire Compliance & Document Sync Hub

A small, self-contained simulation of an HR-tech onboarding integration:
when a new employee is created, the system triggers **I-9 eligibility
verification** and **document completeness validation** (an HRSD-style
content-management check), the way an HCM platform coordinates several
downstream systems for a single new hire.

## Why this project

This was built to demonstrate the core responsibilities of an HR
Technology engineering role: designing integrations with external HR
systems, using AI-assisted development responsibly (with human validation
of AI output before it's trusted), and backing all of it with a solid
test suite.

| JD responsibility | Where it shows up here |
|---|---|
| Integrations with systems like I-9 Tracker, HRSD | `I9VerificationService`, document intake flow |
| Use AI tools responsibly; validate AI output before it's used | `AiDocumentValidator` — never returns a raw model answer uncritically; unparseable or low-confidence output is routed to `NEEDS_HUMAN_REVIEW` instead of auto-approved |
| Translate business requirements into technical solutions | `NewHireOnboardingService` orchestration layer, clean DTO boundaries |
| High-quality, maintainable code + testing discipline | Interface-based validators, unit tests per service, one full end-to-end integration test |
| SDLC ownership | GitHub Actions CI running backend tests + frontend build on every push |

## Architecture

```
React UI  --->  POST /api/employees  --->  EmployeeController
                                                 |
                                        NewHireOnboardingService
                                          /               \
                             I9VerificationService   DocumentValidator
                             (simulated I-9 Tracker)   (rule-based, or
                                                         AI-assisted via
                                                         Claude API)
                                                 |
                                          EmployeeRepository
                                                 |
                                            H2 database
```

`DocumentValidator` is an interface with two implementations selected by
config, not by editing calling code:

- **`RuleBasedDocumentValidator`** (default) — deterministic, offline,
  checks a document for a name, an SSN-last-4 reference, and a date.
- **`AiDocumentValidator`** (opt-in) — sends the same document to an LLM
  and asks for a completeness judgment. It never trusts the response at
  face value: unparseable output, an unexpected status value, or an
  API failure are all mapped to `NEEDS_HUMAN_REVIEW`, not silently passed
  through as `COMPLETE`.

## Running it

### Backend

```bash
cd backend
mvn spring-boot:run
```

Runs on `http://localhost:8080`. H2 console available at `/h2-console`
(JDBC URL: `jdbc:h2:mem:newhirehub`).

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Runs on `http://localhost:5173` and proxies `/api` calls to the backend.

### Enabling AI-assisted validation

By default the rule-based validator runs (no API key needed). To switch
to the AI-assisted path:

```bash
export ANTHROPIC_API_KEY=sk-ant-...
```

```properties
# backend/src/main/resources/application.properties
newhire.ai-validation.enabled=true
```

## Testing

```bash
cd backend
mvn test
```

- `I9VerificationServiceTest` — unit tests covering verified / pending /
  rejected paths.
- `RuleBasedDocumentValidatorTest` — unit tests for each missing-field
  case.
- `EmployeeControllerTest` — MockMvc tests for the HTTP layer, including
  a validation-error case.
- `NewHireOnboardingServiceIntegrationTest` — full Spring context +
  real H2 database, exercising the entire onboard → verify → validate →
  persist → retrieve path.

## API

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/employees` | Create a new hire; triggers I-9 verification and document validation |
| `GET` | `/api/employees` | List all employees |
| `GET` | `/api/employees/{id}` | Get one employee |

## Possible extensions

- Swap the H2 database for Postgres via Testcontainers-backed integration tests.
- Add a webhook/polling flow to simulate an async I-9 Tracker callback instead of a synchronous check.
- Add an audit log table recording every AI validation call and its outcome, for compliance review.

## Resume framing

> Built a Spring Boot/React onboarding compliance tool simulating I-9
> Tracker and HRSD document-intake integrations, with a pluggable
> AI-assisted document validator (Claude API) that routes uncertain or
> unparseable AI output to human review rather than auto-approving it;
> covered with unit and end-to-end integration tests and a GitHub
> Actions CI pipeline.
