<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tld/util.tld" prefix="vgr" %>
<div>
    <style type="text/css">
    </style>


    <div class="document-metadata" id="table-container">

        <table class="ifeed-metadata-table" style="text-decoration: none;">
            <thead><tr><td colspan="2">Dokumentegenskaper</td></tr></thead>
            <tbody>
            <c:if test="${not empty item['vgr:VgrExtension.vgr:Title']}">
            <tr><td class="key" style="text-decoration: none;">Titel</td><td class="value" style="text-decoration: none;">${item['vgr:VgrExtension.vgr:Title']}</td></tr>
            </c:if>
            <c:if test="${not empty item['core:ArchivalObject.core:Producer']}">
            <tr><td class="key" style="text-decoration: none;">Myndighet</td><td class="value" style="text-decoration: none;">${item['core:ArchivalObject.core:Producer']}</td></tr>
            </c:if>
            <c:if test="${not empty item['vgr:VgrExtension.vgr:PublishedForUnit.id']}">
            <tr><td class="key" style="text-decoration: none;">Upprättat för enhet</td><td class="value" style="text-decoration: none;">${item['vgr:VgrExtension.vgr:PublishedForUnit.id']}</td></tr>
            </c:if>
            <c:if test="${not empty item['vgrsd:DomainExtension.vgrsd:ValidityArea']}">
            <tr><td class="key" style="text-decoration: none;">Giltighetsområde</td><td class="value" style="text-decoration: none;">${item['vgrsd:DomainExtension.vgrsd:ValidityArea']}</td></tr>
            </c:if>
            <c:if test="${not empty item['core:ArchivalObject.core:Description']}">
            <tr><td class="key" style="text-decoration: none;">Beskrivning</td><td class="value" style="text-decoration: none;">${item['core:ArchivalObject.core:Description']}</td></tr>
            </c:if>
            <c:if test="${not empty item['vgrsd:DomainExtension.vgrsd:ContentResponsible']}">
            <tr><td class="key" style="text-decoration: none;">Innehållsansvarig</td><td class="value" style="text-decoration: none;">${item['vgrsd:DomainExtension.vgrsd:ContentResponsible']}</td></tr>
            </c:if>
            <c:if test="${not empty item['vgrsd:DomainExtension.vgrsd:DocumentApproved.name']}">
            <tr><td class="key" style="text-decoration: none;">Godkänt av</td><td class="value" style="text-decoration: none;">${item['vgrsd:DomainExtension.vgrsd:DocumentApproved.name']}</td></tr>
            </c:if>
            <c:if test="${not empty item['vgrsd:DomainExtension.vgrsd:ValidFrom']}">
            <tr><td class="key" style="text-decoration: none;">Giltig från</td><td class="value" style="text-decoration: none;">${item['vgrsd:DomainExtension.vgrsd:ValidFrom']}</td></tr>
            </c:if>
            <c:if test="${not empty item['vgrsd:DomainExtension.vgrsd:ValidTo']}">
            <tr><td class="key" style="text-decoration: none;">Giltig till</td><td class="value" style="text-decoration: none;">${item['vgrsd:DomainExtension.vgrsd:ValidTo']}</td></tr>
            </c:if>
            </tbody>
        </table>

    </div>
</div>