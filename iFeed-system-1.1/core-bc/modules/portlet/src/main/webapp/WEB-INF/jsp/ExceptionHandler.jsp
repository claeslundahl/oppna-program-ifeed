<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<portlet:renderURL var="showEditIfeedFormURL">
  <portlet:param name="view" value="showEditIFeedForm"/>
</portlet:renderURL>

<a href="${showEditIfeedFormURL}">&lt;&lt; Tillbaka</a>
<p><spring:message code="${exception.key}" text="${exception.message}" /></p>
