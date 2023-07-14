FROM openjdk:17-jdk-oracle

WORKDIR /app

ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8

COPY ./target/stock-backend-api-0.0.1-SNAPSHOT.jar /app/stock-backend-api.jar

CMD ["java", "-jar", "-Dspring.profiles.active=homepro", "/app/stock-backend-api.jar"]