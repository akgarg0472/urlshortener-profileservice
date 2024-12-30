FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY . .

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/UrlShortenerProfileService.jar .

ENV SPRING_PROFILES_ACTIVE=prod

ENV SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/urlshortener?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
ENV SPRING_DATASOURCE_USERNAME="root"
ENV SPRING_DATASOURCE_PASSWORD="root"
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME="com.mysql.cj.jdbc.Driver"

CMD ["java", "-jar", "UrlShortenerProfileService.jar"]
