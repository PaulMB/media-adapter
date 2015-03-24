/*global Ext,SYNO,Media,Math*/

Ext.define('Media.adapter.form.CommandExecutorPanel', {
    extend: 'SYNO.SDS.Utils.FormPanel',
    constructor: function (config) {
        Ext.apply(this, config);
        this.itemId = config.name;
        this.binaryField = new SYNO.ux.TextField({
            anchor: '95%',
            width: 'auto',
            allowBlank: false,
            fieldLabel: Media.adapter.util.AppUtil.msg('command', 'binary')
        });
        this.environmentGrid = new Media.adapter.grid.CommandExecutorGrid();
        var configuration = {
            bodyStyle:'padding:5px 5px 0',
            border: false,
            title: config.name,
            useDefaultBtn: true,
            items: [ this.binaryField, new SYNO.ux.FieldSet({
                layout: 'fit',
                title: Media.adapter.util.AppUtil.msg('command', 'environment'),
                height: 350,
                items: this.environmentGrid
            })]
        };
        this.callParent([configuration]);
    },
    setConfiguration: function(configuration) {
        this.configuration = configuration;
        this.binaryField.setValue(configuration.binary);
        this.environmentGrid.getStore().loadData(configuration.environment);
    },
    applyHandler: function () {
        var me = this;
        var parentWindow = me.findAppWindow();
        parentWindow.setStatusBusy();
        Ext.Ajax.request({
            url: Media.adapter.util.AppUtil.getUrl() + 'config/executor/component/' + me.itemId,
            method: 'PUT',
            jsonData: {
                type: 'command',
                binary: me.binaryField.getValue(),
                environment: me.environmentGrid.getData()
            },
            scope: this,
            success: function (responseObject) {
                parentWindow.clearStatusBusy();
            },
            failure: function () {
                parentWindow.clearStatusBusy();
                parentWindow.getMsgBox().alert(Media.adapter.util.AppUtil.msg('app', 'app_name'), Media.adapter.util.AppUtil.msg('command', 'error'));
            }
        });
    },
    cancelHandler: function () {
        this.setConfiguration(this.configuration);
    }
});