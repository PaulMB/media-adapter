/*global Ext,SYNO,Media,console*/

var VariableRecord = Ext.data.Record.create([
    {name: 'name'},
    {name: 'value'}
]);

Ext.define('Media.adapter.grid.CommandExecutorGrid', {
    extend: 'SYNO.ux.GridPanel',
    addButton: null,
    contextMenu: null,
    constructor: function (config) {
        var me = this;
        Ext.apply(this, config);
        this.contextMenu = new SYNO.ux.Menu({
            items: {
                text: Media.adapter.util.AppUtil.msg('command', 'remove'),
                handler: function() {
                    var record = me.getSelectionModel().getSelected();
                    me.getStore().remove(record);
                }
            }
        });
        var button = new SYNO.ux.Button({
            text: Media.adapter.util.AppUtil.msg('command', 'add'), handler: this.onCreate, scope: this, name: 'addButton', disabled: false
        });
        this.addButton = button;
        var toolbar = new Ext.Toolbar({
            items: [button]
        });
        var configuration = {
            store: new Ext.data.JsonStore({
                autoDestroy: true,
                fields: ['name', 'value'],
                data: []
            }),
            sm: new Ext.grid.RowSelectionModel({ singleSelect:true }),
            colModel: new Ext.grid.ColumnModel({
                defaults: {
                    sortable: true,
                    resizable: true
                },
                columns: [
                    {header: Media.adapter.util.AppUtil.msg('command', 'name'), dataIndex: 'name' },
                    {header: Media.adapter.util.AppUtil.msg('command', 'value'), dataIndex: 'value' }
                ]
            }),
            enableColumnMove: false,
            tbar: toolbar,
            listeners: {
                rowcontextmenu: this.showContextMenu
            }
        };
        this.callParent([configuration]);
    },
    showContextMenu: function(grid, index, event) {
        event.stopEvent();
        grid.getSelectionModel().selectRow(index);
        this.contextMenu.showAt(event.getXY());
    },
    onCreate: function () {
        var me = this;
        var parent = this.findAppWindow();
        var window = new Media.adapter.window.AddVariableWindow({owner: parent});
        window.show(parent);
        window.on('variable_added', function(variable) {
            me.getStore().add(new VariableRecord(variable));
        });
    },
    getData: function () {
        return this.getStore().getRange().map(function(record) {
            return record.data;
        });
    }
});
