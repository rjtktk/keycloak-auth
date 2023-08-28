package com.sample.auth

import org.keycloak.authorization.client.{AuthorizationDeniedException, AuthzClient}
import org.keycloak.representations.idm.authorization.AuthorizationRequest
import org.scalatra.ScalatraBase
import org.scalatra.auth.ScentryStrategy
import org.slf4j.LoggerFactory

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

class TokenAuthStrategy(protected override val app: ScalatraBase)
    extends ScentryStrategy[User] {

  private val logger = LoggerFactory.getLogger(getClass)

  implicit def request2TokenAuthRequest(r: HttpServletRequest) =
    new TokenAuthRequest(r)

  override def isValid(implicit request: HttpServletRequest) =
    request.isTokenAuth && request.providesAuth

  // catches the case that we got none user.
  override def unauthenticated()(implicit
      request: HttpServletRequest,
      response: HttpServletResponse
  ) {
    app.redirect("/login")
    // app halt Unauthorized()
  }

  // overwrite required authentication request
  def authenticate()(implicit
      request: HttpServletRequest,
      response: HttpServletResponse
  ): Option[User] = validateViaKeycloakAuthZ(request.token)

  private def validateViaKeycloakAuthZ(token: String): Option[User] = {

    // create a new instance based on the configuration defined in keycloak.json
    val authzClient = AuthzClient.create

    // create an authorization request
    val request = new AuthorizationRequest

    // send the entitlement request to the server in order to
    // obtain an RPT with all permissions granted to the user
    try {
      val authorizationResponse =
        authzClient.authorization(token).authorize(request)
      val rpt = authorizationResponse.getToken
      val requestingPartyToken =
        authzClient.protection.introspectRequestingPartyToken(rpt)

      logger.info("Token status is: " + requestingPartyToken.getActive)
      logger.info("Permissions granted by the server: ")

      requestingPartyToken.getPermissions.forEach { case perm =>
        logger.info(
          "Resource name: " + perm.getResourceName
        ) // Application name
        perm.getScopes.forEach { case scope =>
          logger.info("Scope: " + scope) // General / Functional Permission
        }
      }

      Some(User("Success"))
    } catch {
      case err: AuthorizationDeniedException => {
        logger.error("Authorization Denied", err)
        None
      }
      case err: Exception => None
    }
  }
}
