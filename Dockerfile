# Stage 1: Build the WAR using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
# Download dependencies first (better layer caching)
RUN mvn dependency:go-offline -q
COPY src ./src

RUN mvn clean package -DskipTests -q

# Stage 2: Deploy to Tomcat 10.1 with JDK 17
FROM tomcat:10.1-jdk17

# Remove default webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy built WAR as ROOT (deploys at /)
COPY --from=builder /app/target/skillbuilders.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
