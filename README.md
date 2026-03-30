# 🚀 NeuralRide AI - Intelligent Ride-Hailing Platform

> **Next-Generation Ride Booking Powered by Spring AI, Vector Embeddings & Conversational Intelligence**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.1.2-blue.svg)](https://spring.io/projects/spring-ai)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-PGVector-blue.svg)](https://github.com/pgvector/pgvector)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 🧠 Overview

**NeuralRide AI** is an enterprise-grade, AI-powered ride-hailing platform that revolutionizes the traditional ride-booking experience through advanced conversational AI, semantic search, and intelligent function calling. Built on **Spring AI**, this platform enables riders and drivers to interact naturally using AI assistants that understand context, remember conversations, and execute complex operations through natural language.

### 🎯 What Makes This AI-Powered?

Unlike traditional ride-booking apps with rigid UI flows, NeuralRide AI leverages:

- **🤖 Conversational AI Agents** - Natural language interfaces for riders and drivers powered by Groq's LLaMA 3.3 70B
- **🧠 Long-Term Memory** - Vector embeddings stored in PGVector enable semantic search across ride history
- **💾 Persistent Chat Memory** - JDBC-backed conversation history maintains context across sessions
- **🔧 AI Function Calling** - LLM autonomously invokes backend services (book rides, check status, rate drivers)
- **📊 Semantic Search** - Jina AI embeddings (1024-dimensional) power intelligent context retrieval
- **🎭 Role-Based AI Assistants** - Specialized agents for riders and drivers with domain-specific capabilities


---

## 🏗️ Architecture Highlights

### Spring AI Integration

This project showcases production-ready **Spring AI** implementation with:

```
┌─────────────────────────────────────────────────────────────┐
│                    Spring AI Framework                      │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │  Chat Model  │  │  Embeddings  │  │ Vector Store │       │
│  │  (Groq LLM)  │  │  (Jina AI)   │  │  (PGVector)  │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
│         │                  │                  │             │
│         ▼                  ▼                  ▼             │
│  ┌──────────────────────────────────────────────────┐       │
│  │         ChatClient with Advisors                 │       │
│  │  • MessageChatMemoryAdvisor (JDBC)               │       │
│  │  • Function Calling (RiderTools/DriverTools)     │       │
│  │  • RAG Pipeline (Vector Search + Context)        │       │
│  └──────────────────────────────────────────────────┘       │
└─────────────────────────────────────────────────────────────┘
```

### Core AI Components


#### 1. **Conversational AI Services**

- **`RiderAiChatService`** - AI assistant for riders with capabilities:
  - Book rides via natural language
  - Cancel rides
  - Check ride status
  - View ride history with semantic search
  - Rate drivers
  - Check wallet balance

- **`DriverAiChatService`** - AI assistant for drivers with capabilities:
  - Accept ride requests
  - Start/end rides with OTP verification
  - Update availability status
  - View earnings and ride history
  - Rate riders

#### 2. **Vector Embeddings & Semantic Search**

**`RideVectorService`** implements RAG (Retrieval Augmented Generation):

```java
// Store ride information as vector embeddings
storeRideInfo(rideId, userId, description, metadata)

// Semantic search across conversation history
searchRelevantContext(userId, query, maxResults)

// Store conversation context for long-term memory
storeConversationContext(conversationId, userId, context)
```

**Technology Stack:**
- **Jina AI Embeddings** - `jina-embeddings-v5-text-small` (1024 dimensions)
- **PGVector** - PostgreSQL extension for vector similarity search
- **COSINE_DISTANCE** - Similarity metric for semantic matching


#### 3. **AI Function Calling (Tools)**

Spring AI's function calling enables the LLM to autonomously invoke backend services:

**RiderTools:**
- `requestRide()` - Book rides with coordinates and payment method
- `cancelRide()` - Cancel confirmed rides
- `getRideStatus()` - Check ride details
- `getMyRides()` - View ride history
- `getWalletBalance()` - Check wallet balance
- `rateDriver()` - Rate completed rides

**DriverTools:**
- `acceptRide()` - Accept incoming ride requests
- `startRide()` - Start ride with OTP verification
- `endRide()` - Complete ride and process payment
- `updateAvailability()` - Toggle online/offline status
- `rateRider()` - Rate completed rides

#### 4. **Persistent Chat Memory**

**JDBC-backed chat memory** maintains conversation context across sessions:

```yaml
spring:
  ai:
    chat:
      memory:
        repository:
          jdbc:
            initialize-schema: always
            table-name: chat_memory
```

The `MessageChatMemoryAdvisor` automatically:
- Stores user messages and AI responses
- Retrieves conversation history by `conversationId`
- Maintains context for multi-turn conversations


#### 5. **Intelligent Context Retrieval**

The system intelligently determines when to use vector search:

```java
private boolean isHistoricalQuery(String message) {
    // Historical queries: "What rides did I take last week?"
    // Action commands: "Book a ride to the airport"
    
    // Only searches vector store for historical questions
    // Skips vector search for action commands (performance optimization)
}
```

**Smart RAG Pipeline:**
1. User sends message → Stored as embedding
2. System detects query type (historical vs action)
3. If historical → Semantic search retrieves relevant past context
4. Context + Query → Enhanced prompt to LLM
5. AI response → Stored as embedding for future retrieval

---

## 🚀 Features

### For Riders
- 🗣️ **Natural Language Booking** - "Book me a ride from 28.6139, 77.2090 to 28.5562, 77.1000 using wallet"
- 📊 **Semantic Ride History** - "Show me all rides I took last month"
- 💰 **Wallet Management** - Check balance and transaction history
- ⭐ **Driver Ratings** - Rate drivers after completed rides
- 🔍 **Intelligent Search** - Ask about past rides using natural language

### For Drivers
- 🎯 **Smart Ride Acceptance** - AI suggests optimal rides based on location
- 🔐 **OTP Verification** - Secure ride start with rider verification
- 💵 **Earnings Tracking** - View ride history with earnings breakdown
- 📈 **Availability Management** - Toggle online/offline status via chat
- ⭐ **Rider Ratings** - Rate riders after completed rides


### Backend Features
- 🔐 **JWT Authentication** - Secure role-based access control
- 🗺️ **PostGIS Spatial Queries** - Geospatial ride matching
- 💳 **Dual Payment Methods** - Cash and wallet support
- 📍 **Real-time Location Tracking** - Driver-rider matching
- 🔄 **State Machine** - Robust ride lifecycle management
- 📊 **Swagger API Documentation** - Interactive API explorer

---

## 🛠️ Technology Stack

### Core Framework
- **Spring Boot 3.5.10** - Modern Java framework
- **Spring AI 1.1.2** - AI integration framework
- **Java 21** - Latest LTS version with virtual threads

### AI & ML
- **Groq Cloud** - LLaMA 3.3 70B Versatile (ultra-fast inference)
- **Jina AI** - `jina-embeddings-v5-text-small` (1024-dim, FREE 1M tokens/month)
- **PGVector** - PostgreSQL vector extension for similarity search
- **Spring AI ChatClient** - Unified AI client with advisors

### Database
- **PostgreSQL 16+** - Primary database with PGVector extension
- **PostGIS** - Spatial database for geolocation queries
- **Hibernate Spatial** - JPA support for spatial types


### Security & Authentication
- **Spring Security** - Role-based access control (RIDER/DRIVER)
- **JWT (JJWT 0.12.6)** - Stateless authentication
- **BCrypt** - Password hashing

### Additional Tools
- **Lombok** - Boilerplate reduction
- **ModelMapper** - DTO mapping
- **Springdoc OpenAPI** - API documentation
- **Spring Boot Actuator** - Health monitoring

---

## 📦 Installation & Setup

### Prerequisites

- **Java 21+** (OpenJDK or Eclipse Temurin)
- **Maven 3.8+**
- **PostgreSQL 16+** with **PGVector** extension
- **Docker** (optional, for containerized deployment)

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/neuralride-ai.git
cd neuralride-ai
```

### 2. Setup PostgreSQL with PGVector

```sql
-- Create database
CREATE DATABASE uber_db;

-- Connect to database
\c uber_db

-- Enable PostGIS extension
CREATE EXTENSION IF NOT EXISTS postgis;

-- Enable PGVector extension
CREATE EXTENSION IF NOT EXISTS vector;
```


### 3. Configure Environment Variables

Create a `.env` file or set environment variables:

```bash
# Database Configuration
export DB_HOST_URL=localhost
export DB_NAME=uber_db
export DB_USERNAME=postgres
export DB_PASSWORD=your_password

# AI API Keys
export GROQ_API_KEY=your_groq_api_key          # Get from: https://console.groq.com
export JINA_API_KEY=your_jina_api_key          # Get from: https://jina.ai/embeddings/

# JWT Secret
export JWT_SECRET_KEY=your_super_secret_jwt_key_min_256_bits
```

**Get Your Free API Keys:**
- **Groq Cloud**: [https://console.groq.com](https://console.groq.com) - Free tier with fast inference
- **Jina AI**: [https://jina.ai/embeddings/](https://jina.ai/embeddings/) - 1M free tokens/month

### 4. Build and Run

```bash
cd uber
./mvnw clean install
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### 5. Docker Deployment (Optional)

```bash
# Build Docker image
docker build -t neuralride-ai:latest .

# Run container
docker run -p 8080:8080 \
  -e DB_HOST_URL=host.docker.internal \
  -e DB_NAME=uber_db \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=your_password \
  -e GROQ_API_KEY=your_groq_key \
  -e JINA_API_KEY=your_jina_key \
  -e JWT_SECRET_KEY=your_jwt_secret \
  neuralride-ai:latest
```


---

## 🎮 API Usage

### Authentication

#### 1. Signup (Rider)
```bash
POST /auth/signup
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "RIDER"
}
```

#### 2. Login
```bash
POST /auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}

# Response
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### AI Chat Endpoints

#### Rider AI Chat
```bash
POST /riders/ai/chat
Authorization: Bearer <token>
Content-Type: application/json

{
  "message": "Book me a ride from 28.6139, 77.2090 to 28.5562, 77.1000 using wallet",
  "conversationId": "optional-conversation-id"
}

# Response
{
  "response": "Ride booked successfully! Ride Request ID: 123, Fare: ₹450.00, Status: PENDING, Payment: WALLET",
  "conversationId": "550e8400-e29b-41d4-a716-446655440000"
}
```


#### Driver AI Chat
```bash
POST /drivers/ai/chat
Authorization: Bearer <token>
Content-Type: application/json

{
  "message": "Set me online and show my profile",
  "conversationId": "driver-session-123"
}

# Response
{
  "response": "Availability updated successfully! You are now ONLINE and ready to accept rides.\n\nYour Profile — Name: Jane Driver, Rating: 4.8, Vehicle: KA-01-AB-1234, Available: Yes",
  "conversationId": "driver-session-123"
}
```

### Example Conversations

**Rider:**
```
User: "Show me my last 3 rides"
AI: "Your recent rides:
- Ride #45 | Status: ENDED | Fare: ₹320.00 | Payment: WALLET
- Ride #44 | Status: ENDED | Fare: ₹180.00 | Payment: CASH
- Ride #43 | Status: CANCELLED | Fare: ₹0.00 | Payment: WALLET
Total rides: 45"

User: "How much did I spend on rides last week?"
AI: [Searches vector store for past rides] "Based on your ride history, you spent approximately ₹2,450 on rides last week across 8 trips."
```

**Driver:**
```
User: "Accept ride request 789"
AI: "Ride request accepted! Ride ID: 789, Fare: ₹450.00, Status: CONFIRMED, OTP: 1234"

User: "Start ride 789 with OTP 1234"
AI: "Ride started successfully! Ride ID: 789, Status: ONGOING, Fare: ₹450.00"
```


---

## 🧪 API Documentation

Once the application is running, access:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **Health Check**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

---

## 🗄️ Database Schema

### Core Tables
- `users` - User accounts (riders and drivers)
- `riders` - Rider profiles with ratings
- `drivers` - Driver profiles with vehicle info and availability
- `rides` - Completed rides with fare and payment details
- `ride_requests` - Pending ride requests with pickup/dropoff locations
- `wallets` - User wallet balances
- `wallet_transactions` - Transaction history
- `payments` - Payment records
- `ratings` - Rider-driver ratings

### AI-Specific Tables
- `vector_store` - Embeddings for semantic search (PGVector)
- `chat_memory` - Conversation history (Spring AI JDBC)

---

## 🔧 Configuration Deep Dive

### Spring AI Configuration (`application.yml`)

```yaml
spring:
  ai:
    openai:
      # Chat Model (Groq Cloud)
      api-key: ${GROQ_API_KEY}
      base-url: https://api.groq.com/openai
      chat:
        options:
          model: llama-3.3-70b-versatile
      
      # Embedding Model (Jina AI)
      embedding:
        enabled: true
        api-key: ${JINA_API_KEY}
        base-url: https://api.jina.ai/v1
        options:
          model: jina-embeddings-v5-text-small
    
    # Vector Store (PGVector)
    vectorstore:
      pgvector:
        initialize-schema: true
        dimensions: 1024
        distance-type: COSINE_DISTANCE
    
    # Chat Memory (JDBC)
    chat:
      memory:
        repository:
          jdbc:
            initialize-schema: always
            table-name: chat_memory
```


### Custom Jina AI Embedding Configuration

The project uses a custom `JinaAiEmbeddingModel` implementation:

```java
@Configuration
public class JinaEmbeddingConfig {
    @Bean
    public EmbeddingModel embeddingModel() {
        return new JinaAiEmbeddingModel(jinaApiKey, jinaModel);
    }
}
```

**Why Jina AI?**
- ✅ **FREE** - 1M tokens/month (sufficient for most applications)
- ✅ **Latest Model** - `jina-embeddings-v5-text-small` (released Feb 2026)
- ✅ **High Quality** - 1024-dimensional embeddings
- ✅ **Fast** - Optimized for production use
- ✅ **No Rate Limits** - Within free tier

---

## 🎯 How AI Powers This Platform

### 1. **Natural Language Understanding**

The LLM understands user intent and extracts structured data:

```
User: "Book a ride from Connaught Place to Airport using wallet"
↓
AI extracts:
- Action: Book ride
- Pickup: Connaught Place (needs coordinates)
- Dropoff: Airport (needs coordinates)
- Payment: WALLET
↓
AI responds: "Please provide coordinates for Connaught Place and Airport"
```

### 2. **Function Calling (Tool Use)**

Spring AI's function calling enables autonomous service invocation:

```java
@Tool(description = "Book a ride for the rider...")
public String requestRide(
    @ToolParam(description = "Pickup latitude") double pickupLat,
    @ToolParam(description = "Pickup longitude") double pickupLon,
    // ... other params
) {
    // Backend service call
    RideRequestDto result = riderService.requestRide(rideRequestDto);
    return "Ride booked successfully!...";
}
```

The LLM automatically:
1. Detects user wants to book a ride
2. Extracts parameters from conversation
3. Calls `requestRide()` function
4. Returns formatted response


### 3. **Vector Embeddings & Semantic Search**

Every interaction is stored as embeddings for future retrieval:

```java
// Store ride information
String description = "Rider John booked a ride from (28.6139, 77.2090) 
                      to (28.5562, 77.1000) for ₹450.00 using WALLET";
rideVectorService.storeRideInfo(rideId, userId, description, metadata);

// Later, semantic search retrieves relevant context
String context = rideVectorService.searchRelevantContext(
    userId, 
    "How much did I spend last week?", 
    maxResults=3
);
```

**Benefits:**
- 🔍 Semantic search (not just keyword matching)
- 📊 Analyze spending patterns
- 🕐 Time-based queries ("last week", "yesterday")
- 🗺️ Location-based queries ("rides to airport")

### 4. **Long-Term Memory**

Two-tier memory system:

**Short-term (Chat Memory):**
- Stores conversation turns in JDBC
- Maintains context within a session
- Enables multi-turn conversations

**Long-term (Vector Store):**
- Stores all ride data as embeddings
- Enables semantic search across all history
- Powers analytics and insights

```
User: "Book a ride to the airport"
AI: [Uses chat memory] "Sure! Which airport? Last time you went to 
     Terminal 3 of IGI Airport."
     [Retrieved from vector store]
```


### 5. **Intelligent Context Switching**

The system optimizes performance by detecting query types:

```java
private boolean isHistoricalQuery(String message) {
    // Historical: "What rides did I take?"
    // Action: "Book a ride now"
    
    // Only searches vector store for historical queries
    // Skips expensive vector search for action commands
}
```

**Performance Optimization:**
- ⚡ Action commands bypass vector search (faster response)
- 🔍 Historical queries use semantic search (better context)
- 💰 Reduces embedding API costs

---

## 📊 Project Structure

```
uber/
├── src/main/java/com/chinuthon/project/uber/uber/
│   ├── ai/
│   │   ├── service/
│   │   │   ├── RiderAiChatService.java      # Rider AI assistant
│   │   │   ├── DriverAiChatService.java     # Driver AI assistant
│   │   │   └── RideVectorService.java       # Vector store operations
│   │   └── tools/
│   │       ├── RiderTools.java              # Rider function calling
│   │       └── DriverTools.java             # Driver function calling
│   ├── configs/
│   │   ├── JinaEmbeddingConfig.java         # Custom Jina AI config
│   │   ├── JinaAiEmbeddingModel.java        # Jina AI implementation
│   │   ├── SecurityConfig.java              # Spring Security
│   │   └── WebSecurityConfig.java           # JWT configuration
│   ├── controllers/
│   │   ├── RiderAiController.java           # /riders/ai/chat
│   │   ├── DriverAiController.java          # /drivers/ai/chat
│   │   ├── RiderController.java             # Traditional REST APIs
│   │   └── DriverController.java            # Traditional REST APIs
│   ├── entities/                            # JPA entities
│   ├── repositories/                        # Spring Data JPA
│   ├── services/                            # Business logic
│   ├── dto/                                 # Data transfer objects
│   └── advices/                             # Global exception handling
└── src/main/resources/
    └── application.yml                      # Spring AI configuration
```


---

## 🚦 Ride Lifecycle

```
RIDER                          SYSTEM                         DRIVER
  │                              │                              │
  │ "Book ride to airport"       │                              │
  ├─────────────────────────────>│                              │
  │                              │                              │
  │                              │ Create RIDE_REQUEST          │
  │                              │ Status: PENDING              │
  │                              │                              │
  │                              │ Match nearby drivers         │
  │                              │                              │
  │                              │<─────────────────────────────┤
  │                              │ "Accept ride 123"            │
  │                              │                              │
  │                              │ Status: CONFIRMED            │
  │                              │ Generate OTP: 1234           │
  │                              │                              │
  │<─────────────────────────────┤                              │
  │ "Ride confirmed! OTP: 1234"  │                              │
  │                              │                              │
  │                              │<─────────────────────────────┤
  │                              │ "Start ride 123 OTP 1234"    │
  │                              │                              │
  │                              │ Verify OTP                   │
  │                              │ Status: ONGOING              │
  │                              │                              │
  │                              │<─────────────────────────────┤
  │                              │ "End ride 123"               │
  │                              │                              │
  │                              │ Status: ENDED                │
  │                              │ Process payment              │
  │                              │ Update wallets               │
  │                              │                              │
  │<─────────────────────────────┤─────────────────────────────>│
  │ "Rate driver"                │                "Rate rider"  │
```


---

## 🔐 Security

### Authentication Flow

1. **Signup** → User registers with email/password
2. **Login** → Returns JWT access token
3. **API Calls** → Include `Authorization: Bearer <token>` header
4. **Role-Based Access** → `@Secured("ROLE_RIDER")` / `@Secured("ROLE_DRIVER")`

### Security Features

- ✅ **BCrypt Password Hashing**
- ✅ **JWT Stateless Authentication**
- ✅ **Role-Based Authorization** (RIDER/DRIVER)
- ✅ **CORS Configuration**
- ✅ **SQL Injection Prevention** (JPA/Hibernate)
- ✅ **XSS Protection** (Spring Security defaults)

---

## 🧪 Testing

### Test AI Chat (Rider)

```bash
# 1. Signup as rider
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Rider",
    "email": "rider@test.com",
    "password": "password123",
    "role": "RIDER"
  }'

