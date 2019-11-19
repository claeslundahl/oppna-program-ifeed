var uniqCallbackSequence = 0;

function appendParam(url, key, param) {
    return url
        + (url.indexOf("?") > 0 ? "&" : "?")
        + key + "=" + param;
}

function createScript(url, callback) {
    var head = document.getElementsByTagName('head')[0],
        script = document.createElement("script");

    script.setAttribute("src", url);
    head.appendChild(script);
    callback(function () {
        setTimeout(function () {
            head.removeChild(script);
        }, 0);
    });
}

function jsonp(url, key, callback) {
    var q = "q" + uniqCallbackSequence;
    uniqCallbackSequence++;

    if (window['jsonpPreventCache']) {
        url = url + '&t=' + new Date().getTime();
    }

    createScript(
        appendParam(url, key, q), function (remove) {
            window[q] =
                function (json) {
                    window[q] = undefined;
                    remove();
                    callback(json);
                };
        });
}

function getDomPath(el) {
    if (!el) {
        return;
    }
    var stack = [];
    var isShadow = false;
    while (el.parentNode != null) {
        var sibCount = 0;
        var sibIndex = 0;
        for ( var i = 0; i < el.parentNode.childNodes.length; i++ ) {
            var sib = el.parentNode.childNodes[i];
            if ( sib.nodeName == el.nodeName ) {
                if ( sib === el ) {
                    sibIndex = sibCount;
                }
                sibCount++;
            }
        }
        var nodeName = el.nodeName.toLowerCase();
        if (isShadow) {
            nodeName += "::shadow";
            isShadow = false;
        }
        if ( sibCount > 1 ) {
            stack.unshift(nodeName + ':nth-of-type(' + (sibIndex + 1) + ')');
        } else {
            stack.unshift(nodeName);
        }
        el = el.parentNode;
        if (el.nodeType === 11) { // for shadow dom, we
            isShadow = true;
            el = el.host;
        }
    }
    stack.splice(0,1); // removes the html element
    return stack.join(' > ');
}

function getAllFeedDivs() {
    var result = [];
    var divs = document.getElementsByTagName("div");
    for (var i = 0; i < divs.length; i++) {
        var div = divs[i];
        if (div.className && div.className.indexOf('ifeedDocList') > -1) {
            result.push(div);
        }
    }
    return result;
}

var feedAttributeNames = [
    'columnes','fontsize','defaultsortcolumn','defaultsortorder','extrasortcolumns','showtableheader','linkoriginaldoc','limit','hiderightcolumn','usepost','feedid'
];

function renderFeed(div) {
    var url = getDataHostUrl() + "/iFeed-web/display?v=1";

    for (var i = 0; i < feedAttributeNames.length; i++) {
        var name = feedAttributeNames[i];
        var value = div.getAttribute(name);
        if (value) {
            url += '&' + name + '=' + encodeURIComponent(value);
        }
    }
    jsonp(url, 'callback', function (result) {
        div.innerHTML = result.content;
    });
    // div.innerHTML = get(url);
}

function startIfeedRend() {
    // alert('I startIfeedRend');
    try {
        var feedDivs = getAllFeedDivs();
        for (var i = 0; i < feedDivs.length; i++) {
            var feedDiv = feedDivs[i];
            renderFeed(feedDiv);
        }
    }catch (e) {
        alert(e);
        alert(e.message);
    }
}

function toggleFeedOrder(div, key) {
    while (div.className.indexOf('ifeedDocList') == -1) {
        div = div.parentNode;
    }
    if (div.getAttribute('defaultsortcolumn') != key) {
        div.setAttribute('defaultsortcolumn', key);
        div.setAttribute("defaultsortorder", "asc");
    } else {
        div.setAttribute("defaultsortorder", "asc" == div.getAttribute('defaultsortorder') ? 'desc' : 'asc');
    }
    renderFeed(div);
}

function getDataHostUrl() {
    var urlConfigElement = document.getElementById('ifeed-data2');
    if (urlConfigElement) {
        return urlConfigElement.innerText;
    } else {
        return "//ifeed.vgregion.se"
    }
}

var currentTooltip = null;

