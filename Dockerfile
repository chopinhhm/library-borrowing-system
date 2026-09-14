FROM maven:3.9.9-eclipse-temurin-8 AS build

WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:8-jre-jammy

WORKDIR /app
RUN addgroup --system app && adduser --system --ingroup app app

COPY --from=build /workspace/target/library-borrowing-system-*.jar /app/app.jar
RUN chown app:app /app/app.jar

USER app
EXPOSE 18080
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=mysql"]
