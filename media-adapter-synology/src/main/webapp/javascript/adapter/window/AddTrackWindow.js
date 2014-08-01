/*global Ext,SYNO,Media*/

Ext.define('Media.adapter.window.AddTrackWindow', {
    extend: 'SYNO.SDS.ModalWindow',
    constructor: function (config) {
        Ext.apply(this, config);
        var me = this;
        var mergeForm = new Media.adapter.form.MergeForm({owner: this});
        var mergeButton = new SYNO.ux.Button({
            text: Media.adapter.util.AppUtil.msg('edit', 'add'),
            btnStyle: "blue",
            name: 'mergeButton',
            disabled: false,
            handler: function(){
                if(mergeForm.getForm().isValid()) {
                    me.fireEvent('track_added', mergeForm.getFieldValues());
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
            items: mergeForm,
            buttons: [mergeButton, closeButton]
        };
        this.callParent([configuration]);
    }
});