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
    console.log('refreshResults before');
    setTimeout(function () {document.getElementById('rerun-ifeed-loading').click();}, 500);
    console.log('refreshResults after');
}