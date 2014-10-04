/*global Ext,SYNO,Media,console*/

Ext.define('Media.adapter.tab.ConfigurationPanel', {
    extend: 'SYNO.ux.TabPanel',
    tabCreated: false,
    constructor: function (config) {
        Ext.apply(this, config);
        var me = this;
        this.globalConfiguration = new Media.adapter.form.GlobalConfigurationPanel({appWin: config.appWin});
        var configuration = {
            border: false,
            items: [ this.globalConfiguration ]
        };
        this.callParent([configuration]);
    },
    onPageActivate: function () {
        var me = this;
        this.appWin.setStatusBusy();
        Ext.Ajax.request({
            url: Media.adapter.util.AppUtil.getUrl() + 'config/executor',
            method: "GET",
            scope: this,
            success: function (responseObject) {
                var config = Ext.decode(responseObject.responseText);
                me.appWin.clearStatusBusy();
                me.globalConfiguration.setConfiguration(config);
                me.createTabsIfNecessary(config);
                me.setTabsConfiguration(config);
            },
            failure: function (responseObject) {
                me.appWin.clearStatusBusy();
                me.appWin.getMsgBox().alert(Media.adapter.util.AppUtil.msg('app', 'app_name'), responseObject.responseText);
            }
        });
    },
    createTabsIfNecessary: function (config) {
        var me = this;
        if ( me.tabCreated === false) {
            me.processExecutors(config.executors, {
                onCommandExecutor: function (executor, tabPanel) {
                    tabPanel.add(new Media.adapter.form.CommandExecutorPanel({name: executor.name}));
                }
            });
            me.tabCreated = true;
        }
    },
    setTabsConfiguration: function(config) {
        var me = this;
        me.processExecutors(config.executors, {
            onCommandExecutor: function(executor, tabPanel) {
                tabPanel.getItem(executor.name).setConfiguration(executor.configuration);
            }
        });
    },
    processExecutors: function(executors, handler) {
        var me = this;
        executors.forEach(function(executor) {
            switch ( executor.configuration.type) {
                case 'command':
                    if ( handler.onCommandExecutor ) {
                        handler.onCommandExecutor(executor, me);
                    }
                    break;
            }
        });
    }
});