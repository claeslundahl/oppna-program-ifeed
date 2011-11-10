<%@page import="se.vgregion.ifeed.types.IFeed"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/security" prefix="liferay-security"%>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util"%>

<%@ taglib uri="/WEB-INF/tld/vgr-access-guard.tld" prefix="guard"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />
<portlet:resourceURL var="findPeople" />
<portlet:resourceURL var="metdataTooltipURL" id="metadata"  />

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

<portlet:actionURL name="updateIFeed" var="updateIFeedURL">
  <portlet:param name="action" value="updateIFeed" />
</portlet:actionURL>

<aui:layout cssClass="ifeed-block ifeed-header">
    <c:if test="${saveAction}">
      <div class="portlet-msg-success">Ändringarna har sparats.</div>
    </c:if>

  <aui:column columnWidth="85" first="true">
    <h1 class="heading">
      <span id="<portlet:namespace />headingText" class="heading-text">${ifeed.name}</span> 
          <c:if test="${guard:mayEditFeed(user, ifeed)}">
            <span class="ifeed-edit-trigger ifeed-edit-trigger-heading">Redigera text</span>
          </c:if>
    </h1>
    <c:if test="${not guard:mayEditFeed(user, ifeed)}">
      <div>(Eftersom du inte är ägare till dokumentflödet kan inga ändringar göras)</div>
    </c:if>
  </aui:column>
  <aui:column columnWidth="15" last="true">
    <c:if test="${guard:mayEditFeed(user, ifeed)}">
     <aui:form id="metaDataForm" name="metaDataForm" method="post" action="<%=updateIFeedURL%>">
        <aui:input id="headingTextInput" name="name" type="hidden" value="${ifeed.name}" />
        <aui:input id="descriptionTextInput" name="description" type="hidden" value="${ifeed.description}" />
     </aui:form>
   </c:if>
    <aui:form method="post" action="<%=saveIFeedURL%>">
      <ul class="button-toolbar clearfix">
        <li>
        <aui:button type="submit" cssClass="link-button link-button-icon link-button-save" value="Spara" disabled="${not guard:mayEditFeed(user, ifeed)}" /></li>
        <li>
        <a href="${cancelURL}" class="link-button link-button-icon link-button-cancel"> <span
            class="link-button-content">Avbryt</span> </a></li>
        <!--             Enable when there is a help section written -->
        <!--             <li> -->
        <!--                 <a href="" class="link-button link-button-icon link-button-help"> -->
        <!--                     <span class="link-button-content">Hjälp</span> -->
        <!--                 </a> -->
        <!--             </li> -->
      </ul>
    </aui:form>
  </aui:column>
</aui:layout>

