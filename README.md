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
  
   * unfiltered-filter - 0.5
   * unfiltered-jetty - 0.5
   * unfiltered-oauth - 0.5
   * unfiltered-json - 0.5