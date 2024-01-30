A REST API for recipes that utilizes Spring Boot 3, mongodb, and Keycloak.

Recipes are kept simple as far as how we store measurements, instructions, ingredients, etc.

Users are able to favorite/unfavorite recipes.

Lists of recipes (returned when searching for recipes or browsing favorited recipes) are paginated.

To run this locally, first clone the project:

`git clone https://github.com/hopk8412/table-recipes-api-v2.git`

Next, be sure you have your environment variables configured. The needed variables are:

`MONGODB_DBNAME=your_mongodb_dbname`

`PUBLIC_KEY=your_keycloakserver_realms_publickey`

`MONGO_URI=your_mongodb_connection_string`

If you need to configure your mongodb collections, please do so before running the below commands.

Next, build the project (navigate to the root project directory first):

`./mvnw clean install`

Finally, run the project:

`./mvnw spring-boot:run`