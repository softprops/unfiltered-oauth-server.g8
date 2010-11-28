package com.example

import unfiltered.request._
import unfiltered.response._
import org.clapper.avsl.Logger
import unfiltered.oauth.{Consumer, Token}

class App(host: Host, tokens: Tokens, consumers: Consumers) extends Templates with unfiltered.filter.Plan {
  import unfiltered.Cookie
  import QParams._
  import unfiltered.oauth.OAuth._
  
  val log = Logger(classOf[App])
  
  def intent = {
    // index
    case GET(Path("/", r)) => index(r.underlying.getRequestURL.toString, host.current(r))
      
    // handler for user authentication
    case POST(Path("/authenticate", Params(params, r))) =>
      val expected = for {
        token <- lookup(TokenKey) is required("oauth_token is required") is
          nonempty("oauth_token can not be blank")
        username <- lookup("username") is required("username is required") is
          nonempty("username can not be blank")
        password <- lookup("password") is required("password is required") is
          nonempty("password can not be blank")
      } yield {
        val sid = host.createSession(username.get, password.get)
        log.info("authenticated %s in session %s. back to authorize with token %s" format(username.get, sid, token.get))
        ResponseCookies(Cookie("sid", sid)) ~>
          Redirect("/oauth/authorize?%s=%s" format(TokenKey, token.get))
      }

      expected(params) orFail { fails =>
        BadRequest ~> ResponseString(fails.map { _.error } mkString(". "))
      }
      
    /** get a list of the current users oauth connections */
    case GET(Path("/connections", request)) => host.current(request) match {
      case Some(user) => connections(((Nil: List[(Token, Consumer)]) /: tokens.forUser(user.id)) ((l, t) =>
        consumers.get(t.consumerKey) match {
          case Some(c) => (t, c) :: l
          case _ => l
        })
      )
      case None => Redirect("/")
    }
    
    /** delete a target oauth connection */
    case GET(Path(Seg("connections" :: "disconnect" :: key :: Nil), request)) =>
      tokens.delete(key)
      Redirect("/connections")
  }
}