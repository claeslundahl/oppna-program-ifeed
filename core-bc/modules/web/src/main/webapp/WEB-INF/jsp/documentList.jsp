<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Dokumentlista</title>
</head>
<body>

<table>
<thead>
<tr>
    <th colspan="2">Titel</th>
    <th>Version</th>
    <th>Författare</th>
    <th>Datum</th>
</tr>
</thead>
<tbody>
<c:forEach items="${result}" var="item">
<tr>
    <td>${item['dc.title']}</td>
    <td>${item['dc.identifier.version']}</td>
    <td>${item['dc.creator']}</td>
    <td>${item['dc.date.saved']}</td>
</tr>
</c:forEach>
</tbody>
</table>

</body>
</html>