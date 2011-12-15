organization := "com.example"

name := "$name$"

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "net.databinder" %% "unfiltered-filter" % "$unfiltered_version$",
  "net.databinder" %% "unfiltered-jetty" % "$unfiltered_version$",
  "net.databinder" %% "unfiltered-oauth" % "$unfiltered_version$",
  "net.databinder" %% "unfiltered-json" % "$unfiltered_version$",
  "org.clapper" %% "avsl" % "0.3.6"
)
