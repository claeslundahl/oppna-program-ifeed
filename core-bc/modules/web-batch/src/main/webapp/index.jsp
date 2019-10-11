<%@ page import="se.vgregion.ifeed.jobs.MidnightRun" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>iFeed batch application</title>
</head>
<body>

<div>
    This application starts a batch job that will get Apelon terms copied to the ifeed application (via the db).
    These terms, or vocabulary, will then be used as suggestions when the user formulates constraints for the feeds in
    the
    portlet.
</div>
<div>previousRun:</div>
<div>
    <%=MidnightRun.getPreviousRun()%>
</div>
<div>nextRun:</div>
<div>
    <%=MidnightRun.getNextRun()%>
</div>
<div>lastErrorIfAny:</div>
<div>
    <%=MidnightRun.getLastErrorIfAny()%>
</div>

<script type="text/javascript">
    var script = document.createElement('script');
    script.src = '//ifeed-stage.vgregion.se/iFeed-web-script/se.vgregion.ifeed.Module/se.vgregion.ifeed.Module.nocache.js' + '?ua=' + encodeURIComponent(navigator.userAgent);
    script.type = "text/javascript";
    document.getElementsByTagName('head')[0].appendChild(script);
</script>
<div id="ifeed-data2" style="display:none;">https://ifeed-stage.vgregion.se</div>


</body>
</html>
