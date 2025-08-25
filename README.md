# MovieStash

### Описание

Демонстрационное приложение сервиса для просмотра информации о кинематографе. Позволяет просматривать
фильмы, сериалы и мультфильмы, оставлять отзывы и оценки, а также создавать собственные подборки. 
Для получения информации приложение взаимодействует с внешним API, описание которого можно найти 
[здесь](https://gitlab.com/SharpRainbow/MovieStashApi).

### Технологии

Приложение разработано с использованием актуальных технологий и рекомендованных практик:
- Clean architecture с разделением на слои (presentation, domain, data)
- Single Activity Architecture с использованием Jetpack Navigation Component и Safe Args
- Android Jetpack компоненты (ViewModel, Room и др.)
- Kotlin Coroutines и Flow для фоновых задач
- Dependency Injection с помощью Dagger2
- Retrofit для сетевых запросов и Glide для загрузки изображений
