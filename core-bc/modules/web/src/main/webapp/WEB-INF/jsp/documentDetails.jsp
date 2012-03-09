<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Dokumentegenskaper</title>
    <link href="${pageContext.request.contextPath}/resources/style.css" rel="stylesheet" type="text/css">
</head>
<body>

<div id="table-container">
    <h1>Metadata för ${idValueMap['dc.title']}</h1>
    <c:forEach items="${fieldInfs}" var="fieldInf">
    <c:if test="${fieldInf.inHtmlView}">
    <table class="ifeed-metadata-table">
        <thead>
            <tr>
                <td colspan="2">
                    <c:out value="${fieldInf.name}"/>
                </td>
            </tr>
        </thead>
        <tbody>
        <c:forEach items="${fieldInf.children}" var="child">
            <c:if test="${child.inHtmlView and not empty idValueMap[child.id]}">
                <tr>
                    <td class="key">
                        <c:out value="${child.name}:"/>
                    </td>
                    <td class="value">
                        <c:set var="value" value="${idValueMap[child.id]}"/>
                        <c:choose>
                            <c:when test="${fn:startsWith(value, 'http')==true}">
                                <a href="${value}" target="_blank">${value}</a>
                            </c:when>
                            <c:otherwise>
                                <c:out value="${idValueMap[child.id]}"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:if>
        </c:forEach>
        </tr>
        </c:if>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>
