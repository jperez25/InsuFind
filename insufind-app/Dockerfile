# Build context is the repo root: docker build -f insufind-app/Dockerfile -t insufind-app:latest .
FROM amazoncorretto:25 AS build
# The Maven wrapper needs tar/gzip to unpack Maven.
RUN yum install -y tar gzip && yum clean all
WORKDIR /src
COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY insufind-integrations-tests/pom.xml insufind-integrations-tests/pom.xml
COPY insufind-ui-tests/pom.xml insufind-ui-tests/pom.xml
COPY insufind-app insufind-app
RUN ./mvnw -q -pl insufind-app -am package -DskipTests

FROM amazoncorretto:25
WORKDIR /app
COPY --from=build /src/insufind-app/target/insufind-app-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
