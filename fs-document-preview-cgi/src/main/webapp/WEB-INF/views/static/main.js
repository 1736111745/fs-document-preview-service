/**
 * Created by liuq on 16/9/8.
 */
var path = getQueryStringByName("path");
var token = getQueryStringByName("token");
var loaded = 0;
var pageCount = 0;
var type = 1;
var pageClass = "page";
function getPreviewInfo() {
    $.ajax({
        type: 'get',
        dataType: 'json',
        async: false,
        url: window.contextPath + '/preview/getPreviewInfo?path=' + path + '&token=' + token,
        success: function (data) {
            if (data.canPreview) {
                pageCount = data.pageCount;
                path = data.path;
                type = path.toLowerCase().indexOf(".doc") > 0 ? 1 : path.toLowerCase().indexOf(".xls") > 0 ? 2 : 3;
                pageClass = path.toLowerCase().indexOf(".xls")>0 ? "excel" : "page";
                maxWidth = type == 3 ? 893 : 793;
                loadFirst();
            }
            else {
                document.write(data.errorMsg);
            }
        }
    });
}

function getQueryStringByName(name) {
    var result = location.search.match(new RegExp("[\?\&]" + name + "=([^\&]+)", "i"));
    if (result == null || result.length < 1) {
        return "";
    }
    return result[1];
}

//分页加载
function loadData(pageIndex) {
    if (pageIndex >= pageCount) return;
    var contentId = "divContent" + pageIndex;
    var content = $('#' + contentId);
    if (content.length > 0) return;
    loaded++;
    var img = $("<img src='" + window.contextPath + "/static/loading.gif' width='100px' height='100px' style='display:block; margin:0 auto;'>");
    var page = $("<div class='" + pageClass + "' style='max-width:" + maxWidth + "px'></div>");
    // content = $("<div class='word-content'  id='" + contentId + "'></div>");
    page.append(img);
    $("#divPages").append(page);
    // page.append(content);
    $.ajax({
        type: 'get',
        timeout: 1800000,
        dataType: 'json',
        async: false,
        url: window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + pageIndex + "&pageCount=" + pageCount,
        beforeSend: function () {
        },
        success: function (data) {
            if (data.successed) {
                var src = window.contextPath + "/preview/" + data.filePath;
                var dataHtml = "<embed src='" + src + "' width='100%' height='100%' type='image/svg+xml'></embed>"
                if (type == 3) {
                    dataHtml = "<img src='" + src + "' width='100%' height='100%'>";
                }
                else if (type == 2) {
                    dataHtml = $.ajax({url: src, async: false}).responseText;
                }
                var data = $(dataHtml);
                img.remove();
                page.append(data);
            }
            else {
                page.remove();
            }
        },
        error: function () {
            page.remove();
        }
    });
}


//首次加载
function loadFirst() {
    var topN = pageCount > 3 ? 3 : pageCount;
    loadTopN(topN);
}

//滚动加载
function scrollEvent() {
    $(window).scroll(function () {
        var $body = $("body");
        var h1 = $(window).scrollTop() + $(window).height();
        var h2 = $(document).height();
        if (h1 > 0.5 * h2) {
            if (loaded > 0 && pageCount > loaded)
                loadTopN(3);
        }
    });
}

//入口
$(document).ready(function () {
    getPreviewInfo();
    if (type == 2) {
        var url = window.contextPath + '/preview/handleExcel?path=' + path + '&page=0&pageCount=' + pageCount;
        location.href = url;
    }
    else
        scrollEvent();
});

//加载N页
function loadTopN(n) {
    for (var i = 0; i < n; i++)
        loadData(loaded);
}