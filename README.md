# TGparser — Telegram price-tracking bot

Telegram-бот на Spring Boot для отслеживания цен на товары **Wildberries** и **NicePrice62**. Бот раз в сутки проверяет цену по сохранённым артикулам и присылает уведомление в чат, если цена изменилась.

## Возможности

- Добавление товара на отслеживание по артикулу Wildberries (на товары без указания размера) и NicePrice — раздельно
- Ежедневная автоматическая проверка цены по расписанию
- Уведомление в Telegram при повышении/понижении цены
- Удаление товара из отслеживания
- Хранение отслеживаемых товаров в SQLite, привязка к конкретному чату

## Команды бота

| Команда | Описание                                     |
|---|----------------------------------------------|
| `/start` | Приветственное сообщение                     |
| `/help` | Список доступных команд                      |
| `/checkwildberries <артикул>` | Добавить товар с Wildberries на отслеживание |
| `/checkniceprice <артикул>` | Добавить товар с NicePrice62 на отслеживание |
| `/delete <артикул>` | Убрать товар из отслеживания                 |

## Технологии

- **Java 25**, **Spring Boot 4**
- **Spring Data JPA** + **SQLite** (`sqlite-jdbc`, `hibernate-community-dialects`)
- [**TelegramBots**](https://github.com/telegram-wrapper/TelegramBots) (long polling) — `telegrambots-springboot-longpolling-starter`
- **Jackson** — разбор JSON-ответов API Wildberries/NicePrice
- **Jsoup**, **Spring Scheduling** (`@Scheduled`) — ежедневная проверка цен (по умолчанию в 12:00)
- **Maven** (сборка через `mvnw`)

## Структура проекта

```
src/main/java/com/github/kirekq/pricebot/
├── BotConfigurator.java       # конфигурация бота
├── BotInitializer.java        # приём и маршрутизация сообщений Telegram
├── Main.java                  # точка входа Spring Boot
├── command/                   # реализация команд бота
│   ├── BotCommand.java        # интерфейс для команд бота
│   ├── CheckNicePrice.java    # команда отслеживания цены товара с NicePrice62
│   ├── CheckWildberries.java  # команда отслеживания цены товара с Wildberries
│   ├── DeleteCommand.java     # команда удаления артикула из отслеживаемых(Wildberries/NicePrice62)
│   ├── HelpCommand.java       # команда help
│   └── StartCommand.java      # сообщение при старте
├── data/                      # сущности, репозитории, планировщик
│   ├── PriceScheduler.java    # класс, проверяющий цены на артикулы каждый день
│   ├── Product.java           # таблица для товаров с NicePrice62
│   ├── ProductRepository.java # таблица для товаров с Wildberries
│   ├── ProductRepositoryWildberries.java # репозиторий таблицы Wildberries
│   └── ProductWildberries.java # репозиторий таблицы NicePrice62
└── parse/                     # парсинг цен с внешних API
    ├── ParseNicePrice.java    # класс, получающий цены на товары с NicePrice62
    └── ParseWildberries.java  # класс, получающий цены на товары с Wildberries
```

## Запуск локально

**Требования:** JDK 25+, токен Telegram-бота от [@BotFather](https://t.me/BotFather).

```bash
git clone https://github.com/Kirekq/TGparser.git
cd TGparser
chmod +x mvnw

export BOT_TOKEN=твой_токен_бота
./mvnw spring-boot:run
```

Токен бота читается из переменной окружения `BOT_TOKEN` (см. `application.properties`, `telegram.bot.token=${BOT_TOKEN}`). База данных SQLite создаётся автоматически в рабочей директории (`pricebot.db`).

## Сборка jar-файла

```bash
./mvnw clean package -DskipTests
java -jar target/pricebot-0.0.1-SNAPSHOT.jar
```

