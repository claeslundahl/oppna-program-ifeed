<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Dokumentegenskaper</title>
</head>
<body>

<dl class="ifeed-metadata">
<c:forEach items="${result}" var="item">
    <dt><spring:message key="${item.key}.label" />:</dt>
    <dd>
      <c:choose>
        <c:when test="${item.value != ''}">${item.value}</c:when>
        <c:otherwise>&ndash;</c:otherwise>
      </c:choose>
    </dd>
</c:forEach>
</dl>

hej

</body>
</html>