# Unfiltered OAuth Server

A VERY SIMPLE example of an unfiltered oauth server. Use in conjunction with unfiltered oauth client

# usage

get and run the client

    g8 softprops/unfiltered-oauth-client
    cd unfiltered-oauth-client && sbt update run
    
get and run the server 
    g8 softprops/unfiltered-oauth-server
    cd unfiltered-oauth-server && sbt update run
    
# dependencies
  
   * unfiltered-filter - 0.2.2-SNAPSHOT
   * unfiltered-jetty - 0.2.2-SNAPSHOT
   * unfiltered-oauth - 0.7.8-SNAPSHOT