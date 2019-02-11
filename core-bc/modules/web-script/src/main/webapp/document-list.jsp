<%--
  Created by IntelliJ IDEA.
  User: clalu4
  Date: 2018-10-09
  Time: 15:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

<div><%= request.getContextPath() %>
</div>
<div><%= request.getRequestURI() %>
</div>
<div><%= request.getRequestURL().toString().replace("/iFeed-web-script/document-list.jsp", "").replace("http://", "") %>
</div>

<%--<div>
    /iFeed-web-script
    /iFeed-web-script/document-list.jsp
    http://vgas0564.vgregion.se:8081/iFeed-web-script/document-list.jsp
</div>--%>

<script type="text/javascript">
    var script = document.createElement('script');
    script.src = 'se.vgregion.ifeed.Module/se.vgregion.ifeed.Module.nocache.js' + '?ua=' + encodeURIComponent(navigator.userAgent);
    script.type = "text/javascript";
    document.getElementsByTagName('head')[0].appendChild(script);
</script>

<div id="ifeed-data2"><%= request.getRequestURL().toString().replace("/iFeed-web-script/document-list.jsp", "") %></div>

<div>
    <h3>Kontaktombudsmöten SkaS Falköping</h3>
    <div class="ifeedDocList" columnes="dc.title|Titel (autokomplettering)|left|70" fontsize="auto" defaultsortcolumn="dc.title" defaultsortorder="desc" showtableheader="yes" linkoriginaldoc="no" limit="0" hiderightcolumn="no" feedid="36307" data-url="https://ifeed.vgregion.se/iFeed-web/meta.json?instance=36307&amp;f=dc.title&amp;f=dc.title">
    </div>

    <h3>Kontaktombudsmöten SkaS Lidköping</h3>
    <div class="ifeedDocList" columnes="dc.title|Titel (autokomplettering)|left|70" fontsize="auto" defaultsortcolumn="dc.title" defaultsortorder="desc" showtableheader="yes" linkoriginaldoc="no" limit="0" hiderightcolumn="no" feedid="36303" data-url="https://ifeed.vgregion.se/iFeed-web/meta.json?instance=36307&amp;f=dc.title&amp;f=dc.title">
    </div>

    <h3>Kontaktombudsmöten SkaS Skövde</h3>
    <div class="ifeedDocList" columnes="dc.title|Titel (autokomplettering)|left|70" fontsize="auto" defaultsortcolumn="dc.title" defaultsortorder="desc" showtableheader="yes" linkoriginaldoc="no" limit="0" hiderightcolumn="no" feedid="36312" data-url="https://ifeed.vgregion.se/iFeed-web/meta.json?instance=36307&amp;f=dc.title&amp;f=dc.title">
    </div>
</div>

<div>
    <h3>Alla riktlinjer 121938</h3>
    <div class="ifeedDocList" columnes="dc.title|Titel (autokomplettering)|left|70" fontsize="auto" defaultsortcolumn="dc.title" defaultsortorder="desc" showtableheader="yes" linkoriginaldoc="no" limit="0" hiderightcolumn="no" feedid="121938" data-url="https://ifeed.vgregion.se/iFeed-web/meta.json?instance=36307&amp;f=dc.title&amp;f=dc.title">
    </div>

    <h3>Alla riktlinjer 3806331</h3>
    <div class="ifeedDocList" columnes="dc.title|Titel (autokomplettering)|left|70" fontsize="auto" defaultsortcolumn="dc.title" defaultsortorder="desc" showtableheader="yes" linkoriginaldoc="no" limit="0" hiderightcolumn="no" feedid="3806331" data-url="https://ifeed.vgregion.se/iFeed-web/meta.json?instance=36307&amp;f=dc.title&amp;f=dc.title">
    </div>

    <h3>Andningsorganen 3331656</h3>
    <div class="ifeedDocList" columnes="dc.title|Titel (autokomplettering)|left|70" fontsize="auto" defaultsortcolumn="dc.title" defaultsortorder="desc" showtableheader="yes" linkoriginaldoc="no" limit="0" hiderightcolumn="no" feedid="3331656" data-url="https://ifeed.vgregion.se/iFeed-web/meta.json?instance=36307&amp;f=dc.title&amp;f=dc.title">
    </div>
</div>

<div>
    <a href="http://fokus.skar.vgregion.se/sv/Skaraborgs-Sjukhus/Omraden-Nya-Fokus/M2/M2---test/Vardplaneringsteamet/AnteckningarProtokoll/">
        Link to original
    </a>
</div>

</body>
</html>
