Ext.ns("Media.Adapter");
Media.Adapter.URL = '3rdparty/MediaAdapter/media/';

_MSG = function (category, key) {
    return _TT("Media.Adapter.AppInstance", category, key)
};

toTime = function (timeInSeconds) {
    var sec_num = parseInt(timeInSeconds, 10);
    var hours   = Math.floor(sec_num / 3600);
    var minutes = Math.floor((sec_num - (hours * 3600)) / 60);
    var seconds = sec_num - (hours * 3600) - (minutes * 60);

    if (hours   < 10) {hours   = "0"+hours;}
    if (minutes < 10) {minutes = "0"+minutes;}
    if (seconds < 10) {seconds = "0"+seconds;}
    return hours + 'h ' + minutes + 'm ' + seconds + 's';
};

mergeStatusRenderer = function(value) {
    var display = value.charAt(0) + value.slice(1).toLowerCase();
    return "<span style='color:" + toMergeStatusColor(value) + ";'>" + display + "</span>";
};

toMergeStatusColor = function(value) {
    switch (value) {
        case 'PENDING':
            return 'gray';
        case 'RUNNING':
            return 'green';
        case 'ERROR':
            return 'red';
        case 'COMPLETED':
            return 'blue';
        default:
            return 'gray';
    }
};

var TrackRecord = Ext.data.Record.create([
    {name: 'path'},
    {name: 'name'},
    {codecId: 'codecId'},
    {language: 'language'},
    {trackType: 'trackType'}
]);

Ext.define('Media.Adapter.TrackStore', {
    extend: 'Ext.data.JsonStore',
    constructor: function (config) {
        Ext.apply(this, config);
        var configuration = {
            autoDestroy: true,
            root: 'tracks',
            fields: [
                {name: 'path',      type: 'string'},
                {name: 'name',      type: 'string'},
                {name: 'codecId',   type: 'string'},
                {name: 'language',  type: 'string'},
                {name: 'trackType', type: 'string'}
            ]
        };
        this.callParent([configuration]);
    }
});

Ext.define('Media.Adapter.MergeStore', {
    extend: 'Ext.data.JsonStore',
    constructor: function (config) {
        Ext.apply(this, config);
        var configuration = {
            autoDestroy: true,
            fields: [
                {name: 'id',       type: 'string'},
                {name: 'input',    type: 'string'},
                {name: 'status',   type: 'string'},
                {name: 'message',  type: 'string'}
            ]
        };
        this.callParent([configuration]);
    }
});

