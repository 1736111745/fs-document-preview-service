var sg = getQueryStringByName("sg");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
var end = pageCount > 3 ? 3 : pageCount;
$(function () {
    loadViewPort();
    loadAllPages();
    loadPageLoader();
});

function loadAllPages() {
    for (var i = 0; i < pageCount; i++) {
        var pageIndex = i + 1;
        var div = "<div id='pf" + pageIndex + "' class='lazy pf ww" + pageIndex + " hh" + pageIndex + "' data-page-no='" + i + "' data-loader='pageLoader'>";
        $('#page-container').append($(div));
        loadPageLoader();
    }
}

function loadViewPort() {
    var docWidth = $(window).width();
    var scale = docWidth * 0.95 / 1000;
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
    $.ajax({
        type: 'get',
        timeout: 1800000,
        dataType: 'json',
        async: true,
        url: url,
        beforeSend: function () {
        },
        complete: function (request) {
            if(request.status==200) {
                var data = $(request.responseText)
                var childStyle = $(data[0]);
                var dataDiv = $(data[1]);
                var childDiv = dataDiv.prop("innerHTML");
                element.append(childStyle).append(childDiv).load();
                var nav = $("<div class='center'><span>第" + (i + 1) + "页,共" + pageCount + "页</span></div>");
                element.after(nav);
            }
            else {
                var spanMsg = $("<span>改页面无法预览！</span>");
                element.append(spanMsg).load();
            }
            element.removeClass("lazy");
        }
    });
}

