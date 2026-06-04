FROM eclipse-temurin:21-jre

WORKDIR /app

# Копируем только исходники и gradle-файлы
COPY app /app
COPY Makefile Makefile

# Даём права на выполнение gradlew
RUN chmod +x gradlew

# Собираем JAR
RUN ./gradlew shadowJar

# Запускаем
CMD ["java", "-jar", "build/libs/app-all.jar"]
