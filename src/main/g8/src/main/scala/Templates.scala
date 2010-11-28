package com.example

trait Templates {
  import unfiltered.response._
  import unfiltered.oauth.{Consumer, Token, UserLike}
  
  def page(body: scala.xml.NodeSeq) = Html(
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <title>oauth provider</title>
        <link href="/css/app.css" type="text/css" rel="stylesheet" />
      </head>
      <body>
        <div id="container">
          <h1><a href="/">oauth provider</a></h1>
          {body}
        </div>
      </body>
    </html>
  )
  
  def index(urlBase: String, currentUser: Option[UserLike]) = page(
    <div>{
        currentUser match {
          case Some(user) => <div>
            <p>welcome {user.id}</p>
            <p> view your <a href="/connections">connections</a></p>
          </div>
          case _ => <p>not logged in.</p>
        }
      }
      <p>These are your endpoints</p>
      <ul id="oauth-endpoints">
        <li>{urlBase}oauth/request_token</li>
        <li>{urlBase}oauth/authorize</li>
        <li>{urlBase}oauth/access_token</li>
      </ul>
      <a href="http://localhost:8081/">dance with me &rarr;</a>
    </div>
  )
  
  def authorizationForm(consumerName: String, token: String, approve: String, deny: String) = page(
    <div>
      <form action="/oauth/authorize" method="POST">
        <p>
          A 3rd party application named <strong>{consumerName}</strong> has requested access to your data.
        </p>
        <input type="hidden" name="oauth_token" value={token} />
        <div id="oauth-opts">
          <input type="submit" name="submit" value={approve} />
          <input type="submit" name="submit" value={deny} />
        </div>
      </form>
    </div>
  )
  
  def deniedNotice(consumerName: String) = page(
    <div>You have denied a 3rd party application named <strong>{consumerName}</strong> access to your data</div>
  )
  
  def oobNotice(verifier: String) = page(
    <p>Enter the following code into your consumer: <strong>{verifier}</strong></p>
  )
  
  def loginForm(token: String) = page(
    <div>
      <form action="/authenticate" method="POST">
        <p>sign in. someone wants your stuff</p>
        <input type="hidden" name="oauth_token" value={token}/>
        <dl>
          <dt><label for="username">username</label></dt>
          <dd><input type="text" name="username" value="jim"/></dd>
          <dt><label for="password">password</label></dt>
          <dd><input type="password" name="password" value="jim"/></dd>
          <dt></dt>
          <dd><input type="submit" value="sign in" /></dd>
        </dl>
      </form>
    </div>
  )
  
  def connections(consumers: Seq[(Token, Consumer)]) = page(
    <div>
      <h2>Connections</h2> {
        if(consumers.isEmpty) <li>You have no oauth connections</li>
        else {
          <p>You have connections with the following applications </p>
        }
      } {
        consumers.map { (_: (Token, Consumer)) match {
          case (t, ExampleConsumer(key,_,name)) => 
            <li>
              <strong>{name}</strong>
              <a href={"/connections/disconnect/%s" format(t.key) }>break it</a>
            </li>
          case _ => <li>?</li>
        } }
      }
    </div>
  )
}