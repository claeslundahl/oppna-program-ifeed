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


<link rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.4/themes/redmond/jquery-ui.css" type="text/css" />
<link rel="stylesheet" href="http://webframe.vgregion.se/documentListing/docListCreator/lib/jquery.cluetip.css" type="text/css" />
<link rel="stylesheet" href="http://webframe.vgregion.se/documentListing/docListCreator/lib/jquery-impromptu.4.0.min.css" type="text/css" />
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.4/jquery-ui.min.js"></script>
<script src="http://webframe.vgregion.se/documentListing/docListCreator/lib/jquery-impromptu.4.0.min.js"></script>
<script src="http://webframe.vgregion.se/documentListing/docListCreator/lib/jquery.cluetip.all.js"></script>
<script type="text/javascript" src="http://webframe.vgregion.se/documentListing/docListCreator/local.js"></script>


<portlet:defineObjects />
<liferay-theme:defineObjects />
<portlet:resourceURL id="findPeople" var="findPeople" />
<portlet:resourceURL id="findOrgs" var="findOrgs" />
<portlet:resourceURL id="findOrganizationByHsaId" var="findOrganizationByHsaId" />

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
  <aui:column columnWidth="66">
    
    <div class="ifeed-meta-item ifeed-meta-inline">
      <div class="ifeed-meta-label">Id:</div>
      <div class="ifeed-meta-content">${ifeed.id}</div>
    </div>

    <div class="ifeed-meta-item ifeed-meta-inline">
      <div class="ifeed-meta-label">Länkar till dokumentlista:</div>
      <div class="ifeed-meta-content">

      <a href="${atomFeedLink}" target="_blank">Atom</a>
      <a href="${webFeedLink}" target="_blank">Html</a>
      <a id="json-feed-link" href="${jsonFeedLink}" target="_blank">Json</a>
      <a href="javascript:loadCodeFrame('${ifeed.id}');">Jsonp-hjälp</a>

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
    <%@ include file="editIFeedFormFilters.jspf" %>
  </aui:column>

  <aui:column columnWidth="33">
    <%@ include file="editIFeedFormDataFilter.jspf" %>
  </aui:column>

  <aui:column columnWidth="33" last="true">
    <%@ include file="editIFeedFormResult.jspf" %>
  </aui:column>
  
</aui:layout>
<liferay-util:html-top>
  <%@ include file="ifeed_css.jsp"%>
  <c:if test="${guard:mayEditFeed(user, ifeed)}">
    <script type="text/javascript" src="${renderRequest.contextPath}/js/vgr-ifeed-config.js"></script>
  </c:if>
</liferay-util:html-top>

<c:if test="${guard:mayEditFeed(user, ifeed)}">
<aui:script use="aui-base,aui-tree,json-parse,vgr-ifeed-config">
  <%@ include file="editIFeedFormJs.jspf" %>
</aui:script>



</c:if>
