<%--
  Created by IntelliJ IDEA.
  User: liuq
  Date: 16/9/1
  Time: 下午2:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta Name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="/static/bootstrap-combined.min.css" rel="stylesheet">
    <link href="/static/font-awesome.css" rel="stylesheet">
    <link href="/static/style.css" rel="stylesheet">
    <link href="/static/doc.css" rel="stylesheet">
    <script type="text/javascript" src="/static/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="/static/jquery.isloading.min.js"></script>
    <style>body {
    font: 14px "宋体", "Arial Narrow", HELVETICA;
    background: #fff;
    -webkit-text-size-adjust: 100%;
    }
    </style>
    <script>
        var path=getQueryStringByName("path");
        var loaded=0;
        var maxPageIndex=9999;
        function getQueryStringByName(name){
            var result = location.search.match(new RegExp("[\?\&]" + name+ "=([^\&]+)","i"));
            if(result == null || result.length < 1) {
                return "";
            }
            return result[1];
        }
        function loadSvg(pageIndex) {
            $.ajax({
                type: 'get',
                dataType: 'json',
                async: false,
                url: '/preview/getsvg?path=' + path + '&page=' + pageIndex,
                beforeSend: function () {
                    $.isLoading({text: "文档加载中..."});
                    console.log("load "+pageIndex);
                },
                success: function (data) {
                    $.isLoading("hide");
                    if(data.successed) {
                        loaded++;
                        var html = $("<DIV class='word-page' STYLE='max-width:793px' id='doc0'><DIV class='word-content'><embed src='/preview/" + data.svgFile + "' width='100%' height='100%' type='image/svg+xml'></embed></DIV></DIV>");
                        $("#content").append(html);
                    }
                    else
                    {
                        maxPageIndex=loaded;
                    }
                },
                complete: function () {
                    console.log('mission complete.')
                }
            });
        }
        $(document).ready(function () {
            loadSvg(loaded);
            $(window).scroll(function () {
                var $body = $("body");
                /*判断窗体高度与竖向滚动位移大小相加 是否 超过内容页高度*/
                if (($(window).height() + $(window).scrollTop()) >= $body.height()) {
                    if(loaded>0&&maxPageIndex>loaded)
                    loadSvg(loaded);
                }
            });
        });
    </script>
<body class="word-body">
<div id="load-overlay">
</div>
<div id="content">
</div>
</body>