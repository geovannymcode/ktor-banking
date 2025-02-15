<p align="center">
  <a href="#"><img src="https://img.shields.io/badge/Ktor-2.2.4-brightgreen" alt="Ktor"></a>
  <a href="#"><img src="https://img.shields.io/badge/Kotlin-1.8.10-blue" alt="Kotlin"></a>
  <a href="#"><img src="https://img.shields.io/badge/Database-H2-orange" alt="H2 Database"></a>
  <a href="#"><img src="https://img.shields.io/badge/Exposed-DAO-red" alt="Exposed"></a>
  <a href="#"><img src="https://img.shields.io/badge/Testing-JUnit5-purple" alt="JUnit 5"></a>
  <a href="#"><img src="https://img.shields.io/badge/DI-Koin-green" alt="Koin"></a>
</p>

# Ktor Banking

## 📌 Overview
Ktor Banking is a backend application built using [Ktor](https://ktor.io/) to handle banking transactions. It leverages **H2 Database**, **Exposed ORM**, **Koin Dependency Injection**, and **JUnit 5** for testing.

## 📂 Project Structure
```plaintext
ktor-banking/
│── src/
│   ├── main/
│   │   ├── kotlin/com/geovannycode/
│   │   │   ├── config/
│   │   │   │   ├── ApplicationConfiguration.kt
│   │   │   ├── di/
│   │   │   │   ├── KoinModule.kt
│   │   │   ├── dto/
│   │   │   │   ├── AccountDto.kt
│   │   │   │   ├── AccountOverviewDto.kt
│   │   │   │   ├── ApiResult.kt
│   │   │   │   ├── UserDto.kt
│   │   │   │   ├── UserOverviewDto.kt
│   │   │   ├── entities/
│   │   │   │   ├── account/
│   │   │   │   │   ├── AccountEntity.kt
│   │   │   │   ├── administrator/
│   │   │   │   │   ├── AdministratorEntity.kt
│   │   │   │   ├── transaction/
│   │   │   │   │   ├── TransactionEntity.kt
│   │   │   │   ├── user/
│   │   │   │   │   ├── UserEntity.kt
│   │   │   │   ├── DatabaseFactory.kt
│   │   │   ├── repository/
│   │   │   │   ├── AccountRepository.kt
│   │   │   │   ├── DefaultAccountRepository.kt
│   │   │   │   ├── DefaultTransactionRepository.kt
│   │   │   │   ├── DefaultUserRepository.kt
│   │   │   │   ├── TransactionRepository.kt
│   │   │   │   ├── UserRepository.kt
│   │   │   ├── service/
│   │   │   │   ├── AccountService.kt
│   │   │   │   ├── DefaultAccountService.kt
│   │   │   │   ├── DefaultUserService.kt
│   │   │   │   ├── UserService.kt
│   │   │   ├── Application.kt
│   ├── resources/
│   │   ├── db/
│   │   ├── application.conf
│   │   ├── logback.xml
│   ├── test/
│── .gitignore
│── LICENSE
│── README.md
│── build.gradle.kts
│── gradle.properties
│── settings.gradle.kts
```

## 🚀 Technologies Used
- **Kotlin** (`1.8.10`)
- **Ktor** (`2.2.4`)
- **Logback** (`1.4.6`)
- **Exposed ORM** (`0.39.2`)
- **H2 Database** (`2.1.212`)
- **Liquibase** (`4.20.0`)
- **Koin Dependency Injection** (`3.2.0`)
- **JUnit 5** (`5.9.0`)

## ▶️ Running the Application
To start the project, run:
```sh
./gradlew run
```
This will launch the Ktor server using Netty as the engine.

## 🛠️ Environment Configuration
The application checks for a development environment flag:
```kotlin
val isDevelopment: Boolean = project.ext.has("development")
applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
```

## 🗄️ Database Setup
- **Exposed ORM:** Used for interacting with the database.
- **Liquibase:** Handles database migrations.
- **H2 Database:** Used as an in-memory database for local development and testing.

## 🧪 Testing
Run the tests using:
```sh
./gradlew test
```
This will execute the unit tests configured for the project.

## 📜 License
This project is open-source and distributed under the MIT License.

