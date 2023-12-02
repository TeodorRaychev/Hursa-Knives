#
# Build stage
#
FROM gradle:jdk17-jammy AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean build -x test

#
# Package stage
#
FROM eclipse-temurin:17-jdk-jammy
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
EXPOSE 8080
ARG POSTGRES_PORT
ENV POSTGRES_PORT=$POSTGRES_PORT
ARG USERNAME
ENV USERNAME=$USERNAME
ARG PASSWORD
ENV PASSWORD=$PASSWORD
ARG ADMIN_EMAIL
ENV ADMIN_EMAIL=$ADMIN_EMAIL
ARG ADMIN_PASSWORD
ENV ADMIN_PASSWORD=$ADMIN_PASSWORD
ARG REMEMBER_ME_KEY
ENV REMEMBER_ME_KEY=$REMEMBER_ME_KEY

ENTRYPOINT ["java", "-jar", "/app.jar"]
