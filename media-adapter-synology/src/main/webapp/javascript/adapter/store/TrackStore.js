/*global Ext,SYNO,Media*/

Ext.define('Media.adapter.store.TrackStore', {
    extend: 'Ext.data.JsonStore',
    constructor: function (config) {
        Ext.apply(this, config);
        var configuration = {
            autoDestroy: true,
            root: 'tracks',
            fields: [
                {name: 'trackId',   type: 'string'},
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