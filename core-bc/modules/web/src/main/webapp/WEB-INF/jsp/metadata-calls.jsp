<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/tld/util.tld" prefix="vgr"%>

<html>
	<head>
		<link href="${pageContext.request.contextPath}/resources/style.css" rel="stylesheet" type="text/css">
	</head>
	<body>
		<table class="doc-list">
		    <tr>
		        <th>Anropad från sida</th>
		        <th>Antal gånger</th>
		    </tr>
			<c:forEach items="${keys}" var="key">
				<tr>
                    <td>${key}</td>
                    <td>${values[key]}</td>
				</tr>
			</c:forEach>
		</table>
	</body>
</html>