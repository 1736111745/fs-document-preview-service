/**
 * Created by liuq on 16/9/8.
 */
var path = getQueryStringByName("path");
var loaded = 0;
var pageCount = 0;

function getPageCount() {
    $.ajax({
        type: 'get',
        dataType: 'text',
        async: false,
        url: window.contextPath+'/preview/getPageCount?path=' + path,
        success: function (data) {
            pageCount = parseInt(data);
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
function loadSvg(pageIndex) {
    if (pageIndex >= pageCount) return;
//            $.isLoading({text: "文档加载中..."});
    $.ajax({
        type: 'get',
        dataType: 'json',
        async: false,
        url: window.contextPath + '/preview/getsvg?path=' + path + '&page=' + pageIndex + "&pageCount=" + pageCount,
        beforeSend: function () {
            console.log("load " + pageIndex);
        },
        success: function (data) {
//                    $.isLoading("hide");
            if (data.successed) {
                var dataSrc = "<embed src='"+window.contextPath+"/preview/" + data.filePath + "' width='100%' height='100%' type='image/svg+xml'></embed>"
                if (data.type == 2) {
                    dataSrc = "<img src='"+window.contextPath+"/preview/" + data.filePath + "' width='100%' height='100%'>";
                }
                var html = $("<DIV class='word-page' STYLE='max-width:793px' id='doc0'><DIV class='word-content'>" + dataSrc + "</DIV></DIV>");
                $("#content").append(html);
                loaded++;
            }
            else {
                maxPageIndex = loaded;
            }
        },
        complete: function () {
        }
    });
}
function loadFirst() {
    getPageCount();
    var topN = pageCount > 3 ? 3 : pageCount;
    loadTopN(topN);
}
function scrollEvent() {
    $(window).scroll(function () {
        var $body = $("body");
        /*判断窗体高度与竖向滚动位移大小相加 是否 超过内容页高度*/
        var h1 = $(window).scrollTop() + $(window).height();
        var h2 = $(document).height();
        console.log("h1:" + h1 + ",h2:" + h2);
        if (h1 > 0.75 * h2) {
            if (loaded > 0 && pageCount > loaded)
                loadTopN(3);
        }
    });
}
$(document).ready(function () {
    loadFirst();
    scrollEvent();
});
function loadTopN(n) {
    for (var i = 0; i < n; i++)
        loadSvg(loaded);
}