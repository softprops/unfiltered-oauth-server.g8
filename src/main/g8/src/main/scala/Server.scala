package com.example

import unfiltered.Cookie
import unfiltered.request._
import unfiltered.response._
import unfiltered.oauth._
import org.clapper.avsl.Logger

/** oauth server */
object Server {
  val log = Logger(Server.getClass)
  val port = 8080
  def resources = new java.net.URL(getClass.getResource("/web/robots.txt"), ".")
  
  trait Templates {
    def page(body: scala.xml.NodeSeq) = Html(
      <html>
        <head>
          <title>oauth server</title>
          <link href="/css/app.css" type="text/css" rel="stylesheet" />
        </head>
        <body>
          <div id="container">
            <h1>OAuth Server</h1>
            {body}
          </div>
        </body>
      </html>
    )
  }
  
  class Host extends UserHost with Templates {
    import QParams._
    val umap = scala.collection.mutable.Map.empty[String, UserLike]
    
    case class User(id: String, password: String) extends UserLike

    /** everyone is a user! */
    def createSession(login:String, password:String) = {
      log.info("creating session for %s" format login)
      val sid = java.util.UUID.randomUUID.toString
      umap += (sid -> User(login, password))
      sid
    }
    
    def current[T](r: HttpRequest[T]) = r match {
      case Cookies(cookies, _) => cookies("sid") match {
        case Some(Cookie(_, value, _, _, _, _)) => umap.get(value)
        case _ => None
      } 
      case _ => None
    }
    
    def accepted[T](token:String, r: HttpRequest[T]) = r match {
      case Params(params, _) => params("submit") match {
        case Seq("Approve") => true
        case _ => false
      }
    }
    
    def denied[T](token: String, r: HttpRequest[T]) = r match {
      case Params(params, _) => params("submit") match {
        case Seq("Deny") => true
        case _ => false
      }
    }
    
    def login(token: String) = page(
        <div>
          <p>sign in. someone wants your stuff</p>
          <form action="/authenticate" method="POST">
            <input type="hidden" name="oauth_token" value={token}/>
            <dl>
              <dt><label for="username">username</label></dt>
              <dd><input type="text" name="username" value="username"/></dd>
              <dt><label for="password">password</label></dt>
              <dd><input type="password" name="password" value="password"/></dd>
            </dl>
            <input type="submit" value="sign in" />
          </form>
        </div>
    )
  }
  
  /** A ConsumerStore with a default consumer (key, secret) */
  class Consumers extends ConsumerStore {
    val cmap = scala.collection.mutable.Map(
      "key" -> new Consumer {
        val key = "key"
        val secret = "secret"
      }
    )
    def get(consumerKey: String) = cmap.get(consumerKey)
  }
  
  class App(host: Host) extends Templates with unfiltered.filter.Plan {
    import QParams._
    import unfiltered.oauth.OAuth._
    
    val log = Logger(classOf[App])
    
    def intent = {
      // index
      case GET(Path("/", r)) => page(
        <div>
          <ul>
            <li>{r.underlying.getRequestURL.toString}oauth/request_token</li>
            <li>{r.underlying.getRequestURL.toString}oauth/authorize</li>
            <li>{r.underlying.getRequestURL.toString}oauth/access_token</li>
          </ul>
          <a href="http://localhost:8081/">dance with me &rarr;</a>
        </div>)
        
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
          println("authenticated %s in session %s. back to authorize with token %s" format(username.get, sid, token.get))
          ResponseCookies(Cookie("sid", sid)) ~>
            Redirect("/oauth/authorize?%s=%s" format(TokenKey, token.get))
        }

        expected(params) orFail { fails =>
          BadRequest ~> ResponseString("opps!")
        }
    }
  }
    
  def main(args: Array[String]) {
    
    val host = new Host
    
    var stores = new OAuthStores {
      
      val nonces = new NonceStore {
        val nl = new scala.collection.mutable.ListBuffer[String]()
        def put(consumer: String, timestamp: String, nonce: String) = ("ok", 201)
      }
      
      val tokens = new DefaultTokenStore {
        val tmap = scala.collection.mutable.Map.empty[String, Token]
        def put(token: Token) = {
          tmap += (token.key -> token)
          token
        }
        def get(tokenId: String) = tmap.get(tokenId)
        def delete(tokenId: String) = tmap -= tokenId
      }
      
      val consumers = new Consumers
      
      val users = host
    }
    
    log.info("starting unfiltered oauth server at localhost on port %s" format port)
    unfiltered.util.Browser.open("http://localhost:8080/")
    unfiltered.jetty.Http(port)
      .resources(Server.resources)
      .context("/oauth") { _.filter(new OAuth(stores)) }
      .filter(new App(host)).run
  }
}
