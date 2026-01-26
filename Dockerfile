# Stage 1: Build using official Maven image
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the WAR file with Java 21 reflection fix
RUN mvn clean package -DskipTests -Dmaven.compiler.fork=true \
    -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

# Stage 2: Runtime
FROM eclipse-temurin:21-jre

# Install Tomcat (Tomcat 9 for javax servlet compatibility)
ENV TOMCAT_VERSION=9.0.89
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*
RUN cd /opt && wget -q https://archive.apache.org/dist/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    tar xzf apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    mv apache-tomcat-${TOMCAT_VERSION} tomcat && \
    rm apache-tomcat-${TOMCAT_VERSION}.tar.gz

# Configure Tomcat environment
ENV CATALINA_HOME=/opt/tomcat
ENV PATH="$CATALINA_HOME/bin:$PATH"

WORKDIR /opt/tomcat

# Copy built WAR from builder stage
COPY --from=builder /build/target/odtBank.war webapps/

# Expose Tomcat port
EXPOSE 8080

# Start Tomcat
CMD ["/opt/tomcat/bin/catalina.sh", "run"]
