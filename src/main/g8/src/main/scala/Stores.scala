package com.example

import unfiltered.request._
import unfiltered.response._
import unfiltered.oauth._

case class ExampleConsumer(key: String, secret: String, name: String) extends Consumer

/** Consumer store with default consumer with key of "key" and secret of "secret" */
class Consumers extends ConsumerStore {
  val cmap = scala.collection.mutable.Map(
    "key" -> new ExampleConsumer("key", "secret", "trustly")
  )
  def get(consumerKey: String) = cmap.get(consumerKey)
}

/** Token Store with default token generator */
class Tokens extends DefaultTokenStore {
  val tmap = scala.collection.mutable.Map.empty[String, Token]
  
  def put(token: Token) = {
    tmap += (token.key -> token)
    token
  }
  def get(tokenId: String) = tmap.get(tokenId)
  def delete(tokenId: String) = tmap -= tokenId
  
  def forUser(userId: String) = tmap.values flatMap {
    case a@AccessToken(key, sec, uid, consumerKey) if(a.user == userId) =>
      Seq(a)
  }
}

/** Implementation of OAuthStores used by OAuth server */
case class Stores(host: Host) extends OAuthStores {
  /** access to nonces */
  val nonces = new NonceStore {
    val nl = new scala.collection.mutable.ListBuffer[String]()
    def put(consumer: String, timestamp: String, nonce: String) = true
  }
  
  /** access to consumers */
  val consumers = new Consumers
  
  /** access to tokens */
  val tokens = new Tokens
  
  
  /** access to users and host */
  val users = host
}