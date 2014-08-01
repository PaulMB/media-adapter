/*global Ext,SYNO,Media*/

var TrackRecord = Ext.data.Record.create([
    {name: 'path'},
    {name: 'name'},
    {codecId: 'codecId'},
    {language: 'language'},
    {trackType: 'trackType'}
]);

Ext.define('Media.adapter.grid.TrackGrid', {
    extend: 'SYNO.ux.GridPanel',
    addButton: null,
    input: null,
    contextMenu: null,
    constructor: function (config) {
        var me = this;
        Ext.apply(this, config);
        this.contextMenu = new SYNO.ux.Menu({
            items: {
                text: Media.adapter.util.AppUtil.msg('work', 'remove'),
                handler: function() {
                    var record = me.getSelectionModel().getSelected();
                    if ( record.mustBeAdded === true ) {
                        me.getStore().remove(record);
                    } else {
                        record.mustBeRemoved = true;
                        me.getStore().fireEvent('datachanged', me.getStore());
                    }
                }
            }
        });
        var button = new SYNO.ux.Button({
            text: Media.adapter.util.AppUtil.msg('track', 'add'), handler: this.onCreate, scope: this, name: 'addButton', disabled: true
        });
        this.addButton = button;
        var toolbar = new Ext.Toolbar({
            items: [button]
        });
        var configuration = {
            store: new Media.adapter.store.TrackStore(),
            sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
            colModel: new Ext.grid.ColumnModel({
                defaults: {
                    sortable: true,
                    resizable: true,
                    width: 150
                },
                columns: [
                    {header: Media.adapter.util.AppUtil.msg('track', 'name'), dataIndex: 'name', id: 'name_column'},
                    {header: Media.adapter.util.AppUtil.msg('track', 'codec'), dataIndex: 'codecId'},
                    {header: Media.adapter.util.AppUtil.msg('track', 'language'), dataIndex: 'language'},
                    {header: Media.adapter.util.AppUtil.msg('track', 'type'), dataIndex: 'trackType'}
                ]
            }),
            viewConfig: {
                getRowClass: function(record) {
                    if (record.mustBeAdded === true) {
                        return 'track-addition';
                    } else if ( record.mustBeRemoved === true ) {
                        return 'track-deletion';
                    }
                }
            },
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
        var window = new Media.adapter.window.AddTrackWindow({input: me.input, owner: parent});
        window.show(parent);
        window.on('track_added', function(track) {
            var record = new TrackRecord(track);
            record.mustBeAdded = true;
            me.getStore().add(record);
            me.fireEvent('track_added', record);
        });
    },
    getAddButton: function() {
        return this.addButton;
    },
    setInput: function(inputValue) {
        this.input = inputValue;
    },
    getTracksToAdd: function() {
        return this.getTracksBy(function(record) {
            return record.mustBeAdded === true;
        });
    },
    getTracksToRemove: function() {
        return this.getTracksBy(function(record) {
            return record.mustBeRemoved === true;
        });
    },
    getTracksBy: function(filter) {
        var tracks = [];
        this.getStore().each(function(record) {
            if ( filter(record) === true ) {
                tracks.push(record.data);
            }
        });
        return tracks;
    },
    hasTrackModification: function() {
        return this.getStore().findBy(function(record) {
            return record.mustBeAdded === true || record.mustBeRemoved === true;
        }) !== -1;
    }
});
