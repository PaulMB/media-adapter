/*global Ext,SYNO,Media*/

Ext.define('Media.adapter.form.MainPanel', {
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
            fieldLabel: Media.adapter.util.AppUtil.msg('info', 'input'),
            emptyText:Media.adapter.util.AppUtil.msg('info', 'message')
        });
        this.durationField = new SYNO.ux.TextField({
            anchor: '100%',
            readOnly: true,
            disabled: true,
            fieldLabel: Media.adapter.util.AppUtil.msg('info', 'duration'),
            name: 'duration'
        });
        this.titleField = new SYNO.ux.TextField({
            anchor: '100%',
            readOnly: true,
            disabled: true,
            fieldLabel: Media.adapter.util.AppUtil.msg('info', 'title'),
            name: 'title'
        });
        this.mergeButton = new SYNO.ux.Button({
            text: Media.adapter.util.AppUtil.msg('info', 'merge'),
            btnStyle: "blue",
            name: 'mergeButton',
            disabled: true,
            handler: function() {
                me.submitMerge();
            }
        });
        var resetButton = new SYNO.ux.Button({
            text: Media.adapter.util.AppUtil.msg('info', 'reset'),
            btnStyle: 'grey',
            handler: function(){
                me.resetInfo();
            }
        });
        this.tracks = new Media.adapter.grid.TrackGrid();
        var configuration = {
            bodyStyle:'padding:5px 5px 0',
            border: false,
            items: [{
                    xtype: "syno_compositefield",
                    items: [
                        this.inputField, {
                            xtype: "syno_button",
                            text: Media.adapter.util.AppUtil.msg('info', 'select'),
                            scope: this,
                            handler: me.onGetInfoBtnClick
                        }
                    ]
                },
                this.titleField,
                this.durationField,
                new SYNO.ux.FieldSet({
                    layout: 'fit',
                    title: Media.adapter.util.AppUtil.msg('track', 'title'),
                    height: 450,
                    items: this.tracks
                })
            ],
            buttons: [ this.mergeButton, resetButton]
        };
        this.callParent([configuration]);
        var enableFn = function() {
            me.enableMergeButton();
        };
        this.tracks.getStore().on('add', enableFn);
        this.tracks.getStore().on('remove', enableFn);
        this.tracks.getStore().on('datachanged', enableFn);
    },
    enableMergeButton: function() {
        this.mergeButton.setDisabled(!this.tracks.hasTrackModification());
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
        this.durationField.setValue(Media.adapter.util.AppUtil.toTime(this.durationField.getValue() / 1000));
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
            title: Media.adapter.util.AppUtil.msg('chooser', 'title'),
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
    submitMerge: function() {
        var me = this;
        me.appWin.setStatusBusy(Media.adapter.util.AppUtil.msg('edit', 'submit'));
        Ext.Ajax.request({
            method: 'POST',
            url: Media.adapter.util.AppUtil.getUrl() + 'merge',
            jsonData: {
                input: me.inputField.getValue(),
                tracksToAdd: me.tracks.getTracksToAdd(),
                tracksToRemove: me.tracks.getTracksToRemove()
            },
            success: function (responseObject) {
                me.appWin.clearStatusBusy();
                me.resetInfo();
                me.appWin.selectPage('Media.adapter.grid.MergeGrid');
            },
            failure: function (responseObject) {
                me.appWin.clearStatusBusy();
                me.appWin.getMsgBox().alert(Media.adapter.util.AppUtil.msg('app', 'app_name'), responseObject.responseText);
            }
        });
    },
    getInfo: function(path) {
        var me = this;
        var parentWindow = me.findAppWindow();
        parentWindow.setStatusBusy({text: Media.adapter.util.AppUtil.msg('info', 'load')});
        Ext.Ajax.request({
            method: 'GET',
            url: Media.adapter.util.AppUtil.getUrl() + 'info'+ encodeURI(path),
            success: function(responseObject) {
                parentWindow.clearStatusBusy();
                me.setContainerInfo(path, Ext.decode(responseObject.responseText));
            },
            failure: function(responseObject) {
                parentWindow.clearStatusBusy();
                parentWindow.getMsgBox().alert(Media.adapter.util.AppUtil.msg('app', 'app_name'), responseObject.responseText);
            }
        });
    }
});