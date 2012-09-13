<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<liferay-util:html-top>
  <%@ include file="ifeed_css.jsp"%>
</liferay-util:html-top>

<portlet:renderURL var="showEditIfeedFormURL">
  <portlet:param name="view" value="showEditIFeedForm"/>
</portlet:renderURL>

<a href="${showEditIfeedFormURL}">&lt;&lt; Tillbaka</a>
<dl class="ifeed-metadata">
  <c:forEach var="metadata" items="${documentInfo.metadata}">
    <dt><liferay-ui:message key="${metadata.key}.label" />:</dt>
    <dd>
      <c:choose>
        <c:when test="${metadata.value != ''}">${metadata.value}</c:when>
        <c:otherwise>&ndash;</c:otherwise>
      </c:choose>
    </dd>
  </c:forEach>
</dl>