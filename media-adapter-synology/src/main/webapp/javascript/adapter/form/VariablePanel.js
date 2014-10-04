/*global Ext,SYNO,Media*/

Ext.define('Media.adapter.form.VariablePanel', {
    extend: 'SYNO.ux.FormPanel',
    constructor: function (config) {
        Ext.apply(this, config);
        this.nameField = new SYNO.ux.TextField({
            anchor: '100%',
            width: 'auto',
            name: 'name',
            allowBlank: false,
            fieldLabel: Media.adapter.util.AppUtil.msg('command', 'name')
        });
        this.valueField = new SYNO.ux.TextField({
            anchor: '100%',
            width: 'auto',
            name: 'value',
            fieldLabel: Media.adapter.util.AppUtil.msg('command', 'value')
        });
        var configuration = {
            bodyStyle:'padding:5px 5px 0',
            border: false,
            items: [ this.nameField, this.valueField ]
        };
        this.callParent([configuration]);
    },
    getFieldValues: function() {
        return this.getForm().getFieldValues();
    }
});