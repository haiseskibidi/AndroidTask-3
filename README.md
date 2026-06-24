**Выполнил:** Копаницкий Захар Александрович
**Группа:** ПИКД 2 подгруппа

---

## API

**Название:** Shikimori API
**Ссылка:** `https://shikimori.one/api/doc/1.0`
**Что дает:** Предоставляет информацию об аниме (списки, поиск, детали, рейтинги, постеры).
**Ключ:** Не требуется (Open API).

## Запуск

1. Открыть проект в Android Studio.
2. Дождаться синхронизации Gradle.
3. Запустить на эмуляторе или реальном устройстве (Android 7.0+).
4. Интернет соединение обязательно.

## Скриншоты


| Loading | Error |
|:---:|:---:|
| <img src="screenshots/Loading.png" width="200"/> | <img src="screenshots/Error.png" width="200"/> |

| List | Detail | Favourites (Room State) |
|:---:|:---:|:---:|
| <img src="screenshots/List.png" width="200"/> | <img src="screenshots/Detail.png" width="200"/> | <img src="screenshots/Favourites%20(Room%20state).png" width="200"/> |

---

## Финальный проект — Offline-first аниме-клиент с локальными профилями

### 1. Архитектура и Чистота слоя домена (DDD)
- **Изоляция домена:** Слой домена (`domain/model`) полностью очищен от зависимостей баз данных и Room-сущностей (`UserEntity` и `AnimeNoteEntity`). Интерфейс репозитория `AnimeRepository` оперирует исключительно чистыми доменными сущностями `User` и `AnimeNote`.
- **Маппинг данных:** Конвертация между доменными сущностями (`User`, `AnimeNote`) и сущностями базы данных (`UserEntity`, `AnimeNoteEntity`) изолирована в слое данных (`Mapper.kt`).

### 2. Локальные профили и пользовательские данные
- **Многопользовательский режим:** Создана система полностью изолированных локальных профилей пользователей с хранением настроек в Jetpack DataStore и пользовательских данных в Room.
- **Новые сущности БД:**
  - `users`: Таблица профилей пользователей (`UserEntity`).
  - `recent_anime`: Таблица истории просмотров (`RecentAnimeEntity`), привязанная по внешнему ключу (`cascade delete`) к пользователю.
  - `anime_notes`: Личные заметки и оценки к аниме (`AnimeNoteEntity`), изолированные между профилями.
  - `cached_anime_details`: Локальный кэш полной информации об аниме с TTL для offline-first режима.

### 3. Экранные модули
- **Настройки ([SettingsScreen.kt](file:///C:/Users/akopa/StudioProjects/AndroidTask-3/app/src/main/java/ru/fefu/task3/ui/screens/SettingsScreen.kt)):** Управление профилями (создание, удаление с подтверждением, переключение), смена темы оформления, очистка истории просмотров.
- **История ([RecentScreen.kt](file:///C:/Users/akopa/StudioProjects/AndroidTask-3/app/src/main/java/ru/fefu/task3/ui/screens/RecentScreen.kt)):** Список недавно просмотренных аниме для текущего профиля.
- **Личные заметки ([DetailScreen.kt](file:///C:/Users/akopa/StudioProjects/AndroidTask-3/app/src/main/java/ru/fefu/task3/ui/screens/DetailScreen.kt)):** Текстовое поле ввода заметок и 5-звездочная оценка аниме.

### 4. Реактивность и Производительность
- **Debounce & flatMapLatest:** Поиск аниме оптимизирован для предотвращения race conditions. Предыдущие сетевые запросы автоматически отменяются при быстром вводе.
- **Атомарные транзакции Room:** Добавление и удаление из избранного (`toggleFavourite`) объединены в транзакции Room (`@Transaction`), исключая некорректную последовательность операций.

### 5. Безопасность и Дизайн-токены
- **Сетевая безопасность:** Создана конфигурация [network_security_config.xml](file:///C:/Users/akopa/StudioProjects/AndroidTask-3/app/src/main/res/xml/network_security_config.xml), запрещающая небезопасный cleartext (HTTP) трафик по умолчанию.
- **Компоненты манифеста:** Все Activity, кроме лаунчера `MainActivity`, закрыты для внешнего доступа (`android:exported="false"`).
- **Дизайн-токены Spacing:** Все размеры, отступы и формы UI-разметки стандартизированы через систему `MaterialTheme.spacing` (CompositionLocal), устраняя магические dp-числа в Compose.
- **Локализация:** Все строковые ресурсы и диалоговые окна вынесены в `strings.xml`.

### 6. Фоновая синхронизация (WorkManager + Hilt)
- **Hilt-воркер:** Реализован воркер `CacheSyncWorker` в [CacheSyncWorker.kt](file:///C:/Users/akopa/StudioProjects/AndroidTask-3/app/src/main/java/ru/fefu/task3/worker/CacheSyncWorker.kt), который автоматически внедряет зависимости через `@HiltWorker` и `@AssistedInject`.
- **Автоматическая инициализация:** Стандартная инициализация WorkManager отключена в манифесте, а вместо нее настроен собственный `Configuration.Provider` в [Task3App.kt](file:///C:/Users/akopa/StudioProjects/AndroidTask-3/app/src/main/java/ru/fefu/task3/Task3App.kt).
- **Задачи воркера:**
  - Раз в сутки удаляет локальный кэш деталей аниме старше 24 часов (TTL).
  - Фоново обновляет и предзагружает свежие детали из сети для всех аниме из списков избранного у всех пользователей.

### 7. Тестирование
- Добавлены тесты на логику ViewModel (выбор пользователя, смена темы, очистка истории).
- Написаны интеграционные тесты для Room (`AnimeDaoTest.kt`) и репозитория (`RepositoryIntegrationTest.kt`) с валидацией ограничений внешних ключей.

### 8. Исправления по результатам код-ревью
По результатам код-ревью были внесены следующие доработки:
- **Удаление локального мусора IDE из Git:** Настроен файл `.gitignore` для полного исключения папок `/.idea/` и `/.kotlin/`. Все ранее отслеживаемые файлы и логи локальных ошибок компилятора удалены из индекса репозитория.
- **Корректный повтор загрузки деталей аниме:** Внедрен `_detailRetryTrigger` (SharedFlow), что позволило осуществлять повторные попытки загрузки деталей аниме при возникновении сбоев сети без необходимости обнуления `animeId`.
- **Обработка ошибок базы данных:** В `AnimeViewModel.kt` добавлен метод-хелпер `launchWithDbError`, перехватывающий любые исключения при работе с Room/БД и транслирующий их через поток `errorEvents`. В `MainActivity` добавлена реактивная обработка, показывающая ошибки в виде Toast.
- **Исправление Espresso на современных Android API:** Устранена несовместимость с эмуляторами на базе Android 16 (API 36) и выше. Удалены временные заглушки тестирования (`ExampleInstrumentedTest.kt`, `StubViewMatchers.kt`) и принудительное исключение библиотеки Espresso в `build.gradle.kts`. Тестовые зависимости (`espresso-core` обновлена до `3.7.0`, `runner` и `rules` обновлены до `1.7.0`) успешно разрешают внутреннюю рефлексию `InputManager.getInstance()` и обеспечивают полноценное прохождение всех 8 инструментальных Compose-тестов.
