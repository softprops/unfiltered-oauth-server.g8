import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {
  val uf_version = "0.5.0"

  // unfiltered
  lazy val uff = "net.databinder" %% "unfiltered-filter" % uf_version
  lazy val ufj = "net.databinder" %% "unfiltered-jetty" % uf_version
  lazy val ufoa = "net.databinder" %% "unfiltered-oauth" % uf_version
  lazy val ufjs = "net.databinder" %% "unfiltered-json" % uf_version

  // logging
  val javaNetRepo = "Java.net Repository for Maven" at "http://download.java.net/maven/2"
  val newReleaseToolsRepository = ScalaToolsSnapshots
  val avsl = "org.clapper" %% "avsl" % "0.3.6"
}
