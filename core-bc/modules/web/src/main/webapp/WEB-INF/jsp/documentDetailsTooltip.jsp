<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tld/util.tld" prefix="vgr" %>
<div>
    <style type="text/css">
    </style>


    <div class="document-metadata" id="table-container">
        <%--<h1 class="title-header">${doc.value}</h1>--%>

        <c:forEach items="${doc.children}" var="paragraph">
            <div>
                <table class="ifeed-metadata-table">
                    <thead>
                    <tr>
                        <td colspan="2">
                                ${paragraph.name}
                        </td>
                    </tr>
                    </thead>
                    <tbody>

                    <c:forEach items="${paragraph.children}" var="field">
                        <tr>
                            <td class="key">
                                    ${field.name}
                            </td>
                            <td class="value">
                                    ${field.value}
                            </td>
                        </tr>
                    </c:forEach>

                    </tbody>
                </table>
            </div>
        </c:forEach>

    </div>
</div>