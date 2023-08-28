package com.sample.auth

import org.scalatra.ScalatraBase
import org.scalatra.auth.{ScentryConfig, ScentrySupport}

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

trait OAuthSupport extends ScentrySupport[User] {
  self: ScalatraBase =>

  protected def fromSession: PartialFunction[String, User] = {
    case id: String => User(id)
  }

  protected def toSession: PartialFunction[User, String] = { case usr: User =>
    usr.id
  }

  protected val scentryConfig: ScentryConfiguration =
    new ScentryConfig {}.asInstanceOf[ScentryConfiguration]

  override protected def configureScentry(): Unit = {
    scentry.unauthenticated {
      scentry.strategies("Token").unauthenticated()
    }
  }

  override protected def registerAuthStrategies(): Unit = {
    scentry.register("Token", app => new TokenAuthStrategy(app))
  }

  // verifies if the request is a token request
  protected def auth()(implicit
      request: HttpServletRequest,
      response: HttpServletResponse
  ) = {
    val tokenReq = new TokenAuthRequest(request)
    if (!tokenReq.providesAuth || !tokenReq.isTokenAuth)
      halt(401, "Unauthenticated")
    scentry.authenticate("Token")
  }
}
