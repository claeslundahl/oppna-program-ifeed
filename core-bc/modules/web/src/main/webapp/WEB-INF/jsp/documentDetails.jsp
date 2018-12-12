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
    <style type="text/css">
        .sys-info {
            display: none;
        }
    </style>
</head>
<body>

<div id="table-container">
    <h1>Metadata för ${item['title']}</h1>
    <table class="ifeed-metadata-table">
        <tbody>
        <c:forEach items="${fields}" var="field">
            <tr>
                <td class="sys-info">${field.key}</td>
                <td class="${(empty field.key) ? 'heading': 'row key'}">${field.label}</td>
                <td class="value">${field.value}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>
