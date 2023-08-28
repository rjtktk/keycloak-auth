package com.sample.auth

import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import javax.servlet.http.HttpServletRequest

class TokenAuthRequestSpec
    extends AnyFlatSpec
    with MockFactory
    with Matchers
    with BeforeAndAfter {

  var request: HttpServletRequest = _

  before {
    request = mock[HttpServletRequest]
  }

  it should "handle request without auth" in {
    (request.getHeader _)
      .expects("Authorization")
      .returning(null)
      .anyNumberOfTimes()

    val authRequest = new TokenAuthRequest(request)

    authRequest.providesAuth shouldBe false
    authRequest.scheme shouldBe None
    authRequest.token shouldBe ""
    authRequest.isTokenAuth shouldBe false
  }

  it should "handle request with Bearer auth" in {
    (request.getHeader _)
      .expects("Authorization")
      .returning("Bearer tokenValue")
      .anyNumberOfTimes()

    val authRequest = new TokenAuthRequest(request)

    authRequest.providesAuth shouldBe true
    authRequest.scheme shouldBe Some("bearer")
    authRequest.token shouldBe "tokenValue"
    authRequest.isTokenAuth shouldBe true
  }

  it should "handle request with Token auth" in {
    (request.getHeader _)
      .expects("Authorization")
      .returning("Token tokenValue")
      .anyNumberOfTimes()

    val authRequest = new TokenAuthRequest(request)

    authRequest.providesAuth shouldBe true
    authRequest.scheme shouldBe Some("token")
    authRequest.token shouldBe "tokenValue"
    authRequest.isTokenAuth shouldBe false
  }
}
