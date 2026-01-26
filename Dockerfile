# Stage 1: Build
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /build

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the WAR file
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre

# Install Tomcat
ENV TOMCAT_VERSION=10.1.24
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*
RUN cd /opt && wget -q https://archive.apache.org/dist/tomcat/tomcat-10/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    tar xzf apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    mv apache-tomcat-${TOMCAT_VERSION} tomcat && \
    rm apache-tomcat-${TOMCAT_VERSION}.tar.gz

WORKDIR /opt/tomcat

# Copy built WAR from builder stage
COPY --from=builder /build/target/odtBank.war webapps/

# Expose Tomcat port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
