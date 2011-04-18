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

<c:set var="toolbarItem" value="view-all" />

<%@include file="toolbar.jspf" %>

<liferay-ui:search-container 
	id='<%= renderResponse.getNamespace() + "parentSearchContainer" %>'
	delta="10">
	<liferay-ui:search-container-results results="${ifeeds}"
		total="${fn:length(ifeeds)}" />

	<liferay-ui:search-container-row
		className="se.vgregion.ifeed.types.IFeed" keyProperty="id"
		modelVar="iFeed">

		<liferay-ui:search-container-column-text name="Namn" property="name" />
		<liferay-ui:search-container-column-text name="Beskrivning"
			property="description" />

		<liferay-ui:search-container-column-text>
			<liferay-ui:icon-menu cssClass="">
				<portlet:renderURL var="editIFeedURL">
					<portlet:param name="action" value="showEditIFeedForm" />
					<portlet:param name="feedId" value="${iFeed.id}" />
				</portlet:renderURL>
				<liferay-ui:icon image="edit"
					url="<%= editIFeedURL.toString() %>" />
				<portlet:actionURL name="removeIFeed" var="removeIFeedURL">
					<portlet:param name="action" value="removeIFeed" />
					<portlet:param name="feedId" value="${iFeed.id}" />
				</portlet:actionURL>
				<liferay-ui:icon image="delete"
					url="<%=removeIFeedURL.toString() %>" />
			</liferay-ui:icon-menu>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>
	<liferay-ui:search-iterator />
</liferay-ui:search-container>
