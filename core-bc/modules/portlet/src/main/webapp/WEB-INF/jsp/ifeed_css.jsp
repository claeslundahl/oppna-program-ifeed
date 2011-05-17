<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

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


<style type="text/css">

.ifeed-portlet {}

.ifeed-portlet .ifeed-step {
	margin: 10px 0;
}

.ifeed-portlet .ifeed-step span {
	background: #ccc;
	color: #444;
	display: block;
	font-size: 1.6em;
	padding: 10px 10px;
}

.ifeed-portlet .lfr-panel-content {
	padding: 5px;
}

.ifeed-portlet .lfr-panel {
	margin: 0 0 10px 0;
}

.ifeed-portlet .tree-node-label,
.ifeed-portlet .tree-node-label,
.ifeed-portlet .tree-node-link,
.ifeed-portlet .tree-node-tooltip {
	display: block;
}

.ifeed-portlet .tree-node-label,
.ifeed-portlet .tree-node-link,
.ifeed-portlet .tree-node-tooltip {
	float: left;
	margin: 0 5px 0 0;
}

.ifeed-portlet .tree-node-link {
	height: 16px;
	padding-left: 20px;
}

.ifeed-portlet .tree-node-tooltip {
    font-size: 0;
    height: 16px;
    text-align: -9999em;
    width: 16px;
}

.ifeed-portlet .tree-node-label {
	color: #444;
	font-weight: bold;
}

.ifeed-portlet .tree-node-edit {
	background: transparent url(${themeDisplay.pathThemeImages}/common/edit.png) 0 0 no-repeat;
}

.ifeed-portlet .tree-node-delete {
	background: transparent url(${themeDisplay.pathThemeImages}/common/close.png) 0 0 no-repeat;
}

.ifeed-portlet .tree-node-use {
	background: transparent url(${themeDisplay.pathThemeImages}/common/add.png) 0 0 no-repeat;
}

.ifeed-portlet .tree-node-tooltip {
	background: transparent url(${themeDisplay.pathThemeImages}/common/help.png) 0 0 no-repeat;
}

</style>
