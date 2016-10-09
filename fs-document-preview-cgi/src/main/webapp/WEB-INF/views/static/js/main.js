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
    var route = '';
    if (path.toLowerCase().indexOf(".xls") > 0) {
        route = "handleExcel";
    }
    else if (path.toLowerCase().indexOf(".pdf") > 0) {
        route = "handlePdf";
    }
    else {
        route = "handleWordAndPPT";
    }
    var url = window.contextPath + '/preview/' + route + '?path=' + path + '&page=0&pageCount=' + pageCount;
    location.href = url;
}
//入口
$(document).ready(function () {
    $('#divLoading').hide();
    getPreviewInfo();
    doPreview();
});