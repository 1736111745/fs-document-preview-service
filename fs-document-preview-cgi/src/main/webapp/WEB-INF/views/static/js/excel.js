/**
 * Created by liuq on 16/9/19.
 */
var page = getQueryStringByName("page");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
var sg = getQueryStringByName("sg");
var sharetoken = getQueryStringByName("shareToken");
function loadSheetNames() {
    $.ajax({
        type: 'get',
        dataType: 'json',
        async: false,
        url: window.contextPath + '/preview/getSheetNames?path=' + path+"&sg="+sg+"&sharetoken="+sharetoken,
        success: function (data) {
            if (data.success) {
                var sheets = data.sheets;
                var activeSheetIndex = 0;
                for (var i = 0; i < sheets.length; i++) {
                    var sheetName = sheets[i];
                    var isHidden = sheetName.indexOf("_$h1$") > -1;//隐藏的sheet，默认不隐藏
                    if (!isHidden) {
                        var isActive = sheetName.indexOf("_$a1$") > -1;//激活的sheet，默认都激活
                        if (isActive) {
                            activeSheetIndex = i;
                        }
                        sheetName = sheetName.replace("_$h0$", "").replace("_$a0$", "").replace("_$h1$", "").replace("_$a1$", "");
                        var li = "<li id='li" + i + "'><a href='#' onclick='loadSheet(" + i + ")' data-toggle='tab'>" + sheetName + "</a></li>";
                        $('#navSheet').append($(li));
                    }
                }
                var activeSheetName = sheets[activeSheetIndex];
                activeSheetName = activeSheetName.replace("_$h0$", "").replace("_$a0$", "").replace("_$h1$", "").replace("_$a1$", "");
                $('#li' + activeSheetIndex).addClass("active");
                $('#aTitle').html(activeSheetName);
                loadSheet(activeSheetIndex);
                loadNav();
            }
            else {
                document.write(data.errorMsg);
            }
        }
    });
}

function loadSheet(i) {
    $.ajax({
        type: 'get',
        timeout: 1800000,
        dataType: 'json',
        async: true,
        url: window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + i + "&pageCount=" + pageCount + "&sg=" + sg+"&sharetoken="+sharetoken+"&ver=2.0",
        beforeSend: function () {
            $('#divLoading').show();
        },
        complete: function (request) {
            $('#divLoading').hide();
            var excelContent = $(request.responseText)
            $('#content').html(excelContent);
        }
    });
}

function move(direction) {
    if (direction == 0) {
        for (var i = $(".excel-tab-title li").length - 1; i > -1; i--) {
            if ($(".excel-tab-title li:eq(" + i + ")").css("display") == "none") {
                $(".excel-tab-title li:eq(" + i + ")").css("display", "block");
                break;
            }
        }
    }
    else {
        if ($(".excel-tab-title")[0].scrollHeight > 42) {
            for (var i = 0; i < $(".excel-tab-title li").length; i++) {
                if ($(".excel-tab-title li:eq(" + i + ")").css("display") != "none") {
                    $(".excel-tab-title li:eq(" + i + ")").css("display", "none");
                    break;
                }
            }
        }
    }
}
function loadNav() {
    $(".lnk-file-title").html($(".excel-tab-title .active>a").html());
    $(".excel-tab-title li>a").click(function () {
        $(".navbar-fixed-top").css("height", "");
        $(".lnk-file-title").html($(this).html());
        $(".nav-collapse.collapse").height("0px");
        $(".btn.btn-navbar").addClass("collapsed");
        $(".nav-collapse.in.collapse").removeClass("in");
    })
    $(".btn-navbar").click(function (e) {
        $(".navbar-fixed-top").height(document.documentElement.clientHeight);
        if ($(".nav-collapse").hasClass("in")) {
            $(".navbar-fixed-top").css("height", "");
        }
    });

}
$(document).ready(function () {
    $('#divLoading').hide();
    loadSheetNames();
});