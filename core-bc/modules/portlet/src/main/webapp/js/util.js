function toggle() {
    var dta = 'display-toggle'; // name of the attribute... for brievety.
   for (var i = 0; i < arguments.length; i++) {
       var id = arguments[i];
       $('#' + id).toggle();
       //var e = document.getElementById(id);
       //var displayToggle = e.hasAttribute(dta) ? e.getAttribute(dta) : (e.style.display != 'none' ? 'none' : '');
       //e.setAttribute(dta, e.style.display);
       //e.style.display = displayToggle;
    }
}

function refreshResults() {
    setTimeout(function () {
        // document.getElementById('rerun-ifeed-loading').click();
        se.vgregion.ifeed.client.Starter.init();
    }, 500);
}

function httpGet(url, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', url);
    xhr.send(null);

    xhr.onreadystatechange = function () {
        var DONE = 4; // readyState 4 means the request is done.
        var OK = 200; // status 200 is a successful return.
        if (xhr.readyState == DONE) {
            if (xhr.status == OK)
                callback(xhr.responseText)
        } else {
            // console.log('Error: ' + xhr.status); // An error occurred during the request.
        }
    };
}


