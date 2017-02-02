<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>iFeed batch application</title>
</head>
<body>

<div>
  This application starts a batch job that will get Apelon terms copied to the ifeed application (via the db).
  These terms, or vocabulary, will then be used as suggestions when the user formulates constraints for the feeds in the
  portlet.
</div>
<div>previousRun:</div>
<div>
  <%=se.vgregion.ifeed.jobs.DailyRun.previousRun%>
</div>
<div>nextRun:</div>
<div>
  <%=se.vgregion.ifeed.jobs.DailyRun.nextRun%>
</div>
<div>lastErrorIfAny:</div>
<div>
  <%=se.vgregion.ifeed.jobs.DailyRun.lastErrorIfAny}%>
</div>

</body>
</html>