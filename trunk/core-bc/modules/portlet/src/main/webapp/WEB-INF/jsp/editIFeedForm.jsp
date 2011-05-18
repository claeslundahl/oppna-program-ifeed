<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>
<%@ taglib uri="http://liferay.com/tld/security" prefix="liferay-security"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util"%>

<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow"%>

<%@ page import="com.liferay.portal.kernel.util.Constants"%>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil"%>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@ page import="com.liferay.portal.kernel.util.PropsKeys"%>
<%@ page import="com.liferay.portal.kernel.util.PropsUtil"%>
<%@ page import="com.liferay.portal.kernel.util.StringPool"%>
<%@ page import="com.liferay.portal.kernel.util.Validator"%>

<%@ page import="com.liferay.portal.util.PortalUtil"%>

<%@ page import="javax.portlet.PortletURL"%>
<%@ page import="javax.portlet.ActionRequest"%>
<%@ page import="javax.portlet.PortletRequest"%>
<%@ page import="javax.portlet.PortletPreferences"%>
<%@ page import="javax.portlet.PortletSession"%>
<%@ page import="javax.portlet.WindowState"%>

<%@ page import="java.text.Format"%>

<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.ResourceBundle"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Locale"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />

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
  <aui:column columnWidth="100" first="true" last="true">
    <h1 class="heading">
      <span id="<portlet:namespace />headingText">${ifeed.name}</span>
    </h1>
    <p class="description">
      <span id="<portlet:namespace />descriptionText">${ifeed.description}</span>
    </p>
  </aui:column>
  <aui:column columnWidth="33" first="true">
    <div class="ifeed-step">
      <span>1. Välj filter</span>
    </div>
  </aui:column>
  <aui:column columnWidth="33">
    <div class="ifeed-step">
      <span>2. Mata in data</span>
    </div>
  </aui:column>
  <aui:column columnWidth="33" last="true">
    <div class="ifeed-step">
      <span>3. Din träfflista</span>
    </div>
  </aui:column>
  <aui:column columnWidth="33" first="true">
    <liferay-ui:panel-container>
      <liferay-ui:panel title="Tillgängliga filter" collapsible="true" extended="true">
        <div id="<portlet:namespace />existingFiltersWrap">
          <ul>
            <c:forEach items="${filterTypes}" var="filterType" varStatus="filterRow">
              <li><span class="tree-node-wrap clearfix"> <span class="tree-node-label"><liferay-ui:message
                      key="${filterType.keyString}" /> </span> </span>
                <ul>
                  <c:forEach items="${filterType.filters}" var="filter">
                    <li>
                      <span class="tree-node-wrap clearfix">
                        <portlet:actionURL name="selectFilter" var="selectFilter">
                          <portlet:param name="action" value="selectFilter" />
                          <portlet:param name="filter" value="${filter}" />
                        </portlet:actionURL>
                        <span class="tree-node-tooltip" title="Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean malesuada dolor vel nisl venenatis tincidunt non a leo.">Icon</span>
                        <a href="${selectFilter}" class="tree-node-use tree-node-link">
                          <span class="tree-node-label"><liferay-ui:message key="${filter.keyString}" /></span>
                        </a>
                        </span>
                    </li>
                  </c:forEach>
                </ul>
              </li>
            </c:forEach>
          </ul>
        </div>
      </liferay-ui:panel>
    </liferay-ui:panel-container>
  </aui:column>
  <aui:column columnWidth="33">
    <liferay-ui:panel-container>
      <liferay-ui:panel title="Nytt filter" collapsible="true" extended="true">
        <div id="<portlet:namespace />filter-value-box">
          <c:choose>
            <c:when test="${newFilter.metadataType == 'MULTI_VALUE'}">
              <aui:select label="${newFilter.keyString}" name="filterValue">
                <c:forEach items="${vocabulary}" var="meta">
                  <aui:option label="${meta}" value="${meta}" />
                </c:forEach>
              </aui:select>
            </c:when>
            <c:when test="${newFilter.metadataType == 'MULTI_VALUE_EXT'}">
              <aui:input label="${newFilter.keyString}" name="filterValue" id="filter-value" />
            </c:when>
            <c:when test="${newFilter.metadataType == 'DATE'}">
              <liferay-ui:input-date yearRangeStart="1990" yearRangeEnd="2020" yearParam="validFromYear"
                monthParam="validFromMonth" dayParam="validFromDay" />
            </c:when>
            <c:when test="${newFilter.metadataType == 'TEXT'}">
              <aui:input label="${newFilter.keyString}" name="filterValue" id="${newFilter.keyString}" />
            </c:when>
          </c:choose>
        </div>
        <c:if test="${not empty newFilter}">
          <aui:button-row>
            <aui:button onClick="${editIFeedURL}" value="save" />
            <aui:button onClick="${cancelURL}" type="cancel" />
          </aui:button-row>
          </c:if>
      </liferay-ui:panel>
      <liferay-ui:panel title="Valda filter" collapsible="true" extended="true">
        <div id="<portlet:namespace />usedFiltersWrap">
          <ul>
            <c:forEach items="${ifeed.filters}" var="iFeedFilter" varStatus="filtersRow">
              <portlet:actionURL name="removeFilter" var="removeFilter">
                <portlet:param name="action" value="removeFilter" />
                <portlet:param name="feedId" value="${ifeed.id}" />
                <portlet:param name="filter" value="${iFeedFilter.filter}" />
                <portlet:param name="filterQuery" value="${iFeedFilter.filterQuery}" />
              </portlet:actionURL>
              <li><span class="tree-node-wrap clearfix"> <aui:a href="${removeFilter}"
                    class="tree-node-link tree-node-delete">Ta bort</aui:a> <span class="tree-node-label"> <liferay-ui:message
                      key="${iFeedFilter.filter.keyString}" />: ${iFeedFilter.filterQuery} </span> </span>
              </li>
            </c:forEach>
          </ul>
        </div>
      </liferay-ui:panel>
    </liferay-ui:panel-container>
  </aui:column>
  <aui:column columnWidth="33" last="true">
    <liferay-ui:panel-container>
      <liferay-ui:panel title="Träfflista" collapsible="true" extended="true">
        <liferay-ui:search-container id="<portlet:namespace/>-parent-search-container" delta="10">
          <liferay-ui:search-container-results results="${hits}" total="${fn:length(hits)}" />
          <liferay-ui:search-container-row className="java.util.Map" modelVar="hit" stringKey="true">
            <liferay-ui:search-container-column-text name="Title" property="title" />
          </liferay-ui:search-container-row>
          <liferay-ui:search-iterator />
        </liferay-ui:search-container>
      </liferay-ui:panel>
    </liferay-ui:panel-container>
  </aui:column>
