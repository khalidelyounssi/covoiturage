# Covoiturage multi-étapes

API REST pédagogique, Java 17 et PostgreSQL.

## Démarrer

```bash
java -version
mvn -v
docker compose up -d
docker compose ps
mvn spring-boot:run
```

Test rapide : ouvrir http://localhost:8080/api/conducteurs. La réponse attendue sur une base vierge est HTTP 200 avec [].

## Tester

```bash
mvn test
docker compose exec postgres psql -U cov -d covoiturage -c "SELECT 'conducteur' AS table_name, count(*) FROM conducteur UNION ALL SELECT 'passager', count(*) FROM passager UNION ALL SELECT 'vehicule', count(*) FROM vehicule UNION ALL SELECT 'trajet', count(*) FROM trajet UNION ALL SELECT 'point_arret', count(*) FROM point_arret UNION ALL SELECT 'reservation', count(*) FROM reservation;"
```

Importer COV.postman_collection.json, conserver baseUrl=http://localhost:8080 et exécuter les dossiers 01 à 05 dans l’ordre.
Pour les appels manuels, Content-Type est application/json quand un corps est envoyé.
X-Conducteur-Id et X-Passager-Id simulent l’identité pendant les tests locaux. Ils ne constituent pas une authentification.
Le filtre date correspond au jour de départ du trajet, selon la zone Africa/Casablanca.

## Règles métier

Prix d’un segment = prix cumulé à la descente − prix cumulé à la montée.
Chaque place confirmée est déduite sur l’ensemble du trajet.
Un passager peut annuler jusqu’à 24 heures avant le départ.
Les écritures concurrentes sur un trajet verrouillent la même ligne dans une transaction.

## Livrables

[Projet Jira COV](https://elyounssiox-java.atlassian.net/jira/software/projects/COV/boards/69).
Ajouter l’image du diagramme de classes et le lien du dépôt GitHub à la remise.