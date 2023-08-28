package com.sample.example

import com.sample.auth.OAuthSupport
import com.sample.auth.filter.AuthUtil
import org.scalatra._

class MyScalatraServlet
    extends ScalatraServlet
    with OAuthSupport {

  get("/") {
    "This is a public resource."
  }

  get("/login") {
    "Error authenticating. Redirected to login Page."
  }

  get("/protected") {
    authenticate()
    "You have permission to view the protected resource."
  }

  get("/protected-via-enforcer/1") {
    AuthUtil.isAuthorized(request, response)
    "Protected via enforcer"
  }
}
