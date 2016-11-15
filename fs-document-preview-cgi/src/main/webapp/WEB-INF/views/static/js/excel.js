/**
 * Created by liuq on 16/9/19.
 */
var page = getQueryStringByName("page");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
var sg=getQueryStringByName("sg");
function loadSheetNames() {
    $.ajax({
        type: 'get',
        dataType: 'json',
        async: false,
        url: window.contextPath + '/preview/getSheetNames?path=' + path,
        success: function (data) {
            if (data.success) {
                var sheets = data.sheets;
                for (var i = 0; i < sheets.length; i++) {
                    var sheetName = sheets[i];
                    var cls = '';
                    if (i == 0) {
                        cls = 'active';
                        $('#aTitle').html(sheetName);
                    }
                    var li = "<li class='" + cls + "'><a href='#' onclick='loadSheet(" + i + ")' data-toggle='tab'>" + sheetName + "</a></li>";
                    $('#navSheet').append($(li));
                }
                loadSheet(0);
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
        url: window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + i + "&pageCount=" + pageCount + "&sg=" + sg,
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

function move(direction){
    if(direction == 0){
        for (var i = $(".excel-tab-title li").length - 1; i > -1; i--) {
            if ($(".excel-tab-title li:eq("+i+")").css("display") == "none") {
                $(".excel-tab-title li:eq("+i+")").css("display","block");
                break;
            }
        }
    }
    else{
        if($(".excel-tab-title")[0].scrollHeight > 42 ){
            for (var i = 0; i < $(".excel-tab-title li").length; i++) {
                if ($(".excel-tab-title li:eq("+i+")").css("display") != "none") {
                    $(".excel-tab-title li:eq("+i+")").css("display","none");
                    break;
                }
            }
        }
    }
}
function loadNav() {
    $(".lnk-file-title").html($(".excel-tab-title .active>a").html());
    $(".excel-tab-title li>a").click(function(){
        $(".navbar-fixed-top").css("height","");
        $(".lnk-file-title").html($(this).html());
        $(".nav-collapse.collapse").height("0px");
        $(".btn.btn-navbar").addClass("collapsed");
        $(".nav-collapse.in.collapse").removeClass("in");
    })
    $(".btn-navbar").click(function(e) {
        $(".navbar-fixed-top").height(document.documentElement.clientHeight);
        if($(".nav-collapse").hasClass("in")){
            $(".navbar-fixed-top").css("height","");
        }
    });

}
$(document).ready(function () {
    $('#divLoading').hide();
    loadSheetNames();
});