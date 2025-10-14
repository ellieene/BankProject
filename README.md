# Bank Project 🏦

Система управления банковскими картами с авторизацией и безопасным доступом. Проект предоставляет REST API для управления пользователями, банковскими картами и операциями перевода средств.

## 🚀 Технологии

- **Java** + **Spring Boot**
- **Spring Security** + **JWT**
- **Spring Data JPA**
- **PostgreSQL**
- **Liquibase** (миграции БД)
- **ModelMapper**
- **OpenAPI 3.0** (документация API)
- **Docker** (контейнеризация)
- **Lombok**

### Модели данных
- **User** - пользователь системы (логин, email, ФИО, статус, роль)
- **Card** - банковская карта (номер, срок действия, баланс, статус)
- **CardBlockRequest** - запрос на блокировку карты

### Сервисы
- **AuthService** - регистрация, аутентификация, JWT-токены
- **UserService** - управление пользователями
- **CardService** - управление банковскими картами и операциями

## 🔑 Роли пользователей

- **USER** - базовые операции со своими картами
- **ADMIN** - полный доступ ко всем функциям системы

## 📡 API Endpoints

### 🔐 Аутентификация (`/auth`)

| Метод | Endpoint | Описание | Доступ |
|-------|----------|-----------|---------|
| POST | `/auth/registration` | Регистрация нового пользователя | PUBLIC |
| POST | `/auth/authorization` | Авторизация, получение JWT токена | PUBLIC |

### 💳 Управление картами (`/card`)

| Метод | Endpoint | Описание | Доступ |
|-------|----------|-----------|---------|
| GET | `/card` | Получить все карты пользователей | ADMIN |
| POST | `/card/create` | Создать новую карту | ADMIN |
| PUT | `/card/edit/{cardId}` | Изменить карту | ADMIN |
| PUT | `/card/activate/{cardId}` | Активировать карту | ADMIN |
| DELETE | `/card/delete/{cardId}` | Удалить карту | ADMIN |
| GET | `/card/balance/{cardId}` | Получить баланс карты | USER, ADMIN |
| POST | `/card/transfer` | Перевод между картами | USER, ADMIN |
| POST | `/card/block-request/{cardId}` | Запрос на блокировку карты | USER, ADMIN |
| GET | `/card/block-requests/pending` | Ожидающие запросы на блокировку | ADMIN |
| PUT | `/card/block-requests/{requestId}` | Обработать запрос на блокировку | ADMIN |
| GET | `/card/search` | Поиск карт по последним 4 цифрам | USER, ADMIN |

### 👥 Управление пользователями (`/user-management`)

| Метод | Endpoint | Описание | Доступ |
|-------|----------|-----------|---------|
| PUT | `/user-management/{userId}` | Изменить пользователя | ADMIN |
| DELETE | `/user-management/{userId}` | Удалить пользователя | ADMIN |
| PUT | `/user-management/blocke-user/{userId}` | Заблокировать пользователя | ADMIN |

## 🛡️ Безопасность

- **JWT аутентификация** - токены для доступа к API
- **Шифрование данных** - номера карт и пароли шифруются в БД
- **Ролевая модель** - разделение прав USER/ADMIN
- **Валидация** - Hibernate Validator для проверки входных данных

## 💾 База данных

- **Production**: PostgreSQL
- **Миграции**: Liquibase

### Основные таблицы
- `user` - пользователи системы
- `card` - банковские карты
- `card_block_request` - запросы на блокировку карт

## 🔧 Запуск проекта

### Требования
- Docker
- Docker Compose

### Запуск
```bash
./run.sh
```
### Swagger
http://localhost:8080/swagger-ui/index.html#/# BankProject
