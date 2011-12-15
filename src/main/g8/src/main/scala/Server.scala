package com.example

import org.clapper.avsl.Logger
import unfiltered.oauth._
import java.net.URL
/** oauth server */
object Server {
  val log = Logger(Server.getClass)
  val port = 8080

  def main(args: Array[String]) {
    val host = new Host
    val stores = Stores(host)
    unfiltered.jetty.Http(port)
      .resources(new URL(getClass.getResource("/web/robots.txt"), "."))
      .context("/oauth") { _.filter(OAuth(stores)) }
      .filter(new App(host, stores.tokens, stores.consumers))
      .context("/api") {
        _.filter(Protection(stores))
         .filter(Api)
      }.run { s =>
        unfiltered.util.Browser.open(s.url)
      }
  }
}
