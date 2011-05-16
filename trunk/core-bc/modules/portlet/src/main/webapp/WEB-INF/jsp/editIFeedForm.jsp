<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow" %>

<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.PropsKeys" %>
<%@ page import="com.liferay.portal.kernel.util.PropsUtil" %>
<%@ page import="com.liferay.portal.kernel.util.StringPool" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>

<%@ page import="com.liferay.portal.util.PortalUtil" %>

<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="javax.portlet.ActionRequest" %>
<%@ page import="javax.portlet.PortletRequest" %>
<%@ page import="javax.portlet.PortletPreferences" %>
<%@ page import="javax.portlet.PortletSession" %>
<%@ page import="javax.portlet.WindowState" %>

<%@ page import="java.text.Format" %>

<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>

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
			<span>1. V&auml;lj filter</span>
		</div>
	</aui:column>
	<aui:column columnWidth="33">
		<div class="ifeed-step">
			<span>2. Mata in data</span>
		</div>
	</aui:column>
	<aui:column columnWidth="33" last="true">
		<div class="ifeed-step">
			<span>3. Din tr&auml;fflista</span>
		</div>
	</aui:column>
	<aui:column columnWidth="33" first="true">
		<liferay-ui:panel-container>
			<liferay-ui:panel title="Tillg&auml;ngliga filter" collapsible="true" extended="true">
				<div id="<portlet:namespace />existingFiltersWrap">
					<ul>
					<c:forEach items="${filterTypes}" var="filterType" varStatus="filterRow">
						<li>
							<span class="tree-node-wrap clearfix">
								<span class="tree-node-label"><liferay-ui:message key="${filterType.keyString}" /></span>
							</span>
							<ul>
							<c:forEach items="${filterType.filters}" var="filter">
								<li>
									<span class="tree-node-wrap clearfix">
										<a href="#" class="tree-node-link tree-node-use">Anv&auml;nd</a>
										<span class="tree-node-tooltip" title="Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean malesuada dolor vel nisl venenatis tincidunt non a leo.">Icon</span>
										<span class="tree-node-label"><liferay-ui:message key="${filter.keyString}" /></span>
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
				<p>New filter here</p>
			</liferay-ui:panel>
			<liferay-ui:panel title="Valda filter" collapsible="true" extended="true">
				<div id="<portlet:namespace />usedFiltersWrap">
					<ul>
						<li>
							<span class="tree-node-wrap clearfix">
								<span class="tree-node-label">Vem skapades dokumentet f&ouml;r?</span>
							</span>
							<ul>
								<li>
									<span class="tree-node-wrap clearfix">
										<a href="#" class="tree-node-link tree-node-delete">Ta bort</a>
										<span class="tree-node-label">M&aring;lgrupp HoS</span>
									</span>
								</li>
								<li>
									<span class="tree-node-wrap clearfix">
										<a href="#" class="tree-node-link tree-node-delete">Ta bort</a>
										<span class="tree-node-label">Skapad enhet</span>
									</span>
								</li>
								<li>
									<span class="tree-node-wrap clearfix">
										<a href="#" class="tree-node-link tree-node-delete">Ta bort</a>
										<span class="tree-node-label">Publicerad f&ouml;r enhet</span>
									</span>
								</li>
							</ul>
						</li>
						<li>
							<span class="tree-node-wrap clearfix">
								<span class="tree-node-label">Vilket sammanhang tillh&ouml;r dokumentet?</span>
							</span>
							<ul>
								<li>
									<span class="tree-node-wrap clearfix">
										<a href="#" class="tree-node-link tree-node-delete">Ta bort</a>
										<span class="tree-node-label">Handlingstyp</span>
									</span>
								</li>
							</ul>
						</li>
					</ul>
				</div>
			</liferay-ui:panel>
		</liferay-ui:panel-container>
	</aui:column>
	<aui:column columnWidth="33" last="true">
		<liferay-ui:panel-container>
			<liferay-ui:panel title="Tr&auml;fflista" collapsible="true" extended="true">
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
	<%@ include file="ifeed_css.jsp" %>
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

</aui:script>
