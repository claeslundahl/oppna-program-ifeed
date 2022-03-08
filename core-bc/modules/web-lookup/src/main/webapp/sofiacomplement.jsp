<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<jsp:useBean id="complementationApp" class="se.vgregion.ifeed.lookup.ComplementationApp" scope="application"/>
<jsp:useBean id="app" class="se.vgregion.ifeed.lookup.App" scope="application"/>
<html>
<head>
    <link rel="stylesheet" href="style.css">
</head>
<body>
<div class="content">
    <h1>Skapa flödeskomplettering, SOFIA</h1>
    <%--<h1>Skapa flödeskomplettering, SOFIA-STY</h1>--%>

    <form>
        <input class="filter-input" name="input" value="${param['input']}"
               placeholder="Id:n på flöden som ska kompletteras här.">
        <input type="submit" value="Kör" onclick="var e=this;setTimeout(function(){e.disabled=true;},0);return true;">
    </form>

    <div style="display: none">
        <!--
            ${complementationApp.run(param.input)}
        -->
    </div>


    <h2>Kompletterade flöden</h2>
    <div class="result">
        <c:if test="${empty complementationApp.justComplemented}">
            <div>
                <div>Inga.</div>
            </div>
        </c:if>
        <c:forEach var="item" items="${complementationApp.justComplemented}">
            <div>
                <div>${item.name}</div>
                <a href="${app.adminUrl}?feedId=${item['id']}">
                        ${item['id']}
                </a>
            </div>
        </c:forEach>
    </div>

    <h2>Ej bearbetade, hade redan komplettering</h2>
    <div class="result">
        <c:if test="${empty complementationApp.alreadyComplemented}">
            <div>
                <div>inga.</div>
            </div>
        </c:if>
        <c:forEach var="item" items="${complementationApp.alreadyComplemented}">
            <div>
                <div>${item.name}</div>
                <a href="${app.adminUrl}?feedId=${item['id']}">
                        ${item['id']}
                </a>
            </div>
        </c:forEach>
    </div>

    <h2>Ej bearbetade, hade inte översättningsbar metadata</h2>
    <div class="result">
        <c:if test="${empty complementationApp.notComplemented}">
            <div>
                <div>inga.</div>
            </div>
        </c:if>
        <c:forEach var="item" items="${complementationApp.notComplemented}">
            <div>
                <div>${item.name}</div>
                <a href="${app.adminUrl}?feedId=${item['id']}">
                        ${item['id']}
                </a>
            </div>
        </c:forEach>
    </div>

    <h2>Ej funna flöden</h2>
    <div class="result">
        <c:if test="${empty complementationApp.notFound}">
            <div>
                <div>inga.</div>
            </div>
        </c:if>
        <c:forEach var="item" items="${complementationApp.notFound}">
            <div>
                <div>${item}</div>
            </div>
        </c:forEach>
    </div>

</div>
</body>
</html>
