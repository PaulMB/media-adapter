/*global Ext,SYNO,Media*/

Ext.define('Media.adapter.form.GlobalConfigurationPanel', {
    extend: 'SYNO.SDS.Utils.FormPanel',
    constructor: function (config) {
        Ext.apply(this, config);
        this.executorComboBox = new SYNO.ux.ComboBox({
            anchor: '100%',
            width: 'auto',
            fieldLabel: Media.adapter.util.AppUtil.msg('config', 'executor'),
            displayField: 'name',
            valueField: 'name',
            mode: 'local',
            editable: false,
            store: new Ext.data.JsonStore({
                autoDestroy: true,
                fields: ['name'],
                data: []
            })
        });
        var configuration = {
            bodyStyle:'padding:5px 5px 0',
            border: false,
            useDefaultBtn: true,
            title: Media.adapter.util.AppUtil.msg('config', 'global'),
            items: [ this.executorComboBox ]
        };
        this.callParent([configuration]);
    },
    setConfiguration: function(configuration) {
        this.configuration = configuration;
        this.executorComboBox.getStore().loadData(configuration.executors);
        this.executorComboBox.setValue(configuration.active);
    },
    applyHandler: function () {
        var me = this;
        var parentWindow = me.findAppWindow();
        parentWindow.setStatusBusy();
        Ext.Ajax.request({
            url: Media.adapter.util.AppUtil.getUrl() + 'config/executor/active',
            method: 'PUT',
            jsonData: this.executorComboBox.getValue(),
            scope: this,
            success: function (responseObject) {
                parentWindow.clearStatusBusy();
            },
            failure: function () {
                parentWindow.clearStatusBusy();
                parentWindow.getMsgBox().alert(Media.adapter.util.AppUtil.msg('app', 'app_name'), Media.adapter.util.AppUtil.msg('config', 'save_error'));
            }
        });
    },
    cancelHandler: function () {
        this.setConfiguration(this.configuration);
    }
});