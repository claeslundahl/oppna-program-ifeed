<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/util.tld" prefix="vgr"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Dokumentlista</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/style.css"></link>
</head>
<body>

<ul class="doc-list">
<c:forEach items="${result}" var="item">
<li>
  <a href="${pageContext.request.contextPath}/ifeed/${vgr:urlEncode(item['dc.identifier.documentid'])}/details">
    <img src="${pageContext.request.contextPath}/information.png"/>
  </a> 
  <a href="${item.url}">${item['dc.title']}</a>
</li>
</c:forEach>
</ul>

</body>
</html>