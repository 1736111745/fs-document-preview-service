var sg = getQueryStringByName("sg");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
var filePathList=[];
var currentPage;
$(function () {
    loadViewPort();
    timingQuery();
    loadAllPages();
});

function loadAllPages() {
    for (var i = 0; i < pageCount; i++) {
        var div = "<div class='lazy border' data-page-no='" + i + "' data-loader='pageLoader'>";
        $('#main').append($(div));
        loadPageLoader();
    }
}

function loadViewPort() {
    var docWidth = $(window).width();
    var scale = docWidth * 0.96 / 1000;
    var viewport = document.querySelector("meta[name=viewport]");
    viewport.setAttribute('content', 'initial-scale=' + scale + ', width=device-width');
}

function loadPageLoader() {
    $('.lazy').lazy({
        pageLoader: function (element) {
            var pageIndex = parseInt(element.attr("data-page-no"));
            currentPage = pageIndex;
            loadData(currentPage);
        }
    });
}

function loadData(i) {
    var htmlName = (i + 1) + ".html";
    if ($.inArray(htmlName, filePathList)>0) {
        var url = window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + i + "&pageCount=" + pageCount + "&sg=" + sg + "&ver=1.0";
        var iframeId = 'frame' + i;
        var element=$("div[data-page-no='"+i+"']");
        if($('#'+iframeId).length==0) {
            $("<iframe id='" + iframeId + "' src='" + url + "' onload='resize(" + i + ",this)' onresize='resize(" + i + ",this)' scrolling='no' frameborder='0' width='100%'></iframe>").prependTo(element);
            element.load();
        }
    }
}

function resize(i, obj) {
    var height = $(obj.contentWindow.document).find("div[id='page-container']").height()
    $(obj).height(height + 20);
    $(obj.parentElement).removeClass("lazy");
    var nav = $("<div class='center'><span>第" + (i + 1) + "页,共" + pageCount + "页</span></div>");
    $(obj.parentElement).after(nav);
}

function timingQuery() {
    var id = window.setInterval(function () {
        if (filePathList.length == pageCount) {
            clearInterval(id);
        }
        queryDocStatus()
    }, 1000);
}

function queryDocStatus() {
    var url = window.contextPath + '/preview/queryDocConvertStatus?path=' + path + "&sg=" + sg
    $.ajax({
        type: 'get',
        dataType: 'json',
        async: true,
        url: url,
        success: function (data) {
            filePathList = data.list;
            loadData(currentPage);
        }
    });
}

