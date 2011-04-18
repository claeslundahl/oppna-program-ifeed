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
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<c:set var="toolbarItem" value="" />

<%@include file="toolbar.jspf"%>

<portlet:actionURL name="editIFeedURL" var="editIFeedURL">
	<portlet:param name="action" value="editIFeed" />
	<portlet:param name="feedId" value="${iFeed.id}" />
</portlet:actionURL>

<aui:layout>
	<aui:column columnWidth="66">
		<h1>${ifeed.name}</h1>
		<p>${ifeed.description}</p>
	</aui:column>
	<aui:column columnWidth="33">
		<a href=""><img
			src="${themeDisplay.pathThemeImages}/common/edit.png" /> Redigera</a>
	</aui:column>
</aui:layout>

<%-- <aui:form action="<%= editIFeedURL %>" method="post" --%>
<!-- 	name="editFeedConfigurationFm" cssClass="edit-feed-configuration-fm"> -->
<%-- 	<aui:fieldset> --%>
<%-- 		<aui:input name="name" value="${ifeed.name}" /> --%>
<%-- 		<aui:input name="description" value="${ifeed.description}" /> --%>
<%-- 	</aui:fieldset> --%>
<%-- 	<aui:fieldset> --%>
<%-- 		<c:forEach items="${ifeed.filters}" var="filter" --%>
<!-- 			varStatus="filtersRow"> -->
<%-- 			<aui:input name="filters[${filtersRow.index}]" --%>
<%-- 				value="${filter.filterQuery}" type="text" /> --%>
<%-- 		</c:forEach> --%>
<%-- 	</aui:fieldset> --%>
<%-- 	<aui:button-row> --%>
<%-- 		<aui:button type="submit" value="save" /> --%>
<%-- 		<portlet:renderURL var="viewURL"> --%>
<%-- 			<portlet:param name="action" value="showIFeeds" /> --%>
<%-- 		</portlet:renderURL> --%>
<%-- 		<aui:button onClick="${viewURL}" type="cancel" /> --%>
<%-- 	</aui:button-row> --%>
<%-- </aui:form> --%>
<liferay-ui:panel-container extended="true" id="feedConfiguration"
	persistState="false" accordion="true">
	<liferay-ui:panel collapsible="true" defaultState="open"
		extended="true" id="feedConfigurationBlock1" persistState="false"
		title="Dokumentbeskrivning">
		<p>H&auml;r kan du l&auml;gga till filter som verkar p&aring;
			f&auml;lten i dokumentbeskrivningen.</p>
		<aui:form action="<%= editIFeedURL %>" method="post"
			name="editFeedConfigurationFm2" cssClass="edit-feed-configuration-fm">
			<aui:fieldset>
				<aui:input name="title" value="" />
				<aui:input name="altTitle" value="" />
				<aui:input name="docType" value="" />
				<aui:input name="HSA" value="" />
				<aui:input name="HoS" value="" />
				<aui:input name="savedBy" value="" />
				<aui:input name="docStatus" value="" />
			</aui:fieldset>
		</aui:form>
		<a href=""><img
			src="${themeDisplay.pathThemeImages}/common/edit.png" /> Redigera
			filter</a>
		<a href=""><img
			src="${themeDisplay.pathThemeImages}/common/delete.png" /> Ta bort
			filter</a>
		<a href=""><img
			src="${themeDisplay.pathThemeImages}/common/add.png" /> Lägg till
			filter</a>
	</liferay-ui:panel>
	<liferay-ui:panel collapsible="true" defaultState="collapsed"
		extended="true" id="feedConfigurationBlock2" persistState="false"
		title="Skapat av och f&ouml;r">
		<p>H&auml;r kan du l&auml;gga till filter som verkar p&aring;
			f&auml;lten som anger vem som skapat dokumentet och vilka de &auml;r
			skapat f&ouml;r.</p>
		<aui:form action="<%= editIFeedURL %>" method="post"
			name="editFeedConfigurationFm3" cssClass="edit-feed-configuration-fm">
			<aui:fieldset>
				<aui:input name="createdByFix" value="" />
				<aui:input name="createdByFree" value="" />
				<aui:input name="createdForUnit" value="" />
				<aui:input name="createdForProject" value="" />
				<a href=""><img
					src="${themeDisplay.pathThemeImages}/common/edit.png" /> Redigera
					filter</a>
				<a href=""><img
					src="${themeDisplay.pathThemeImages}/common/delete.png" /> Ta bort
					filter</a>
			</aui:fieldset>
		</aui:form>
		<a href=""><img
			src="${themeDisplay.pathThemeImages}/common/add.png" /> Lägg till
			filter</a>
	</liferay-ui:panel>
	<liferay-ui:panel collapsible="true" defaultState="collapsed"
		extended="true" id="feedConfigurationBlock3" persistState="false"
		title="Ansvariga">
		<p>H&auml;r kan du l&auml;gga till filter som verkar p&aring;
			f&auml;lten som anger ansvariga f&ouml;r dokumentet.</p>
		<aui:form action="<%= editIFeedURL %>" method="post"
			name="editFeedConfigurationFm4" cssClass="edit-feed-configuration-fm">
			<aui:fieldset>
				<aui:input name="responsible" value="" />
				<aui:input name="responsibleFunc" value="" />
				<aui:input name="archiveCreator" value="" />
				<a href=""><img
					src="${themeDisplay.pathThemeImages}/common/edit.png" /> Redigera
					filter</a>
				<a href=""><img
					src="${themeDisplay.pathThemeImages}/common/delete.png" /> Ta bort
					filter</a>
			</aui:fieldset>
		</aui:form>
		<a href=""><img
			src="${themeDisplay.pathThemeImages}/common/add.png" /> Lägg till
			filter</a>
	</liferay-ui:panel>
	<liferay-ui:panel collapsible="true" defaultState="collapsed"
		extended="true" id="feedConfigurationBlock4" persistState="false"
		title="Giltighet och tillg&auml;nglighet">
		<p>H&auml;r kan du l&auml;gga till filter som verkar p&aring;
			f&auml;lten som beskriver dokumentets giltighet och
			tillg&auml;nglighet.</p>
		<aui:form action="<%= editIFeedURL %>" method="post"
			name="editFeedConfigurationFm5" cssClass="edit-feed-configuration-fm">
			<aui:fieldset>
				<aui:legend label="filter" />
				<aui:layout>
					<aui:column columnWidth="30">
						<aui:field-wrapper name="validFromDate" label="validFromDate">
							<liferay-ui:input-date yearRangeEnd="2100" yearRangeStart="1970"
								yearParam="validFromYear" monthParam="validFromMonth"
								dayParam="validFromDay">
							</liferay-ui:input-date>
						</aui:field-wrapper>
						<aui:field-wrapper name="validToDate" label="validToDate">
							<liferay-ui:input-date yearRangeEnd="2100" yearRangeStart="1970"
								yearParam="validToYear" monthParam="validToMonth"
								dayParam="validToDay">
							</liferay-ui:input-date>
						</aui:field-wrapper>
						<aui:field-wrapper name="availableFromDate"
							label="availableFromDate">
							<liferay-ui:input-date yearRangeEnd="2100" yearRangeStart="1970"
								yearParam="availableFromYear" monthParam="availableFromMonth"
								dayParam="availableFromDay">
							</liferay-ui:input-date>
						</aui:field-wrapper>
						<aui:field-wrapper name="availableToDate" label="availableToDate">
							<liferay-ui:input-date yearRangeEnd="2100" yearRangeStart="1970"
								yearParam="availableToYear" monthParam="availableToMonth"
								dayParam="availableToDay">
							</liferay-ui:input-date>
						</aui:field-wrapper>
					</aui:column>
					<aui:column columnWidth="30">
						<aui:field-wrapper label="filterByValidFromDate">
							<aui:input name="filterByValidFromDate" type="checkbox" value=""
								label="" />
						</aui:field-wrapper>
						<aui:field-wrapper label="filterByValidToDate">
							<aui:input name="filterByValidToDate" type="checkbox" value=""
								label="" />
						</aui:field-wrapper>
						<aui:field-wrapper label="filterByAvailableFromDate">
							<aui:input name="filterByAvailableFromDate" type="checkbox"
								value="" label="" />
						</aui:field-wrapper>
						<aui:field-wrapper label="filterByAvailableToDate">
							<aui:input name="filterByAvailableToDate" type="checkbox"
								value="" label="" />
						</aui:field-wrapper>
					</aui:column>
					<aui:column columnWidth="40">
						<a href=""><img
							src="${themeDisplay.pathThemeImages}/common/edit.png" />
							Redigera filter</a>
						<a href=""><img
							src="${themeDisplay.pathThemeImages}/common/delete.png" /> Ta
							bort filter</a>
					</aui:column>
				</aui:layout>
				<aui:button-row>
					<aui:button type="submit" value="save" />
					<portlet:renderURL var="viewURL">
						<portlet:param name="action" value="showIFeeds" />
					</portlet:renderURL>
					<aui:button onClick="${viewURL}" type="cancel" />
				</aui:button-row>
			</aui:fieldset>
		</aui:form>
		<a href=""><img
			src="${themeDisplay.pathThemeImages}/common/add.png" /> Lägg till
			nytt filter</a>
	</liferay-ui:panel>
	<liferay-ui:panel collapsible="true" defaultState="collapsed"
		extended="true" id="feedConfigurationBlock5" persistState="false"
		title="Publicerat">
		<p>H&auml;r kan du l&auml;gga till filter som verkar p&aring;
			f&auml;lten som beskriver publiceringen av dokumentet.</p>
		<aui:form action="<%= editIFeedURL %>" method="post"
			name="editFeedConfigurationFm6" cssClass="edit-feed-configuration-fm">
			<aui:fieldset>
				<aui:input name="publishedForUnit" value="" />
				<aui:input name="publishedForProject" value="" />
				<aui:input name="publicAccessibility" value="" />
				<aui:input name="publishedBy" value="" />
				<aui:field-wrapper name="publishingDate" label="publishing-date"
					inlineField="true">
					<liferay-ui:input-date yearRangeEnd="2100" yearRangeStart="1970"
						yearParam="publishingYear" monthParam="publishingMonth"
						dayParam="publishingDay">
					</liferay-ui:input-date>
				</aui:field-wrapper>
				<aui:input name="filterByPublishingDate" type="checkbox" value=""
					label="filterByPublishingDate" />
				<a href=""><img
					src="${themeDisplay.pathThemeImages}/common/edit.png" /> Redigera
					filter</a>
				<a href=""><img
					src="${themeDisplay.pathThemeImages}/common/delete.png" /> Ta bort
					filter</a>
			</aui:fieldset>
		</aui:form>
		<a href=""><img
			src="${themeDisplay.pathThemeImages}/common/add.png" /> Lägg till
			filter</a>
	</liferay-ui:panel>
	<liferay-ui:panel collapsible="true" defaultState="collapsed"
		extended="true" id="feedConfigurationBlock6" persistState="false"
		title="Sammanhang">
		<p>H&auml;r kan du l&auml;gga till filter som verkar p&aring;
			f&auml;lten som beskriver i vilket sammanhang dokumentet har
			producerats.</p>
		<aui:form action="<%= editIFeedURL %>" method="post"
			name="editFeedConfigurationFm7" cssClass="edit-feed-configuration-fm">
			<aui:fieldset>
				<aui:legend label="filter" />
				<aui:layout>
					<aui:column columnWidth="25">
						<aui:input name="processName" value="" />
						<aui:input name="journalId" value="" />
					</aui:column>
					<aui:column columnWidth="25">
						<aui:input name="caseType" value="" />
						<aui:input name="case" value="" />
					</aui:column>
					<aui:column columnWidth="25">
						<aui:input name="documentSeries" value="" />
						<aui:input name="documentSeriesId" value="" />
					</aui:column>
					<aui:column columnWidth="25">
						<a href=""><img
							src="${themeDisplay.pathThemeImages}/common/edit.png" />
							Redigera filter</a>
						<a href=""><img
							src="${themeDisplay.pathThemeImages}/common/delete.png" /> Ta
							bort filter</a>
					</aui:column>
				</aui:layout>
			</aui:fieldset>
		</aui:form>
		<a href=""><img
			src="${themeDisplay.pathThemeImages}/common/add.png" /> Lägg till
			filter</a>
	</liferay-ui:panel>
	<liferay-ui:panel collapsible="true" defaultState="collapsed"
		extended="true" id="feedConfigurationBlock7" persistState="false"
		title="Nyckelord">
		<p>H&auml;r kan du l&auml;gga till filter som verkar p&aring; de
			nyckelord som knytits till dokumentet.</p>
		<aui:form action="<%= editIFeedURL %>" method="post"
			name="editFeedConfigurationFm8" cssClass="edit-feed-configuration-fm">
			<aui:fieldset>
				<aui:input name="keywords" value="" />
				<aui:input name="authorsKeywords" value="" />
				<a href=""><img
					src="${themeDisplay.pathThemeImages}/common/edit.png" /> Redigera
					filter</a>
				<a href=""><img
					src="${themeDisplay.pathThemeImages}/common/delete.png" /> Ta bort
					filter</a>
			</aui:fieldset>
		</aui:form>

		<a href=""><img
			src="${themeDisplay.pathThemeImages}/common/add.png" /> Lägg till
			filter</a>
	</liferay-ui:panel>
	<liferay-ui:panel collapsible="true" defaultState="collapsed"
		extended="true" id="feedConfigurationBlock8" persistState="false"
		title="&Ouml;vrigt">
		<p>H&auml;r kan du l&auml;gga till filter som verkar p&aring;
			&ouml;vriga f&auml;lt som inte passar in i n&aring;gon av grupperna
			ovan.</p>
		<aui:form action="<%= editIFeedURL %>" method="post"
			name="editFeedConfigurationFm9" cssClass="edit-feed-configuration-fm">
			<aui:fieldset>
				<aui:input name="language" value="" />
				<aui:input name="physicalLocation" value="" />
				<aui:input name="fileExtension" value="" />
				<a href=""><img
					src="${themeDisplay.pathThemeImages}/common/edit.png" /> Redigera
					filter</a>
				<a href=""><img
					src="${themeDisplay.pathThemeImages}/common/delete.png" /> Ta bort
					filter</a>
			</aui:fieldset>
		</aui:form>

		<a href=""><img
			src="${themeDisplay.pathThemeImages}/common/add.png" /> Lägg till
			filter</a>
	</liferay-ui:panel>
</liferay-ui:panel-container>