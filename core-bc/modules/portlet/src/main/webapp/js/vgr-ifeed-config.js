AUI().add('vgr-ifeed-config',function(A) {
	var Lang = A.Lang,
		isArray = Lang.isArray,
		isDate = Lang.isDate,
		isFunction = Lang.isFunction,
		isNull = Lang.isNull,
		isObject = Lang.isObject,
		isString = Lang.isString,
		isUndefined = Lang.isUndefined,
		getClassName = A.ClassNameManager.getClassName,
		concat = function() {
			return Array.prototype.slice.call(arguments).join(SPACE);
		},

		BOUNDING_BOX = 'boundingBox',
		CONTENT_BOX = 'contentBox',
		CSS_CLASS_EDIT_TRIGGER = 'ifeed-edit-trigger',
		CSS_CLASS_EDIT_TRIGGER_HEADING = 'ifeed-edit-trigger-heading',
		CSS_CLASS_EDIT_TRIGGER_DESCRIPTION = 'ifeed-edit-trigger-description',
		CSS_CLASS_TREE_NODE_TOOLTIP = 'tree-node-tooltip',
		DESCRIPTION_NODE = 'descriptionNode',
		EXISTING_FILTERS_TREE_BOUNDING_BOX = 'existingFiltersTreeBoundingBox',
		EXISTING_FILTERS_TREE_CONTENT_BOX = 'existingFiltersTreeContentBox',
		HEADING_NODE = 'headingNode',
		HREF = 'href',
		ID = 'id',
		NAME = 'vgr-ifeed-config',
		NS = 'vgr-ifeed-config',
		PARENT_NODE = 'parentNode',
		PORTLET_NAMESPACE = 'portletNamespace',
		PORTLET_WRAP = 'portletWrap',
		USED_FILTERS_TREE_BOUNDING_BOX = 'usedFiltersTreeBoundingBox',
		USED_FILTERS_TREE_CONTENT_BOX = 'usedFiltersTreeContentBox'

	;

	var VgrIfeedConfig = A.Component.create(
			{
				ATTRS: {
					existingFiltersTreeBoundingBox: {
						setter: A.one
					},
					existingFiltersTreeContentBox: {
						setter: A.one
					},
					descriptionNode: {
						setter: A.one
					},
					headingNode: {
						setter: A.one
					},
					portletNamespace: {
						value: ''
					},
					portletWrap: {
						setter: A.one
					},
					usedFiltersTreeBoundingBox: {
						setter: A.one
					},
					usedFiltersTreeContentBox: {
						setter: A.one
					}
				},
				EXTENDS: A.Component,
				NAME: NAME,
				NS: NS,
				editInlineTooltip: null,
				existingFiltersTree: null,
				headingEditable: null,
				treeNodeTooltip: null,
				usedFiltersTree: null,
				prototype: {
					initializer: function(config) {
						var instance = this;
						
						// Init debugger console (if console is activated, console must be added to the module dependency list)
						//instance._initConsole();
					},
					
					renderUI: function() {
						var instance = this;
						
						var contentBox = instance.get(CONTENT_BOX);
						
						// Create existing filter tree view
						instance.existingFiltersTree = new A.TreeView({
							boundingBox: instance.get(EXISTING_FILTERS_TREE_BOUNDING_BOX),
							contentBox: instance.get(EXISTING_FILTERS_TREE_CONTENT_BOX),
							type: 'normal'
						})
						.render();
						
						// Expand all existing filters node
						instance.existingFiltersTree.expandAll();
						
						// Create existing filter tree view
						instance.usedFiltersTree = new A.TreeView({
							boundingBox: instance.get(USED_FILTERS_TREE_BOUNDING_BOX),
							contentBox: instance.get(USED_FILTERS_TREE_CONTENT_BOX),
							type: 'normal'
						})
						.render();
						
						// Expand allused filters node
						instance.usedFiltersTree.expandAll();
						
						// Init editable for heading node
						instance.headingEditable = new A.Editable({
							node: instance.get(HEADING_NODE)
						});
						
						// Init editable for description node
						instance.descriptionEditable = new A.Editable({
							inputType: 'textarea',
							node: instance.get(DESCRIPTION_NODE)
						});
						
						// Setup tree node tooltips
						// 
						instance.treeNodeTooltip =  new A.Tooltip({
							trigger: '#' + instance.get(PORTLET_WRAP).get(ID) + ' .' + CSS_CLASS_TREE_NODE_TOOLTIP,
							align: { points: [ 'bc', 'tc' ] },
							hideDelay: 50,
							title: true							
						})
						.render();
					},
					
					bindUI: function() {
						var instance = this;
						
						// Bind edit triggers
						var portletWrap = instance.get(PORTLET_WRAP);
						var editTriggers = portletWrap.all('.' + CSS_CLASS_EDIT_TRIGGER);
						editTriggers.on('click', instance._onEditTriggersClick, instance);
					},
					
					_initConsole: function() {
						var instance = this;
						
						new A.Console({
							//height: '250px',
							newestOnTop: false,
							style: 'block',
							visible: true//,
							//width: '600px'
						}).render();
					},
					
					_onEditTriggersClick: function(e) {
						var instance = this;
						e.halt();
						
						var editableNode = false;
						var currentTarget = e.currentTarget;
						
						if(currentTarget.hasClass(CSS_CLASS_EDIT_TRIGGER_HEADING)) {
							editableNode = instance.headingEditable.get('node');
						}
						else if(currentTarget.hasClass(CSS_CLASS_EDIT_TRIGGER_DESCRIPTION)) {
							editableNode = instance.descriptionEditable.get('node');
						}
						
						if(editableNode) {
							editableNode.simulate('click');	
						}
					}
					
				}
			}
	);

	A.VgrIfeedConfig = VgrIfeedConfig;
		
	},1, {
		requires: [
			'aui-base',
			'aui-editable',
			'aui-io',
			'aui-tooltip',
			'aui-tree-view'
			//'console'
      ]
	}
);