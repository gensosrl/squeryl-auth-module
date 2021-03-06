package net.liftmodules.squerylauth

import org.joda.time.Days
import org.joda.time.Hours
import org.joda.time.ReadablePeriod

import net.liftweb._
import net.liftweb.common._
import net.liftweb.http.Factory
import net.liftweb.http.LiftRules
import net.liftweb.http.SessionVar
import net.liftweb.util.Helpers
import net.liftweb.util.BundleBuilder

object SquerylAuth extends Factory {


  //set default resource bundle
  LiftRules.resourceBundleFactories.prepend( {
    case (_,locale) if LiftRules.loadResourceAsXml("/toserve/squerylauth.resources.html").isDefined =>
      LiftRules
        .loadResourceAsXml("/toserve/squerylauth.resources.html")
        .flatMap { BundleBuilder.convert(_,locale) }
        .openOrThrowException("isDefined is called in the guard")
  })

  // AuthUserMeta object
  val authUserMeta = new FactoryMaker[AuthUserMeta[_]](model.SimpleUser) {}

  // urls
  val indexUrl = new FactoryMaker[String]("/") {}
  val loginUrl = new FactoryMaker[String]("/login") {}
  val logoutUrl = new FactoryMaker[String]("/logout") {}
  val registerUrl = new FactoryMaker[String]("/register") {}

  // site settings
  val siteName = new FactoryMaker[String]("Example") {}
  val systemEmail = new FactoryMaker[String]("info@example.com") {}
  val systemUsername = new FactoryMaker[String]("Example Staff") {}
  //For support of postgres schemas
  val schemaName = new FactoryMaker[Option[String]](None) {}

  def systemFancyEmail = AuthUtil.fancyEmail(systemUsername.vend, systemEmail.vend)

  // LoginToken
  val loginTokenUrl = new FactoryMaker[String]("/login-token") {}
  val loginTokenAfterUrl = new FactoryMaker[String]("/set-password") {}
  val loginTokenExpires = new FactoryMaker[ReadablePeriod](Hours.hours(48)) {}

  // ExtSession
  val extSessionExpires = new FactoryMaker[ReadablePeriod](Days.days(90)) {}
  val extSessionCookieName = new FactoryMaker[String]("EXTSESSID") {}
  val extSessionCookiePath = new FactoryMaker[String]("/") {}
  val extSessionCookieDomain = new FactoryMaker[Box[String]](Empty) {}

  // Permission
  val permissionWilcardToken = new FactoryMaker[String]("*") {}
  val permissionPartDivider = new FactoryMaker[String](":") {}
  val permissionSubpartDivider = new FactoryMaker[String](",") {}
  //val permissionCaseSensitive = new FactoryMaker[Boolean](true) {}

  def init(
            authUserMeta: AuthUserMeta[_] = model.SimpleUser,
            indexUrl: String = "/",
            loginUrl: String = "/login",
            logoutUrl: String = "/logout",
            siteName: String = "Example",
            systemEmail: String = "info@example.com",
            systemUsername: String= "Example Staff",
            loginTokenUrl: String = "/login-token",
            loginTokenAfterUrl: String = "/set-password",
            loginTokenExpires: ReadablePeriod = Hours.hours(48),
            extSessionExpires: ReadablePeriod = Days.days(90),
            extSessionCookieName: String = "EXTSESSID",
            extSessionCookiePath: String = "/",
            permissionWilcardToken: String = "*",
            permissionPartDivider: String = ":",
            permissionSubpartDivider: String = ","
            ): Unit = {
    this.authUserMeta.default.set(authUserMeta)
    this.indexUrl.default.set(indexUrl)
    this.loginUrl.default.set(loginUrl)
    this.logoutUrl.default.set(logoutUrl)
    this.siteName.default.set(siteName)
    this.systemEmail.default.set(systemEmail)
    this.systemUsername.default.set(systemUsername)
    this.loginTokenUrl.default.set(loginTokenUrl)
    this.loginTokenAfterUrl.default.set(loginTokenAfterUrl)
    this.loginTokenExpires.default.set(loginTokenExpires)
    this.extSessionExpires.default.set(extSessionExpires)
    this.extSessionCookieName.default.set(extSessionCookieName)
    this.extSessionCookiePath.default.set(extSessionCookiePath)
    this.permissionWilcardToken.default.set(permissionWilcardToken)
    this.permissionPartDivider.default.set(permissionPartDivider)
    this.permissionSubpartDivider.default.set(permissionSubpartDivider)
  }
}

object AuthUtil {
  def tryo[T](f: => T): Box[T] = {
    try {
      f match {
        case null => Empty
        case x => Full(x)
      }
    } catch {
      case (e: Throwable) => Failure(e.getMessage, Full(e), Empty)
    }
  }

  def fancyEmail(name: String, email: String): String = "%s <%s>".format(name, email)
}

/*
 * User gets sent here after a successful login.
 */
object LoginRedirect extends SessionVar[Box[String]](Empty) {
  override def __nameSalt = Helpers.nextFuncName
}
