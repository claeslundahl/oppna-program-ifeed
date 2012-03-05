<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Dokumentegenskaper</title>
<link href="${pageContext.request.contextPath}/resources/style.css" rel="stylesheet" type="text/css">
</head>
<body>
  <table class="ifeed-metadata">
    <c:forEach items="${documentInfo.metadata}" var="item">
      <c:if test="${item.value != '' and fields[item.key].inHtml}">
        <tr>
          <td>${fields[item.key].name}</td> <td>${item.value}</td>
        </tr>
      </c:if>
    </c:forEach>
  </table>
  </dl>
</body>
</html>
