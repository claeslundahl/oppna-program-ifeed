//console.log('start-dialog.js run start');

        PrimeFaces.widget.Dialog = PrimeFaces.widget.Dialog.extend({
  init: function(cfg) {
    this._super(cfg);

    if(!this.cfg.appendToBody) {
      this.appendToForm();
    }
  }, blockEvents:'',

  appendToForm: function() {
    //console.log('appendToForm starts');
    var dialogs = this.jq;

    if (dialogs.size() > 1) {

      $(dialogs[0]).replaceWith($(dialogs[1]));

      for (p in window) {
        if (window[p] instanceof PrimeFaces.widget.BaseWidget) {
          var widget = window[p];

          if (widget.jq && $.contains(dialogs[1], widget.jq[1])) {
            widget.init(widget.cfg);
          }
        }
      }

      this.jq = $(this.jqId);
    }
    else {
      $(dialogs[0]).appendTo($(dialogs[0]).closest("form"));
    }
    //console.log('appendToForm ends');
  }
});

/*
dlg2.__proto__.appendToForm = function() {
                   console.log('appendToForm starts');
                   var dialogs = this.jq;

                   if (dialogs.size() > 1) {

                     $(dialogs[0]).replaceWith($(dialogs[1]));

                     for (p in window) {
                       if (window[p] instanceof PrimeFaces.widget.BaseWidget) {
                         var widget = window[p];

                         if (widget.jq && $.contains(dialogs[1], widget.jq[1])) {
                           widget.init(widget.cfg);
                         }
                       }
                     }

                     this.jq = $(this.jqId);
                   }
                   else {
                     $(dialogs[0]).appendTo($(dialogs[0]).closest("form"));
                   }
                   console.log('appendToForm ends');
                 };




dlg2.appendToForm();
        dlg2.show();*/

//console.log('start-dialog.js run ends');