Ext.define('Media.Adapter.MergeForm', {
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
            name: 'track',
            fieldLabel:_MSG('edit', 'label'),
            emptyText: _MSG('edit', 'help')
        });
        this.nameField = new SYNO.ux.TextField({
            anchor: '100%',
            width: 'auto',
            allowBlank: false,
            name: 'name',
            fieldLabel: _MSG('edit', 'name')
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
                        text: _MSG('edit', 'select'),
                        scope: this,
                        handler: this.onSelectTrackBtnClick
                    }
                ]
            }, this.nameField]
        };
        this.callParent([configuration]);
    },
    getFieldValues: function() {
        return {
            path: this.trackField.getValue(),
            name: this.nameField.getValue(),
            trackType: 'SUBTITLE'
        };
    },
    onSelectTrackBtnClick: function() {
        var me = this;
        new SYNO.SDS.Utils.FileChooser.Chooser({
            parent: this,
            owner: this.owner,
            usage: {type: "open", folder_path: "/"},
            title: _MSG('chooser', 'title'),
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

Ext.define('Media.Adapter.AddTrackWindow', {
    extend: 'SYNO.SDS.ModalWindow',
    constructor: function (config) {
        Ext.apply(this, config);
        var me = this;
        var mergeForm = new Media.Adapter.MergeForm({owner: this});
        var mergeButton = new SYNO.ux.Button({
            text: _MSG('edit', 'add'),
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
            text: _MSG('edit', 'close'),
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

Ext.define('Media.Adapter.MergeGrid', {
    extend: 'SYNO.ux.GridPanel',
    task: null,
    contextMenu: null,
    constructor: function (config) {
        Ext.apply(this, config);
        var me = this;
        this.contextMenu = new SYNO.ux.Menu({
            items: {
                text: _MSG('work', 'remove'),
                handler: function() {
                    var record = me.getSelectionModel().getSelected();
                    var parentWindow = me.findAppWindow();
                    parentWindow.setStatusBusy();
                    Ext.Ajax.request({
                        method: 'DELETE',
                        url: Media.Adapter.URL + 'merge/'+ record.get('id'),
                        success: function(responseObject) {
                            parentWindow.clearStatusBusy();
                            me.reloadStatus();
                        },
                        failure: function(responseObject) {
                            parentWindow.clearStatusBusy();
                            parentWindow.getMsgBox().alert(_MSG('app', 'app_name'), responseObject.responseText);
                        }
                    });
                }
            }
        });
        var configuration = {
            store: new Media.Adapter.MergeStore(),
            colModel: new Ext.grid.ColumnModel({
                defaults: {
                    sortable: true,
                    resizable: true,
                    width: 250
                },
                columns: [
                    {header: _MSG('work', 'input'), dataIndex: 'input', id: 'input_column'},
                    {header: _MSG('work', 'status'), dataIndex: 'status', renderer: mergeStatusRenderer},
                    {header: _MSG('work', 'message'), dataIndex: 'message', renderer:
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
        this.onCreate();
    },
    showContextMenu: function(grid, index, event) {
        event.stopEvent();
        grid.getSelectionModel().selectRow(index);
        this.contextMenu.showAt(event.getXY());
    },
    onCreate: function () {   //TODO events
        var me = this;
        this.task = this.addAjaxTask({id: "task_get_merge_status", interval: 5 * 1000, url: Media.Adapter.URL + 'merge', method: "GET", autoJsonDecode: false, callback: this.onAjaxRequestDone, scope: this});
        this.task.start();
        this.on('destroy', function() {
            me.task.stop();
            me.removeTask("task_get_merge_status")
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


Ext.define('Media.Adapter.TrackGrid', {
    extend: 'SYNO.ux.GridPanel',
    addButton: null,
    input: null,
    contextMenu: null,
    constructor: function (config) {
        var me = this;
        Ext.apply(this, config);
        this.contextMenu = new SYNO.ux.Menu({
            items: {
                text: _MSG('work', 'remove'),
                handler: function() {
                    var record = me.getSelectionModel().getSelected();
                    if ( record.localAddition === true ) {
                        me.getStore().remove(record);
                    } else {
                        record.markForRemoval = true;
                        me.getStore().fireEvent('datachanged', me.getStore());
                    }
                    //TODO
                }
            }
        });
        var button = new SYNO.ux.Button({
            text: _MSG('track', 'add'), handler: this.onCreate, scope: this, name: 'addButton', disabled: true
        });
        this.addButton = button;
        var toolbar = new Ext.Toolbar({
            items: [button]
        });
        var configuration = {
            store: new Media.Adapter.TrackStore(),
            colModel: new Ext.grid.ColumnModel({
                defaults: {
                    sortable: true,
                    resizable: true,
                    width: 150
                },
                columns: [
                    {header: _MSG('track', 'name'), dataIndex: 'name', id: 'name_column'},
                    {header: _MSG('track', 'codec'), dataIndex: 'codecId'},
                    {header: _MSG('track', 'language'), dataIndex: 'language'},
                    {header: _MSG('track', 'type'), dataIndex: 'trackType'}
                ]
            }),
            viewConfig: {
                getRowClass: function(record, index, rowParams) {
                    if (record.localAddition === true) {
                        rowParams.tstyle += 'background-color:lightgreen;';
                    } else if ( record.markForRemoval === true ) {
                        rowParams.tstyle += 'background-color:orange;';
                    } else {
                        rowParams.tstyle += 'background-color:white;';
                    }
                }
            },
            enableColumnMove: false,
            //height: 400,
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
        var window = new Media.Adapter.AddTrackWindow({input: me.input, owner: parent});
        window.show(parent);
        window.on('track_added', function(track) {
            var record = new TrackRecord(track);
            record.localAddition = true;
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
    getAddedTracks: function() {
        var tracks = [];
        this.getStore().each(function(record) {
            if ( record.localAddition === true ) {
                tracks.push(record.data);
            }
        });
        return tracks;
    }
});

Ext.define('Media.Adapter.MainPanel', {
    extend: 'SYNO.ux.FormPanel',
    titleField: null,
    durationField: null,
    inputField: null,
    mergeButton: null,
    constructor: function (config) {
        Ext.apply(this, config);
        var me = this;
        this.inputField = new SYNO.ux.TextField({
            anchor: '100%',
            width: 'auto',
            readOnly: true,
            fieldLabel: _MSG('info', 'input'),
            emptyText:_MSG('info', 'message')
        });
        this.durationField = new SYNO.ux.TextField({
            anchor: '100%',
            readOnly: true,
            disabled: true,
            fieldLabel: _MSG('info', 'duration'),
            name: 'duration'
        });
        this.titleField = new SYNO.ux.TextField({
            anchor: '100%',
            readOnly: true,
            disabled: true,
            fieldLabel: _MSG('info', 'title'),
            name: 'title'
        });
        this.mergeButton = new SYNO.ux.Button({
            text: _MSG('info', 'merge'),
            btnStyle: "blue",
            name: 'mergeButton',
            disabled: true,
            handler: function() {
                me.appWin.setStatusBusy(_MSG('edit', 'submit'));
                Ext.Ajax.request({
                    method: 'POST',
                    url: Media.Adapter.URL + 'merge',   //TODO check if local modifications, if not, no submission
                    jsonData: {
                        input: me.inputField.getValue(),
                        tracks: me.tracks.getAddedTracks()
                    },
                    success: function(responseObject) {
                        me.appWin.clearStatusBusy();
                        me.resetInfo();
                        me.appWin.selectPage('Media.Adapter.MergeGrid');
                    },
                    failure: function(responseObject) {
                        me.appWin.clearStatusBusy();
                        me.appWin.getMsgBox().alert(_MSG('app', 'app_name'), responseObject.responseText);
                    }
                });
            }
        });
        var resetButton = new SYNO.ux.Button({
            text: _MSG('info', 'reset'),
            btnStyle: 'grey',
            handler: function(){
                me.resetInfo();
            }
        });
        this.tracks = new Media.Adapter.TrackGrid();
        var configuration = {
            bodyStyle:'padding:5px 5px 0',
            border: false,
            items: [{
                    xtype: "syno_compositefield",
                    items: [
                        this.inputField, {
                            xtype: "syno_button",
                            text: _MSG('info', 'select'),
                            scope: this,
                            handler: me.onGetInfoBtnClick
                        }
                    ]
                },
                this.titleField,
                this.durationField,
                new SYNO.ux.FieldSet({
                    layout: 'fit',
                    title: _MSG('track', 'title'),
                    height: 450,
                    items: this.tracks
                })
            ],
            buttons: [ this.mergeButton, resetButton]
        };
        this.callParent([configuration]);
        this.tracks.on('track_added', function() {
            me.mergeButton.setDisabled(false);
        });
    },
    resetInfo: function() {
        this.durationField.reset();
        this.titleField.reset();
        this.tracks.getStore().removeAll();
        this.tracks.getAddButton().setDisabled(true);
        this.inputField.reset();
        this.mergeButton.setDisabled(true);
    },
    setContainerInfo: function(path, info) {
        this.getForm().setValues(info);
        this.durationField.setValue(toTime(this.durationField.getValue() / 1000));
        this.tracks.getStore().loadData(info);
        this.tracks.getAddButton().setDisabled(false);
        this.tracks.setInput(path);
        this.inputField.setValue(path);
    },
    onGetInfoBtnClick: function() {
        var me = this;
        new SYNO.SDS.Utils.FileChooser.Chooser({
            parent: this,
            owner: this.findAppWindow(),
            usage: {type: "open", folder_path: "/"},
            title: _MSG('chooser', 'title'),
            folderToolbar: true,
            listeners: {
                scope: me,
                choose: function (chooser, entry, e) {
                    var path = entry.fullpath;
                    if (path) {
                        me.getInfo(path);
                    }
                    chooser.close();
                }
            }
        }).show();
    },
    getInfo: function(path) {
        var me = this;
        var parentWindow = me.appWin;
        parentWindow.setStatusBusy({text: _MSG('info', 'load')});
        Ext.Ajax.request({
            method: 'GET',
            url: Media.Adapter.URL + 'info'+ encodeURI(path),
            success: function(responseObject) {
                parentWindow.clearStatusBusy();
                me.setContainerInfo(path, Ext.decode(responseObject.responseText));
            },
            failure: function(responseObject) {
                parentWindow.clearStatusBusy();
                parentWindow.getMsgBox().alert(_MSG('app', 'app_name'), responseObject.responseText);
            }
        });
    }
});

Ext.define("Media.Adapter.AppInstance", {
    extend: "SYNO.SDS.AppInstance",
    appWindowName: "Media.Adapter.AppWindow"
});

Ext.define("Media.Adapter.AppWindow" ,{
    extend : "SYNO.SDS.PageListAppWindow",
    activePage: "Media.Adapter.MainPanel",
    constructor: function (config) {
        var me = this;
        var configuration = Ext.apply({autoScroll: true, resizable: true, width: 1000, height: 600, minWidth: 480, minHeight: 360, dsmStyle: "v5", listItems: [
            {text: _MSG('info', 'menu'), fn: "Media.Adapter.MainPanel", iconCls: "icon-overview"},
            {text: _MSG('work', 'menu'), fn: "Media.Adapter.MergeGrid", iconCls: "icon-queue"}
        ]}, config);
        this.callParent([configuration]);
    }
});
