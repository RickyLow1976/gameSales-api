# Game Sales API

## Setup

1. **Clone the repository:**
   ```bash
   https://github.com/RickyLow1976/gameSales-api.git
2. **Navigate to the project directory:**
   ```bash
   cd /src/main/java/com/example/demo
3. **Build the application:**
   ```bash
   ./mvnw clean install
4. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
5. **CsvGenerator**
   ```bash
   cd /src/main/java/com/example
   javac CsvGenerator.java
   java CsvGenerator


## Endpoints
**POST /api/import: Import CSV data.**

**GET /api/getGameSales: Retrieve game sales with optional filters and pagination.**

**GET /api/getTotalSales: Retrieve total sales and counts with optional filters.**
