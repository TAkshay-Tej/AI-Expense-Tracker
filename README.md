# 💰 AI Expense Tracker

AI Expense Tracker is an Android application designed to help users track their spending, automatically categorize transactions, detect overspending patterns, and gain actionable insights to improve financial habits.

---

## 🚀 Features

### 📊 Automatic Transaction Classification
- Uses **Groq LLaMA** to categorize transactions automatically
- Eliminates manual tagging

### 🔔 AI Overspending Alerts
- Analyzes spending patterns against **30-day averages**
- Notifies users when spending exceeds typical limits

### 📈 Analytics Dashboard
- Visualizes category-wise spending
- Provides LLM-generated spending summaries and recommendations

### 🔐 Secure Data Management
- **JWT-secured REST APIs** to manage expenses and sync data
- Backend ready for containerization with **Docker Compose**

---

## 🏗️ Tech Stack

### 📱 Frontend
- Kotlin, Jetpack Compose
- Hilt Dependency Injection
- Room Database for local storage
- StateFlow for reactive UI updates

### ⚙️ Backend
- Spring Boot
- REST APIs

### 🗄️ Database
- PostgreSQL

### 🐳 DevOps
- Docker Compose for containerized backend

---

## 📋 Architecture

- Clean Architecture on Android
- MVVM pattern with Repository layer
- Backend API integration for sync and analytics

---

## 🚧 Project Status
This project is currently under development. Code and full features will be added soon.

---

## 💡 Future Improvements
- Real-time expense insights
- Budget planning and goals
- Multi-device sync with cloud backend
- Advanced AI predictions for personal finance trends

---
