<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tld/util.tld" prefix="vgr" %>

<table onmouseoutx="for (var i = 0; i < previousTooltip.length; i++) onTooltipOut(previousTooltip[i]);"
       style="font-size: ${fontSize}">
    <tbody>
    <c:if test="${showTableHeader}">
        <col width="16px">

        <c:if test="${fn:length(columns) > 1}">
            <c:forEach items="${columns}" var="column">
                <col width="${column.width}%"
                     found-match="${defaultSortColumn eq column.key}"
                     exp="${defaultSortColumn} == ${column.key}">
            </c:forEach>
        </c:if>

    </c:if>

    <tr>
        <td></td>
        <c:forEach items="${columns}" var="column">
            <td class="ifeed-head-td">
                <div>
                    <a class="gwt-Anchor" href="#"
                       onclick="javascript:toggleFeedOrder(this, '${column.key}'); this.onclick = ''; return false;"
                       style="font-weight: bold; text-decoration: underline;">
                            ${column.title}
                        <c:if test="${defaultSortColumn eq column.key}">
                            <c:choose>
                                <c:when test="${defaultSortOrder eq 'asc'}">
                                    <img
                                            src="data:image/gif;base64,R0lGODlhEwAJAOMMAFpaWszMzGZmZtPT05mZme7u7nFxcbe3t+Dg4Pf394ODg+np6f///////////////yH+EUNyZWF0ZWQgd2l0aCBHSU1QACH5BAEUAA8ALAAAAAATAAkAAAQ8MMhJnz3H2kJp0QCgWUMXDBoREmNiJlsYflo5oZYiA0pLwY/CbjZCSBAawxBgGD0ktWUIZ1ksNAIpQBABADs="
                                            width="19" height="9" class="gwt-Image" style="border: none;">
                                </c:when>
                                <c:otherwise>
                                    <img
                                            src="data:image/gif;base64,R0lGODlhEwAJAOMMAFpaWszMzGZmZtPT05mZme7u7nFxcbe3t+Dg4Pf394ODg+np6f///////////////yH5BAEUAA8ALAAAAAATAAkAAAQ9UIBJp3h4LYxHrQMXBBxmfIDBISNSFmiBJeOYlEqlcEMdhBwYRfag+QI3DmFC4B1/pcck+BwRMYeDqDqKAAA7"
                                            width="19" height="9" class="gwt-Image" style="border: none;">
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                    </a>
                </div>
            </td>
        </c:forEach>
    </tr>

    <c:forEach items="${items}" var="item" varStatus="status">

        <tr>
            <td class="ifeed-info-td" style="vertical-align: top;">
                <table cellspacing="0" cellpadding="0">
                    <tbody>
                    <tr>
                        <td align="left" style="vertical-align: top;">
                            <a onclick="openMetadataDetails('${item.id}')" class="root-tool"
                               style="text-decoration: none;"
                               onmouseout="onTooltipOut(this.children[1]); return false;"
                               onmouseover="fillDocumentDetailTooltip('${item.id}', this.children[1])">
                                <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAACoElEQVR42q2T3U+SYRjG/VvgT+B9PaiD8qC51odvkHpgWWGZqK10FWWDVX4FaPKRIA2B/MrETIWSlKSA14/eUCDMjzcHZtDWVieStSyvHmnTuTmPfLbf2XNd9309z31nZOz1Ycy88Lgpqjiqj/CHGoIrWRpu5aBmkj+gmVDsU7PC3cWmWclhXSRZag/B7o2jj11OY/PEIDVNgFZ4kpRyRLKj+JgpKslunE41DM7DHUyidTiOWscs7j75AJ1zHg7fEur6oqDLBlLU5cHtJkeMEUF201RiQ+x8m4SCiG52htEd+IRu/xKut4Ug7wij7dUiantDoIsdCepcr2DTIFs/pSyxBOHivqCqMwo5iXDVOo0fv9bSVFg5VLRykD8KovN1DAXql6ALOpSbBln3Od7micPg5HHNPo1KaxAVFg7t3kW0EcrM7H9aAiRWGKbhOWTmWflNg/217Gr/5GdUtYdIpXcofzhOLrPYOOsEmdGPkgdvCD5cavHDwcZASZpXNw1opXf12fhyOutGpVJTgIgCiMa/I/VzDcVaLy40jeI8QaYfRc8YMWD0WwaZyhHe4uGhejqD8pZxyJr9uGh4g/exb8TgN4o0IygiuaVqN260sjC4I8gUa7ci0HK3slDnRbcvjnIjC5nOBze3hD9/19MRQh+/orDWRXDC/DyKvFt9oE9otx6RutIroGWOxJ2eabR5FlCsG4W0YRhnVEMorHHidPUATlX3o7EnSL53jLSvSlDiesG2WaCkXRJRri2leMzBNjIHucVPWn6Bs3UuVBpHYXKGIW9nQefUpChGvfM0Unl2CZVrTubfHoRhaAZdfp6wAK0rAnGVA1ROXZJi6iW77oMo3ywUnWxWUGI9TzGaFSrn3komo+JpRqUQMdXCPd/ef9SW0N736u7wAAAAAElFTkSuQmCC"
                                     width="16" height="16" class="gwt-Image">
                                <div class="tip" style="display: none; z-index: ${status.index + 10}" id="item${status.index}" name="ifeed-tooltip"
                                     onmouseoverx="onTooltipOut(this); return false;">
                                    Laddar dokumentinformation, var god vänta.
                                </div>
                            </a>
                        </td>
                        <c:if test="${not empty item['warning']}">
                            <td>
                                <img src="data:image/gif;base64,R0lGODlhCAAQAOMOAPkPEfkfIf7Pz/yPkPo/Qf2vsPtvcP/v7/tPUP2/wPx/gP2foP7f3/ovMf///////yH5BAEAAA8ALAAAAAAIABAAAAQg8MlJXwCCJgBC5cMHFGL1cCYBmMZaLS51IGZtn1h5PxEAOw=="
                                     width="8" height="16" class="gwt-Image"
                                     title="${item['warning']}">
                            </td>
                        </c:if>
                    </tr>
                    </tbody>
                </table>
            </td>
            <c:forEach items="${columns}" var="column">
                <td class="ifeed-link-td ifeed-field-dc-title"
                    style="vertical-align: top; text-align: ${column.alignment};">
                    <a class="gwt-Anchor" href="${item['url']}" target="_blank">
                            ${item[column.key]}
                    </a>
                </td>
            </c:forEach>
        </tr>

    </c:forEach>
    </tbody>
</table>
