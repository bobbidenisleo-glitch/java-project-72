FROM eclipse-temurin:21-jre

WORKDIR /app

# Копируем содержимое папки app (где лежат gradlew и build.gradle.kts)
COPY app /app

# Даём права на выполнение gradlew
RUN chmod +x gradlew

# Собираем JAR файл
RUN ./gradlew shadowJar

# Запускаем приложение
CMD ["java", "-jar", "build/libs/app-all.jar"]
