/*global Ext,SYNO,Media*/

Ext.define('Media.adapter.store.MergeStore', {
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