</aui:layout>

<liferay-util:html-top>
  <%@ include file="ifeed_css.jsp"%>
  <script type="text/javascript" src="${renderRequest.contextPath}/js/vgr-ifeed-config.js"></script>
</liferay-util:html-top>
<aui:script use="vgr-ifeed-config">

	var vgrIfeedConfig = new A.VgrIfeedConfig({
		existingFiltersTreeBoundingBox: '#<portlet:namespace />existingFiltersWrap',
		existingFiltersTreeContentBox: '#<portlet:namespace />existingFiltersWrap > ul',
		descriptionNode: '#<portlet:namespace />descriptionText',
		headingNode: '#<portlet:namespace />headingText',
		portletNamespace: '<portlet:namespace />',
		portletWrap: '#p_p_id<portlet:namespace />',
		usedFiltersTreeBoundingBox: '#<portlet:namespace />usedFiltersWrap',
		usedFiltersTreeContentBox: '#<portlet:namespace />usedFiltersWrap > ul'
	})
	.render();

<!--     AUI().ready('aui-autocomplete', function(A) { -->
<!--       var instance = new A.AutoComplete({ -->
<%--           dataSource: ${vocabularyJson}, --%>
<!--           typeAhead: false, -->
<%--           contentBox: '#<portlet:namespace />filter-value-box', --%>
<%--           input: '#<portlet:namespace />filter-value' --%>
<!--       }).render(); -->
<!--     }); -->

</aui:script>
