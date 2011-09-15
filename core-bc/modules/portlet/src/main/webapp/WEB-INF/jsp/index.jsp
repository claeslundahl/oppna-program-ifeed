<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<portlet:defineObjects />

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

<liferay-ui:search-container id='<portlet:namespace/>-parent-search-container' delta="${delta}"
  orderByCol="${orderByCol}" orderByType="${orderByType}">
  <liferay-ui:search-container-results results="${ifeeds}" total="${numberOfIfeeds}" />

  <liferay-ui:search-container-row className="se.vgregion.ifeed.types.IFeed" keyProperty="id" modelVar="iFeed">
    <portlet:actionURL var="editIFeedURL">
      <portlet:param name="action" value="editIFeed" />
      <portlet:param name="feedId" value="${iFeed.id}" />
    </portlet:actionURL>

    <liferay-ui:search-container-column-text>
      <liferay-ui:icon method="get" image="rss" target="_blank" url="${fn:replace(atomFeedUrl, '%s', iFeed.id)}" />
    </liferay-ui:search-container-column-text>
    <liferay-ui:search-container-column-text name="Namn" property="name" orderableProperty="name" orderable="true"
      href="${editIFeedURL}" />
    <liferay-ui:search-container-column-text name="Ägare" property="userId" orderableProperty="userId"
      orderable="true" />
    <liferay-ui:search-container-column-text name="Beskrivning" property="description" />

    <liferay-ui:search-container-column-text>
      <liferay-ui:icon-menu cssClass="">
        <portlet:actionURL name="removeIFeed" var="removeIFeedURL">
          <portlet:param name="action" value="removeIFeed" />
          <portlet:param name="feedId" value="${iFeed.id}" />
        </portlet:actionURL>
        <liferay-ui:icon image="delete" url="${removeIFeedURL}" />
      </liferay-ui:icon-menu>
    </liferay-ui:search-container-column-text>
  </liferay-ui:search-container-row>
  <liferay-ui:search-iterator />
</liferay-ui:search-container>