# 2. Login
TOKEN=$(curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "rider@test.com",
    "password": "password123"
  }' | jq -r '.accessToken')

# 3. Chat with AI
curl -X POST http://localhost:8080/riders/ai/chat \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Show me my profile and wallet balance"
  }'
```


---

## 🎓 Learning Resources

### Spring AI Documentation
- [Spring AI Reference](https://docs.spring.io/spring-ai/reference/)
- [ChatClient API](https://docs.spring.io/spring-ai/reference/api/chatclient.html)
- [Function Calling](https://docs.spring.io/spring-ai/reference/api/functions.html)
- [Vector Stores](https://docs.spring.io/spring-ai/reference/api/vectordbs.html)

### AI Models Used
- [Groq Cloud](https://console.groq.com/docs/quickstart) - LLaMA 3.3 70B
- [Jina AI Embeddings](https://jina.ai/embeddings/) - Latest embedding models
- [PGVector](https://github.com/pgvector/pgvector) - PostgreSQL vector extension

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- **Spring AI Team** - For the excellent AI integration framework
- **Groq** - For providing ultra-fast LLM inference
- **Jina AI** - For free, high-quality embedding models
- **PostgreSQL & PGVector** - For robust vector storage

---

## 📧 Contact

**Project Maintainer**: [Your Name]
- Email: your.email@example.com
- GitHub: [@yourusername](https://github.com/yourusername)
- LinkedIn: [Your Profile](https://linkedin.com/in/yourprofile)

---

## 🌟 Star History

If you find this project useful, please consider giving it a ⭐ on GitHub!

---

**Built with ❤️ using Spring AI, LLaMA 3.3, and Vector Embeddings**

