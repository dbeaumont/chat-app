# ===== Build stage =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# Récupération des dépendances jar depuis le cache
# en ne recopiant que les pom.xml, puis en faisant mvn dependency:go-offline pour résoudre les dépendences, on récupère les dépendances depuis le cache si les pom n'ont pas changées
# les sources ne sont recopiés que ensuite, la compile profite donc du cache des libs par ce moyen
COPY pom.xml ./
COPY domain/pom.xml domain/pom.xml
COPY application/pom.xml application/pom.xml
COPY infrastructure/pom.xml infrastructure/pom.xml
COPY app/pom.xml app/pom.xml
RUN mvn -q -f pom.xml -Dmaven.test.skip=true -DskipTests dependency:go-offline -Pprod
COPY . ./
RUN mvn -q -DskipTests clean package -Pprod

# ===== Run stage =====
FROM eclipse-temurin:21-jre
ENV TZ=UTC
WORKDIR /opt/app
COPY --from=build /app/app/target/app-*-fat.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
