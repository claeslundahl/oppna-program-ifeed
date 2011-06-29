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
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

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

<portlet:actionURL name="saveIFeed" var="saveIFeedURL">
  <portlet:param name="action" value="saveIFeed" />
</portlet:actionURL>


<aui:layout cssClass="ifeed-block ifeed-header">
    <aui:column columnWidth="50" first="true">
        <h1 class="heading">
            <span id="<portlet:namespace />headingText" class="heading-text">${ifeed.name}</span>
            <span class="ifeed-edit-trigger ifeed-edit-trigger-heading">Redigera text</span>
        </h1>
    </aui:column>
    <aui:column columnWidth="50" last="true">
        <ul class="button-toolbar clearfix">
            <li>
                <a href="${saveIFeedURL}" class="link-button link-button-icon link-button-save">
                    <span class="link-button-content">Spara</span>
                </a>
            </li>
            <li>
                <a href="${cancelURL}" class="link-button link-button-icon link-button-cancel">
                    <span class="link-button-content">Avbryt</span>
                </a>
            </li>
<!--             Enable when there is a help section written -->
<!--             <li> -->
<!--                 <a href="" class="link-button link-button-icon link-button-help"> -->
<!--                     <span class="link-button-content">Hjälp</span> -->
<!--                 </a> -->
<!--             </li> -->
        </ul>
    </aui:column>
</aui:layout>

<aui:layout cssClass="ifeed-block ifeed-meta-info">
    <aui:column columnWidth="33" first="true">
        <div class="ifeed-meta-item ifeed-meta-block">
            <div class="ifeed-meta-label">Beskrivning:</div>
            <div class="ifeed-meta-content">
                <span id="<portlet:namespace />descriptionText" class="description">${ifeed.description}</span>
                <span class="ifeed-edit-trigger ifeed-edit-trigger-description">Redigera text</span>
            </div>
        </div>
    </aui:column>
    <aui:column columnWidth="33">
        <div class="ifeed-meta-item ifeed-meta-inline">
            <div class="ifeed-meta-label">Id: </div>
            <div class="ifeed-meta-content">${ifeed.id}</div>
        </div>
        <div class="ifeed-meta-item ifeed-meta-inline">
            <div class="ifeed-meta-label">Skapat:</div>
            <div class="ifeed-meta-content">2011-06-17*</div>
        </div>
        <div class="ifeed-meta-item ifeed-meta-inline">
            <div class="ifeed-meta-label">Senast ändrat:</div>
            <div class="ifeed-meta-content">2011-06-17*</div>
        </div>
    </aui:column>
    <aui:column columnWidth="33" last="true">
        <div class="ifeed-meta-item ifeed-meta-block">
            <div class="ifeed-meta-label">Länk till feed:</div>
            <div class="ifeed-meta-content">
                <a href="${atomFeedLink}" target="_blank">${atomFeedLink}</a>
            </div>
        </div>
    </aui:column>
</aui:layout>

<aui:layout cssClass="ifeed-block">
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
</aui:layout>


<aui:layout cssClass="ifeed-block">
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
                            <span class="tree-node-tooltip" title="<liferay-ui:message key='${filter.keyString}.help' />">Icon</span>
                            <a href="${selectFilter}" class="tree-node-use tree-node-link tree-node-link-label">
                                <span class="tree-node-label">
                                    <liferay-ui:message key="${filter.keyString}.label" />
                                </span>
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
        <aui:form action="${addFilterURL}" method="post">
          <aui:input name="filter" type="hidden" value="${newFilter}" />
          <div id="<portlet:namespace />filter-value-box">
            <c:choose>
              <c:when test="${newFilter.metadataType == 'TEXT_FIX'}">
                <aui:select label="${newFilter.keyString}.label" name="filterValue">
                  <c:forEach items="${vocabulary}" var="meta">
                    <aui:option label="${meta}" value="${meta}" selected="${filterValue eq meta}"/>
                  </c:forEach>
                </aui:select>
              </c:when>
              <c:when test="${newFilter.metadataType == 'DATE'}">
                <liferay-ui:input-date yearRangeStart="1990" yearRangeEnd="2020" yearParam="validFromYear"
                  monthParam="validFromMonth" dayParam="validFromDay" firstDayOfWeek="1" yearValue="${filterFormBean.validFromYear}" monthValue="${filterFormBean.validFromMonth }" dayValue="${filterFormBean.validFromDay }"/>
              </c:when>
              <c:when test="${newFilter.metadataType == 'TEXT_FREE'}">
                <aui:input label="${newFilter.keyString}.label" name="filterValue" id="filter-value" value="${filterValue}"/>
              </c:when>
            </c:choose>
          </div>
          <c:if test="${not empty newFilter}">
            <aui:button-row>
              <aui:button type="submit" value="Add" name="add" />
            </aui:button-row>
          </c:if>
        </aui:form>
      </liferay-ui:panel>
      <liferay-ui:panel title="Valda filter" collapsible="true" extended="true">
        <div id="<portlet:namespace />usedFiltersWrap">
          <ul>
            <c:forEach items="${ifeed.filters}" var="iFeedFilter" varStatus="filtersRow">
              <portlet:actionURL name="removeFilter" var="removeFilter">
                <portlet:param name="action" value="removeFilter" />
                <portlet:param name="filter" value="${iFeedFilter.filter}" />
                <portlet:param name="filterQuery" value="${iFeedFilter.filterQuery}" />
              </portlet:actionURL>
              <portlet:actionURL name="editFilter" var="editFilter">
                <portlet:param name="action" value="editFilter" />
                <portlet:param name="filter" value="${iFeedFilter.filter}" />
                <portlet:param name="filterQuery" value="${iFeedFilter.filterQuery}" />
              </portlet:actionURL>
              <li>
                <span class="tree-node-wrap clearfix">
                    <a href="${editFilter}" class="tree-node-link tree-node-edit">Redigera</a>
                    <a href="${removeFilter}" class="tree-node-link tree-node-delete">Ta bort</a>
                    <span class="tree-node-label">
                        <liferay-ui:message key="${iFeedFilter.filter.keyString}.label" />: <fmt:formatDate value="${now}" type="both" timeStyle="long" dateStyle="long" />${iFeedFilter.filterQuery}
                    </span>
                </span>
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
        <c:if test="${not empty ifeed.filters}">
          <liferay-ui:search-container id="<portlet:namespace/>-parent-search-container" delta="100">
            <liferay-ui:search-container-results results="${hits}" total="${fn:length(hits)}" />
            <liferay-ui:search-container-row className="java.util.Map" modelVar="hit" stringKey="true">
              <liferay-ui:search-container-column-text name="Title" property="title" />
            </liferay-ui:search-container-row>
            <liferay-ui:search-iterator />
          </liferay-ui:search-container>
        </c:if>
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
