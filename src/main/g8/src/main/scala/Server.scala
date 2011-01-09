package com.example

import org.clapper.avsl.Logger
import unfiltered.oauth._

/** oauth server */
object Server {
  val log = Logger(Server.getClass)
  val port = 8080
  def resources = new java.net.URL(getClass.getResource("/web/robots.txt"), ".")

  def main(args: Array[String]) {

    log.info("starting unfiltered oauth server at localhost on port %s" format port)

    new java.util.Timer().schedule(new java.util.TimerTask() {
      def run = unfiltered.util.Browser.open("http://localhost:%s/" format port)
    }, 1000)

    val host = new Host
    val stores = Stores(host)
    unfiltered.jetty.Http(port)
      .resources(Server.resources)
      .context("/oauth") { _.filter(OAuth(stores)) }
      .context("/api") {
        _.filter(Protection(stores))
         .filter(new App(host, stores.tokens, stores.consumers))
      }.run
  }
}
