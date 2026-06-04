FROM eclipse-temurin:21-jre

WORKDIR /app

# Копируем исходники и gradle-файлы
COPY app /app

# Даём права на выполнение gradlew
RUN chmod +x gradlew

# Собираем JAR файл (явно указываем задачу)
RUN ./gradlew clean shadowJar

# Запускаем приложение
CMD ["java", "-jar", "build/libs/app-all.jar"]
