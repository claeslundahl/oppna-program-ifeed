

AUI().add('ifeed-jsonp-builder',function(A) {
    var Lang = A.Lang,
        isNull = Lang.isNull,
        isUndefined = Lang.isUndefined,

        ADD_COLUMN_LINK = 'addColumnLink',

        EMBED_CODE_TEXTAREA = 'embedCodeTextarea',
        FORM = 'form',

        NAME = 'ifeed-jsonp-builder',
        NS = 'ifeed-jsonp-builder',

        PORTLET_NAMESPACE = 'portletNamespace',

        CSS_COLUMN_ROWS_WRAP = 'ifeed-jsonp-column-bd'
        CSS_COLUMN_ROW_ITEM = 'ifeed-jsonp-column-bd-item'
    ;

    var IfeedJsonpBuilder = A.Component.create(
            {
                ATTRS: {

                	addColumnLink: {
                		setter: A.one
                	},

                	embedCodeTextarea: {
                		setter: A.one
                	},

                	form: {
                        setter: A.one
                    },

                    portletNamespace: {
                    	value: ''
                    }

                },
                EXTENDS: A.Component,
                NAME: NAME,
                NS: NS,
                prototype: {
                    columnRowNodeTemplate: null,

                    initializer: function(config) {
                        var instance = this;
                    },

                    renderUI: function() {
                        var instance = this;

                        instance._copyColumnRowTemplate();
                    },

                    bindUI: function() {
                        var instance = this;

                        var form = instance.get(FORM);

                        if(form) {
                        	// Bind selects
                            var selects = form.all('select');
                            selects.on('change', instance._onSelectChange, instance);

                            // Bind text inputs
                            var textInputs = form.all('input[type="text"]');
                            textInputs.on('change', instance._onTextInputChange, instance);

                            // Bind delete column links (through delegate since new rows may be inserted after bind)
                            var columnRowsWrap = form.one('.' + CSS_COLUMN_ROWS_WRAP);

                            columnRowsWrap.delegate('click', instance._onDeleteColumRow, '.link-icon-delete', instance);

                            // Bind add column click
                            var addColumnLink = instance.get(ADD_COLUMN_LINK);
                            addColumnLink.on('click', instance._onAddColumnLinkClick, instance);
                        }
                    },

                    _copyColumnRowTemplate: function() {
                    	var instance = this;

                    	var form = instance.get(FORM);

                    	var firstColumnRow = form.one('.' + CSS_COLUMN_ROW_ITEM);

                		var clonedNode = firstColumnRow.clone(true);

                		instance.columnRowNodeTemplate = clonedNode;
                    },

                    _onAddColumnLinkClick: function(e) {
                    	var instance = this;

                    	e.halt();

                    	var form = instance.get(FORM);

                    	var columnRows = form.all('.' + CSS_COLUMN_ROW_ITEM);

                    	var columnRowsWrap = form.one('.' + CSS_COLUMN_ROWS_WRAP);

                    	var clonedNode = instance.columnRowNodeTemplate;

                    	columnRowsWrap.append(clonedNode);

                    	// Make a new template
                    	instance._copyColumnRowTemplate();

                    	//instance._renumberColumnRows();

                    	instance._submitForm();
                    },

                    _onDeleteColumRow: function(e) {
                    	var instance = this;

                    	e.halt();

                    	var currentTarget = e.currentTarget;

                    	// Find wrap node and delete it
                    	var wrapNode = currentTarget.ancestor('.' + CSS_COLUMN_ROW_ITEM);

                    	wrapNode.remove(true);

                    	instance._renumberColumnRows();

                    	instance._submitForm();
                    },

                    _onFormSubmitSuccess: function(e, id, xhr) {
                    	var instance = this;
                    	console.log(e);
                    	console.log(id);
                    	console.log(xhr);
                    	/* Todo - add logic to retrieve new embed code from xhr response */
                    	/* Currently just printing out random dummy data */
                    	//var embedCode = '<p>Dummy Code here now</p>' + '<div>' + Math.floor(Math.random()*100000001) + '</div>foo';
                    	var embedCode = xhr.response;

                    	instance._updateEmbedCode(embedCode);
                    },

                    _onSelectChange: function(e) {
                    	var instance = this;

                    	instance._submitForm();
                    },

                    _onTextInputChange: function(e) {
                    	var instance = this;

                    	instance._submitForm();
                    },

                    _renumberColumnRows: function() {
                        return;
                    	var instance = this;

                    	var form = instance.get(FORM);
                    	var portletNamespace = instance.get(PORTLET_NAMESPACE);

                    	var columnRows = form.all('.' + CSS_COLUMN_ROW_ITEM);

                    	columnRows.each(function(columnRow, rowIndex, columnRowsList) {

                    		var rowIndexDom = rowIndex + 1;

                    		columnRow.set('className', 'aui-layout ' + CSS_COLUMN_ROW_ITEM + ' ' + CSS_COLUMN_ROW_ITEM + '-' + rowIndexDom);

                    		// Rename all selects
                    		var selects = columnRow.all('select');
                    		selects.each(function(select, selectIndex, selectsList) {
                    			var dataName = select.getAttribute('data-name');
                    			var updatedId = portletNamespace + 'column_' + rowIndexDom + '_' + dataName;

                    			select.setAttribute('id', updatedId);
                    			select.setAttribute('name', updatedId);
                    		});

                    		// Rename all text inputs
                    		var textInputs = columnRow.all('input[type="text"]');
                    		textInputs.each(function(textInput, textInputIndex, textInputsList) {
                    			var dataName = textInput.getAttribute('data-name');
                    			var updatedId = portletNamespace + 'column_' + rowIndexDom + '_' + dataName;

                    			textInput.setAttribute('id', updatedId);
                    			textInput.setAttribute('name', updatedId);
                    		});


                    	});

                    },

                    _submitForm: function() {
                    	var instance = this;

                    	var form = instance.get(FORM);
                    	var formId = form.getAttribute('id');
                    	var url = form.getAttribute('action');

                    	// Create new IO request
                    	var formRequest = A.io.request(url, {
                			autoLoad : false,
                    	    cache: false,
                    	    dataType : 'html',
                    	    method : 'POST',
                    	    form : {
                    	    	id : formId
                    	    }
                    	});

                    	formRequest.on('success', instance._onFormSubmitSuccess, instance);

                    	formRequest.start();
                    },

                    _updateEmbedCode: function(embedCode) {
                    	var instance = this;

                    	var textarea = instance.get(EMBED_CODE_TEXTAREA);

                    	textarea.set('value', embedCode);
                    },


                    _someFunction: function() {
                        var instance = this;
                    }


                }
            }
    );

    A.IfeedJsonpBuilder = IfeedJsonpBuilder;

    },1, {
        requires: [
            'aui-base',
            'aui-io',
            'event'
      ]
    }
);