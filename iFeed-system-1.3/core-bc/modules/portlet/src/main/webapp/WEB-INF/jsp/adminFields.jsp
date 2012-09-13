<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<portlet:defineObjects />


<%@include file="toolbar.jspf"%>


<portlet:actionURL name="addFieldConfig" var="url">
  <portlet:param name="action" value="addFieldConfig" />
</portlet:actionURL>

<aui:form action="<%=url%>" method="post" name="editFeedFieldConfiguration"
  cssClass="edit-feed-configuration-fm">
  
  <textarea name="text" style="width:100%; min-height: 100px;">${fields.text}</textarea>
  
      <aui:button type="submit" value="save" />
  
      <portlet:renderURL var="viewIFeedsURL">
      <portlet:param name="view" value="showIFeeds" />
    </portlet:renderURL>
    <aui:button onClick="${viewIFeedsURL}" type="cancel" />
  
</aui:form>