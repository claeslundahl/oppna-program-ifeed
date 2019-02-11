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

            <tr><td class="key">Titel</td><td class="value">${item['title']}</td></tr>
            <tr><td class="key">Myndighet</td><td class="value">${item['core:ArchivalObject.core:Producer']}</td></tr>
<%--
            <tr><td class="key">Klassificeringsstruktur (process)</td><td class="value">${item['core:ArchivalObject.core:Classification.core:Classification.name']}</td></tr>
            <tr><td class="key">Handlingstyp</td><td class="value">${item['core:ArchivalObject.core:ObjectType']}</td></tr>
--%>
            <tr><td class="key">Upprättat för enhet</td><td class="value">${item['vgr:VgrExtension.vgr:PublishedForUnit']}</td></tr>
            <%--<tr><td class="key">Regional ämnesindelning</td><td class="value">${item['vgrsy:DomainExtension.vgrsy:SubjectClassification']}</td></tr>
            <tr><td class="key">Egen ämnesindelning</td><td class="value">${item['vgrsy:DomainExtension.vgrsy:SubjectLocalClassification']}</td></tr>
            <tr><td class="key">DokumentId källa</td><td class="value">${item['vgr:VgrExtension.vgr:Source.id']}</td></tr>
            <tr><td class="key">Källsystem</td><td class="value">${item['vgr:VgrExtension.vgr:SourceSystem']}</td></tr>
            <tr><td class="key">Upprättat av enhet</td><td class="value">${item['vgr:VgrExtension.vgr:CreatedByUnit.id']}</td></tr>
            <tr><td class="key">Upprättat av (person)</td><td class="value">${item['vgr:VgrExtension.vgr:CreatedBy']}</td></tr>
            <tr><td class="key">Åtkomsträtt</td><td class="value">${item['vgr:VgrExtension.vgr:SecurityClass']}</td></tr>
            <tr><td class="key">Länk för webben</td><td class="value">${item['url']}</td></tr>
            <tr><td class="key">Länk för webben (orginalformat)</td><td class="value">${item['originalDownloadLatestVersionUrl']}</td></tr>
--%>
            </tbody>
        </table>

    </div>
</div>