/**
 * Created by liuq on 16/9/8.
 */
var path = getQueryStringByName("path");
var token = getQueryStringByName("token");
var pageCount = 0;
var sg = "";//安全组
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
                doPreview();
            }
            else {
                document.write("<h3>" + data.errorMsg + "</h3>");
            }
        }
    });
}


function doPreview() {
    if (path.toLowerCase().indexOf("txt") >= 0) {
        doPreviewTxt();
    }
    else {
        doPreviewOffice();
    }
}

function doPreviewTxt() {
    $.ajax({
        type: 'get',
        dataType: 'json',
        async: false,
        url: window.contextPath + '/preview/getTxtPreviewInfo?path=' + path,
        success: function (data) {
            var dirName = data.dirName;
            var fileName = data.fileName;
            var url = window.contextPath + '/preview/' + dirName + "/" + fileName;
            location.href = url;
        }
    });
}

function doPreviewOffice() {
    var route = '';
    if (path.toLowerCase().indexOf("xls") >= 0) {
        route = "excel2html";
    }
    else
        route = "pdf2html";
    console.log("route:" + route);
    var url = window.contextPath + '/preview/' + route + '?path=' + path + '&pageCount=' + pageCount + "&sg=" + sg + "&rdm=3";
    location.href = url;
}

//入口
$(document).ready(function () {
    $('#divLoading').hide();
    getPreviewInfo();
});