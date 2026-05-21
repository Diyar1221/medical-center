# Медицинский центр — Система управления

REST API для управления медицинским центром: пациенты, врачи, записи на приём, медицинские карты.

## Технологии

- Java 17 + Spring Boot 3.2
- Spring Security + JWT (Cookie)
- PostgreSQL
- Maven
- Swagger / OpenAPI
- Apache POI (XLSX экспорт)
- Telegram Bot (уведомления)

## Запуск

### 1. Создать базу данных

```sql
CREATE DATABASE medical_center;
```

Выполнить `src/main/resources/schema.sql` в PostgreSQL.

### 2. Настроить application.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/medical_center
    username: postgres
    password: ваш_пароль

telegram:
  bot:
    token: ВАШ_ТОКЕН
    username: ВАШ_БОТ
    chat-id: ВАШ_CHAT_ID
```

### 3. Запустить

```bash
mvn spring-boot:run
```

## Доступ

- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html

## Авторизация

POST `/api/auth/login` с телом:
```json
{ "username": "admin", "password": "admin123" }
```

## API Endpoints

### Пациенты `/api/patients`
| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/patients` | Все пациенты |
| GET | `/api/patients/{id}` | Пациент по ID |
| GET | `/api/patients/search?name=Иван` | Поиск |
| POST | `/api/patients` | Создать пациента |
| PUT | `/api/patients/{id}` | Обновить |
| DELETE | `/api/patients/{id}` | Удалить |

### Врачи `/api/doctors`
| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/doctors` | Все врачи |
| GET | `/api/doctors/{id}` | Врач по ID |
| GET | `/api/doctors/search?name=Петров` | Поиск |
| GET | `/api/doctors/by-specialization/{id}` | По специализации |
| POST | `/api/doctors` | Создать врача |
| PUT | `/api/doctors/{id}` | Обновить |
| DELETE | `/api/doctors/{id}` | Удалить |

### Записи на приём `/api/appointments`
| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/appointments` | Все записи |
| GET | `/api/appointments/schedule?doctorId=1&date=2024-12-01` | Расписание врача |
| GET | `/api/appointments/by-status?status=SCHEDULED` | По статусу |
| POST | `/api/appointments` | Создать запись |
| PATCH | `/api/appointments/{id}/status?status=COMPLETED` | Изменить статус |
| DELETE | `/api/appointments/{id}` | Удалить |

### Медицинские карты `/api/medical-records`
| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/medical-records/by-patient/{id}` | История пациента |
| POST | `/api/medical-records` | Создать карту |
| PUT | `/api/medical-records/{id}` | Обновить |

### Отчёты `/api/reports`
| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/reports/stats` | Общая статистика |
| GET | `/api/reports/appointments/export?from=...&to=...` | XLSX записи |
| GET | `/api/reports/patients/export` | XLSX пациенты |

## Модели

```
Patient ←──── Appointment ────→ Doctor ────→ Specialization
   └──────────────────────────────┘
         MedicalRecord
```

## Уведомления в Telegram

Автоматические уведомления при:
- Создании новой записи на приём
- Отмене записи
- Завершении приёма
