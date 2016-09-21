/**
 * Created by liuq on 16/9/19.
 */

var page = getQueryStringByName("page");
var pageIndex = parseInt(page)
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
function getQueryStringByName(name) {
    var result = location.search.match(new RegExp("[\?\&]" + name + "=([^\&]+)", "i"));
    if (result == null || result.length < 1) {
        return "";
    }
    return result[1];
}
//分页加载
function loadData() {
    if (pageIndex >= pageCount) return;
    $.ajax({
        type: 'get',
        timeout: 1800000,
        dataType: 'json',
        async: true,
        url: window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + pageIndex + "&pageCount=" + pageCount,
        beforeSend: function () {
            $('#divLoading').show();
        },
        success: function (data) {
            $('#divLoading').hide();
            if (data.successed) {
                var filePath = data.filePath;
                var dirName = filePath.substr(0, filePath.indexOf('/'));
                var cssURL = window.contextPath+"/preview/" + dirName + "/js/stylesheet.css"
                var linkTag = $('<link href="' + cssURL + '" rel="stylesheet">');
                $($('head')[0]).append(linkTag);
                $(document).attr("title", "文档(" + page + "/" + pageCount + ")");//修改title值
                paging();
                var dataHtml = $.ajax({url: filePath, async: false}).responseText;
                $('#content').html(dataHtml);
            }
            else {
            }
        },
        error: function () {
        }
    });
}
function paging() {
    var urlNext = window.contextPath + '/preview/handleExcel?path=' + path + '&page=' + (pageIndex + 1) + '&pageCount=' + pageCount;
    var urlLast = window.contextPath + '/preview/handleExcel?path=' + path + '&page=' + (pageIndex - 1) + '&pageCount=' + pageCount;
    if (pageCount == 1) //只有一页的时候
    {
        $('#linkNext').addClass("disabled");
        $('#linkNext').removeAttr("href");
        $('#linkLast').addClass("disabled");
        $('#linkLast').removeAttr("href");
    }
    else {
        if (pageIndex == 0) { //第一页
            $('#linkLast').addClass("disabled");
            $('#linkLast').removeAttr("href");
            $('#linkNext').removeClass("disabled");
            $('#linkNext').attr("href", urlNext);
        }
        else {
            if (pageIndex == pageCount - 1) {//最后一页
                $('#linkNext').addClass("disabled");
                $('#linkNext').removeAttr("href");
                $('#linkLast').removeClass("disabled");
                $('#linkLast').attr("href", urlLast);
            }
            else {
                //中间页
                $('#linkLast').removeClass("disabled");
                $('#linkNext').removeClass("disabled");
                $('#linkNext').attr("href", urlNext);
                $('#linkLast').attr("href", urlLast);
            }
        }
    }
}

$(document).ready(function () {
    $('#divLoading').hide();
    loadData();
});