FROM eclipse-temurin:17 AS build
WORKDIR /work
COPY . .
RUN /work/gradlew build -x test

FROM openjdk:17-alpine AS deployment
WORKDIR /work
COPY --from=build /work/build/libs/*-SNAPSHOT.jar /work/app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/work/app.jar"]