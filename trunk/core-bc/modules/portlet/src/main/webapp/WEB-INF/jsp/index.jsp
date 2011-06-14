<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<portlet:defineObjects />

<c:set var="toolbarItem" value="${view}" />

<%@include file="toolbar.jspf"%>

<h1>${view}</h1>
<liferay-ui:search-container id='<portlet:namespace/>-parent-search-container' delta="10" orderByCol="1">
  <liferay-ui:search-container-results results="${ifeeds}" total="${fn:length(ifeeds)}" />

  <liferay-ui:search-container-row className="se.vgregion.ifeed.types.IFeed" keyProperty="id" modelVar="iFeed">
    <portlet:actionURL var="editIFeedURL">
      <portlet:param name="action" value="editIFeed" />
      <portlet:param name="feedId" value="${iFeed.id}" />
    </portlet:actionURL>

    <portlet:resourceURL var="atomLinkUrl">
      <portlet:param name="feedId" value="${iFeed.id}" />
    </portlet:resourceURL>

    <liferay-ui:search-container-column-text>
      <liferay-ui:icon image="rss" url="${fn:replace(atomFeedUrl, '%s', iFeed.id)}" />
    </liferay-ui:search-container-column-text>
    <liferay-ui:search-container-column-text name="Namn" property="name" orderable="true" href="${editIFeedURL}" />
    <liferay-ui:search-container-column-text name="Ägare" property="userId" orderable="true" />
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
