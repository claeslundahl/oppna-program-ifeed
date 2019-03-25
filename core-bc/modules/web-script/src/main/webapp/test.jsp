<%@ page isELIgnored="false" %>
<!doctype html>
<!-- The DOCTYPE declaration above will set the -->
<!-- browser's rendering engine into -->
<!-- "Standards Mode". Replacing this declaration -->
<!-- with a "Quirks Mode" doctype may lead to some -->
<!-- differences in layout. -->

<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Web Application Starter Project</title>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>
    <script type="text/javascript" src="se.vgregion.ifeed.Module/se.vgregion.ifeed.Module.nocache.js"></script>
</head>

<!--                                           -->
<!-- The body can have arbitrary html, or      -->
<!-- you can leave the body empty if you want  -->
<!-- to toTableDef a completely dynamic UI.        -->
<!--                                           -->
<body>

<h1>${param.ifeedId}</h1>
<div class="ifeedDocList" columnes="dc.title|Titel|left|70" fontSize="auto"
     defaultsortcolumn="dc.title" defaultsortorder="asc" extraSortColumns="[]" showTableHeader="yes"
     linkOriginalDoc="no" limit="0" hiderightcolumn="no" usepost="no" feedid='${param.ifeedId}'></div>

<h2>Sorterat baklänges</h2>
<div class="ifeedDocList" columnes="dc.title|Titel|left|70" fontSize="auto"
     defaultsortcolumn="dc.title" defaultsortorder="desc" extraSortColumns="[]" showTableHeader="yes"
     linkOriginalDoc="no" limit="0" hiderightcolumn="no" usepost="no" feedid='${param.ifeedId}'></div>

<h2>Utan kolumnhuvuden</h2>
<div class="ifeedDocList" columnes="dc.title|Titel|left|70" fontSize="auto"
     defaultsortcolumn="dc.title" defaultsortorder="asc" extraSortColumns="[]" showTableHeader="no"
     linkOriginalDoc="no" limit="0" hiderightcolumn="yes" usepost="no" feedid='${param.ifeedId}'></div>

<h2>Länkar till orginal</h2>
<div class="ifeedDocList" columnes="dc.title|Titel|left|70" fontSize="auto"
     defaultsortcolumn="dc.title" defaultsortorder="asc" extraSortColumns="[]" showTableHeader="yes"
     linkOriginalDoc="yes" limit="0" hiderightcolumn="yes" usepost="no" feedid='${param.ifeedId}'></div>

<h2>Med fontsize 20</h2>
<div class="ifeedDocList" columnes="dc.title|Titel|left|70" fontSize="20"
     defaultsortcolumn="dc.title" defaultsortorder="asc" extraSortColumns="[]" showTableHeader="yes"
     linkOriginalDoc="no" limit="0" hiderightcolumn="no" usepost="no" feedid='${param.ifeedId}'></div>

<h2>Max tre dokument</h2>
<div class="ifeedDocList" columnes="dc.title|Titel|left|70" fontSize="auto"
     defaultsortcolumn="dc.title" defaultsortorder="asc" extraSortColumns="[]" showTableHeader="yes"
     linkOriginalDoc="no" limit="3" hiderightcolumn="no" usepost="no" feedid='${param.ifeedId}'></div>

<h2>Sorterat på giltighetsdatum</h2>
<div
        class="ifeedDocList"
        columnes="dc.title|Titel|left|40,dc.date.validto|Giltighetsdatum tom|left|15,dc.date.availablefrom|Tillgänglighetsdatum from|left|15,dc.date.availablefrom|Tillgänglighetsdatum from|left|15,dc.date.availableto|Tillgänglighetsdatum tom|left|15"
        fontSize="auto"
        defaultsortcolumn="dc.date.validto"
        defaultsortorder="asc"
        extraSortColumns="[]"
        showTableHeader="yes"
        linkOriginalDoc="no"
        limit="0"
        hiderightcolumn="no"
        usepost="no"
        feedid='${param.ifeedId}'>
</div>

<h2>Några kolumner</h2>
<div
        class="ifeedDocList"
        columnes="dc.title|Titel|left|70,dc.date.validfrom|Giltighetsdatum from|left|15,dc.date.validto|Giltighetsdatum tom|left|15"
        fontSize="auto"
        defaultsortcolumn="dc.title"
        defaultsortorder="asc"
        extraSortColumns="[]"
        showTableHeader="yes"
        linkOriginalDoc="no"
        limit="0"
        hiderightcolumn="no"
        usepost="no"
        feedid='${param.ifeedId}'>
</div>
<noscript>
    <iframe src='http://ifeed.vgregion.se/iFeed-web/documentlists/118188/?by=dc.title&dir=asc' id='iframenoscript'
            name='iframenoscript' style='width: 100%; height: 400px' frameborder='0'>
    </iframe>
</noscript>

<h2>Som iframe</h2>
<iframe src='#{request.contextPath}/documentlists/${param.ifeedId}/?by=dc.title&dir=asc' id='iframenoscript'
        name='iframenoscript' style='width: 100%; height: 400px' frameborder='0'>
</iframe>

<div id="ifeed-data2">http://localhost:8081</div>

<script type="text/javascript">
    document.getElementById('ifeed-data2').innerHTML = (location.protocol + '//' + location.host);
</script>

</body>
</html>
