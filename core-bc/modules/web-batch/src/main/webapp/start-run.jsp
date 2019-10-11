<%@ page import="se.vgregion.ifeed.jobs.MidnightRun" %>
<%@ page import="java.util.function.Function" %>
<%@ page import="se.vgregion.ifeed.jobs.MetadataJob" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Starts the job!</title>
</head>
<body>
<div>
    <%=
        (Function<String, String>) s -> {
            new MetadataJob().todo();
            return "Called the thing!";
        }
    %>
</div>

</body>
</html>
