# Keycloak Auth #

## Build & Run ##

```sh
$ cd keycloak-auth-using-authz/
$ sbt
> jetty:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.


## Testing

1. Start a Keycloak instance. You may easily do so by running this Docker command:

```shell
docker run -p 9090:9090 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:21.1.2 start-dev
```

2. Create a Keycloak client, take note of the client name. Then copy its client secret.


3. Update `src/main/resources/keycloak.json` and update the
   - realm (usually this is master)
   - auth-server-url (the where keycloak is running, ex: http://localhost:9090)
   - resource (client id from step 2)
   - credentials.secret (client secret from step 2)

4. If you wish to enforce authorization policy using a config file instead, you may update policy-enforcer.
You may invoke this using `AuthUtil`

5. If you wish to enforce authorization programmatically, you may use `OAuthSupport`

6. Alternatively, you can create a `Filter` and add it to `web.xml`
  - or make use of `org.keycloak.adapters.authorization.integration.elytron.PolicyEnforcerFilter`
  - Just make sure the app is using jakarta EE and not javax

7. Test the endpoints from `MyScalatraServlet` via Postman collection provided.

