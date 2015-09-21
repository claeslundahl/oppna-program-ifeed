iFeed is a service that enables easy configuration and consumption of push enabled ATOM feeds based on search queries against a SOLR server.

The web based configurator is implemented as a portlet where iFeeds easily can be configured without any knowledge of SOLR query syntax. The UI includes the relevant elements from the Dublin Core based meta data model used at VGR. Terminology is pulled in to the configurator from Apelon DTS (Distributed Terminology System) as well from different ldap-sources, which greatly simplifies the creation of quality queries.

The iFeeds configured in the configurator are made available in a web application. The web application provides a unique ATOM/RSS feed for each iFeed and a xbrowser script and jsonp-service that eneable external pages to include content from the iFeeds.

The project consists of four applications:

* Portlet, a portlet designed to run in Liferay. Editing SOLR searches, which in turn can produce RSS, Atom, Jsonp or a simple html-view.

**Output RSS/Atom**
* Intsvc. A web application that produces RSS/Atom feeds from search results to Solr index.

**Output Jsonp/html**
* Web, receives a request with a feed-id and produces a response in jsonp or html.

* web-script, is a script for building ajax-generated displays of feeds on various web-pages. It consumes data (as jsonp) via the iFeed-web web-application 
iFeed is a service that enables easy configuration and consumption of push enabled ATOM feeds based on search queries against a SOLR server.

The web based configurator is implemented as a portlet where iFeeds easily can be configured without any knowledge of SOLR query syntax. The UI includes the relevant elements from the Dublin Core based meta data model used at VGR. Terminology is pulled in to the configurator from Apelon DTS (Distributed Terminology System) as well from different ldap-sources, which greatly simplifies the creation of quality queries.

The iFeeds configured in the configurator are made available in a web application. The web application provides a unique ATOM/RSS feed for each iFeed and a xbrowser script and jsonp-service that eneable external pages to include content from the iFeeds.

The project consists of four applications:

* Portlet, a portlet designed to run in Liferay. Editing SOLR searches, which in turn can produce RSS, Atom, Jsonp or a simple html-view.

**Output RSS/Atom**
* Intsvc. A web application that produces RSS/Atom feeds from search results to Solr index.

**Output Jsonp/html**
* Web, receives a request with a feed-id and produces a response in jsonp or html.

* web-script, is a script for building ajax-generated displays of feeds on various web-pages. It consumes data (as jsonp) via the iFeed-web web-application 

  <p>
    <tt>
      oppna-program-ifeed
    </tt>
     ?r en del i V?stra G?talandsregionens satsning p? ?ppen k?llkod inom ramen f?r 
    <a href="https://github.com/Vastra-Gotalandsregionen//oppna-program">
      ?ppna Program
    </a>
    . 
  </p>