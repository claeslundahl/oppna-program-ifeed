<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<jsp:useBean id="app" class="se.vgregion.ifeed.lookup.App" scope="application"/>
<html>
<head>
    <link rel="stylesheet" href="style.css">
</head>
<body>
<div class="content">
    <h1>Sök flöden med hjälp av dokument-id</h1>

    <form>
        <input class="filter-input" name="input" value="${param['input']}" placeholder="Dokument-id här">
        <input type="submit" value="Sök">
    </form>

    <table>

        <thead>
        <tr>
            <th>Flödes-Id</th>
            <th>Namn</th>
            <th>Skapare</th>
            <th>Admins</th>
            <th>Förvaltning</th>
            <th>Grupp</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach var="item" items="${app.fetch(param.input)}">
            <tr>
                <td>
                    <a href="${app.adminUrl}?feedId=${item['id']}">
                            ${item['id']}
                    </a>
                </td>
                <td>${item['name']}</td>
                <td>${item['userid']}</td>
                <td>
                    <c:forEach var="ownership" items="${item.getOwnerships()}">
                        ${ownership.user_id}
                    </c:forEach>
                </td>
                <td>${item.getDepartment() == null ? '' : item.getDepartment().name}</td>
                <td>${item.getGroup() == null ? '' : item.getGroup().name}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

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
