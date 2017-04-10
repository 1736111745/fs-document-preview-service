var sg = getQueryStringByName("sg");
var path = getQueryStringByName("path");
var type = path.indexOf(".doc") > -1 ? "checkWord2Pdf" : "checkPPT2Pdf"
var tryCount = 0;
var pdfPageCount = 0;
$(function () {
    loadViewPort();
    checkOffice2PdfStatus();
    checkConvertStatus();
});
function loadViewPort() {
    var docWidth = $(window).width();
    var scale = docWidth * 0.96 / 1000;
    var viewport = document.querySelector("meta[name=viewport]");
    viewport.setAttribute('content', 'initial-scale=' + scale + ', width=device-width');
}

function checkOffice2PdfStatus() {
    var url = window.contextPath + '/preview/checkOffice2PdfStatus?path=' + path + "&sg=" + sg
    $.get(url);
}

function queryOffice2PdfStatus() {
    var flag = false;//是否转换完毕
    var url = window.contextPath + '/preview/queryOffice2PdfStatus?path=' + path + "&sg=" + sg
    $.ajax({
        type: 'get',
        dataType: 'json',
        async: false,
        url: url,
        success: function (data) {
            flag = data.finished;
            pdfPageCount=data.pdfPageCount;
        }
    });
    return flag;
}

var idChkConvertStatus;
//定时监测转换状态，当全部转换完毕停止检测
function checkConvertStatus() {
    idChkConvertStatus = setInterval(function () {
        tryCount++;
        if (tryCount++ > 40) {
            clearInterval(idChkConvertStatus);
            $('#spanLoading').html("转换超时，请稍后再试～")
        }
        if (queryOffice2PdfStatus()) {
            clearInterval(idChkConvertStatus);
            location.href = window.contextPath + '/preview/handlePdf?path=' + path + '&pageCount=' + pdfPageCount + "&sg=" + sg;
        }
    }, 1000);
}