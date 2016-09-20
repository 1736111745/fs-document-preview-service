<%--
  Created by IntelliJ IDEA.
  User: liuq
  Date: 16/9/19
  Time: 下午5:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>iframe tag test</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/yozo/jquery-1.11.1.min.js"></script>
    <script>
        window.contextPath = "<%=request.getContextPath()%>";
    </script>
    <link href="<%=request.getContextPath()%>/static/yozo/bootstrap.min.css" rel="stylesheet">
    <script type="application/javascript" src="<%=request.getContextPath()%>/static/yozo/bootstrap.min.js"></script>
    <script type="application/javascript" src="<%=request.getContextPath()%>/static/excel.js"></script>
    <style type="text/css">
        <!--
        * html {
            overflow: hidden;
        }

        * body {
            height: 100%;
            overflow: auto;
            margin: 0;
        }

        #fd {
            position: fixed;
            *position: absolute;
            width: 100px;
            height: 100px;
            top: 90%;
            left: 50%;
            margin: -50px 0 0 -50px;
        }
    </style>
</head>
<body>
<div id="loading">
    <img src="<%=request.getContextPath()%>/static/loading.gif" alt="" width="100px"/>
</div>
<div id="content">

</div>
<div id="fd">
    <div class="btn-group">
        <a type="button" class="btn btn-default" id="linkLast">上一页</a>
        <a type="button" class="btn btn-default" id="linkNext">下一页</a>
    </div>
</div>
</body>
</html>
