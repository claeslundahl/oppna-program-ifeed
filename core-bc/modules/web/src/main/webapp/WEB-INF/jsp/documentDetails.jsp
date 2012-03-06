<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Dokumentegenskaper</title>
    <link href="${pageContext.request.contextPath}/resources/style.css" rel="stylesheet" type="text/css">
</head>
<body>

<dl class="ifeed-metadata">
    <table id="ifeed-metadata-table">
        <tbody>
        <c:forEach items="${fieldInfs}" var="fieldInf">
            <c:if test="${fieldInf.inHtmlView}">
                <tr><td>&nbsp;</td></tr><%--Empty row for appearance--%>
                <tr class="section">
                <td colspan="2">
                    <div class="heading"><c:out value="${fieldInf.name}"/></div>
                </td>
                <c:set var="count" value="0"/>
                <c:forEach items="${fieldInf.children}" var="child">
                    <c:if test="${child.inHtmlView and not empty idValueMap[child.id]}">
                        <c:set var="count" value="${count + 1}"/>
                        <tr class="${count % 2 == 0 ? 'even' : 'odd'}">
                            <td class="key">
                                <c:out value="${child.name}:"/>
                            </td>
                            <td class="value">
                                <c:out value="${idValueMap[child.id]}"/>
                            </td>
                        </tr>
                    </c:if>
                </c:forEach>
                </tr>
            </c:if>
        </c:forEach>
        </tbody>
    </table>
</dl>
</body>
</html>
