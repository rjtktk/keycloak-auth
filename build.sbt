val ScalatraVersion = "2.8.2"
val KeycloakVersion = "21.1.2"

ThisBuild / scalaVersion := "2.12.17"
ThisBuild / organization := "com.sample"

lazy val hello = (project in file("."))
  .settings(
    name := "Keycloak Auth Using Authz",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      "org.scalatra" %% "scalatra" % ScalatraVersion,
      "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
      "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
      "org.eclipse.jetty" % "jetty-webapp" % "9.4.43.v20210629" % "container",
      "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
      // Added dependencies
      "org.keycloak" % "keycloak-authz-client" % KeycloakVersion,
      "org.keycloak" % "keycloak-policy-enforcer" % KeycloakVersion,
      "org.scalatra" %% "scalatra-auth" % ScalatraVersion,

      // Test
      "org.scalatest" %% "scalatest" % "3.2.9" % Test,
      "org.scalamock" %% "scalamock" % "5.2.0" % Test,
    ),
  )

enablePlugins(SbtTwirl)
enablePlugins(JettyPlugin)
