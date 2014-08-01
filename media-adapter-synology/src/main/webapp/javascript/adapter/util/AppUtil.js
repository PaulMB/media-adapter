/*global Ext,SYNO,Media,_TT*/

Ext.ns("Media.adapter");

Ext.define("Media.adapter.util.AppUtil", {
    singleton: true,

    getUrl: function() {
        return '3rdparty/MediaAdapter/media/';
    },

    msg: function (category, key) {
        return _TT("Media.adapter.AppInstance", category, key);
    },

    toTime: function (timeInSeconds) {
        var sec_num = parseInt(timeInSeconds, 10);
        var hours   = Math.floor(sec_num / 3600);
        var minutes = Math.floor((sec_num - (hours * 3600)) / 60);
        var seconds = sec_num - (hours * 3600) - (minutes * 60);

        if (hours   < 10) {hours   = "0" + hours;}
        if (minutes < 10) {minutes = "0" + minutes;}
        if (seconds < 10) {seconds = "0" + seconds;}
        return hours + 'h ' + minutes + 'm ' + seconds + 's';
    },

    mergeStatusRenderer: function(value) {
        var display = value.charAt(0) + value.slice(1).toLowerCase();
        return "<span style='color:" + Media.adapter.util.AppUtil.toMergeStatusColor(value) + ";'>" + display + "</span>";
    },

    toMergeStatusColor: function(value) {
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
    }
});
