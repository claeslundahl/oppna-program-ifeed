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


<style type="text/css">
.ifeed-portlet .portlet {
  background: #fff;
}

.ifeed-portlet .lfr-panel-content {
  padding: 5px;
}

.ifeed-portlet .lfr-panel {
  margin: 0 0 10px 0;
}

.ifeed-portlet .aui-tree-view {
  overflow: visible;
}

.ifeed-portlet .tree-node-label,.ifeed-portlet .tree-node-label,.ifeed-portlet .tree-node-link,.ifeed-portlet .tree-node-tooltip
  {
  display: block;
}

.ifeed-portlet .tree-node-label,.ifeed-portlet .tree-node-link,.ifeed-portlet .tree-node-tooltip {
  float: left;
  margin: 0 5px 0 0;
}

.ifeed-portlet .tree-node-link {
  height: 16px;
  text-decoration: none;
}

.ifeed-portlet .tree-node-link-label {
  padding-left: 20px;
}

.ifeed-portlet .tree-node-delete,
.ifeed-portlet .tree-node-tooltip,
.ifeed-portlet .tree-node-edit {
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
  background: transparent url(${themeDisplay.pathThemeImages}/common/delete.png) 0 0 no-repeat;
}

.ifeed-portlet .tree-node-use {
  background: transparent url(${themeDisplay.pathThemeImages}/common/add.png) 0 0 no-repeat;
}

.ifeed-portlet .tree-node-tooltip {
  background: transparent url(${themeDisplay.pathThemeImages}/common/help.png) 0 0 no-repeat;
}

/*-- Layout blocks ------*/
.ifeed-block {
  margin: 10px 0;
}

/*-- Edit triggers ------*/
.ifeed-edit-trigger {
  background: transparent url(${themeDisplay.pathThemeImages}/common/edit.png) right center no-repeat;
  cursor: pointer;
  display: block;
  font-size: 0;
  height: 16px;
  text-indent: -9999em;
  width: 16px;
}

.ifeed-edit-trigger-heading {
  
}

.ifeed-edit-trigger-description {
  
}

/*-- Header block ------*/
.ifeed-header {
  
}

.ifeed-header h1 {
  margin: 0;
}

.ifeed-header h1 .heading-text {
  margin: 0;
}

.ifeed-header ul.button-toolbar {
  float: right;
  list-style: none;
  margin: 0;
  padding: 0;
}

.ifeed-header ul.button-toolbar li {
  float: left;
  margin: 0 5px;
  position: relative;
}

.ie6 .ifeed-header ul.button-toolbar li {
  width: 0;
  white-space: nowrap;
}

a.link-button {
  background: #d4d4d4 url(${themeDisplay.pathThemeImages}/portlet/header_bg.png) 0 0 repeat-x;
  border: 1px solid #dedede;
  border-color: #c8c9ca #9e9e9e #9e9e9e #c8c9ca;
  -moz-border-radius: 4px;
  -webkit-border-radius: 4px;
  border-radius: 4px;
  color: #34404f;
  cursor: pointer;
  display: block;
  float: left;
  font-weight: bold;
  line-height: 1.3;
  margin: 0 3px 0 0;
  padding: 5px;
  position: relative;
  text-decoration: none;
  text-shadow: 1px 1px #fff;
  width: auto;
}

a.link-button:hover {
  background: #b9ced9 url(${themeDisplay.pathThemeImages}/application/state_hover_bg.png) 0 0 repeat-x;
  border-color: #627782;
  color: #336699;
}

a.link-button-icon .link-button-content {
  display: block;
  height: 16px;
  line-height: 16px;
  padding-left: 20px;
}

a.link-button-save .link-button-content {
  background: transparent url(${themeDisplay.pathThemeImages}/common/export.png) 0 0 no-repeat;
}

a.link-button-cancel .link-button-content {
  background: transparent url(${themeDisplay.pathThemeImages}/common/close.png) 0 0 no-repeat;
}

a.link-button-help .link-button-content {
  background: transparent url(${themeDisplay.pathThemeImages}/common/help.png) 0 0 no-repeat;
}

/*-- Meta info block ------*/
.ifeed-meta-info {
  border: #cecece 1px solid;
  border-width: 1px 0;
  padding: 10px 0;
}

.ifeed-meta-item {
  display: block;
  margin: 0 0 5px 0;
}

.ifeed-meta-label {
  display: block;
  font-weight: bold;
}

.ifeed-meta-content {
  display: block;
}

/* Clearfix style clearing */
.ifeed-meta-block:after {
  clear: both;
  content: '.';
  display: block;
  visibility: hidden;
  height: 0
}

.ifeed-meta-block {
  display: inline-block
}

* html .ifeed-meta-block {
  height: 1%
}

.ifeed-meta-block {
  display: block
}

.ifeed-meta-inline .ifeed-meta-label {
  float: left;
  width: 150px;
}

.ifeed-meta-inline .ifeed-meta-content {
  float: none;
  margin: 0 150px 0 0;
  margin: 0;
}

/*-- Steps (arrow flow) ------*/
.ifeed-portlet .ifeed-step {
  background: transparent url(<%=request.getContextPath()%>/img/ifeed-arrow-sprite.png ) 0 0 no-repeat;
  margin: 10px 0;
  padding: 0 0 0 45px;
}

.ifeed-portlet .ifeed-step span {
  background: transparent url(<%=request.getContextPath()%>/img/ifeed-arrow-sprite.png ) right -70px no-repeat;
  color: #444;
  display: block;
  font-size: 1.6em;
  height: 48px;
  line-height: 48px;
  margin: 0;
  padding: 0 45px 0 0;
}
</style>