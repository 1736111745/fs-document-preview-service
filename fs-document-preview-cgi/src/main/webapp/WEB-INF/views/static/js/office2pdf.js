var sg = getQueryStringByName("sg");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
var type = path.indexOf(".doc") > -1 ? "checkWord2Pdf" : "checkPPT2Pdf"
var tryCount = 0;
$(function () {

    checkOffice2Pdf();
});
var checkOffice2Pdf = function () {
    console.log("try count:" + tryCount);
    var url = window.contextPath + '/preview/' + type + '?path=' + path + "&sg=" + sg
    if (tryCount++ > 10) {
        return;
    }
    $.ajax({
        type: 'get',
        dataType: 'json',
        async: true,
        url: url,
        timeout: 5000,
        complete: function (XMLHttpRequest, status) { //请求完成后最终执行参数
            if (data.finished) {
                location.href = window.contextPath + '/preview/handlePdf?path=' + path + '&pageCount=' + pageCount + "&sg=" + sg;
            }
            else {
                checkOffice2Pdf();
            }
            if (status == 'timeout') {
                console.log("time out!")
            }
        }
    });
}