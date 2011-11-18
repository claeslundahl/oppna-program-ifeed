<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>

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

.ifeed-portlet .lfr-panel .aui-toolbar {
  display: none;
}

.ifeed-portlet .lfr-panel .aui-field-content {
  display: block;
  padding-right: 4px;
}

.ifeed-portlet .lfr-panel .aui-field-select .aui-field-content {
  padding-right: 0px;
}

.ifeed-portlet .lfr-panel .aui-combobox-content .aui-field-content select,.ifeed-portlet .lfr-panel .aui-field-content input
  {
  width: 100%;
}

.ifeed-portlet a.taglib-icon:link {
  text-decoration: none;
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

.ifeed-portlet .tree-node-delete,.ifeed-portlet .tree-node-tooltip,.ifeed-portlet .tree-node-edit {
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
  clear: both; /* Claes */
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

.link-button-save input {
  width: 65px !important;
  height: 28px;
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

.ifeed-portlet .description {
  display: block;
  width: 100%;
}

.ifeed-portlet ul {
  list-style-type: none; /* Claes */
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
  width: auto;
  margin-right: 6px;
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

/* Search container style */
.ifeed-portlet .parent-search-container .col-1 {
  width: 22px;
}

.ifeed-portlet .parent-search-container .col-4 {
  width: 60%;
}

.ifeed-portlet .parent-search-container .col-5 {
  width: 68px;
}

/* Document metadata block */
.ifeed-metadata {
  font-size: 14px;
}

.ifeed-metadata dt {
  font-weight: bold;
  color: black;
  float: left;
  margin-bottom: 0.5em;
  padding-right: 6px;
  width: 20em;
  text-align: right;
}

.ifeed-metadata dd {
  font-style: italic;
  margin-bottom: 0.5em;
  color: #333;
  margin-left: 0px;
  #display: inline-block;
}

.metadata-tooltip dt {
    font-weight: bold;
}

.metadata-tooltip dd {
    font-style: italic;
}

.metadata-tooltip-more {
    display: block;
    text-align: right;
}

/* Search result block */
.ifeed-search-result-list .col-3 {
    width: 6em;
}
</style>
