/*global Ext,SYNO,Media*/

Ext.define('Media.adapter.grid.MergeGrid', {
    extend: 'SYNO.ux.GridPanel',
    task: null,
    contextMenu: null,
    constructor: function (config) {
        Ext.apply(this, config);
        var me = this;
        this.contextMenu = new SYNO.ux.Menu({
            items: {
                text: Media.adapter.util.AppUtil.msg('work', 'remove'),
                handler: function() {
                    var record = me.getSelectionModel().getSelected();
                    var parentWindow = me.findAppWindow();
                    parentWindow.setStatusBusy();
                    Ext.Ajax.request({
                        method: 'DELETE',
                        url: Media.adapter.util.AppUtil.getUrl() + 'merge/'+ record.get('id'),
                        success: function(responseObject) {
                            parentWindow.clearStatusBusy();
                            me.reloadStatus();
                        },
                        failure: function(responseObject) {
                            parentWindow.clearStatusBusy();
                            parentWindow.getMsgBox().alert(Media.adapter.util.AppUtil.msg('app', 'app_name'), responseObject.responseText);
                        }
                    });
                }
            }
        });
        var configuration = {
            store: new Media.adapter.store.MergeStore(),
            sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
            colModel: new Ext.grid.ColumnModel({
                defaults: {
                    sortable: true,
                    resizable: true,
                    width: 250
                },
                columns: [
                    {header: Media.adapter.util.AppUtil.msg('work', 'input'), dataIndex: 'input', id: 'input_column'},
                    {header: Media.adapter.util.AppUtil.msg('work', 'status'), dataIndex: 'status', renderer: Media.adapter.util.AppUtil.mergeStatusRenderer},
                    {header: Media.adapter.util.AppUtil.msg('work', 'message'), dataIndex: 'message', renderer:
                        function(value, metadata){
                            metadata.attr = 'ext:qtip="' + value + '"';
                            return value;
                        }
                    }
                ]
            }),
            enableColumnMove: false,
            height: 200,
            listeners: {
                rowcontextmenu: this.showContextMenu
            }
        };
        this.callParent([configuration]);
        this.appWin.getPageList().getSelectionModel().on('selectionchange', function(model, node) {
            if ( node.id === 'Media.adapter.grid.MergeGrid') {
                me.reloadStatus();
            }
        });
        this.onCreate();
    },
    showContextMenu: function(grid, index, event) {
        event.stopEvent();
        grid.getSelectionModel().selectRow(index);
        this.contextMenu.showAt(event.getXY());
    },
    onCreate: function () {   //TODO events
        var me = this;
        this.task = this.addAjaxTask({id: "task_get_merge_status", interval: 5 * 1000, url: Media.adapter.util.AppUtil.getUrl() + 'merge', method: "GET", autoJsonDecode: false, callback: this.onAjaxRequestDone, scope: this});
        this.task.start();
        this.on('destroy', function() {
            me.task.stop();
            me.removeTask("task_get_merge_status");
        });
    },
    reloadStatus: function() {
        this.task.restart();
    },
    onAjaxRequestDone: function(request, flag, response) {
        var text = Ext.decode(response.responseText);
        this.getStore().loadData(text);
    }
});
