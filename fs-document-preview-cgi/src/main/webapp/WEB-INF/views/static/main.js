/**
 * Created by liuq on 16/9/8.
 */
var path = getQueryStringByName("path");
var loaded = 0;
var pageCount = 0;
var type = 1;
function getPreviewInfo() {
    $.ajax({
        type: 'get',
        dataType: 'json',
        async: false,
        url: window.contextPath + '/preview/getPreviewInfo?path=' + path,
        success: function (data) {
            if (data.canPreview) {
                pageCount = data.pageCount;
                type = data.type;
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

var content=null;
//分页加载
function loadData(pageIndex) {
    if (pageIndex >= pageCount) return;
    var contentId="divContent"+pageIndex;
    content=$('#'+contentId);
    if(content.length>0) return;
    var img=$("<img src='"+window.contextPath+"/static/loading.gif' width='100%' height='100%'>");
    var page = $("<div class='word-page' style='max-width:893px'></div>");
    content=$("<div class='word-content'  id='"+contentId+"'></div>");
    content.append(img);
    $("#divPages").append(page);
    page.append(content);
    $.ajax({
        type: 'get',
        dataType: 'json',
        async: true,
        url: window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + pageIndex + "&pageCount=" + pageCount,
        beforeSend:function () {
        },
        success: function (data) {
            loaded++;
            if (data.successed) {
                var maxWidth = 793;
                var dataSrc = "<embed src='" + window.contextPath + "/preview/" + data.filePath + "' width='100%' height='100%' type='image/svg+xml'></embed>"
                if (type == 2) {
                    maxWidth = 893
                    dataSrc = "<img src='" + window.contextPath + "/preview/" + data.filePath + "' width='100%' height='100%'>";
                }
                var data=$(dataSrc);
                img.remove();
                content.append(data);
            }
            else {
                pageCount = loaded;
            }
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
        /*判断窗体高度与竖向滚动位移大小相加 是否 超过内容页高度*/
        var h1 = $(window).scrollTop() + $(window).height();
        var h2 = $(document).height();
        console.log("h1:" + h1 + ",h2:" + h2);
        if (h1 > 0.5 * h2) {
            if (loaded > 0 && pageCount > loaded)
                loadTopN(1);
        }
    });
}

//入口
$(function () {
    getPreviewInfo();
    scrollEvent();
});

//加载N页
function loadTopN(n) {
    for (var i = 0; i < n; i++)
        loadData(loaded);
}