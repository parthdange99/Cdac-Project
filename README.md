# FundBridge Microservices Architecture

> **Migrated from monolith to production-ready microservices**
> Spring Boot 3.2.5 | Spring Cloud 2023.0.3 | Java 21 | MySQL 8

---

## 📐 Architecture Overview

```
                    ┌─────────────────────────────────┐
                    │        API Gateway               │
                    │   Spring Cloud Gateway           │
Client  ─────────►  │   Port: 8080                     │
                    │   JWT Validation Filter          │
                    └──────────────┬──────────────────┘
                                   │ Routes + X-User-* Headers
              ┌────────────────────┼──────────────────────────┐
              │                    │                          │
   ┌──────────▼──────┐  ┌──────────▼──────┐  ┌──────────────▼──────┐
   │  auth-service   │  │  user-service   │  │   loan-service       │
   │   Port: 8081    │  │   Port: 8082    │  │   Port: 8083         │
   │ DB: _auth       │  │ DB: _users      │  │ DB: _loans           │
   └─────────────────┘  └─────────────────┘  └──────────────────────┘
   ┌─────────────────┐  ┌─────────────────┐  ┌──────────────────────┐
   │campaign-service │  │donation-service │  │  payment-service      │
   │   Port: 8084    │  │   Port: 8085    │  │  Port: 8086           │
   │ DB: _campaigns  │  │ DB: _donations  │  │ DB: _payments         │
   └─────────────────┘  └─────────────────┘  └──────────────────────┘
   ┌─────────────────┐
   │notification-svc │
   │   Port: 8087    │
   │ DB: _notifs     │
   └─────────────────┘
              │
              ▼ Eureka
   ┌─────────────────────┐    ┌─────────────────────┐
   │  discovery-server   │    │    config-server    │
   │  (Eureka) Port:8761 │    │     Port: 8888      │
   └─────────────────────┘    └─────────────────────┘
```

---

## 📦 Module Structure

```
fundbridge-microservices/
├── pom.xml                    ← Root parent POM (manages all dependencies)
├── common-lib/                ← Shared: enums, exceptions, DTOs, JwtUtil
├── discovery-server/          ← Eureka Service Registry (Port 8761)
├── config-server/             ← Spring Cloud Config (Port 8888)
├── api-gateway/               ← Spring Cloud Gateway + JWT Filter (Port 8080)
├── auth-service/              ← Registration, Login, JWT issuance (Port 8081)
├── user-service/              ← User profile management (Port 8082)
├── loan-service/              ← Loan requests, offers, repayment (Port 8083)
├── campaign-service/          ← Crowdfunding campaigns (Port 8084)
├── donation-service/          ← Campaign donations (Port 8085)
├── payment-service/           ← Razorpay integration (Port 8086)
├── notification-service/      ← Notifications (Port 8087)
├── init-databases.sql         ← MySQL database init script
└── docker-compose.yml         ← Full stack Docker Compose
```

---

## 🚀 Quick Start (Local Development)

### Prerequisites
- Java 21+
- Maven 3.9+
- MySQL 8.0+

### Step 1: Initialize Databases
```sql
-- Run in MySQL Workbench or CLI:
mysql -u root -p < init-databases.sql
```

### Step 2: Build common-lib first (required by all services)
```bash
cd common-lib
mvn clean install -DskipTests
```

### Step 3: Start services in order

**Start discovery-server first:**
```bash
cd discovery-server
mvn spring-boot:run
# Verify at: http://localhost:8761
```

**Start config-server:**
```bash
cd config-server
mvn spring-boot:run
```

**Start business services (any order):**
```bash
# In separate terminals:
cd auth-service && mvn spring-boot:run
cd user-service && mvn spring-boot:run
cd loan-service && mvn spring-boot:run
cd campaign-service && mvn spring-boot:run
cd donation-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
```

**Start api-gateway last:**
```bash
cd api-gateway
mvn spring-boot:run
```

### OR: Build all at once from root
```bash
# From fundbridge-microservices/ root:
mvn clean install -DskipTests
```

---

## 🐳 Docker Compose

```bash
# Build all services
mvn clean package -DskipTests

# Start the full stack
docker-compose up -d

# View logs
docker-compose logs -f api-gateway

# Stop everything
docker-compose down
```

---

## 🔑 JWT Authentication Flow

```
Client → POST /auth/register or /auth/login
       ← JWT token

Client → GET /loans/pending
       Authorization: Bearer <jwt>
       ↓
API Gateway → validates JWT → extracts email, role, userId
           → forwards headers:
              X-User-Email: user@example.com
              X-User-Role: ROLE_USER
              X-User-Id: 42
           ↓
Downstream Service → reads X-User-* headers (no re-auth needed)
```

---

## 🌐 API Endpoints (via API Gateway at port 8080)

### Auth Service (`/auth/**`)
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | /auth/register | ❌ | Register new user |
| POST | /auth/login | ❌ | Login, get JWT |

### User Service (`/users/**`)
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | /users/profile | ✅ | Get own profile |
| PUT | /users/profile | ✅ | Update own profile |
| GET | /users/admin/all | ✅ ADMIN | List all users |
| PATCH | /users/admin/{id}/deactivate | ✅ ADMIN | Deactivate user |

### Loan Service (`/loans/**`)
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | /loans/request | ✅ | Create loan request |
| GET | /loans/my-requests | ✅ | My loan requests |
| GET | /loans/pending | ✅ | Browse pending loans |
| GET | /loans/{id} | ✅ | Get loan details |
| POST | /loans/{id}/offer | ✅ | Submit loan offer |
| PATCH | /loans/offers/{id}/accept | ✅ | Accept loan offer |
| PATCH | /loans/offers/{id}/reject | ✅ | Reject loan offer |
| GET | /loans/{id}/repayment-schedule | ✅ | View schedule |
| PATCH | /loans/repayment/{scheduleId}/pay | ✅ | Pay installment |

