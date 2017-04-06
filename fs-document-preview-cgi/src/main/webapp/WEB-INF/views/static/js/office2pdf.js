var sg = getQueryStringByName("sg");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
var type = path.indexOf(".doc") > -1 ? "checkWord2Pdf" : "checkPPT2Pdf"
var tryCount = 0;
$(function () {
    checkOffice2Pdf();
});
function checkOffice2Pdf() {
    var url = window.contextPath + '/preview/' + type + '?path=' + path + "&sg=" + sg
    var call = function () {
        if(tryCount++>10) {
            return;
        }
        $.ajax({
            type: 'get',
            dataType: 'json',
            async: true,
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