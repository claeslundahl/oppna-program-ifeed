<%--

    Copyright 2010 Västra Götalandsregionen

      This library is free software; you can redistribute it and/or modify
      it under the terms of version 2.1 of the GNU Lesser General Public
      License as published by the Free Software Foundation.

      This library is distributed in the hope that it will be useful,
      but WITHOUT ANY WARRANTY; without even the implied warranty of
      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
      GNU Lesser General Public License for more details.

      You should have received a copy of the GNU Lesser General Public
      License along with this library; if not, write to the
      Free Software Foundation, Inc., 59 Temple Place, Suite 330,
      Boston, MA 02111-1307  USA


--%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<portlet:defineObjects/>

<c:set var="toolbarItem" value="add" />

<%@include file="toolbar.jspf" %>

<portlet:actionURL name="addIFeed" var="addIFeedURL">
	<portlet:param name="action" value="addIFeed" />
	<portlet:param name="feedId" value="${iFeed.id}" />
</portlet:actionURL>

<aui:form action="${addIFeedURL}" method="post"
	name="editFeedConfigurationFm" cssClass="edit-feed-configuration-fm">
	<aui:fieldset>
		<h1>Edit iFeed Configuration</h1>
	</aui:fieldset>
	<liferay-ui:panel-container extended="true" id="feedConfiguration"
		persistState="false">
		<liferay-ui:panel collapsible="true" defaultState="open"
			extended="true" id="feedConfigurationBlock1" persistState="false"
			title="Feed info">
			<aui:fieldset>
				<aui:input name="name" value="${ifeed.name}" />
				<aui:input name="description" value="${ifeed.description}" />
			</aui:fieldset>
			<aui:fieldset>
				<c:forEach items="${ifeed.filters}" var="filter" varStatus="filtersRow">
					<aui:input name="filters[${filtersRow.index}]" value="${filter}" type="text" />
				</c:forEach>
			</aui:fieldset>
		</liferay-ui:panel>
	</liferay-ui:panel-container>
	<aui:button-row>
		<aui:button type="submit" value="save" />
		<portlet:renderURL var="viewIFeedsURL">
			<portlet:param name="view" value="showIFeeds" />
		</portlet:renderURL>
		<aui:button onClick="${viewIFeedsURL}" type="cancel" />
	</aui:button-row>
</aui:form>