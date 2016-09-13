/**
 * Created by liuq on 16/9/8.
 */
var path = getQueryStringByName("path");
var loaded = 0;
var pageCount = 0;
var maxWidth=893;
var type=1;
function getPreviewInfo() {
    $.ajax({
        type: 'get',
        dataType: 'json',
        async: false,
        url: window.contextPath + '/preview/getPreviewInfo?path=' + path,
        success: function (data) {
            if (data.canPreview) {
                pageCount = data.pageCount;
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
    var contentId="divContent"+pageIndex;
    var content=$('#'+contentId);
    if(content.length>0) return;
    loaded++;
    var img=$("<img src='"+window.contextPath+"/static/loading.gif' width='100%' height='100%'>");
    var page = $("<div class='word-page' style='max-width:"+maxWidth+"px'></div>");
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
            if (data.successed) {
                var dataSrc = "<embed src='" + window.contextPath + "/preview/" + data.filePath + "' width='100%' height='100%' type='image/svg+xml'></embed>"
                if (type == 2) {
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
                loadTopN(3);
        }
    });
}

//入口
$(document).ready(function () {
    type = path.indexOf(".pdf")>0?2:1;
    maxWidth=type==1?793:893;
    getPreviewInfo();
    scrollEvent();
});

//加载N页
function loadTopN(n) {
    for (var i = 0; i < n; i++)
        loadData(loaded);
}