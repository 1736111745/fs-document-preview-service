var sg=getQueryStringByName("sg");
function loadData() {
    var pageCount = getQueryStringByName("pageCount");
    var path = getQueryStringByName("path");
    for (var i = 0; i < pageCount; i++) {
        var src = window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + i + "&pageCount=" + pageCount+"&sg="+sg;
        var page = $("<div class='content'><img class='lazy' data-original='" + src + "' height='30%' width='100%'/></div>");
        $("#divPages").append(page);
        var nav = $("<div class='center'><span>第" + (i + 1) + "页,共" + pageCount + "页</span></div>");
        page.append(nav);
    }
    $("img.lazy").lazyload();
}
//入口
$(function () {
    loadData();
});