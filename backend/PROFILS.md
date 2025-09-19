# Conseils d’utilisation

Active le profil voulu via Maven (ex. dev) :
```
mvn -pl app spring-boot:run -Pdev
```
En Docker/compose, passe les variables SPRING_DATASOURCE_* et SERVER_PORT via environment: (déjà prévu dans `docker-compose.


