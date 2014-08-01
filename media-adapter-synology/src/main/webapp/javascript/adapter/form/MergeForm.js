/*global Ext,SYNO,Media*/

Ext.define('Media.adapter.form.MergeForm', {
    extend: 'SYNO.ux.FormPanel',
    trackField: null,
    owner: null,
    constructor: function (config) {
        Ext.apply(this, config);
        this.owner = config.owner;
        this.trackField = new SYNO.ux.TextField({
            anchor: '100%',
            width: 'auto',
            readOnly: true,
            allowBlank: false,
            fieldLabel:Media.adapter.util.AppUtil.msg('edit', 'label'),
            emptyText: Media.adapter.util.AppUtil.msg('edit', 'help')
        });
        this.nameField = new SYNO.ux.TextField({
            anchor: '100%',
            width: 'auto',
            allowBlank: false,
            fieldLabel: Media.adapter.util.AppUtil.msg('edit', 'name')
        });
        this.typeComboBox = new SYNO.ux.ComboBox({
            anchor: '100%',
            width: 'auto',
            allowBlank: false,
            fieldLabel: Media.adapter.util.AppUtil.msg('edit', 'type'),
            valueField: "value",
            displayField: "display",
            value: 'SUBTITLE',
            store: new Ext.data.ArrayStore({
                autoDestroy: true,
                fields: ["display", "value"],
                data: [
                    [Media.adapter.util.AppUtil.msg("type", "subtitle"), "SUBTITLE"],
                    [Media.adapter.util.AppUtil.msg("type", "audio"), "AUDIO"],
                    [Media.adapter.util.AppUtil.msg("type", "video"), "VIDEO"]
                ]
            })
        });
        var configuration = {
            bodyStyle:'padding:5px 5px 0',
            border: false,
            fileUpload: false,
            items: [{
                xtype: "syno_compositefield",
                items: [
                    this.trackField, {
                        xtype: "syno_button",
                        text: Media.adapter.util.AppUtil.msg('edit', 'select'),
                        scope: this,
                        handler: this.onSelectTrackBtnClick
                    }
                ]
            }, this.nameField, this.typeComboBox]
        };
        this.callParent([configuration]);
    },
    getFieldValues: function() {
        return {
            path: this.trackField.getValue(),
            name: this.nameField.getValue(),
            trackType: this.typeComboBox.getValue()
        };
    },
    onSelectTrackBtnClick: function() {
        var me = this;
        new SYNO.SDS.Utils.FileChooser.Chooser({
            parent: this,
            owner: this.owner,
            usage: {type: "open", folder_path: "/"},
            title: Media.adapter.util.AppUtil.msg('chooser', 'title'),
            folderToolbar: true,
            listeners: {
                scope: me,
                choose: function (chooser, entry, e) {
                    var path = entry.fullpath;
                    if (path) {
                        me.trackField.setValue(path);
                    }
                    chooser.close();
                }
            }
        }).show();
    }
});