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
    <link href="/static/doc.css" rel="stylesheet">
    <script type="text/javascript" src="/static/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="/static/jquery.isloading.min.js"></script>
    <%--<style>body {--%>
    <%--font: 14px "宋体", "Arial Narrow", HELVETICA;--%>
    <%--background: #fff;--%>
    <%---webkit-text-size-adjust: 100%;--%>
    <%--}--%>
    <%--</style>--%>
    <script>
        var path=getQueryStringByName("path");
        $(function () {
            loadSvg(path,0);
        })
        var loaded=0;
        function getQueryStringByName(name){
            var result = location.search.match(new RegExp("[\?\&]" + name+ "=([^\&]+)","i"));
            if(result == null || result.length < 1) {
                return "";
            }
            return result[1];
        }
        function loadSvg(path,pageIndex) {
            $.ajax({
                type: 'get',
                dataType: 'json',
                url: '/preview/getsvg?path=' + path + '&page=' + pageIndex,
                beforeSend: function () {
                    $.isLoading({text: "文档加载中..."});
                },
                success: function (data) {
                    loaded++;
                    $.isLoading("hide");
                    var html = $("<DIV class='word-page' STYLE='max-width:793px' id='doc0'><DIV class='word-content'>"+data.svgData+"</DIV></DIV>");
                    $("#content").append(html);
                },
                complete: function () {
                    console.log('mission complete.')
                }
            });
        }
        var sign = 10;
        $(window).scroll(function () {
            var scrtop = $(window).scrollTop();
            var height = $(document).height();
            if (scrtop > sign) {
                if ($(window).scrollTop() + $(window).height() >= $(document).height() * 0.75) {
                    loadSvg(path, loaded);
                }
                sign = scrtop;
            }
        });
    </script>
<body>
<div id="content">
</div>

</body>