FROM eclipse-temurin:25
RUN apt-get update && apt-get install -y curl
WORKDIR /app
COPY target/payment_service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]