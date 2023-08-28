package com.sample.auth.filter

import com.sample.auth.TokenAuthRequest
import org.keycloak.adapters.authorization.spi.{HttpRequest, HttpResponse}
import org.keycloak.adapters.authorization.{PolicyEnforcer, TokenPrincipal}
import org.keycloak.representations.adapters.config.AdapterConfig
import org.keycloak.util.JsonSerialization
import org.scalatra.Unauthorized
import org.slf4j.LoggerFactory

import java.io.InputStream
import java.util
import java.util.Collections
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

object AuthUtil {

  private val logger = LoggerFactory.getLogger(getClass)

  private val config: AdapterConfig = JsonSerialization.readValue(
    Thread.currentThread.getContextClassLoader
      .getResourceAsStream("keycloak.json"),
    classOf[AdapterConfig]
  )

  private val policyEnforcer: PolicyEnforcer = PolicyEnforcer
    .builder()
    .realm(config.getRealm)
    .authServerUrl(config.getAuthServerUrl)
    .clientId(config.getResource)
    .credentials(config.getCredentials)
    .enforcerConfig(config.getPolicyEnforcerConfig)
    .build()

  def isAuthorized(
      request: HttpServletRequest,
      response: HttpServletResponse
  ): Unit = {

    logger.info("checking if token exists")

    val wrappedRequest = RequestWrapper(
      request,
      new TokenPrincipal {
        override def getRawToken: String = new TokenAuthRequest(request).token
      }
    )

    val wrappedResponse = ResponseWrapper(response)

    if (!policyEnforcer.enforce(wrappedRequest, wrappedResponse).isGranted) {
      logger.info("Request unauthorized")
      Unauthorized("Unauthorized access")
    }
  }
}

case class RequestWrapper(
    request: HttpServletRequest,
    tokenPrincipal: TokenPrincipal
) extends HttpRequest {
  override def getRelativePath: String = request.getServletPath

  override def getMethod: String = request.getMethod

  override def getURI: String = request.getRequestURI

  override def getHeaders(name: String): util.List[String] =
    Collections.list(request.getHeaders(name))

  override def getFirstParam(name: String): String = request.getParameter(name)

  override def getCookieValue(name: String): String = request.getCookies
    .filter(cookie => cookie.getName.equals(name))
    .map(_.getValue)
    .head

  override def getRemoteAddr: String = request.getRemoteAddr

  override def isSecure: Boolean = request.isSecure

  override def getHeader(name: String): String = request.getHeader(name)

  override def getInputStream(buffered: Boolean): InputStream =
    request.getInputStream

  override def getPrincipal: TokenPrincipal = tokenPrincipal
}

case class ResponseWrapper(response: HttpServletResponse) extends HttpResponse {
  override def sendError(statusCode: Int): Unit = response.sendError(statusCode)

  override def sendError(statusCode: Int, reason: String): Unit =
    response.sendError(statusCode, reason)

  override def setHeader(name: String, value: String): Unit =
    response.setHeader(name, value)
}
