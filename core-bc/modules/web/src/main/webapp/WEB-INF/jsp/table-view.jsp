<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tld/util.tld" prefix="vgr" %>

<html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=UTF-8">
        <title>Web Application Starter Project</title>
        <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7"/>
        <script type="text/javascript"
                src="${webScriptUrl}">
        </script>
    </head>
    <body>
        <div id="ifeed-data2" style="display:none;">${webScriptJsonUrl}</div>
        <h1>${tableName}</h1>
        <div>
            ${iFeedCode}
        </div>
    </body>
</html>