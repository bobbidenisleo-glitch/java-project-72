FROM eclipse-temurin:21-jre
WORKDIR /app
COPY app/build/libs/app-all.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
