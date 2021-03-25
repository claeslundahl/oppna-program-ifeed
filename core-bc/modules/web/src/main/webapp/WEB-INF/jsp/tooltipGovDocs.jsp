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
            <tr><td class="key" style="text-decoration: none;">Titel</td><td class="value" style="text-decoration: none;">${item['vgr:VgrExtension.vgr:Title']}</td></tr>
            <tr><td class="key" style="text-decoration: none;">Myndighet</td><td class="value" style="text-decoration: none;">${item['core:ArchivalObject.core:Producer']}</td></tr>
            <tr><td class="key" style="text-decoration: none;">Upprättat för enhet</td><td class="value" style="text-decoration: none;">${item['vgr:VgrExtension.vgr:PublishedForUnit.id']}</td></tr>
            <tr><td class="key" style="text-decoration: none;">Giltighetsområde</td><td class="value" style="text-decoration: none;">${item['vgrsd:DomainExtension.vgrsd:ValidityArea']}</td></tr>
            <tr><td class="key" style="text-decoration: none;">Beskrivning</td><td class="value" style="text-decoration: none;">${item['core:ArchivalObject.core:Description']}</td></tr>
            <tr><td class="key" style="text-decoration: none;">Godkänt av</td><td class="value" style="text-decoration: none;">${item['vgrsd:DomainExtension.vgrsd:DocumentApproved.name']}</td></tr>
            <tr><td class="key" style="text-decoration: none;">Giltig från</td><td class="value" style="text-decoration: none;">${item['vgrsd:DomainExtension.vgrsd:ValidFrom']}</td></tr>
            <tr><td class="key" style="text-decoration: none;">Giltig till</td><td class="value" style="text-decoration: none;">${item['vgrsd:DomainExtension.vgrsd:ValidTo']}</td></tr>
            </tbody>
        </table>

    </div>
</div>