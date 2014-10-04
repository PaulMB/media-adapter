/*global Ext,SYNO,Media*/

Ext.define("Media.adapter.AppInstance", {
    extend: "SYNO.SDS.AppInstance",
    appWindowName: "Media.adapter.AppWindow"
});

Ext.define("Media.adapter.AppWindow" ,{
    extend : "SYNO.SDS.PageListAppWindow",
    activePage: "Media.adapter.form.MainPanel",
    constructor: function (config) {
        var me = this;
        var configuration = Ext.apply({autoScroll: true, resizable: true, width: 1000, height: 600, minWidth: 480, minHeight: 360, dsmStyle: "v5", listItems: [
            {text: Media.adapter.util.AppUtil.msg('info', 'menu'), fn: "Media.adapter.form.MainPanel", iconCls: "icon-overview"},
            {text: Media.adapter.util.AppUtil.msg('work', 'menu'), fn: "Media.adapter.grid.MergeGrid", iconCls: "icon-general"},
            {text: Media.adapter.util.AppUtil.msg('config', 'menu'), fn: "Media.adapter.tab.ConfigurationPanel", iconCls: "icon-utilities"}
        ]}, config);
        this.callParent([configuration]);
    }
});