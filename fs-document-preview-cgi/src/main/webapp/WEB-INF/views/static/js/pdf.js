function loadData() {
    var pageCount = getQueryStringByName("pageCount");
    var path = getQueryStringByName("path");
    for (var i = 0; i < pageCount; i++) {
        var pdfUrl = location.protocol + "//" + location.host + window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + i + "&pageCount=" + pageCount + "&t=" + Math.random();
        var src = window.contextPath + '/static/pdfjs/generic/web/viewer.html?file=' + encodeURIComponent(pdfUrl);
        var page = $("<div class='content'><iframe class='scrollLoading' id='frame" + i + "' data-url='" + src + "' height='1000px' width='100%'/></div>");
        $("#divPages").append(page);
    }
}


function adjust(iframe) {
    var container = $(iframe).contents().find("#pageContainer1");
    var _times = 100, //100次
        _interval = 20, //20毫秒每次
        _self = null,
        _iIntervalID; //定时器id
    if (container.length) { //如果已经获取到了，就直接执行函数
        $(iframe).height(container.height());
        var toolBar=$(iframe).contents().find(".toolbar");
        toolBar.hide();
    } else {
        _iIntervalID = setInterval(function () {
            if (!_times) { //是0就退出
                clearInterval(_iIntervalID);
            }
            _times <= 0 || _times--; //如果是正数就 --
            _self = $(iframe).contents().find("#pageContainer1"); //再次选择
            if (_self.length) { //判断是否取到
                $(iframe).height(_self.height());
                var toolBar=$(iframe).contents().find(".toolbar");
                toolBar.hide();
                clearInterval(_iIntervalID);
            }
        }, _interval);
    }
}
//入口
var tm=null;
$(function () {
    loadData();
    $(".scrollLoading").scrollLoading();
    $(".scrollLoading").on("load", function () {
       adjust(this);
    })
});

