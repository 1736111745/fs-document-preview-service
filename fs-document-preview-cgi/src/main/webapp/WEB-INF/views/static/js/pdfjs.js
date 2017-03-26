/**
 * Created by liuq on 2017/3/25.
 */
var sg = getQueryStringByName("sg");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");


$(function () {
   // loadViewPort()
    loadAllPages();
});

function loadAllPages() {
    for (var i = 0; i < pageCount; i++) {
        var div = $("<div id='divPage" + i + "' class='lazy border' data-page-no='" + i + "' data-loader='pageLoader'>");
        var nav = $("<div class='center'><span>第" + (i + 1) + "页,共" + pageCount + "页</span></div>");
        $('#main').append(div).append(nav);
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
    var pdfDataUrl = window.location.origin + window.contextPath + "/preview/pdf/getData?path=" + path + "&page=" + i + "&sg=" + sg;
    var iframeUrl = window.contextPath + "/static/pdfjs/web/viewer.html?file=" + encodeURIComponent(pdfDataUrl);
    console.log(iframeUrl);
    var iframeId = 'frame' + i;
    $("<iframe id='" + iframeId + "' src='" + iframeUrl + "'  scrolling='no' frameborder='0' width='100%'></iframe>").prependTo(element);
    element.load();
}