<aui:layout cssClass="ifeed-block ifeed-meta-info">
  <aui:column columnWidth="33" first="true">
    <div class="ifeed-meta-item ifeed-meta-block">
      <div class="ifeed-meta-label">Beskrivning:</div>
      <div class="ifeed-meta-content">
        <c:choose>
                <c:when test="${guard:mayEditFeed(user, ifeed)}">
        <span id="<portlet:namespace />descriptionText" class="description">${ifeed.description}</span>
         <span
          class="ifeed-edit-trigger ifeed-edit-trigger-description">Redigera text</span>
                </c:when>
        <c:otherwise>
          <span class="description">${ifeed.description}</span>
        </c:otherwise>
        </c:choose>
      </div>
    </div>
  </aui:column>
  <aui:column columnWidth="67">
    <div class="ifeed-meta-item ifeed-meta-inline">
      <div class="ifeed-meta-label">Id:</div>
      <div class="ifeed-meta-content">${ifeed.id}</div>
    </div>
    <div class="ifeed-meta-item ifeed-meta-block">
      <div class="ifeed-meta-label">Länk till feed:</div>
      <div class="ifeed-meta-content">
        <a href="${atomFeedLink}" target="_blank">${atomFeedLink}</a>
      </div>
    </div>
    <!--         Enable when we have valid dates -->
    <!--         <div class="ifeed-meta-item ifeed-meta-inline"> -->
    <!--             <div class="ifeed-meta-label">Skapat:</div> -->
    <!--             <div class="ifeed-meta-content">2011-06-17*</div> -->
    <!--         </div> -->
    <!--         <div class="ifeed-meta-item ifeed-meta-inline"> -->
    <!--             <div class="ifeed-meta-label">Senast ändrat:</div> -->
    <!--             <div class="ifeed-meta-content">2011-06-17*</div> -->
    <!--         </div> -->
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
        <div id="<portlet:namespace />existingFiltersWrap" class="clear-fix">
          <ul>
            <c:forEach items="${filterTypes}" var="filterType" varStatus="filterRow">
              <li><span class="tree-node-wrap clearfix"> <span class="tree-node-label"><liferay-ui:message
                      key="${filterType.keyString}" /> </span> </span>
                <ul>
                  <c:forEach items="${filterType.filters}" var="filter">
                    <li><span class="tree-node-wrap clearfix"> <portlet:actionURL name="selectFilter"
                          var="selectFilter">
                          <portlet:param name="action" value="selectFilter" />
                          <portlet:param name="filter" value="${filter}" />
                        </portlet:actionURL> <span class="tree-node-tooltip"
                        title="<liferay-ui:message key='${filter.helpKey}' />">Icon</span>
                        <c:choose>
                          <c:when test="${guard:mayEditFeed(user, ifeed)}">
                            <a href="${selectFilter}" class="tree-node-use tree-node-link tree-node-link-label"> <span
                              class="tree-node-label"> <liferay-ui:message key="${filter.labelKey}" /> </span>
                            </a>
                          </c:when>
                          <c:otherwise>
                            <a class="tree-node-use tree-node-link tree-node-link-label"> <span
                              class="tree-node-label"> <liferay-ui:message key="${filter.labelKey}" /> </span>
                            </a>
                          </c:otherwise>
                        </c:choose> 
                      </span></li>
                  </c:forEach>
                </ul></li>
            </c:forEach>
          </ul>
        </div>
      </liferay-ui:panel>
    </liferay-ui:panel-container>
  </aui:column>

  <aui:column columnWidth="33">
    <liferay-ui:panel-container>
      <liferay-ui:panel title="Nytt filter" collapsible="true" extended="true">
        <aui:form action="<%=addFilterURL%>" method="post">
          <aui:input name="filter" type="hidden" value="${newFilter}" />
          <div id="<portlet:namespace />filter-value-box">
            <c:choose>
              <c:when test="${newFilter.metadataType == 'TEXT_FIX'}">
                <aui:select label="${newFilter.labelKey}" name="filterValue" id="text-fix">
                  <c:forEach items="${vocabulary}" var="meta">
                    <aui:option label="${meta}" value="${meta}" selected="${filterValue eq meta}" />
                  </c:forEach>
                </aui:select>
              </c:when>
              <c:when test="${newFilter.metadataType == 'DATE'}">
                <aui:field-wrapper label="${newFilter.labelKey}">
                  <liferay-ui:input-date yearRangeStart="1990" yearRangeEnd="2020" yearParam="validFromYear"
                    monthParam="validFromMonth" dayParam="validFromDay" firstDayOfWeek="1"
                    yearValue="${filterFormBean.validFromYear}" monthValue="${filterFormBean.validFromMonth }"
                    dayValue="${filterFormBean.validFromDay }"/>
                </aui:field-wrapper>
              </c:when>
              <c:when test="${newFilter.metadataType == 'TEXT_FREE'}">
                <aui:input label="${newFilter.labelKey}" name="filterValue" id="text-free" value="${filterValue}" />
              </c:when>
              <c:when test="${newFilter.metadataType == 'LDAP_VALUE'}">
                <div id="<portlet:namespace />ldap-people" class="ifeed-portlet-ldap-people">
                  <aui:input label="${newFilter.labelKey}" name="filterValue" id="ldap-value" value="${filterValue}" cssClass="ifeed-portlet-filter-input"/>
                </div>
              </c:when>
            </c:choose>
          </div>
          <c:if test="${not empty newFilter}">
            <aui:button-row>
              <aui:button type="submit" value="Lägg Till" name="add" />
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
              <li><span class="tree-node-wrap clearfix"> 
                <c:choose>
                  <c:when test="${guard:mayEditFeed(user, ifeed)}">
                    <a href="${editFilter}" class="tree-node-link tree-node-edit">Redigera</a> 
                    <a href="${removeFilter}" class="tree-node-link tree-node-delete">Ta bort</a>
                  </c:when>
                  <c:otherwise>
                    <span class="tree-node-link tree-node-edit">Redigera</span> 
                    <span class="tree-node-link tree-node-delete">Ta bort</span>
                  </c:otherwise>
                </c:choose>
                  <span class="tree-node-label"> <liferay-ui:message
                      key="${iFeedFilter.filter.labelKey}" />: 
                      <c:choose>
                        <c:when test="${iFeedFilter.filter.metadataType == 'DATE'}">
                          ${fn:substringBefore(iFeedFilter.filterQuery, 'T')}
                        </c:when>
                        <c:otherwise>${iFeedFilter.filterQuery}</c:otherwise>
                      </c:choose></span> </span></li>
            </c:forEach>
          </ul>
        </div>
      </liferay-ui:panel>
      
      <%--
      <liferay-security:permissionsURL
    modelResource="<%= IFeed.class.getName() %>"
    modelResourceDescription="${ifeed.name}"
    resourcePrimKey="${ifeed.id}"
    var="entryURL"/>

   <liferay-ui:icon image="permissions" url="<%= entryURL %>" />
       --%>
      
    </liferay-ui:panel-container>
  </aui:column>
  <aui:column columnWidth="33" last="true">
    <liferay-ui:panel-container>
      <liferay-ui:panel title="Träfflista" collapsible="true" extended="true" cssClass="ifeed-search-result-list">
        <c:if test="${not empty ifeed.filters}">
          <liferay-ui:search-container id="<portlet:namespace/>-parent-search-container" delta="100" orderByCol="${orderByCol}" orderByType="${orderByType}" iteratorURL="${portletUrl}">
            <liferay-ui:search-container-results results="${hits}" total="${fn:length(hits)}" />
            <liferay-ui:search-container-row className="se.vgregion.ifeed.formbean.SearchResultList.SearchResult" modelVar="hit" stringKey="true">
              <liferay-ui:search-container-column-text>
                <liferay-ui:icon-menu>
                  <portlet:renderURL var="metadataURL">
                    <portlet:param name="view" value="documentMetadata" />
                    <portlet:param name="documentId" value="${hit.documentId}" />
                  </portlet:renderURL>
                  <liferay-ui:icon cssClass="metadata-icon-tooltip" image="" method="get" url="${metadataURL}" src="/vgr-theme/i/icons/information.png"/>
                </liferay-ui:icon-menu>
              </liferay-ui:search-container-column-text>
              <liferay-ui:search-container-column-text name="Title" property="title" href="${hit.link}" orderable="true" orderableProperty="dc:title"/>
              <liferay-ui:search-container-column-text name="Ändrad" property="processingTime" orderable="true" orderableProperty="processingtime"/>
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
  <c:if test="${guard:mayEditFeed(user, ifeed)}">
    <script type="text/javascript" src="${renderRequest.contextPath}/js/vgr-ifeed-config.js"></script>
  </c:if>
