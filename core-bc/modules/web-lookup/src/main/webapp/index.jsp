<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<jsp:useBean id="app" class="se.vgregion.ifeed.lookup.App" scope="application"/>
<html>
<head>
    <style>
        body {
            background: #555;
        }

        .content {
            max-width: 800px;
            height: 100%;
            margin: auto;
            background: white;
            padding: 10px;
        }
    </style>
</head>
<body>
<div class="content">
    <h1>Sök flöden med hjälp av dokument-id</h1>

    <form>
        <input name="input" value="${param['input']}" placeholder="Dokument-id här">
        <input type="submit" value="!">
    </form>
    <div>
        <hr>
        Svar:
        <c:forEach var="item" items="${app.fetch(param.input)}">
            <c:out value="${item.get('ifeed_id')}"/>
        </c:forEach>
        <hr>
    </div>

    <div style="display: none;">
        <form>
            <hr>
            <input type="submit" name="startIndexing" value="Indexera om">
            <c:if test="${not empty param['startIndexing']}">
                <div>${app.reindex()}</div>
            </c:if>
            <hr>
        </form>
    </div>
</div>
</body>
</html>