/**
 * Created by liuq on 16/9/8.
 */
var path = getQueryStringByName("path");
var token = getQueryStringByName("token");
var pageCount = 0;
function getPreviewInfo() {
    $('#divLoading').show();
    $.ajax({
        type: 'get',
        dataType: 'json',
        async: false,
        url: window.contextPath + '/preview/getPreviewInfo?path=' + path + '&token=' + token,
        success: function (data) {
            $('#divLoading').hide();
            if (data.canPreview) {
                pageCount = data.pageCount;
                path = data.path;
            }
            else {
                document.write(data.errorMsg);
            }
        }
    });
}
function doPreview() {
    var htmlWay = path.toLowerCase().indexOf(".xls") > 0;
    if(htmlWay)
    {
        var url = window.contextPath + '/preview/handleExcel?path=' + path + '&page=0&pageCount=' + pageCount;
        location.href = url;
    }
    else {
        var preloadImg = window.contextPath + "/static/pixel.gif";
        for (var i = 0; i < pageCount; i++) {
            var src = window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + i + "&pageCount=" + pageCount
            var page = $("<div class='content'><img class='scrollLoading' data-url='" + src + "' src='" + preloadImg + "' /></div>");
            $("#divPages").append(page);
            var nav = $("<div><span>" + (i + 1) + "/" + pageCount + "</span></div>");
            page.append(nav);
        }
        $(".scrollLoading").scrollLoading();
    }
}
function getQueryStringByName(name) {
    var result = location.search.match(new RegExp("[\?\&]" + name + "=([^\&]+)", "i"));
    if (result == null || result.length < 1) {
        return "";
    }
    return result[1];
}
//入口
$(document).ready(function () {
    $('#divLoading').hide();
    getPreviewInfo();
    doPreview();
});