### Campaign Service (`/campaigns/**`)
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | /campaigns/public/all | ❌ | List active campaigns |
| GET | /campaigns/public/{id} | ❌ | Get campaign |
| GET | /campaigns/public/search | ❌ | Search campaigns |
| POST | /campaigns | ✅ | Create campaign |
| GET | /campaigns/my | ✅ | My campaigns |
| PUT | /campaigns/{id} | ✅ | Update campaign |
| DELETE | /campaigns/{id} | ✅ | Delete campaign |
| PATCH | /campaigns/{id}/status | ✅ ADMIN | Update status |

### Donation Service (`/donations/**`)
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | /donations/campaign/{id} | ✅ | Initiate donation |
| PATCH | /donations/{id}/confirm | ✅ | Confirm payment |
| GET | /donations/campaign/{id} | ✅ | Campaign donations |
| GET | /donations/my | ✅ | My donations |

### Payment Service (`/payments/**`)
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | /payments/create-order | ✅ | Create Razorpay order |
| POST | /payments/record | ✅ | Record transaction |
| GET | /payments/history | ✅ | Transaction history |
| GET | /payments/key | ✅ | Get Razorpay public key |

### Notification Service (`/notifications/**`)
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | /notifications/send | ✅ | Send notification |
| GET | /notifications/my | ✅ | Get my notifications |
| GET | /notifications/unread | ✅ | Unread notifications |
| PATCH | /notifications/{id}/read | ✅ | Mark as read |

---

## 🔗 Inter-Service Communication (OpenFeign)

| Caller | Calls | Purpose |
|--------|-------|---------|
| donation-service | campaign-service | Verify campaign status, update raised amount |

All inter-service calls use **Eureka service names** (e.g., `lb://campaign-service`) for client-side load balancing.

---

## 📊 Database Mapping

| Service | Database | Tables |
|---------|----------|--------|
| auth-service | `fundbridge_auth` | `auth_users` |
| user-service | `fundbridge_users` | `user_profiles` |
| loan-service | `fundbridge_loans` | `loan_requests`, `loan_offers`, `repayment_schedules` |
| campaign-service | `fundbridge_campaigns` | `campaigns` |
| donation-service | `fundbridge_donations` | `donations` |
| payment-service | `fundbridge_payments` | `transactions` |
| notification-service | `fundbridge_notifications` | `notifications` |

---

## ⚙️ Environment Variables

| Variable | Default | Used By |
|----------|---------|---------|
| `DB_URL` | per-service default | All services |
| `DB_USERNAME` | `root` | All services |
| `DB_PASSWORD` | `pkd123` | All services |
| `JWT_SECRET` | `FundBridge_Super_Secret_Key_2026_MicroservicesEdition` | auth-service, api-gateway |
| `JWT_EXPIRATION_MS` | `86400000` (24h) | auth-service |
| `RAZORPAY_KEY_ID` | `rzp_test_TH8MltEKux2Yht` | payment-service |
| `RAZORPAY_KEY_SECRET` | `BxhfgTALeqnbCF6OMTXbNbIN` | payment-service |

---

## 🛠️ Key Technical Decisions

### 1. JWT at Gateway Level
JWT validation happens **only** at the API Gateway using a reactive `GlobalFilter`. Downstream services receive pre-validated `X-User-Email`, `X-User-Role`, and `X-User-Id` headers — no Spring Security overhead per service.

### 2. Database Isolation
Each service owns its own MySQL database schema. Cross-service references are stored as foreign key IDs (e.g., `borrower_id`, `campaign_id`) instead of JPA `@ManyToOne` joins.

### 3. common-lib
Shared enums, exceptions, DTOs, and `JwtUtil` are in `common-lib`. Install it first: `mvn install -pl common-lib`.

### 4. Services Trust Gateway Headers
All business services have permissive Security configs (all requests permitted). Security is enforced at the Gateway. This follows the sidecar/internal network trust model.

### 5. OpenFeign for Sync Communication
`donation-service` uses OpenFeign to call `campaign-service` to verify campaign status and update raised amounts after confirmed donations.

---

## 📋 Test API Workflow

```bash
# 1. Register
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@test.com","password":"pass123","fullName":"John Doe"}'

# 2. Login (get JWT)
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@test.com","password":"pass123"}'

# 3. Use JWT for protected endpoints
export TOKEN="<jwt from login response>"

# 4. View profile
curl http://localhost:8080/users/profile \
  -H "Authorization: Bearer $TOKEN"

# 5. Browse active campaigns (public)
curl http://localhost:8080/campaigns/public/all

# 6. Create a loan request
curl -X POST http://localhost:8080/loans/request \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"amount":50000,"tenureMonths":12,"purpose":"Business expansion"}'
```

---

## 🏃 Service Health Checks

| Service | Health URL |
|---------|-----------|
| Eureka Dashboard | http://localhost:8761 |
| API Gateway | http://localhost:8080/actuator/health |
| Auth Service | http://localhost:8081/actuator/health |
| User Service | http://localhost:8082/actuator/health |
| Loan Service | http://localhost:8083/actuator/health |
| Campaign Service | http://localhost:8084/actuator/health |
| Donation Service | http://localhost:8085/actuator/health |
| Payment Service | http://localhost:8086/actuator/health |
| Notification Service | http://localhost:8087/actuator/health |
