# Dynamic Refresh Configuration for Pharmacy Chain (@RefreshScope & Actuator)

- **GitHub Repository Link:** https://github.com/anvvhe190784/MD04_SS09_Bai2

This project demonstrates **Dynamic Refresh (Hot Reload)** of application configuration without restarting the service using **Spring Cloud Config Server**, **Spring Cloud Context `@RefreshScope`**, and **Spring Boot Actuator** with **Java 21**, **Gradle**, and **Spring Boot 3.4.1**.

---

## 1. Architecture Overview

```
                      +-------------------------------------------------------------+
                      |                 GitHub Remote Repository                    |
                      |         https://github.com/anvvhe190784/MD04_SS09_Bai2      |
                      |            └── config-repo/pharmacy-service.properties      |
                      |                 (pharmacy.vat-rate: 8.0% -> 10.0%)          |
                      +------------------------------+------------------------------+
                                                     |
                                                     | Git clone & fetch via HTTPS
                                                     v
                                      +-----------------------------+
                                      | Spring Cloud Config Server  |
                                      |        (Port 8888)          |
                                      +--------------+--------------+
                                                     ^
                                                     | 1. Pull on startup
                                                     | 2. Re-fetch on /actuator/refresh
                                                     |
                                      +--------------+--------------+
                                      |      pharmacy-service       |
                                      |   (Port 8081 / Client)      |
                                      | - @RefreshScope on          |
                                      |   BillController            |
                                      | - POST /actuator/refresh    |
                                      | - POST /api/v1/bill         |
                                      +-----------------------------+
```

---

## 2. Project Components

| Component | Port | Description |
| :--- | :--- | :--- |
| **`config-repo/`** | - | Subfolder containing `pharmacy-service.properties` (Datasource, branch name, hotline, and `pharmacy.vat-rate`). |
| **`config-server/`** | `8888` | Spring Cloud Config Server pulling configuration dynamically from `https://github.com/anvvhe190784/MD04_SS09_Bai2.git`. |
| **`pharmacy-service/`** | `8081` | Microservice with Actuator (`/actuator/refresh`) and `@RefreshScope` `BillController` calculating medicine bills with dynamic VAT. |

---

## 3. Technology Stack

- **Java Version:** OpenJDK 21
- **Build Tool:** Gradle (Multi-module with `gradlew` wrapper)
- **Spring Boot Version:** `3.4.1`
- **Spring Cloud Version:** `2024.0.0`
- **Key Modules:** `spring-boot-starter-actuator`, `spring-cloud-starter-config`, `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, H2 Database

---

## 4. How to Test Dynamic Refresh (Without Restarting!)

### Step 1: Start Config Server (Port 8888)
```bash
.\gradlew.bat :config-server:bootRun
```
Verify endpoint:
```bash
curl http://localhost:8888/pharmacy-service/default
```

---

### Step 2: Start Pharmacy Service (Port 8081)
In another terminal:
```bash
.\gradlew.bat :pharmacy-service:bootRun
```

---

### Step 3: Test Bill Calculation with Initial VAT (8%)
```bash
curl -X POST http://localhost:8081/api/v1/bill -H "Content-Type: application/json" -d "{\"subtotal\": 100000}"
```
**Response (VAT 8%):**
```json
{
  "subtotal": 100000.0,
  "vatRate": 8.0,
  "vatAmount": 8000.0,
  "finalTotal": 108000.0,
  "calculationDetails": "Subtotal: 100000.00 + VAT (8.0%): 8000.00 = Total: 108000.00"
}
```

---

### Step 4: Modify VAT Rate in Git Repository
Change `pharmacy.vat-rate` in `config-repo/pharmacy-service.properties`:
```properties
pharmacy.vat-rate=10.0
```
Commit and push to GitHub:
```bash
git add config-repo/pharmacy-service.properties
git commit -m "feat: update pharmacy vat rate to 10%"
git push origin main
```

---

### Step 5: Trigger Dynamic Refresh (Hot Reload)
Send a POST request to Actuator refresh endpoint:
```bash
curl -X POST http://localhost:8081/actuator/refresh
```
**Response:**
```json
["config.client.version", "pharmacy.vat-rate"]
```

---

### Step 6: Verify Bill Calculation Immediately (VAT 10% - Zero Restart!)
```bash
curl -X POST http://localhost:8081/api/v1/bill -H "Content-Type: application/json" -d "{\"subtotal\": 100000}"
```
**Response (VAT 10%):**
```json
{
  "subtotal": 100000.0,
  "vatRate": 10.0,
  "vatAmount": 10000.0,
  "finalTotal": 110000.0,
  "calculationDetails": "Subtotal: 100000.00 + VAT (10.0%): 10000.00 = Total: 110000.00"
}
```
Notice that the tax rate changed from **8.0%** to **10.0%** seamlessly without stopping or restarting `pharmacy-service`!

---

## 5. Submission Link

- **GitHub Repository URL:** https://github.com/anvvhe190784/MD04_SS09_Bai2
