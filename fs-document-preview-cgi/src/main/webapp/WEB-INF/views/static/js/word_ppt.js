var sg = getQueryStringByName("sg");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
$(function () {
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
function loadPageLoader() {
    $('.lazy').lazy({
        pageLoader: function (element) {
            var pageIndex = parseInt(element.attr("data-page-no"));
            loadData(pageIndex);
        }
    });
}
function loadData(i) {
    var src = window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + i + "&pageCount=" + pageCount + "&sg=" + sg;
    var page = $("<div class='content'><embed  src='" + src + "' width='100%' height='100%' type='image/svg+xml'/></div>");
    $("#divPage" + i).append(page);
    $("#divPage" + i).removeClass("lazy");
}