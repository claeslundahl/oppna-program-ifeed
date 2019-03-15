try {


    // alert("11");

    alert('Before script!');

    if (!String.prototype.startsWith) {
        String.prototype.startsWith = function (searchString) {
            var position = 0;
            return this.indexOf(searchString, position) === position;
        };
    }

    if (!String.prototype.substring) {
        String.prototype.substring = function (start, end) {
            var result = [];
            if (!end || end > this.length) end = this.length;
            for (var i = start; i < end; i++) // alert(this.charAt(i));
            return result.join('');
        };
    }

    // alert(12);

    var win = null;

    if (window.parent) {
        win = window.parent;
    } else {
        win = window;
    }
    var doc = win.document;

    var uniqueCallbackSequence = 0;

    // alert(13);

    function appendParam(url, key, param) {
        return url
            + (url.indexOf("?") > 0 ? "&" : "?")
            + key + "=" + param;
    }

    // alert(14);

    function createScript(url, callback) {
        // alert('createScript');
        var head = doc.getElementsByTagName('head')[0],
            script = doc.createElement("script");

        script.setAttribute('type', 'text/javascript');

        script.setAttribute("src", url);
        head.appendChild(script);
        callback(function () {
            setTimeout(function () {
                head.removeChild(script);
            }, 0);
        });
    }

    // alert(15);

    function jsonp(url, key, callback) {
        var q = "q" + uniqueCallbackSequence;
        uniqueCallbackSequence++;

        createScript(
            appendParam(url, key, q), function (remove) {
                win[q] =
                    function (json) {
                        win[q] = undefined;
                        remove();
                        callback(json);
                    };
            });
    }

    // alert(16);


    function getDomPath(el) {
        if (!el) {
            return;
        }
        var stack = [];
        var isShadow = false;
        while (el.parentNode != null) {
            // console.log(el.nodeName);
            var sibCount = 0;
            var sibIndex = 0;
            // get sibling indexes
            for (var i = 0; i < el.parentNode.childNodes.length; i++) {
                var sib = el.parentNode.childNodes[i];
                if (sib.nodeName == el.nodeName) {
                    if (sib === el) {
                        sibIndex = sibCount;
                    }
                    sibCount++;
                }
            }
            // if ( el.hasAttribute('id') && el.id != '' ) { no id shortcuts, ids are not unique in shadowDom
            //   stack.unshift(el.nodeName.toLowerCase() + '#' + el.id);
            // } else
            var nodeName = el.nodeName.toLowerCase();
            if (isShadow) {
                nodeName += "::shadow";
                isShadow = false;
            }
            if (sibCount > 1) {
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
        stack.splice(0, 1); // removes the html element
        return stack.join(' > ');
    }

    // alert(17);

    function getAllFeedDivs() {
        try {
            var result = [];
            var divs = doc.getElementsByTagName("div");
            for (var i = 0; i < divs.length; i++) {
                var div = divs[i];
                if (div.className && div.className.indexOf('ifeedDocList') > -1) {
                    result.push(div);
                }
            }
            return result;
        } catch (e) {
            // alert(e.message)
        }
    }

    var feedAttributeNames = [
        'columnes', 'fontsize', 'defaultsortcolumn', 'defaultsortorder', 'extrasortcolumns', 'showtableheader', 'linkoriginaldoc', 'limit', 'hiderightcolumn', 'usepost', 'feedid'
    ];

    // alert(17);

    function renderFeed(div) {
        // alert('renderFeed');
        try {
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
        } catch (e) {
            // alert(e);
        }
    }

    // alert(18);

    function startIfeedRend() {
        // alert('startIfeedRend');
        var feedDivs = getAllFeedDivs();
        for (var i = 0; i < feedDivs.length; i++) {
            var feedDiv = feedDivs[i];
            renderFeed(feedDiv);
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

    // alert(19);

    function getDataHostUrl() {
        // alert('getDataHostUrl');
        var urlConfigElement = doc.getElementById('ifeed-data2');
        var result = null;
        if (urlConfigElement) {
            result = urlConfigElement.innerText;
        } else {
            result = "//ifeed.vgregion.se"
        }
        if (result.startsWith('https://')) {
            result = result.substring(6);
        } else if (result.startsWith('http://')) {
            result = result.substring(5);
        }
        return result;
    }

    var currentTooltip = null;

    function fillDocumentDetailTooltip(id, here) {
        currentTooltip = here;
        jsonp(getDataHostUrl() + "/iFeed-web/documents/" + id + "/metadata?type=tooltip", "callback",
            function (response) {
                if (currentTooltip != here) return;
                here.innerHTML = response.content;
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
                    "        #table-container ul.doc-list a.doc {\n" +
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
                    "        #table-container ul.doc-list a.doc {\n" +
                    "            line-height: 16px;\n" +
                    "            margin: 0 0 0 20px;\n" +
                    "            text-decoration: underline;\n" +
                    "        }\n" +
                    "\n" +
                    "        #table-container ul.doc-list a.doc:hover {\n" +
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

                var allTips = doc.getElementsByName('ifeed-tooltip');
                for (var i = 0; i < allTips.length; i++) {
                    onTooltipOut(allTips[i]);
                }
                here.style.display = 'inline';
            });
    }

    // alert(20);

    function onTooltipOut(here) {
        currentTooltip = null;
        if (!here) return;
        here.style.display = 'none';
    }

    function openMetadataDetails(id) {
        var url = getDataHostUrl() + "/iFeed-web/documents/" + id + "/metadata";
        var w = win.open(url, '_blank');
        w.focus();
    }

    // alert(21);

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

    doc['addedCssHashes'] = [];

    function addCss(css) {
        if (doc['addedCssHashes'].contains(css.hashCode())) return;
        var head = doc.getElementsByTagName('head')[0];
        var s = doc.createElement('style');
        s.setAttribute('type', 'text/css');
        if (s.styleSheet) {   // IE
            s.styleSheet.cssText = css;
        } else {                // the world
            s.appendChild(doc.createTextNode(css));
        }
        head.appendChild(s);
        doc['addedCssHashes'].push(css.hashCode());
    }

    function addEvent(evnt, func) {
        if (win.addEventListener) {
            win.addEventListener(evnt, func, true);
        } else {
            win.attachEvent('on' + evnt, func);
        }
    }


    startIfeedRend();

} catch (e) {
    // alert(e.message);
}