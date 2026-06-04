FROM eclipse-temurin:21-jdk

WORKDIR /app

# Копируем исходники и gradle-файлы
COPY app /app
COPY Makefile Makefile

# Даём права на выполнение gradlew
RUN chmod +x gradlew

# Собираем JAR
RUN ./gradlew shadowJar

# Запускаем (используем JRE из того же образа)
CMD ["java", "-jar", "build/libs/app-all.jar"]
