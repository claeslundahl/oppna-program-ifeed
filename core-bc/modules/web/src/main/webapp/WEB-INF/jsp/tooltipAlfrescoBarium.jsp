<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tld/util.tld" prefix="vgr" %>
<div>
    <style type="text/css">
    </style>


    <div class="document-metadata" id="table-container">

        <%--        <table class="ifeed-metadata-table" style="text-decoration: none;">
                    <thead><tr><td colspan="2">Dokumentegenskaper</td></tr></thead>
                    <tbody>

                    <tr><td class="key">Titel</td><td class="value">${item['title']}</td></tr>
                    <tr><td class="key">Publicerat för enhet</td><td class="value">${item['dc.publisher.forunit']}</td></tr>
                    <tr><td class="key">Dokumentstruktur VGR</td><td class="value">${item['dc.type.document.structure']}</td></tr>

                    </tbody>
                </table>--%>

        <table class="ifeed-metadata-table" style="text-decoration: none;">
            <c:if test="${not empty item['vgr:VgrExtension.vgr:Title'] or not empty item['dc.title'] or not empty item['dc.publisher.forunit'] or not empty item['dc.description'] or not empty item['dc.creator.document'] or not empty item['dc.creator.function'] or not empty item['dc.contributor.acceptedby'] or not empty item['dc.contributor.acceptedby.role'] or not empty item['dc.date.validfrom'] or not empty item['dc.date.validto'] or not empty item['dc.type.document.structure'] or not empty item['core:ArchivalObject.core:Producer'] or not empty item['vgr:VgrExtension.vgr:PublishedForUnit']}">
            <thead>
            <tr>
                <td colspan="2">Dokumentegenskaper</td>
            </tr>
            </thead>
            <tbody>
            <c:if test="${not empty item['dc.title']}">
            <tr>
                <td class="key">Titel</td>
                <td class="value">${item['dc.title']}</td>
            </tr>
            </c:if>
            <c:if test="${not empty item['dc.publisher.forunit']}">
            <tr>
                <td class="key">Publicerat för enhet</td>
                <td class="value">${item['dc.publisher.forunit']}</td>
            </tr>
            </c:if>
            <c:if test="${not empty item['dc.description']}">
            <tr>
                <td class="key">Beskrivning</td>
                <td class="value">${item['dc.description']}</td>
            </tr>
            </c:if>
            <c:if test="${not empty item['dc.creator.document']}">
            <tr>
                <td class="key">Innehålls&shy;ansvarig</td>
                <td class="value">${item['dc.creator.document']}</td>
            </tr>
            </c:if>
            <c:if test="${not empty item['dc.creator.function']}">
            <tr>
                <td class="key">Innehålls&shy;ansvarig, roll</td>
                <td class="value">${item['dc.creator.function']}</td>
            </tr>
            </c:if>
            <c:if test="${not empty item['dc.contributor.acceptedby']}">
            <tr>
                <td class="key">Godkänt av</td>
                <td class="value">${item['dc.contributor.acceptedby']}</td>
            </tr>
            </c:if>
            <c:if test="${not empty item['dc.contributor.acceptedby.role']}">
            <tr>
                <td class="key">Godkänt av, roll</td>
                <td class="value">${item['dc.contributor.acceptedby.role']}</td>
            </tr>
            </c:if>
            <c:if test="${not empty item['dc.date.validfrom']}">
            <tr>
                <td class="key">Giltig fr o m</td>
                <td class="value">${item['dc.date.validfrom']}</td>
            </tr>
            </c:if>
            <c:if test="${not empty item['dc.date.validto']}">
            <tr>
                <td class="key">Giltig t o m</td>
                <td class="value">${item['dc.date.validto']}</td>
            </tr>
            </c:if>
            <c:if test="${not empty item['dc.type.document.structure']}">
            <tr>
                <td class="key">Dokument&shy;struktur VGR</td>
                <td class="value">${item['dc.type.document.structure']}</td>
            </tr>
            </c:if>
            <c:if test="${not empty item['core:ArchivalObject.core:Producer']}">
            <tr>
                <td class="key">Myndighet</td>
                <td class="value">${item['core:ArchivalObject.core:Producer']}</td>
            </tr>
            </c:if>
            <c:if test="${not empty item['vgr:VgrExtension.vgr:PublishedForUnit']}">
            <tr>
                <td class="key">Upprättat för enhet</td>
                <td class="value">${item['vgr:VgrExtension.vgr:PublishedForUnit']}</td>
            </tr>
            </c:if>
            <tbody>
            </c:if>
            <table>

            </table>
    </div>
</div>