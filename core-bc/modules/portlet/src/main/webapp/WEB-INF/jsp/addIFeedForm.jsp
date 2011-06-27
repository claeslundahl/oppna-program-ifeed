<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<portlet:defineObjects/>

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
				<aui:input name="name" label="feed-name" value="${ifeed.name}" />
				<aui:input name="description" label="feed-description" value="${ifeed.description}" />
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