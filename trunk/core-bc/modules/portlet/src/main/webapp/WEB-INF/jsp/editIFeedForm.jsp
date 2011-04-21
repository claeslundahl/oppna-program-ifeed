<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

<portlet:actionURL name="addFilter" var="addFilter">
  <portlet:param name="action" value="addFilter" />
  <portlet:param name="feedId" value="${iFeed.id}" />
</portlet:actionURL>

<aui:layout>
  <aui:column columnWidth="33">
    <h1>${ifeed.name}</h1>
    <p>${ifeed.description}</p>
  </aui:column>
  <aui:column columnWidth="66">
    <aui:fieldset>
      <ul>
        <c:forEach items="${ifeed.filters}" var="filter" varStatus="filtersRow">
            <li>
              <liferay-ui:message key="${filter.filter.keyString}" /> är ${filter.filterQuery}
              <a href=""><img src="${themeDisplay.pathThemeImages}/common/delete.png" />Ta bort</a>
          </li>
        </c:forEach>
      </ul>
    </aui:fieldset>
  </aui:column>
</aui:layout>

<liferay-ui:panel-container extended="true" id="feedConfiguration" persistState="false" accordion="false">
  <aui:fieldset>
    <c:forEach items="${filterTypes}" var="filterType" varStatus="filterRow">
      <liferay-ui:panel collapsible="true" defaultState="open" extended="true"
        id="feedConfigurationBlock-${filterRow.index}" persistState="false" title="${filterType.keyString}">
        <aui:form action="<%= editIFeedURL %>" method="post" name="editFeedConfigurationFm-${filterRow.index}"
          cssClass="edit-feed-configuration-fm">
          <aui:fieldset>
            <aui:field-wrapper inlineField="true" inlineLabel="false">
              <aui:select name="filter-select-${filterRow.index}" label="">
                <c:forEach items="${filterType.filters}" var="filter">
                  <aui:option label="${filter.keyString}" value="${filter}" />
                </c:forEach>
              </aui:select>
            </aui:field-wrapper>
            <aui:field-wrapper inlineField="true" inlineLabel="false">
              <a href="${addFilter}"><img src="${themeDisplay.pathThemeImages}/common/add.png" /><liferay-ui:message
                key="select" /></a>
            </aui:field-wrapper>
          </aui:fieldset>
        </aui:form>
      </liferay-ui:panel>
    </c:forEach>
  </aui:fieldset>

  <aui:button-row>
    <aui:button type="submit" value="save" />
    <portlet:renderURL var="viewURL">
      <portlet:param name="action" value="showIFeeds" />
    </portlet:renderURL>
    <aui:button onClick="${viewURL}" type="cancel" />
  </aui:button-row>

</liferay-ui:panel-container>