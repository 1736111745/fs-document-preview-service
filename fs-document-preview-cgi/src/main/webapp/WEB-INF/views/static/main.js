/**
 * Created by liuq on 16/9/8.
 */
var path = getQueryStringByName("path");
var token = getQueryStringByName("token");
var loaded = 0;
var pageCount = 0;
var type = 1;
var flag = false;
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
                maxWidth = type == 3 ? 893 : 793;
                flag = true;
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
    var page = $("<div class='content'></div>");
    $("#divPages").append(page);
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
                var dataHtml = "<img class='lazy img-responsive' data-original='" + src + "' width='100%' height='100%'>";
                var data = $(dataHtml);
                page.append(data);
                var nav = $("<div class='nav'><span>"+(pageIndex+1)+"/"+pageCount+"</span></div>");
                page.append(nav);
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
            if (loaded > 0 && pageCount > loaded) {
                if (pageCount > 50) {
                    loadTopN(1);
                }
                else
                {
                    loadTopN(3);
                }
            }
        }
    });
}

//入口
$(document).ready(function () {
    getPreviewInfo();
    if (flag) {
        if (type == 2) {
            var url = window.contextPath + '/preview/handleExcel?path=' + path + '&page=0&pageCount=' + pageCount;
            location.href = url;
        }
        else {
            loadFirst();
            scrollEvent();
        }
    }
});

//加载N页
function loadTopN(n) {
    for (var i = 0; i < n; i++) {
        loadData(loaded);
        $("img.lazy").lazyload({threshold: 200});
    }

}