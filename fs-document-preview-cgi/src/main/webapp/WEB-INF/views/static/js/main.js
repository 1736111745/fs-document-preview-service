/**
 * Created by liuq on 16/9/8.
 */
var path = getQueryStringByName("path");
var token = getQueryStringByName("token");
var pageCount = 0;
var sg = "";//安全组
var ext = ""//扩展名
var needOfficePdf = false;
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
                sg = data.sg;
                needOfficePdf = data.needOfficePdf;
                doPreview();
            }
            else {
                document.write("<h3>" + data.errorMsg + "</h3>");
            }
        }
    });
}
function doPreview() {
    var route = '';
    if (needOfficePdf) {
        route = "handleOffice2Pdf"
    }
    else {
        if (ext.indexOf("xls") >= 0) {
            route = "handleExcel";
        }
        else
            route = ext.indexOf("pdf") ? "handlePdf" : "handleWordAndPPT";
    }
    var url = window.contextPath + '/preview/' + route + '?path=' + path + '&pageCount=' + pageCount + "&sg=" + sg;
    location.href = url;
}

//入口
$(document).ready(function () {
    $('#divLoading').hide();
    getPreviewInfo();
});