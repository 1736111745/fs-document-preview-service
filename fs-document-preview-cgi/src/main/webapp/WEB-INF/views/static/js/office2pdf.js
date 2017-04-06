var sg = getQueryStringByName("sg");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
var type = path.indexOf(".doc") > -1 ? "checkWord2Pdf" : "checkPPT2Pdf"
var tryCount = 0;
$(function () {
    checkOffice2Pdf();
});
//定时监测转换状态，当全部转换完毕停止检测
// function checkPPT2PdfStatus() {
//     tryCount++;
//     idChkOffice2Pdf = setInterval(function () {
//         checkOffice2Pdf();
//         if (finished || tryCount > 10) {
//             clearInterval(idChkOffice2Pdf);
//
//         }
//     }, 2000);
// }

function checkOffice2Pdf() {
    var url = window.contextPath + '/preview/' + type + '?path=' + path + "&sg=" + sg
    var call = function () {
        if(tryCount++>10) {
            return;
        }
        $.ajax({
            type: 'get',
            dataType: 'json',
            async: false,
            url: url,
            success: function (data) {
                if (data.finished) {
                    location.href = window.contextPath + '/preview/handlePdf?path=' + path + '&pageCount=' + pageCount + "&sg=" + sg;
                }
                else
                {
                    call();
                }
            }
        });
    }
}