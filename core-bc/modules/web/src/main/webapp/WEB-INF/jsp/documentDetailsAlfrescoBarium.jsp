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

    <div>

        <table class="ifeed-metadata-table" style="text-decoration: none;">
            <c:if test="${not empty item['dc.title'] or not empty item['dc.title.filename'] or not empty item['dc.title.filename.native'] or not empty item['dc.title.alternative'] or not empty item['dc.description'] or not empty item['dc.type.document'] or not empty item['dc.type.document.structure'] or not empty item['dc.type.document.structure.id'] or not empty item['dc.type.record'] or not empty item['dc.coverage.hsacode'] or not empty item['dcterms.audience'] or not empty item['dc.audience'] or not empty item['dc.identifier.version'] or not empty item['dc.contributor.savedby'] or not empty item['dc.contributor.savedby.id'] or not empty item['dc.date.saved'] or not empty item['vgregion.status.document'] or not empty item['vgr.status.document'] or not empty item['vgr.status.document.id'] or not empty item['dc.source.documentid'] or not empty item['dc.source']}">
            <thead>
            <tr>
                <td colspan="2">Dokumentbeskrivning</td>
            </tr>
            </thead>
            <tbody>
            <c:if test="${not empty item['dc.title']}">
            <tr>
                <td class="key">Titel (autokomplettering)</td>
                <td class="value">${item['dc.title']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.title.filename']}">
            <tr>
                <td class="key">Filnamn, utgivet/publicerat</td>
                <td class="value">${item['dc.title.filename']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.title.filename.native']}">
            <tr>
                <td class="key">Filnamn, original</td>
                <td class="value">${item['dc.title.filename.native']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.title.alternative']}">
            <tr>
                <td class="key">Alternativ titel</td>
                <td class="value">${item['dc.title.alternative']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.description']}">
            <tr>
                <td class="key">Beskrivning</td>
                <td class="value">${item['dc.description']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.type.document']}">
            <tr>
                <td class="key">Gruppering av handlingstyper</td>
                <td class="value">${item['dc.type.document']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.type.document.structure']}">
            <tr>
                <td class="key">Dokumentstruktur VGR</td>
                <td class="value">${item['dc.type.document.structure']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.type.document.structure.id']}">
            <tr>
                <td class="key">Dokumentstruktur VGR ID</td>
                <td class="value">${item['dc.type.document.structure.id']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.type.record']}">
            <tr>
                <td class="key">Handlingstyp (autokomplettering)</td>
                <td class="value">${item['dc.type.record']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.coverage.hsacode']}">
            <tr>
                <td class="key">Verksamhetskod enligt HSA</td>
                <td class="value">${item['dc.coverage.hsacode']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dcterms.audience']}">
            <tr>
                <td class="key">Målgrupp HoS (autokomplettering)</td>
                <td class="value">${item['dcterms.audience']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.audience']}">
            <tr>
                <td class="key">Målgrupp HoS (autokomplettering)</td>
                <td class="value">${item['dc.audience']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.identifier.version']}">
            <tr>
                <td class="key">Version</td>
                <td class="value">${item['dc.identifier.version']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.contributor.savedby']}">
            <tr>
                <td class="key">Sparat av</td>
                <td class="value">${item['dc.contributor.savedby']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.contributor.savedby.id']}">
            <tr>
                <td class="key">Sparat av ID</td>
                <td class="value">${item['dc.contributor.savedby.id']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.date.saved']}">
            <tr>
                <td class="key">Sparat datum</td>
                <td class="value">${item['dc.date.saved']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['vgregion.status.document']}">
            <tr>
                <td class="key">Dokumentstatus</td>
                <td class="value">${item['vgregion.status.document']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['vgr.status.document']}">
            <tr>
                <td class="key">Dokumentstatus</td>
                <td class="value">${item['vgr.status.document']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['vgr.status.document.id']}">
            <tr>
                <td class="key">Dokumentstatus</td>
                <td class="value">${item['vgr.status.document.id']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.source.documentid']}">
            <tr>
                <td class="key">Dokumentid källa</td>
                <td class="value">${item['dc.source.documentid']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.source']}">
            <tr>
                <td class="key">Länk till dokumentets källa</td>
                <td class="value">${item['dc.source']}</td>
            </tr>
            </c:if>
            <tbody>
            </c:if>
            <c:if test="${not empty item['dc.creator'] or not empty item['dc.creator.id'] or not empty item['dc.creator.freetext'] or not empty item['dc.creator.forunit'] or not empty item['dc.creator.forunit.id'] or not empty item['dc.creator.project-assignment']}">
            <thead>
            <tr>
                <td colspan="2">Skapat av och för</td>
            </tr>
            </thead>
            <tbody>
            <c:if test="${not empty item['dc.creator']}">
            <tr>
                <td class="key">Skapat av</td>
                <td class="value">${item['dc.creator']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.creator.id']}">
            <tr>
                <td class="key">Skapat av ID</td>
                <td class="value">${item['dc.creator.id']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.creator.freetext']}">
            <tr>
                <td class="key">Skapat av (Fritext)</td>
                <td class="value">${item['dc.creator.freetext']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.creator.forunit']}">
            <tr>
                <td class="key">Skapat av enhet (autokomplettering)</td>
                <td class="value">${item['dc.creator.forunit']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.creator.forunit.id']}">
            <tr>
                <td class="key">Skapat av enhet ID (VGR:s organisationsträd)</td>
                <td class="value">${item['dc.creator.forunit.id']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.creator.project-assignment']}">
            <tr>
                <td class="key">Skapat av Projekt/Uppdrag/Grupp</td>
                <td class="value">${item['dc.creator.project-assignment']}</td>
            </tr>
            </c:if>
            <tbody>
            </c:if>
            <c:if test="${not empty item['dc.creator.document'] or not empty item['dc.creator.document.id'] or not empty item['dc.creator.function'] or not empty item['dc.creator.recordscreator'] or not empty item['dc.creator.recordscreator.id']}">
            <thead>
            <tr>
                <td colspan="2">Ansvariga</td>
            </tr>
            </thead>
            <tbody>
            <c:if test="${not empty item['dc.creator.document']}">
            <tr>
                <td class="key">Innehållsansvarig/Dokumentansvarig</td>
                <td class="value">${item['dc.creator.document']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.creator.document.id']}">
            <tr>
                <td class="key">Innehållsansvarig/Dokumentansvarig ID</td>
                <td class="value">${item['dc.creator.document.id']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.creator.function']}">
            <tr>
                <td class="key">Funktionsansvar</td>
                <td class="value">${item['dc.creator.function']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.creator.recordscreator']}">
            <tr>
                <td class="key">Arkivbildare (autokomplettering)</td>
                <td class="value">${item['dc.creator.recordscreator']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.creator.recordscreator.id']}">
            <tr>
                <td class="key">Arkivbildare ID (VGR:s organisationsträd)</td>
                <td class="value">${item['dc.creator.recordscreator.id']}</td>
            </tr>
            </c:if>
            <tbody>
            </c:if>
            <c:if test="${not empty item['dc.date.validfrom'] or not empty item['dc.date.validto'] or not empty item['dc.date.availablefrom'] or not empty item['dc.date.availableto'] or not empty item['dc.date.copyrighted']}">
            <thead>
            <tr>
                <td colspan="2">Giltighet och tillgänglighet</td>
            </tr>
            </thead>
            <tbody>
            <c:if test="${not empty item['dc.date.validfrom']}">
            <tr>
                <td class="key">Giltighetsdatum from</td>
                <td class="value">${item['dc.date.validfrom']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.date.validto']}">
            <tr>
                <td class="key">Giltighetsdatum tom</td>
                <td class="value">${item['dc.date.validto']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.date.availablefrom']}">
            <tr>
                <td class="key">Tillgänglighetsdatum from</td>
                <td class="value">${item['dc.date.availablefrom']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.date.availableto']}">
            <tr>
                <td class="key">Tillgänglighetsdatum tom</td>
                <td class="value">${item['dc.date.availableto']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.date.copyrighted']}">
            <tr>
                <td class="key">Copyrightdatum</td>
                <td class="value">${item['dc.date.copyrighted']}</td>
            </tr>
            </c:if>
            <tbody>
            </c:if>
            <c:if test="${not empty item['dc.contributor.acceptedby'] or not empty item['dc.contributor.acceptedby.id'] or not empty item['dc.contributor.acceptedby.freetext'] or not empty item['dc.date.accepted'] or not empty item['dc.contributor.acceptedby.role'] or not empty item['dc.contributor.acceptedby.unit.freetext'] or not empty item['dc.contributor.controlledby'] or not empty item['dc.contributor.controlledby.id'] or not empty item['dc.contributor.controlledby.freetext'] or not empty item['dc.date.controlled'] or not empty item['dc.contributor.controlledby.role'] or not empty item['dc.contributor.controlledby.unit.freetext']}">
            <thead>
            <tr>
                <td colspan="2">Granskat/Godkänt</td>
            </tr>
            </thead>
            <tbody>
            <c:if test="${not empty item['dc.contributor.acceptedby']}">
            <tr>
                <td class="key">Godkänt av</td>
                <td class="value">${item['dc.contributor.acceptedby']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.contributor.acceptedby.id']}">
            <tr>
                <td class="key">Godkänt av ID</td>
                <td class="value">${item['dc.contributor.acceptedby.id']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.contributor.acceptedby.freetext']}">
            <tr>
                <td class="key">Godkänt av (Fritext)</td>
                <td class="value">${item['dc.contributor.acceptedby.freetext']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.date.accepted']}">
            <tr>
                <td class="key">Godkänt datum</td>
                <td class="value">${item['dc.date.accepted']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.contributor.acceptedby.role']}">
            <tr>
                <td class="key">Godkänt av Egenskap/Roll</td>
                <td class="value">${item['dc.contributor.acceptedby.role']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.contributor.acceptedby.unit.freetext']}">
            <tr>
                <td class="key">Enhet (Fritext)</td>
                <td class="value">${item['dc.contributor.acceptedby.unit.freetext']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.contributor.controlledby']}">
            <tr>
                <td class="key">Granskat av</td>
                <td class="value">${item['dc.contributor.controlledby']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.contributor.controlledby.id']}">
            <tr>
                <td class="key">Granskat av ID</td>
                <td class="value">${item['dc.contributor.controlledby.id']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.contributor.controlledby.freetext']}">
            <tr>
                <td class="key">Granskat av (Fritext)</td>
                <td class="value">${item['dc.contributor.controlledby.freetext']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.date.controlled']}">
            <tr>
                <td class="key">Granskningsdatum</td>
                <td class="value">${item['dc.date.controlled']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.contributor.controlledby.role']}">
            <tr>
                <td class="key">Granskat av Egenskap/Roll</td>
                <td class="value">${item['dc.contributor.controlledby.role']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.contributor.controlledby.unit.freetext']}">
            <tr>
                <td class="key">Enhet (Fritext)</td>
                <td class="value">${item['dc.contributor.controlledby.unit.freetext']}</td>
            </tr>
            </c:if>
            <tbody>
            </c:if>
            <c:if test="${not empty item['dc.publisher.forunit'] or not empty item['dc.publisher.forunit.flat'] or not empty item['dc.publisher.forunit.id'] or not empty item['dc.publisher.project-assignment'] or not empty item['dc.rights.accessrights'] or not empty item['dc.publisher'] or not empty item['dc.publisher.id'] or not empty item['dc.date.issued'] or not empty item['dc.identifier'] or not empty item['dc.identifier.native']}">
            <thead>
            <tr>
                <td colspan="2">Publicerat</td>
            </tr>
            </thead>
            <tbody>
                <c:if test="${not empty item['dc.publisher.forunit.id']}">
            <tr>
                <td class="key">Publicerat för enhet</td>
                <td class="value">${item['dc.publisher.forunit.id']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.publisher.project-assignment']}">
            <tr>
                <td class="key">Publicerat för Projekt/Uppdrag/Grupp</td>
                <td class="value">${item['dc.publisher.project-assignment']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.rights.accessrights']}">
            <tr>
                <td class="key">Publik åtkomsträtt</td>
                <td class="value">${item['dc.rights.accessrights']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.publisher']}">
            <tr>
                <td class="key">Publicerat av</td>
                <td class="value">${item['dc.publisher']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.publisher.id']}">
            <tr>
                <td class="key">Publicerat av ID</td>
                <td class="value">${item['dc.publisher.id']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.date.issued']}">
            <tr>
                <td class="key">Publiceringsdatum</td>
                <td class="value">${item['dc.date.issued']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.identifier']}">
            <tr>
                <td class="key">Länk till publicerat/utgivet dokument</td>
                <td class="value">${item['dc.identifier']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.identifier.native']}">
            <tr>
                <td class="key">Länk till utgivet orginaldokument</td>
                <td class="value">${item['dc.identifier.native']}</td>
            </tr>
            </c:if>
            <tbody>
            </c:if>
            <c:if test="${not empty item['dc.type.process.name'] or not empty item['dc.type.file.process'] or not empty item['dc.type.file'] or not empty item['dc.identifier.diarie.id'] or not empty item['dc.type.document.serie'] or not empty item['dc.type.document.id']}">
            <thead>
            <tr>
                <td colspan="2">Sammanhang</td>
            </tr>
            </thead>
            <tbody>
            <c:if test="${not empty item['dc.type.process.name']}">
            <tr>
                <td class="key">Processnamn</td>
                <td class="value">${item['dc.type.process.name']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.type.file.process']}">
            <tr>
                <td class="key">Ärendetyp</td>
                <td class="value">${item['dc.type.file.process']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.type.file']}">
            <tr>
                <td class="key">Ärende</td>
                <td class="value">${item['dc.type.file']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.identifier.diarie.id']}">
            <tr>
                <td class="key">Diarienummer</td>
                <td class="value">${item['dc.identifier.diarie.id']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.type.document.serie']}">
            <tr>
                <td class="key">Dokumentserie</td>
                <td class="value">${item['dc.type.document.serie']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.type.document.id']}">
            <tr>
                <td class="key">Referensnummer i dokumentserie</td>
                <td class="value">${item['dc.type.document.id']}</td>
            </tr>
            </c:if>
            <tbody>
            </c:if>
            <c:if test="${not empty item['dc.subject.keywords'] or not empty item['dc.subject.authorkeywords']}">
            <thead>
            <tr>
                <td colspan="2">Nyckelord</td>
            </tr>
            </thead>
            <tbody>
            <c:if test="${not empty item['dc.subject.keywords']}">
            <tr>
                <td class="key">Nyckelord (autokomplettering)</td>
                <td class="value">${item['dc.subject.keywords']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.subject.authorkeywords']}">
            <tr>
                <td class="key">Författarens nyckelord</td>
                <td class="value">${item['dc.subject.authorkeywords']}</td>
            </tr>
            </c:if>
            <tbody>
            </c:if>
            <c:if test="${not empty item['language'] or not empty item['dc.relation.isversionof'] or not empty item['dc.relation.replaces'] or not empty item['dc.format.extent'] or not empty item['dc.identifier.location'] or not empty item['dc.type.templatename'] or not empty item['dc.format.extent.mimetype'] or not empty item['dc.format.extent.mimetype.native'] or not empty item['dc.format.extension'] or not empty item['dc.format.extension.native'] or not empty item['dc.identifier.checksum'] or not empty item['dc.identifier.checksum.native'] or not empty item['dc.source.origin']}">
            <thead>
            <tr>
                <td colspan="2">Övrigt</td>
            </tr>
            </thead>
            <tbody>
            <c:if test="${not empty item['language']}">
            <tr>
                <td class="key">Språk</td>
                <td class="value">${item['language']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.relation.isversionof']}">
            <tr>
                <td class="key">Alternativ variant av</td>
                <td class="value">${item['dc.relation.isversionof']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.relation.replaces']}">
            <tr>
                <td class="key">Ersätter</td>
                <td class="value">${item['dc.relation.replaces']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.format.extent']}">
            <tr>
                <td class="key">Omfattning</td>
                <td class="value">${item['dc.format.extent']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.identifier.location']}">
            <tr>
                <td class="key">Fysisk placering</td>
                <td class="value">${item['dc.identifier.location']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.type.templatename']}">
            <tr>
                <td class="key">Mallnamn</td>
                <td class="value">${item['dc.type.templatename']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.format.extent.mimetype']}">
            <tr>
                <td class="key">Mimetyp, utgivet/publicerat</td>
                <td class="value">${item['dc.format.extent.mimetype']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.format.extent.mimetype.native']}">
            <tr>
                <td class="key">Mimetyp, original</td>
                <td class="value">${item['dc.format.extent.mimetype.native']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.format.extension']}">
            <tr>
                <td class="key">Filändelse, utgivet/publicerat</td>
                <td class="value">${item['dc.format.extension']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.format.extension.native']}">
            <tr>
                <td class="key">Filändelse, original</td>
                <td class="value">${item['dc.format.extension.native']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.identifier.checksum']}">
            <tr>
                <td class="key">Kontrollsumma dokument, utgivet/publicerat</td>
                <td class="value">${item['dc.identifier.checksum']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.identifier.checksum.native']}">
            <tr>
                <td class="key">Kontrollsumma dokument, original</td>
                <td class="value">${item['dc.identifier.checksum.native']}</td>
            </tr>
                </c:if>
                <c:if test="${not empty item['dc.source.origin']}">
            <tr>
                <td class="key">Källsystem</td>
                <td class="value">${item['dc.source.origin']}</td>
            </tr>
            </c:if>
            <tbody>
            </c:if>
            <table>

    </div>


</div>

</body>
</html>