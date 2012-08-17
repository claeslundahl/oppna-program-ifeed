<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/security" prefix="liferay-security"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/tld/vgr-access-guard.tld" prefix="guard"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<aui:layout>
  <aui:column columnWidth="60" first="true">
    <h1>
      <liferay-ui:message key="ifeed.title" />
    </h1>
    <p>
      <liferay-ui:message key="ifeed.description" />
      <!-- Enable when there is a help section written ...<a href="">Läs mer</a>-->
    </p>
  </aui:column>
  <aui:column columnWidth="40" last="true">
    <%-- Keep &nbsp; until column contains content --%>
        &nbsp;
    </aui:column>
</aui:layout>

<%@include file="toolbar.jspf"%>

<div class="parent-search-container">
  <liferay-ui:search-container id='<portlet:namespace/>-parent-search-container' delta="${delta}" orderByCol="${orderByCol}" orderByType="${orderByType}">
    <liferay-ui:search-container-results results="${ifeeds}" total="${numberOfIfeeds}" />
  
    <liferay-ui:search-container-row className="se.vgregion.ifeed.formbean.IFeedFormBeanList.IFeedFormBean" keyProperty="iFeed.id" modelVar="iFeedEntry">
      <portlet:actionURL var="editIFeedURL">
        <portlet:param name="action" value="editIFeed" />
        <portlet:param name="feedId" value="${iFeedEntry.iFeed.id}" />
      </portlet:actionURL>
  
      <liferay-ui:search-container-column-text>
        <liferay-ui:icon method="get" image="rss" target="_blank" url="${iFeedEntry.atomLink}" />
      </liferay-ui:search-container-column-text>
      <liferay-ui:search-container-column-text name="Namn" property="iFeed.name" orderableProperty="name" orderable="true" href="${editIFeedURL}" />
      <liferay-ui:search-container-column-text name="Ägare" property="iFeed.ownershipsText" orderableProperty="ownershipsText" orderable="true" />
      <liferay-ui:search-container-column-text name="Beskrivning" property="iFeed.description" />
  
      <liferay-ui:search-container-column-text>
        <liferay-ui:icon-menu cssClass="">
          <portlet:actionURL name="removeIFeed" var="removeIFeedURL">
            <portlet:param name="action" value="removeIFeed" />
            <portlet:param name="feedId" value="${iFeedEntry.iFeed.id}" />
          </portlet:actionURL>
          <c:if test="${guard:mayEditFeed(user, iFeedEntry.iFeed)}">
            <liferay-ui:icon image="delete" url="${removeIFeedURL}" />
          </c:if>
        </liferay-ui:icon-menu>
      </liferay-ui:search-container-column-text>
    </liferay-ui:search-container-row>
    <liferay-ui:search-iterator />
  </liferay-ui:search-container>
</div>