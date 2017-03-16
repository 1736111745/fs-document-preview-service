var sg = getQueryStringByName("sg");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
var end = pageCount > 3 ? 3 : pageCount;
$(function () {
    loadViewPort();
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
            loadData(pageIndex, element);
        }
    });
}

function loadData(i, element) {

    var url = window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + i + "&pageCount=" + pageCount + "&sg=" + sg + "&ver=1.0";
    var iframeId = 'frame' + i;
    $("<iframe id='" + iframeId + "' src='"+url+"' onload='resize("+i+",this)' onresize='resize("+i+",this)' scrolling='no' frameborder='0' width='100%'></iframe>").prependTo(element);
    element.load();
}

function resize(i,obj) {
    var height=$(obj.contentWindow.document).find("div[id='page-container']").height()
    $(obj).height(height+20);
    $(obj.parentElement).removeClass("lazy");
    var nav=$("<div class='center'><span>第" + (i + 1) + "页,共" + pageCount + "页</span></div>");
    $(obj.parentElement).after(nav);
}


