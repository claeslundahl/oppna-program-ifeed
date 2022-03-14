<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<jsp:useBean id="complementationStyApp" class="se.vgregion.ifeed.lookup.SofiaStyComplementationApp"
             scope="application"/>
<jsp:useBean id="app" class="se.vgregion.ifeed.lookup.App" scope="application"/>
<html>
<head>
    <link rel="stylesheet" href="style.css">
</head>
<body>
<div class="content">
    <h1>Skapa flödeskomplettering, SOFIA-STY</h1>

    <form>
        <input class="filter-input" name="input" value="${param['input']}"
               placeholder="Id:n på flöden som ska kompletteras här.">
        <input type="submit" value="Kör" onclick="var e=this;setTimeout(function(){e.disabled=true;},0);return true;">
    </form>

    <div style="display: none">
        <!--
            ${complementationStyApp.run(param.input)}
        -->
    </div>


    <h2>Kompletterade flöden</h2>
    <div class="result">
        <c:if test="${empty complementationStyApp.justComplemented}">
            <div>
                <div>Inga.</div>
            </div>
        </c:if>
        <c:forEach var="item" items="${complementationStyApp.justComplemented}">
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
        <c:if test="${empty complementationStyApp.alreadyComplemented}">
            <div>
                <div>inga.</div>
            </div>
        </c:if>
        <c:forEach var="item" items="${complementationStyApp.alreadyComplemented}">
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
        <c:if test="${empty complementationStyApp.notComplemented}">
            <div>
                <div>inga.</div>
            </div>
        </c:if>
        <c:forEach var="item" items="${complementationStyApp.notComplemented}">
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
        <c:if test="${empty complementationStyApp.notFound}">
            <div>
                <div>inga.</div>
            </div>
        </c:if>
        <c:forEach var="item" items="${complementationStyApp.notFound}">
            <div>
                <div>${item}</div>
            </div>
        </c:forEach>
    </div>

    <h2>Flöden med otillåtna filter</h2>
    <div>
        Handlingstyp (dc.type.record),
        Gruppering av handlingstyp (dc.type.document) och
        Skapat av enhet (dc.creator.forunit.id)
    </div>
    <div class="result">
        <c:if test="${empty complementationStyApp.withForbiddenFilters}">
            <div>
                <div>inga.</div>
            </div>
        </c:if>
        <c:forEach var="item" items="${complementationStyApp.withForbiddenFilters}">
            <div>
                <div>${item.name}</div>
                <a href="${app.adminUrl}?feedId=${item['id']}">
                        ${item['id']}
                </a>
            </div>
        </c:forEach>
    </div>

</div>
</body>
</html>
