var sg = getQueryStringByName("sg");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
var end = pageCount > 3 ? 3 : pageCount;
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
    var url = window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + i + "&pageCount=" + pageCount + "&sg=" + sg;
    $.ajax({
        type: 'get',
        timeout: 15000,
        dataType: 'json',
        async: true,
        url: url,
        complete: function (request, status) {
            $('#divLoading').hide();
            var svg = status == "success" ? $(request.responseText) : ""
            var page = $("<div class='content'>" + svg + "</div>");
            $("#divPage" + i).append(page);
            $("#divPage" + i).removeClass("lazy");
        }
    });
}