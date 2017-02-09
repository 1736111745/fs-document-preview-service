var sg=getQueryStringByName("sg");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
var end=pageCount>3?3:pageCount;
$(function() {
    loadPrev3Page();
    var __start__=end;
    $(window).bind('scroll',function(){show()});
    function show()
    {
        if($(window).scrollTop()+$(window).height()>=$(document).height()*0.75)
        {
            if(__start__<pageCount){
                loadData(__start__);
                __start__++;
            }
        }
    }
    
    function loadPrev3Page() {
        for (var i = 0; i < end; i++) {
            loadData(i);
        }
    }


    function loadData(i) {
        var src = window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + i + "&pageCount=" + pageCount+"&sg="+sg;
        var page = $("<div class='content'><embed class='lazy' src='" + src + "' width='100%' height='100%' type='image/svg+xml'/></div>");
        $("#divPages").append(page);
        var nav = $("<div class='center'><span>第" + (i + 1) + "页,共" + pageCount + "页</span></div>");
        page.append(nav);
    }
});