</liferay-util:html-top>

<c:if test="${guard:mayEditFeed(user, ifeed)}">
<aui:script use="vgr-ifeed-config">
  /**
   * Monky patched function - issue AUI-416
   * http://issues.liferay.com/browse/AUI-416
   */
  var _onContainerClick = function(event) {
      var instance = this;
  
      var target = event.target;
      var tagName = target.get('nodeName').toLowerCase();
  
      event.halt();
  
      while (target && (tagName != 'table')) {
          switch (tagName) {
              case 'body':
              return;
  
              case 'li':
                  instance._toggleHighlight(target, 'to');
                  instance._selectItem(target);
              return;
  
              default:
              break;
          }
  
          target = target.get('parentNode');
  
          if (target) {
              tagName = target.get('nodeName').toLowerCase();
          }
      }
  };
  
  var vgrIfeedConfig = new A.VgrIfeedConfig({
      existingFiltersTreeBoundingBox: '#<portlet:namespace />existingFiltersWrap',
      existingFiltersTreeContentBox: '#<portlet:namespace />existingFiltersWrap > ul',
      descriptionNode: '#<portlet:namespace />descriptionText',
      descriptionInput: '#<portlet:namespace />descriptionTextInput',
      metaDataForm: '#<portlet:namespace />metaDataForm',
      headingNode: '#<portlet:namespace />headingText',
      headingInput: '#<portlet:namespace />headingTextInput',
      portletNamespace: '<portlet:namespace />',
      portletWrap: '#p_p_id<portlet:namespace />',
      usedFiltersTreeBoundingBox: '#<portlet:namespace />usedFiltersWrap',
      usedFiltersTreeContentBox: '#<portlet:namespace />usedFiltersWrap > ul',
      metadataTooltipURL: '${metdataTooltipURL}'
  })
  .render();
  
  AUI().ready('aui-autocomplete', function(A) {
    if(A.one("#<portlet:namespace />filterValue") != null) {
      var datasource = function(request) {
        var items = null;
        A.io.request('${findPeople}&filterValue=' + A.one('#<portlet:namespace />filterValue').get('value') + "*", {
          cache: false,
          sync: true,
          timeout: 1000,
          dataType: 'json',
          method: 'get',
          on: {
            success: function(param) {
              items = {};
              items.list = eval(this.get('responseData'));
            },
            failure: function() {
              alert("Fail")
            }
          }
        });
        return items;
      };
      var autoComplete = new A.AutoComplete({
          dataSource: datasource,
          schema: {
            resultListLocator: 'list',
            resultFields: ['firstName', 'lastName', 'userName', 'organisation', 'email']
          },
          forceSelection: true,
          queryDelay: 0.5,
          matchKey: 'userName',
          dataSourceType: 'Function',
          schemaType: 'json',
          autoHiglight: true,
          typeAhead: false,
          minQueryLength: 3,
          contentBox: '#<portlet:namespace />ldap-people',
          input: '#<portlet:namespace />ldap-value'
      });
      autoComplete._onContainerClick = _onContainerClick;
      var count = 0;
      autoComplete.formatResult = function(result, request, resultMatch) {
      var resultFormat = '<div class="ifeed-portlet-filter-result ifeed-portlet-filter-result-' + (count++%2==0?'even':'odd') + '"><div><strong>' + result.firstName + " " + result.lastName + '</strong> (' + result.userName + ')</div><div>' + result.organisation + '</div>';
      //var resultFormat = '<ul class="ifeed-portlet-filter-result ifeed-portlet-filter-result-' + (count++%2==0?'even':'odd') + '"><li><strong>' + result.firstName + " " + result.lastName + '</strong> (' + result.userName + ')</li><li>' + result.organisation + '</ul>';
        return resultFormat;
      };
    
      autoComplete.render();
    }
  });
</aui:script>
</c:if>