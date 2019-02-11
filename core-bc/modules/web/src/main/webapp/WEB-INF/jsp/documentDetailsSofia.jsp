<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tld/util.tld" prefix="vgr" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Dokumentegenskaper</title>
    <style type="text/css" name="documentDetailsStyle">

        /* Reset */
        #table-container body, #table-container div, #table-container dl, #table-container dt, #table-container dd, #table-container ul, #table-container ol, #table-container li, #table-container h1, #table-container h2, #table-container h3, #table-container h4, #table-container h5, #table-container h6, #table-container pre, #table-container form, #table-container fieldset, #table-container input, #table-container textarea, #table-container p, #table-container blockquote, #table-container th, #table-container td {
            margin: 0;
            padding: 0;
        }

        #table-container a {
            color: #005baa;
            margin: 0;
            padding: 0;
            margin: 0px;
            text-decoration: underline;
            vertical-align: top;
        }

        #table-container a img {
            border: none;
            margin-right: 0.5em;
        }

        #table-container ul.doc-list {
            list-style-type: none;
        }

        #table-container ul.doc-list li {
            margin: 3px 0;
        }

        #table-container ul.doc-list a.meta,
        #table-container ul.doc-list a.document {
            display: block;
        }

        #table-container ul.doc-list a.meta {
            background: transparent url(information.png) 0 0 no-repeat;
            float: left;
            font-size: 0;
            height: 16px;
            text-indent: -9999em;
            width: 16px;
        }

        #table-container ul.doc-list a.document {
            line-height: 16px;
            margin: 0 0 0 20px;
            text-decoration: underline;
        }

        #table-container ul.doc-list a.document:hover {
            text-decoration: none;
        }

        #table-container {
            color: #333;
            font-family: Arial, Verdana, Helvetica, sans-serif;
            font-size: 12px;
            margin: 0.5em;
        }

        #table-container #table-container {
            padding: 0 10px;
        }

        #table-container #table-container h1 {
            font-size: 16px;
        }

        #table-container .ifeed-metadata-table {
            border-collapse: collapse;
            margin: 20px 0px;
            width: 100%;
        }

        #table-container .ifeed-metadata-table thead td {
            background-color: #ebebeb;
            font-size: 1.2em;
            font-weight: bold;
            padding-left: 6px;
            padding: 5px 0px 5px 6px;
            vertical-align: top;
        }

        #table-container .ifeed-metadata-table tbody td {
            border-bottom: 1px solid lightgray;
            padding-left: 6px;
            padding-top: 1px;
            vertical-align: top;
        }

        #table-container .ifeed-metadata-table td.no-styling {
            border-bottom: none;
        }

        #table-container .ifeed-metadata-table td.key {
            font-weight: bold;
            width: 25%;
        }

        #table-container #json-feed-link {
            position: relative;
            bottom: 2px;
        }

        <c:if test="${param['type'] eq 'tooltip'}">
        .title-header {
            display: none;
        }

        </c:if>

    </style>
</head>
<body>

<div class="document-metadata" id="table-container">

    <h1 class="title-header">${item['title']}</h1>

    <%--    Dokumentegenskaper
        - Titel
        - Myndighet
        - Klassificeringsstruktur (process)
        - Handlingstyp
        - Upprättat för enhet
        - Regional ämnesindelning
        - Egen ämnesindelning
        - DokumentId källa
        - Källsystem
        - Upprättat av enhet
        - Upprättat av (person)
        - Åtkomsträtt
        - Länk för webben
        - Länk för webben (orginalformat)--%>



    <div>
        <table class="ifeed-metadata-table" style="text-decoration: none;">
            <thead><tr><td colspan="2">Dokumentegenskaper</td></tr></thead>
            <tbody>

                <tr><td class="key">Titel</td><td class="value">${item['title']}</td></tr>
                <tr><td class="key">Myndighet</td><td class="value">${item['core:ArchivalObject.core:Producer']}</td></tr>
                <tr><td class="key">Klassificeringsstruktur (process)</td><td class="value">${item['core:ArchivalObject.core:Classification.core:Classification.name']}</td></tr>
                <tr><td class="key">Handlingstyp</td><td class="value">${item['core:ArchivalObject.core:ObjectType']}</td></tr>
                <tr><td class="key">Upprättat för enhet</td><td class="value">${item['vgr:VgrExtension.vgr:PublishedForUnit']}</td></tr>
                <tr><td class="key">Regional ämnesindelning</td><td class="value">${item['vgrsy:DomainExtension.vgrsy:SubjectClassification']}</td></tr>
                <tr><td class="key">Egen ämnesindelning</td><td class="value">${item['vgrsy:DomainExtension.vgrsy:SubjectLocalClassification']}</td></tr>
                <tr><td class="key">DokumentId källa</td><td class="value">${item['vgr:VgrExtension.vgr:Source.id']}</td></tr>
                <tr><td class="key">Källsystem</td><td class="value">${item['vgr:VgrExtension.vgr:SourceSystem']}</td></tr>
                <tr><td class="key">Upprättat av enhet</td><td class="value">${item['vgr:VgrExtension.vgr:CreatedByUnit.id']}</td></tr>
                <tr><td class="key">Upprättat av (person)</td><td class="value">${item['vgr:VgrExtension.vgr:CreatedBy']}</td></tr>
                <tr><td class="key">Åtkomsträtt</td><td class="value">${item['vgr:VgrExtension.vgr:SecurityClass']}</td></tr>
                <tr><td class="key">Länk för webben</td><td class="value">${item['url']}</td></tr>
                <tr><td class="key">Länk för webben (orginalformat)</td><td class="value">${item['originalDownloadLatestVersionUrl']}</td></tr>

            </tbody>
        </table>
    </div>


</div>

</body>
</html>