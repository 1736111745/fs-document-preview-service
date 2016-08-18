<%--
  Created by IntelliJ IDEA.
  User: liuq
  Date: 16/8/18
  Time: 下午1:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<meta charset="utf-8">
<head>
    <title>文档预览</title>
    <script src="/dps/script/jquery-1.11.1.min.js" type="text/javascript"></script>
    <style>
        body {
            font-family: Verdana;
        }

        .wrapper {
            padding-bottom: 50px;
        }

        .header {
            background: #000000;
        }

        .header .container {
            padding-bottom: 20px;
        }

        .header .container h1 {
            color: #FFF;
            line-height: 50px;
            margin-top: 30px;
            margin-bottom: 0;
        }

        .header .container h3 {
            color: #E0E0E0;
            font-size: 16px;
            margin-top: -5px;
        }

        .header .container h3 a {
            color: #66aacc;
        }

        .header .container h3 a:hover {
            color: #b3d5e6;
        }

        .top-menu {
            position: absolute;
            z-index: 9999;
        }

        .top-menu .nav-list {
            background: rgba(255, 255, 255, 0.8);
            border: 1px solid #CCC;
            margin: 20px;
            position: fixed;
            right: 0;
        }

        pre, .well {
            background: #FFFFFF;
        }

        .syntax-container {
            background: #FCFCFC;
            -webkit-border-radius: 7px;
            -webkit-background-clip: padding-box;
            -moz-border-radius: 7px;
            -moz-background-clip: padding;
            border-radius: 7px;
            background-clip: padding-box;
            border: 1px solid #e3e3e3;
            margin-bottom: 30px;
            padding-bottom: 20px;
        }

        .isloading-wrapper.isloading-right {
            margin-left: 10px;
        }

        .isloading-overlay {
            position: relative;
            text-align: center;
        }

        .isloading-overlay .isloading-wrapper {
            background: #FFFFFF;
            -webkit-border-radius: 7px;
            -webkit-background-clip: padding-box;
            -moz-border-radius: 7px;
            -moz-background-clip: padding;
            border-radius: 7px;
            background-clip: padding-box;
            display: inline-block;
            margin: 0 auto;
            padding: 10px 20px;
            top: 10%;
            z-index: 9000;
        }
    </style>
    <script>
        function getQueryStringByName(name){
            var result = location.search.match(new RegExp("[\?\&]" + name+ "=([^\&]+)","i"));
            if(result == null || result.length < 1){
                return "";
            }
            return result[1];
        }
        $(function () {
            convert();
        })
        function convert() {
            $("#loading").show();
            var queryString=window.location.search;
            var url="/preview/convert"+queryString;
            $.ajax({url:url,success:function(result){
                $("#loading").hide();
                if(result.successed) {
                    var path = getQueryStringByName("path");
                    window.location.href = "/preview/show?path=" + path;
                }
                else
                {
                    $("#tip").html(result.errorMsg);
                    $("#tip").show();
                }
            }});
        }
    </script>
</head>
<body>
<div class="isloading-overlay"
     style="position:fixed; left:0; top:0; z-index: 10000; background: rgba(0,0,0,0.5); width: 100%; height: 100%;">
    <span id="loading" class="isloading-wrapper  isloading-show  isloading-overlay" style="top: 299.5px;display: none;">请稍后，正在处理中&nbsp;</span>
    <span id="tip" class="isloading-wrapper  isloading-show  isloading-overlay" style="top: 299.5px;display: none;"></span>
</div>
</body>
</html>
