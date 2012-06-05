package com.example

import unfiltered.Cookie
import unfiltered.oauth.UserLike
import unfiltered.request.{HttpRequest, Cookies, Params}
import unfiltered.oauth.Consumer

case class User(id: String, password: String) extends UserLike

/** Host to OAuth server */
class Host extends unfiltered.oauth.UserHost with Templates {

  val umap = scala.collection.mutable.Map.empty[String, UserLike]
  val ApproveKey = "Approve it"
  val DenyKey = "Deny it"

  /** In a normal application we would validate the users credentials before creating a session. */
  def createSession(login:String, password:String) = {
    val sid = java.util.UUID.randomUUID.toString
    umap += (sid -> User(login, password))
    sid
  }

  /** @return Some(UserLike) if the current user is logged in, None otherwise */
  def current[T](r: HttpRequest[T]) = r match {
    case Cookies(cookies) => cookies("sid") match {
      case Some(Cookie(_, value, _, _, _, _, _, _)) => umap.get(value)
      case _ => None
    }
    case _ => None
  }

  /** @return true if user authorized the request, false otherwise */
  def accepted[T](token:String, r: HttpRequest[T]) = r match {
    case Params(params) => params("submit") match {
      case Seq(ApproveKey) => true
      case _ => false
    }
  }

  /** @return true if user denied authorization to the request, false otherwise */
  def denied[T](token: String, r: HttpRequest[T]) = r match {
    case Params(params) => params("submit") match {
      case Seq(DenyKey) => true
      case _ => false
    }
  }

  /** ask the user to authorize a consumer */
  override def requestAcceptance(token: String, consumer: Consumer) = consumer match {
    case ExampleConsumer(_, _, name) => authorizationForm(name, token, ApproveKey, DenyKey)
  }

  /** notify the user that the consumer was denied access */
  override def deniedConfirmation(consumer: Consumer) = consumer match {
    case ExampleConsumer(_, _, name) => deniedNotice(name)
  }

  /** notify the user with the out-of-bounds verifier string */
  def oobResponse(verifier: String) = oobNotice(verifier)

  /** @return a page to login the user */
  def login(token: String) = loginForm(token)
}
