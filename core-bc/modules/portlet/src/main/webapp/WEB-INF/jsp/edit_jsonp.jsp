<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/security" prefix="liferay-security"%>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util"%>
<%@ taglib uri="/WEB-INF/tld/field-infs.tld" prefix="fi"%>

<portlet:resourceURL id="updateJsonpEmbedCode" var="updateJsonpEmbedCodeURL" />

<%-- Link to test updateJsonpEmbedCode resource url. Controller mapping seems not to be working --%>
<%-- 
<div>
	<a href="${updateJsonpEmbedCode}">Test updateJsonpEmbedCode resource action</a>
</div>
--%>

<aui:form action="${updateJsonpEmbedCodeURL}" cssClass="ifeed-jsonp-form" name="jsonpForm">

	<liferay-ui:panel-container cssClass="ifeed-jspon-container">
	    <liferay-ui:panel title="Inst&auml;llningar f&ouml;r listan" collapsible="true" extended="true" cssClass="ifeed-jsonp-settings">
	    
	    	<aui:input name="ifeedId" type="hidden" value="${ifeedId}" />
	    	
	    	<%-- Input field example --%>
	    	<%--
	    	<aui:field-wrapper>
	    		<aui:input type="text" name="foo" value="4" label ="foo" />
	    		<span class="aui-field-help">Lorem ipsum dolarem sit amet</span>
	    	</aui:field-wrapper>
	    	--%>
	    	
	    	<aui:field-wrapper>
	    		<aui:select name="limitList" label="Antal rader">
					<aui:option value="0" label="Ingen begr&auml;nsning" selected="true" />
					<aui:option value="5" label="5" />
					<aui:option value="10" label="10" />
					<aui:option value="20" label="20" />
					<aui:option value="30" label="30" />
					<aui:option value="50" label="50" />
					<aui:option value="70" label="70" />
					<aui:option value="100" label="100" />
					<aui:option value="150" label="150" />
					<aui:option value="200" label="200" />
	    		</aui:select>
	    		<span class="aui-field-help">
	    			V&auml;lj antal rader du vill begr&auml;nsa listan till
    			</span>
	    	</aui:field-wrapper>
	    	
	    	<aui:field-wrapper>
	    		<aui:select name="hideEpiRightColumn" label="D&ouml;lj EPI h&ouml;gerkolumn">
					<aui:option value="yes" label="Ja" selected="true" />
					<aui:option value="no" label="Nej" />
	    		</aui:select>
	    		<span class="aui-field-help">
	    			Vill du d&ouml;lja h&ouml;gerkolumnen i EPI-server f&ouml;r att f&aring; mer plats?
    			</span>
	    	</aui:field-wrapper>
	    	
	    	<aui:field-wrapper>
	    		<div class="aui-field-multi">
		    		<label class="aui-field-label" for="<portlet:namespace />sortColumn">
		    			V&auml;lj sortering
					</label>
		    		
		    		<div class="aui-field-multi-element">
			    		<aui:select name="sortColumn" label="">
							<c:forEach items="${fi:getFieldInfs()}" var="field" varStatus="status">
								<optgroup label="${field.name}">
									<c:forEach items="${field.filterCriteriaTypes}" var="option" varStatus="status2">
									    <c:if test="${field.inHtmlView}">
										    <aui:option value="${option.id}" label="${option.name} (${option.id})" />
										</c:if>
									</c:forEach>
								</optgroup>
							</c:forEach>
			    		</aui:select>
			    		<aui:select name="sortOrder" label="">
							<aui:option value="asc" label="Stigande" selected="true" />
							<aui:option value="desc" label="Fallande" />
			    		</aui:select>
		    		</div>
	    		</div>
	    		<span class="aui-field-help">
	    			V&auml;lj sorteringskolumn samt om sortering skall ske stigande eller fallande
    			</span>
	    	</aui:field-wrapper>
	    	
	    	<aui:field-wrapper>
	    		<aui:select name="showTableHeader" label="Visa tabellhuvud">
					<aui:option value="yes" label="Ja" selected="true" />
					<aui:option value="no" label="Nej" />
	    		</aui:select>
	    		<span class="aui-field-help">
	    			Vill du visa tabellhuvudet (rubrikerna f&ouml;r kolumnerna)?
    			</span>
	    	</aui:field-wrapper>
	    	
	    	<aui:field-wrapper>
	    		<aui:select name="fontSize" label="Teckenstorlek">
					<aui:option value="auto" label="auto" selected="true" />
					<c:forEach begin="10" end="20" varStatus="status">
						<aui:option value="${status.index}px" label="${status.index}px" />
					</c:forEach>
	    		</aui:select>
	    		<span class="aui-field-help">
	    			V&auml;lj teckenstorlek f&ouml;r listan
    			</span>
	    	</aui:field-wrapper>

	    	<aui:field-wrapper>
	    		<aui:select name="linkOriginalDoc" label="L&auml;nka till orginaldokument">
					<aui:option value="no" label="Nej" selected="true" />
					<aui:option value="yes" label="Ja" />
	    		</aui:select>
	    		<span class="aui-field-help">

    			</span>
	    	</aui:field-wrapper>
				
		</liferay-ui:panel>
		
	    <liferay-ui:panel title="Kolumner" collapsible="true" extended="true" cssClass="ifeed-jsonp-columns">
			<aui:layout cssClass="ifeed-jsonp-columns-hd">
				<aui:column columnWidth="30" first="true">
					F&auml;lt
				</aui:column>
				<aui:column columnWidth="20">
					Alias
				</aui:column>
				<aui:column columnWidth="20">
					Justering
				</aui:column>
				<aui:column columnWidth="20">
					Bredd (%)
				</aui:column>
				<aui:column columnWidth="10" last="true">
					&nbsp;
				</aui:column>
			</aui:layout>

			<div class="ifeed-jsonp-column-bd">
				<aui:layout cssClass="ifeed-jsonp-column-bd-item ifeed-jsonp-column-bd-item-1">
					<aui:column columnWidth="30" first="true">
						<aui:select name="field" label="" data-name="field" id="column_1_field">
							<c:forEach items="${fi:getFieldInfs()}" var="field" varStatus="status">
								<optgroup label="${field.name}">
									<c:forEach items="${field.filterCriteriaAndViewTypes}" var="option" varStatus="status2">

									    <c:if test="${field.inHtmlView}">
										    <aui:option value="${option.id}" label="${option.name} (${option.id})" />
										</c:if>

									</c:forEach>
								</optgroup>
							</c:forEach>
						</aui:select>
					</aui:column>
					<aui:column columnWidth="20">
						<aui:input name="alias" type="text" value="Titel" label="" data-name="alias" id="column_1_alias" />
					</aui:column>
					<aui:column columnWidth="20">
						<aui:select name="orientation" label="" data-name="orientation" id="column_1_orientation">
							<aui:option value="left" label="V&auml;nsterst&auml;lld kolumn" selected="true" />
							<aui:option value="right" label="H&ouml;gerst&auml;lld kolumn" />
							<aui:option value="center" label="Centrerad kolumn" />
						</aui:select>
					</aui:column>
					<aui:column columnWidth="20">
						<aui:input name="width" type="text" value="70" label="" data-name="width" id="column_1_width" />
					</aui:column>
					<aui:column columnWidth="10" last="true">
						<div class="link-icon-wrap">
							<a href="#" class="link-icon link-icon-delete">Ta bort</a>
						</div>
					</aui:column>
				</aui:layout>

			</div>
			
			<div class="clearfix">
		        <a id="<portlet:namespace />addColumn" href="#" class="link-button link-button-icon link-button-add">
					<span class="link-button-content">L&auml;gg till kolumn</span>
				</a>
			</div>
		</liferay-ui:panel>
	    <liferay-ui:panel title="Kod att klistra in" collapsible="true" extended="true" cssClass="ifeed-jsonp-embed-code">

	    	<aui:input name="embedCode" type="textarea" cssClass="embed-code-textarea" onClick="Liferay.Util.selectAndCopy(this);" value="Modifiera parametrar för att generera skript." label="" />

		</liferay-ui:panel>
	</liferay-ui:panel-container>

</aui:form>

<liferay-util:html-bottom>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/ifeed-jsonp-builder.js?id=<%= System.currentTimeMillis() %>"></script>
    <script type="text/javascript">

        AUI().ready('aui-base', 'ifeed-jsonp-builder', function (A) {

            var ifeedJsonpBuilder = new A.IfeedJsonpBuilder({
                addColumnLink: '#<portlet:namespace />addColumn',
            	embedCodeTextarea: '#<portlet:namespace />embedCode',
                form: '#<portlet:namespace />jsonpForm',
                portletNamespace : '<portlet:namespace />'
            });

            ifeedJsonpBuilder.render();
        });

    </script>
</liferay-util:html-bottom>