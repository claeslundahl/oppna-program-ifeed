<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tld/util.tld" prefix="vgr" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Dokumentegenskaper</title>
    <%--<link href="/iFeed-web/resources/style.css" rel="stylesheet" type="text/css">--%>
    <style type="text/css">

        /* Reset */
        body,div,dl,dt,dd,ul,ol,li,h1,h2,h3,h4,h5,h6,pre,form,fieldset,input,textarea,p,blockquote,th,td {
            margin: 0;
            padding: 0;
        }

        body,div,dl,dt,dd,ul,ol,li,h1,h2,h3,h4,h5,h6,pre,form,fieldset,input,textarea,p,blockquote,th,td {
            margin: 0;
            padding: 0;
        }

        a {
            color: #005baa;
            margin: 0;
            padding: 0;
            margin: 0px;
            text-decoration: underline;
            vertical-align: top;
        }

        a img {
            border: none;
            margin-right: 0.5em;
        }

        /* Clearfix style clearing */
        .clearfix:after{clear:both;content:'.';display:block;visibility:hidden;height:0}
        .clearfix{display:inline-block}
        * html .clearfix{height:1%}
        .clearfix{display:block}

        ul.doc-list {
            list-style-type: none;
        }

        ul.doc-list li {
            margin: 3px 0;
        }

        ul.doc-list a.meta,
        ul.doc-list a.document {
            display: block;
        }

        ul.doc-list a.meta {
            background: transparent url(information.png) 0 0 no-repeat;
            float: left;
            font-size: 0;
            height: 16px;
            text-indent: -9999em;
            width: 16px;
        }

        ul.doc-list a.document {
            line-height: 16px;
            margin: 0 0 0 20px;
            text-decoration: underline;
        }

        ul.doc-list a.document:hover {
            text-decoration: none;
        }

        /* Inherited from section#portlet_iFeed_WAR_iFeedportlet.portlet main.css: 1 */
        .portlet {
            text-align: left;
        }

        /* Inherited from div#aui_3_2_0_11035.portlet-layout main.css: 1*/
        .portlet-layout {
            border-collapse: collapse;
            border-spacing: 0;
        }

        /* Inherited from body#aui_3_2_0_1324.controls-hidden.signed-in.private-page main.css: 1 */
        body {
            color: #333;
            font-family: Arial, Verdana, Helvetica, sans-serif;
            font-size: 12px;
            margin: 0.5em;
        }

        #table-container {
            padding: 0 10px;
        }

        #table-container h1 {
            font-size: 16px;
        }

        .ifeed-metadata-table {
            border-collapse: collapse;
            margin: 20px 0px;
            width: 100%;
        }

        .ifeed-metadata-table thead td {
            background-color: #ebebeb;
            font-size: 1.2em;
            font-weight: bold;
            padding-left: 6px;
            padding: 5px 0px 5px 6px;
        }

        .ifeed-metadata-table tbody td {
            border-bottom: 1px solid lightgray;
            padding-left: 6px;
            padding-top: 1px;
        }

        .ifeed-metadata-table td.no-styling {
            border-bottom: none;
        }

        .ifeed-metadata-table td.key {
            font-weight: bold;
            width: 25%;
        }

        #json-feed-link {
            position: relative;
            bottom: 2px;
        }
    </style>
</head>
<body>

<div id="table-container">
    <h1>${doc.value}</h1>

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

</body>
</html>