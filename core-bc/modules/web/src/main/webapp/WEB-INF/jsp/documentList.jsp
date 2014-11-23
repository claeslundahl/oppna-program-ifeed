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
		<ul class="doc-list">
			<c:forEach items="${result}" var="item">
				<li class="clearfix">
					<a class="meta" target="_blank" href="${pageContext.request.contextPath}/documents/${fn:replace(item['dc.identifier.documentid'], 'workspace://SpacesStore/', '')}/metadata">
		        		<%-- 
		          			<img src="${pageContext.request.contextPath}/resources/information.png" />
		          		--%>
	          			Metadata
					</a> 
					<c:choose>
						<c:when test="${not empty(item['dc.identifier.native']) and feed.linkNativeDocument}">
							<a class="document" target="_blank" href="${fn:replace(fn:replace(item['dc.identifier.native'], '[', ''), ']', '')}">${item['dc.title']}</a>
						</c:when>
						<c:otherwise>
							<a class="document" target="_blank" href="${item.url}">${item['dc.title']}</a>
						</c:otherwise>
					</c:choose>
				</li>
			</c:forEach>
		</ul>
	</body>
</html>