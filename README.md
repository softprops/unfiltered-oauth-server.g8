# Unfiltered OAuth Server

A VERY SIMPLE example of an unfiltered oauth server. Use in conjunction with unfiltered oauth client

# usage

get and run the client

    g8 softprops/unfiltered-oauth-client
    cd unfiltered-oauth-client && sbt update run
    
get and run the server 

    g8 softprops/unfiltered-oauth-server
    cd unfiltered-oauth-server && sbt update run


This server is preconfigured with an oauth consumer with an key set to `key` and password set to `secret`
