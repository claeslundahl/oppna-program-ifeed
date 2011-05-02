<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<head>
<style type="text/css">
@import 'resources/styles/style.css';
</style>
<style type="text/css">
div.aui-button-holder {
  text-align: right;
}
</style>
</head>

<portlet:actionURL name="editIFeed" var="editIFeedURL">
  <portlet:param name="action" value="editIFeed" />
  <portlet:param name="feedId" value="${iFeed.id}" />
</portlet:actionURL>

<portlet:actionURL name="addFilter" var="addFilterURL">
  <portlet:param name="action" value="addFilter" />
</portlet:actionURL>

<portlet:actionURL name="cancel" var="cancelURL">
  <portlet:param name="action" value="cancel" />
</portlet:actionURL>

<aui:layout>
    <aui:column columnWidth="66">
    <aui:fieldset label="ifeed-properties">
      <aui:column columnWidth="60">
        <h3>${ifeed.name}</h3>
        <p>${ifeed.description}</p>
      </aui:column>
      <aui:column columnWidth="40">
      <div>Senast ändrad: <fmt:formatDate value="${ifeed.timestamp }" timeStyle="short" dateStyle="long" type="both"/></div>
      <div>Ägare: ${ifeed.userId}</div>
      </aui:column>
      <aui:button-row>
        <aui:button onClick="alert('Not yet implemented')" value="edit" />
      </aui:button-row>
    </aui:fieldset>
  </aui:column>
  <aui:column columnWidth="33">
    <aui:fieldset label="active-filters">
      <ul class="active-filter-list">
        <c:forEach items="${ifeed.filters}" var="iFeedFilter" varStatus="filtersRow">
          <portlet:actionURL name="removeFilter" var="removeFilter">
            <portlet:param name="action" value="removeFilter" />
            <portlet:param name="feedId" value="${ifeed.id}" />
            <portlet:param name="filter" value="${iFeedFilter.filter}" />
            <portlet:param name="filterQuery" value="${iFeedFilter.filterQuery}" />
          </portlet:actionURL>
          <li class="active-filter"><liferay-ui:message key="${iFeedFilter.filter.keyString}" /> :
            ${iFeedFilter.filterQuery} <aui:a href="${removeFilter}">
              <img src="${themeDisplay.pathThemeImages}/common/remove.png" />
            </aui:a></li>
        </c:forEach>
      </ul>
    </aui:fieldset>
    <aui:button-row>
      <aui:button onClick="${editIFeedURL}" value="save" />
      <aui:button onClick="${cancelURL}" type="cancel" />
    </aui:button-row>
  </aui:column>
</aui:layout>

<aui:layout>
  <aui:column columnWidth="66">
    <liferay-ui:search-container id='<portlet:namespace/>-parent-search-container' delta="10">
      <liferay-ui:search-container-results results="${hits}" total="${fn:length(hits)}" />
      <liferay-ui:search-container-row className="java.util.Map" modelVar="hit" stringKey="true">
        <liferay-ui:search-container-column-text name="Title" property="title" />
      </liferay-ui:search-container-row>
      <liferay-ui:search-iterator />
    </liferay-ui:search-container>
  </aui:column>
  <aui:column columnWidth="33">
    <liferay-ui:panel-container extended="true" id="feedConfiguration" persistState="false" accordion="false">
      <aui:fieldset label="available-filters">
        <c:forEach items="${filterTypes}" var="filterType" varStatus="filterRow">
          <liferay-ui:panel collapsible="true" defaultState="close" extended="true"
            id="feedConfigurationBlock-${filterRow.index}" persistState="false" title="${filterType.keyString}">
            <aui:form action="${addFilterURL}" method="post" cssClass="edit-feed-configuration-fm">
              <aui:fieldset>
                <aui:field-wrapper inlineField="true" inlineLabel="false">
                  <aui:select name="filter" label="">
                    <c:forEach items="${filterType.filters}" var="filter">
                      <aui:option label="${filter.keyString}" value="${filter}" />
                    </c:forEach>
                  </aui:select>
                  <aui:input name="filterValue" label="" />
                </aui:field-wrapper>
                <aui:field-wrapper inlineField="true" inlineLabel="false">
                  <aui:button type="submit" value="add" />
                </aui:field-wrapper>
              </aui:fieldset>
            </aui:form>
          </liferay-ui:panel>
        </c:forEach>
      </aui:fieldset>
    </liferay-ui:panel-container>
  </aui:column>
</aui:layout>