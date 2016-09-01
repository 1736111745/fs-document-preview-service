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
    <link href="/dps/static/bootstrap-combined.min.css" rel="stylesheet">
    <link href="/dps/static/font-awesome.css" rel="stylesheet">
    <link href="/dps/static/style.css" rel="stylesheet">
    <script type="text/javascript" src="/dps/static/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="/dps/static/jquery.isloading.min.js"></script>
    <style>body {
        font: 14px "宋体", "Arial Narrow", HELVETICA;
        background: #fff;
        -webkit-text-size-adjust: 100%;
    }
    </style>
    <script>
        function getQueryStringByName(name){
            var result = location.search.match(new RegExp("[\?\&]" + name+ "=([^\&]+)","i"));
            if(result == null || result.length < 1) {
                return "";
            }
            return result[1];
        }
        function convert() {
            $.isLoading({text: "文档加载中..."});
            var queryString=window.location.search;
            var url="/preview/convert"+queryString;
            $.ajax({url:url,success:function(result){
                $.isLoading.hide();
                if(result.successed) {
                    var path=result.filePath;
                    var name=result.fileName;
                    window.location.href = "/preview/show?path="+path+"&name="+name;
                }
                else
                {
                    alert(result.errorMsg);
                }
            }});
        }
        $(function () {
            convert();
        })
    </script>

</head>
<body>
<div id="load-overlay">
</div>
</body>
</html>