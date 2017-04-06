var sg = getQueryStringByName("sg");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
var type = path.indexOf(".doc") > -1 ? "checkWord2Pdf" : "checkPPT2Pdf"
var tryCount = 0;
var idChkOffice2Pdf;
var finished=false;
$(function () {
    checkPPT2PdfStatus();
});
//定时监测转换状态，当全部转换完毕停止检测
function checkPPT2PdfStatus() {
    tryCount++;
    idChkOffice2Pdf = setInterval(function () {
        checkOffice2Pdf();
        if (finished || tryCount > 10) {
            clearInterval(idChkOffice2Pdf);
            var url = window.contextPath + '/preview/handlePdf?path =' + path + ' &pageCount = ' + pageCount + "&sg=" + sg;
            location.href = url;
        }
    }, 2000);
}

function checkOffice2Pdf() {
    var url = window.contextPath + '/preview/' + type + '?path=' + path + "&sg=" + sg
    $.ajax({
        type: 'get',
        dataType: 'json',
        async: false,
        url: url,
        success: function (data) {
           finished=data.finished;
        }
    });
}