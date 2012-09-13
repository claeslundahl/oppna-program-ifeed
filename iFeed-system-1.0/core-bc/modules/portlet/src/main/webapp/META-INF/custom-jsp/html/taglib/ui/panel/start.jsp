<%@ include file="/html/taglib/ui/panel/init.jsp" %>

<div class="lfr-panel <%= cssClass %>" id="<%= id %>">
	<div class="lfr-panel-titlebar">
		<div class="lfr-panel-title">
			<span>
				<liferay-ui:message key="<%= title %>" />
			</span>
		</div>

		<c:if test="<%= collapsible && extended %>">
			<a class="lfr-panel-button" href="javascript:;"></a>
		</c:if>
	</div>

	<div class="lfr-panel-content">