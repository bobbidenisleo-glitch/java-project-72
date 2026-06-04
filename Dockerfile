FROM eclipse-temurin:21-jre

WORKDIR /app

# Копируем весь проект (включая gradle)
COPY . .

# Даём права на выполнение gradlew
RUN chmod +x gradlew

# Собираем JAR файл
RUN ./gradlew shadowJar

# Запускаем приложение
CMD ["java", "-jar", "app/build/libs/app-all.jar"]
