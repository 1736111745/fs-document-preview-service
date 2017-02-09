var sg=getQueryStringByName("sg");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
$(function() {
    $(window).bind('scroll',function(){show()});
    var i = 1;
    function show()
    {
        if($(window).scrollTop()+$(window).height()>=$(document).height()*0.75)
        {
            i++;
            if(i<pageCount){
                var src = window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + i + "&pageCount=" + pageCount+"&sg="+sg;
                var page = $("<div class='content'><embed class='lazy' src='" + src + "' width='100%' height='100%' type='image/svg+xml'/></div>");
                $("#divPages").append(page);
                var nav = $("<div class='center'><span>第" + (i + 1) + "页,共" + pageCount + "页</span></div>");
                page.append(nav);
            }
        }
    }
});