function fillDocumentDetailTooltip(id, here) {
    currentTooltip = here;
    jsonp(getDataHostUrl() + "/iFeed-web/documents/" + id + "/metadata?type=tooltip", "kallback",
        function (response) {
            if (currentTooltip != here) return;
            here.innerHTML = response; // response.content;
            here.style.position = 'absolute';
            here.style.backgroundColor = 'white';
            here.style.width = '500px';
            // here.style.left = '20px';
            here.style.opacity = '1';
            here.style.border = 'grey thin solid';

            addCss("        /* Reset */\n" +
                "        #table-container body, #table-container div, #table-container dl, #table-container dt, #table-container dd, #table-container ul, #table-container ol, #table-container li, #table-container h1, #table-container h2, #table-container h3, #table-container h4, #table-container h5, #table-container h6, #table-container pre, #table-container form, #table-container fieldset, #table-container input, #table-container textarea, #table-container p, #table-container blockquote, #table-container th, #table-container td {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "\n" +
                "        #table-container a {\n" +
                "            color: #005baa;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            margin: 0px;\n" +
                "            text-decoration: none;\n" +
                "            vertical-align: top;\n" +
                "        }\n" +
                "\n" +
                "        #table-container a img {\n" +
                "            border: none;\n" +
                "            margin-right: 0.5em;\n" +
                "        }\n" +
                "\n" +
                "        #table-container ul.doc-list {\n" +
                "            list-style-type: none;\n" +
                "        }\n" +
                "\n" +
                "        #table-container ul.doc-list li {\n" +
                "            margin: 3px 0;\n" +
                "        }\n" +
                "\n" +
                "        #table-container ul.doc-list a.meta,\n" +
                "        #table-container ul.doc-list a.document {\n" +
                "            display: block;\n" +
                "        }\n" +
                "\n" +
                "        #table-container ul.doc-list a.meta {\n" +
                "            background: transparent url(information.png) 0 0 no-repeat;\n" +
                "            float: left;\n" +
                "            font-size: 0;\n" +
                "            height: 16px;\n" +
                "            text-indent: -9999em;\n" +
                "            width: 16px;\n" +
                "        }\n" +
                "\n" +
                "        #table-container ul.doc-list a.document {\n" +
                "            line-height: 16px;\n" +
                "            margin: 0 0 0 20px;\n" +
                "            text-decoration: underline;\n" +
                "        }\n" +
                "\n" +
                "        #table-container ul.doc-list a.document:hover {\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "\n" +
                "        #table-container {\n" +
                "            color: #333;\n" +
                "            font-family: Arial, Verdana, Helvetica, sans-serif;\n" +
                "            font-size: 12px;\n" +
                "            margin: 0.5em;\n" +
                "        }\n" +
                "\n" +
                "        #table-container #table-container {\n" +
                "            padding: 0 10px;\n" +
                "        }\n" +
                "\n" +
                "        #table-container #table-container h1 {\n" +
                "            font-size: 16px;\n" +
                "        }\n" +
                "\n" +
                "        #table-container .ifeed-metadata-table {\n" +
                "            border-collapse: collapse;\n" +
                "            margin: 20px 0px;\n" +
                "            width: 100%;\n" +
                "        }\n" +
                "\n" +
                "        #table-container .ifeed-metadata-table thead td {\n" +
                "            background-color: #ebebeb;\n" +
                "            font-size: 1.2em;\n" +
                "            font-weight: bold;\n" +
                "            padding-left: 6px;\n" +
                "            padding: 5px 0px 5px 6px;\n" +
                "            vertical-align: top;\n" +
                "        }\n" +
                "\n" +
                "        #table-container .ifeed-metadata-table tbody td {\n" +
                "            border-bottom: 1px solid lightgray;\n" +
                "            padding-left: 6px;\n" +
                "            padding-top: 1px;\n" +
                "            vertical-align: top;\n" +
                "        }\n" +
                "\n" +
                "        #table-container .ifeed-metadata-table td.no-styling {\n" +
                "            border-bottom: none;\n" +
                "        }\n" +
                "\n" +
                "        #table-container .ifeed-metadata-table td.key {\n" +
                "            font-weight: bold;\n" +
                "            width: 25%;\n" +
                "        }\n" +
                "\n" +
                "        #table-container #json-feed-link {\n" +
                "            position: relative;\n" +
                "            bottom: 2px;\n" +
                "        }\n" +
                "\n" +
                "        .title-header {\n" +
                "            display: none;\n" +
                "        }\n" +
                " .key a, .value a, .key a:hoover, .value a:hoover { text-decoration:none;}\n" +
                "\n");

            var allTips = document.getElementsByName('ifeed-tooltip');
            for (var i = 0; i < allTips.length; i++) {
                onTooltipOut(allTips[i]);
            }
            here.style.display = 'inline';
        });
}

function onTooltipOut(here) {
    currentTooltip = null;
    if (!here) return;
    here.style.display = 'none';
}

function openMetadataDetails(id) {
    var url = getDataHostUrl() + "/iFeed-web/documents/" + id + "/metadata";
    var win = window.open(url, '_blank');
    win.focus();
}

String.prototype.hashCode = function () {
    var hash = 0, i, chr;
    if (this.length === 0) return hash;
    for (i = 0; i < this.length; i++) {
        chr = this.charCodeAt(i);
        hash = ((hash << 5) - hash) + chr;
        hash |= 0; // Convert to 32bit integer
    }
    return hash;
};

Array.prototype.contains = function (obj) {
    var i = this.length;
    while (i--) {
        if (this[i] === obj) {
            return true;
        }
    }
    return false;
}

document['addedCssHashes'] = [];

function addCss(css) {
    if (document['addedCssHashes'].contains(css.hashCode())) return;
    var head = document.getElementsByTagName('head')[0];
    var s = document.createElement('style');
    s.setAttribute('type', 'text/css');
    if (s.styleSheet) {   // IE
        s.styleSheet.cssText = css;
    } else {                // the world
        s.appendChild(document.createTextNode(css));
    }
    head.appendChild(s);
    document['addedCssHashes'].push(css.hashCode());
}

/*if (window.attachEvent) {
    window.attachEvent('onload', startIfeedRend);
} else {
    if (window.onload) {
        var currentOnLoad = window.onload;
        var newOnLoad = function (evt) {
            currentOnLoad(evt);
            startIfeedRend();
        };
        window.onload = newOnLoad;
    } else {
        window.onload = startIfeedRend;
    }
}*/

setTimeout(startIfeedRend, 500);


function get(url) {
    console.log('Try to get this one', url)
    var xhr = new XMLHttpRequest();
    var result = {};
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4){
            result['text'] = xhr.responseText;
        }
    };
    xhr.open('GET', url, false);
    xhr.send();
    return result['text'];
}
