package com.sample.auth

import java.util.Locale
import javax.servlet.http.HttpServletRequest

class TokenAuthRequest(r: HttpServletRequest) {

  private val AUTHORIZATION_KEYS = List("Authorization")
  private val TOKEN_PREFIX = "bearer"

  private def parts: List[String] = authorizationKey map {
    r.getHeader(_).split(" ", 2).toList
  } getOrElse Nil

  def scheme: Option[String] =
    parts.headOption.map(sch => sch.toLowerCase(Locale.ENGLISH))

  def token: String = parts.lastOption getOrElse ""

  private def authorizationKey: Option[String] = AUTHORIZATION_KEYS.find(r.getHeader(_) != null)

  def isTokenAuth: Boolean =
    scheme.foldLeft(false)((acc, sch) => acc || sch == TOKEN_PREFIX)

  def providesAuth: Boolean = authorizationKey.isDefined
}
