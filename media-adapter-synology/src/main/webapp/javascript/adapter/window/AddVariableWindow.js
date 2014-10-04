/*global Ext,SYNO,Media*/

Ext.define('Media.adapter.window.AddVariableWindow', {
    extend: 'SYNO.SDS.ModalWindow',
    constructor: function (config) {
        Ext.apply(this, config);
        var me = this;
        var addForm = new Media.adapter.form.VariablePanel();
        var addButton = new SYNO.ux.Button({
            text: Media.adapter.util.AppUtil.msg('edit', 'add'),
            btnStyle: "blue",
            disabled: false,
            handler: function(){
                if(addForm.getForm().isValid()) {
                    me.fireEvent('variable_added', addForm.getFieldValues());
                    me.hide();
                }
            }
        });
        var closeButton = new SYNO.ux.Button({
            text: Media.adapter.util.AppUtil.msg('edit', 'close'),
            btnStyle: 'grey',
            handler: function(){
                me.hide();
            }
        });
        var configuration = {
            layout: 'fit',
            closable: false,
            resizable: false,
            width: 500,
            height: 200,
            items: addForm,
            buttons: [addButton, closeButton]
        };
        this.callParent([configuration]);
    }
});