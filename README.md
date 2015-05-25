iFeed is a service that enables easy configuration and consumption of push enabled ATOM feeds based on search queries against a SOLR server.

The web based configurator is implemented as a portlet where iFeeds easily can be configured without any knowledge of SOLR query syntax. The UI includes the relevant elements from the Dublin Core based meta data model used at VGR. Terminology is pulled in to the configurator from Apelon DTS (Distributed Terminology System), which greatly simplifies the creation of quality queries.

The iFeeds configured in the configurator are made available in a web application. The web application provides a unique ATOM feed for each iFeed, queries the SOLR server for changes at an configured interval and pings the push server, a PubSubHubbub? hub, to notify consumers when changes occur in an iFeed.
