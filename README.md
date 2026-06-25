<div align="center">

# 🔗 REST API Test Automation Framework

**A production-grade REST API testing framework built with RestAssured, TestNG & Java — featuring multiple data strategies, JSON Schema validation, Allure reporting, and GitHub Actions CI/CD**

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://www.oracle.com/java/)
[![RestAssured](https://img.shields.io/badge/RestAssured-6.0-green)](https://rest-assured.io/)
[![TestNG](https://img.shields.io/badge/TestNG-7.12-red?logo=testng)](https://testng.org/)
[![Allure](https://img.shields.io/badge/Allure-2.30-yellow?logo=allure)](https://allurereport.org/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?logo=apache-maven)](https://maven.apache.org/)
[![GitHub Actions](https://img.shields.io/badge/CI-GitHub%20Actions-2088FF?logo=github-actions)](https://github.com/features/actions)

</div>

---

## 📌 What Is This?

A clean, layered REST API automation framework that tests the **[Petstore REST API](https://petstore.swagger.io/)** — a publicly available Swagger-based API used as a real-world demo target.

The framework demonstrates **three different test data strategies** side by side, so you can see exactly how each pattern works and when to use it:

| Strategy | Class | When to Use |
|---|---|---|
| 🎲 Faker (dynamic) | `UserTestsUsingFaker` | Random data, no Excel dependency |
| 📊 Excel + DataProvider | `UserTestsUsingDataProvider` | Parameterised, multiple records |
| 🗺️ Excel + Map (key-value) | `UserTestsUsingMap` | Named test data rows |

On top of that, it includes **JSON Schema validation**, **automatic retry on failure**, **Allure reporting published to GitHub Pages**, and a fully configured **GitHub Actions CI/CD pipeline**.

---

## 🏗️ Architecture

```
rest-api-automation-framework/
│
├── .github/workflows/
│   └── api-tests.yml               ← GitHub Actions — manual trigger + weekly schedule
│
├── src/test/java/
│   │
│   ├── clients/                    ← API CLIENT LAYER
│   │   ├── RequestSpecFactory      — centralised RequestSpecification (baseUrl, headers)
│   │   ├── UserClient              — all User API methods (POST, GET, PUT, DELETE)
│   │   └── Routes                  — API endpoint constants
│   │
│   ├── models/
│   │   └── User                    — POJO for request/response serialization via Gson
│   │
│   ├── assertions/
│   │   └── UserAssertions          — field-level assertion methods with @Step (Allure)
│   │
│   ├── config/
│   │   └── ConfigManager           — singleton; reads properties → env vars → system props
│   │
│   ├── listeners/
│   │   ├── RetryAnalyzer           — retries each failed test once before marking FAIL
│   │   ├── RetryAnnotationTransformer — applies RetryAnalyzer to all tests automatically
│   │   └── TestListener            — attaches failure message, stack trace, log to Allure
│   │
│   ├── utils/
│   │   ├── UserDataBuilder         — builds User payloads (Faker / Excel-Map strategies)
│   │   ├── DataProviders           — TestNG @DataProvider feeding from Excel rows
│   │   ├── ExcelUtils              — Apache POI read/write helpers
│   │   ├── TestDataManager         — key-value Excel reader (label → row → field map)
│   │   ├── AllureAttachments       — attaches execution log file to every Allure report
│   │   └── LogManagerUtil          — Log4j2 logger factory wrapper
│   │
│   └── tests/
│       ├── BaseTest                — @BeforeMethod/@AfterMethod logging + assertion setup
│       ├── UserTestsUsingFaker     — CRUD tests with Faker-generated dynamic data
│       ├── UserTestsUsingDataProvider — CRUD tests driven by Excel DataProvider
│       ├── UserTestsUsingMap       — CRUD tests driven by Excel key-value map
│       └── UserNegativeTests       — negative/error scenario tests (404, invalid input)
│
├── src/test/resources/
│   ├── configurations/
│   │   ├── application.properties  — base URL, test data file paths, sheet names
│   │   └── allure.properties       — Allure results directory config
│   ├── schemas/
│   │   └── user-schema.json        — JSON Schema for GET /user response validation
│   ├── testData/
│   │   └── Pet_Store_TestData.xlsx — test data (Input_1: map data, Input_2: DataProvider)
│   └── log4j2.xml                  — structured logging config (console + rolling file)
│
├── testng.xml                      — default suite (all tests)
├── testng-smoke.xml                — smoke group only
├── testng-regression.xml           — regression group only
├── testng-user.xml                 — user tests only
└── testng-negative.xml             — negative tests only
```

---

## ⚙️ Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 21 |
| API Testing | RestAssured | 6.0.0 |
| Test Framework | TestNG | 7.12.0 |
| Serialization | Gson | 2.14.0 |
| JSON Schema | RestAssured JSON Schema Validator | 6.0.0 |
| Fake Data | JavaFaker | 1.0.2 |
| Test Data | Apache POI (Excel) | 5.2.2 |
| Reporting | Allure TestNG | 2.30.0 |
| Logging | Log4j2 | 2.24.3 |
| Build Tool | Apache Maven | 3.8+ |
| CI/CD | GitHub Actions | — |

---

## ✨ Key Design Decisions

### 🏭 Client Layer — Separates API calls from test logic
Every HTTP call goes through `UserClient`. Tests never call RestAssured directly.

```java
// Test calls the client — zero RestAssured in test classes
Response createResponse = UserClient.createUser(payload);
userAssertions.verifyStatusCode(createResponse, 200);

// Client owns the HTTP details
public static Response createUser(User payload) {
    return given()
            .spec(RequestSpecFactory.defaultRequestSpec())
            .body(payload)
        .when()
            .post(Routes.USER);
}
```

**Why?** If the endpoint path changes, you change it in `Routes.java`. If auth headers change, you change them in `RequestSpecFactory`. Tests are untouched.

---

### 🎲 Three Data Strategies — Side by Side

**Strategy 1 — JavaFaker (fully dynamic, no external files)**
```java
// UserTestsUsingFaker — generates a unique user per run
payload = UserDataBuilder.getRandomUserPayload();
// faker.name().username(), faker.internet().safeEmailAddress(), etc.
```

**Strategy 2 — Excel + TestNG @DataProvider (parameterised, multi-row)**
```java
// DataProviders reads all rows from Excel sheet Input_2
// TestNG runs testCreateUsers() once per row — parallel-safe with ConcurrentHashMap
@Test(dataProvider = "payload", dataProviderClass = DataProviders.class)
void testCreateUsers(String id, String un, String fn, ...) { ... }
```

**Strategy 3 — Excel + Map (named rows)**
```java
// TestDataManager reads a specific named row from sheet Input_1
User payload = UserDataBuilder.getTestDataUserPayload("User_1");
```

---

### 🛡️ JSON Schema Validation
Every GET response is validated against `user-schema.json` — not just the status code or a single field, but the **entire contract**: field names, types, and required fields.

```java
// Validated in UserAssertions — attached as an Allure step
response.then().body(matchesJsonSchemaInClasspath("schemas/user-schema.json"));
```

If the API team changes a field type (e.g. `id` from `integer` to `string`), this catches it immediately — without writing field-by-field assertions.

---

### 🔁 Automatic Retry on Failure
`RetryAnalyzer` retries each failed test **once** before marking it as FAILED. `RetryAnnotationTransformer` applies this to every test automatically — no annotation needed on individual tests.

```java
// RetryAnalyzer — retries once
if (retryCount < MAX_RETRY_COUNT) {
    retryCount++;
    logger.warn("Retrying test '{}' | Attempt {} of {}", ...);
    return true;
}
```

This handles transient network failures in CI without marking tests as flaky.

---

### 📋 Allure Reporting with Log Attachment
`TestListener` attaches three things to every Allure report automatically:
- **On failure:** failure message + full stack trace as separate attachments
- **On every test (pass or fail):** the complete `logs/automation.log` file

Combined with `@Step`, `@Epic`, `@Feature`, `@Story`, and `@Description` annotations on every test and assertion method, Allure reports show the full request flow as a readable step timeline.

---

### ⚙️ Smart Config Resolution
`ConfigManager` checks three sources in priority order so nothing is ever hardcoded:

```
JVM system property (-Dbase_url=...) → Environment variable → application.properties
```

This means CI can override any value without touching the codebase.

---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+

### 1. Clone the Repository
```bash
git clone https://github.com/Akshay151093/data-driven-api-test-automation-framework.git
cd rest-api-automation-framework
```

### 2. Run Tests

**Run default suite (all tests):**
```bash
mvn clean test
```

**Run a specific suite:**
```bash
mvn clean test -DsuiteXmlFile=testng-smoke.xml
mvn clean test -DsuiteXmlFile=testng-regression.xml
mvn clean test -DsuiteXmlFile=testng-negative.xml
mvn clean test -DsuiteXmlFile=testng-user.xml
```

**Override base URL at runtime:**
```bash
mvn clean test -Dbase_url=https://your-api.example.com
```

### 3. Generate Allure Report Locally
```bash
# Install Allure CLI first: https://allurereport.org/docs/install/
allure serve target/allure-results
```

---

## 🔁 CI/CD — GitHub Actions

### Pipeline Features
- **Manual trigger** from the GitHub UI — choose which TestNG suite to run from a dropdown
- **Weekly scheduled run** every Monday at 18:30 UTC (catches API regressions automatically)
- **Allure report published to GitHub Pages** after every run — accessible via browser, no local setup needed

### How to Trigger Manually

1. Go to **Actions** tab in your GitHub repository
2. Click **API Automation Tests** workflow
3. Click **Run workflow**
4. Select the suite from the dropdown:

```
testng.xml          → all tests
testng-smoke.xml    → smoke group
testng-regression.xml → regression group
testng-user.xml     → user module only
testng-negative.xml → negative scenarios
```

### Pipeline Flow

```
GitHub Actions Trigger (manual / schedule)
          │
          ▼
  ┌───────────────────┐
  │  Setup Java 21    │
  │  + Maven cache    │
  └────────┬──────────┘
           │
           ▼
  ┌───────────────────┐
  │  mvn clean test   │
  │  -DsuiteXmlFile=  │
  └────────┬──────────┘
           │
    ┌──────┴──────┐
    ▼             ▼
Surefire       Allure
Reports     Results JSON
(artifacts)      │
                 ▼
          Allure CLI generates
             HTML Report
                 │
                 ▼
        Deploy to GitHub Pages
        (live URL in job summary)
```

### Artifacts Published After Each Run

| Artifact | Contents |
|---|---|
| `surefire-reports` | TestNG HTML + XML reports |
| `allure-results` | Raw Allure JSON results |
| `execution-logs` | `logs/automation.log` from the run |
| GitHub Pages | Interactive Allure HTML report (live URL) |

---

## 📊 Test Suite Structure

| TestNG XML | Groups Covered | Test Classes |
|---|---|---|
| `testng.xml` | all | All test classes |
| `testng-smoke.xml` | smoke | Faker + DataProvider + Map (create only) |
| `testng-regression.xml` | regression | Full CRUD via all 3 strategies |
| `testng-user.xml` | user | All user tests |
| `testng-negative.xml` | negative | Error / 404 scenarios |

---

## 🛡️ Security

- No credentials stored anywhere in the codebase
- Base URL overridable via environment variable in CI
- `application.properties` contains only public demo API URLs — safe to commit

---

## 📬 Contact

**Akshay Thorat** — [GitHub](https://github.com/Akshay151093) · [LinkedIn](https://linkedin.com/in/akshaythoratqa)  
**Email:** akshaythorat.qa@gmail.com

---

<div align="center">
  <sub>Built with ☕ Java 21, 🔗 RestAssured, and 🧪 TestNG</sub>
</div>