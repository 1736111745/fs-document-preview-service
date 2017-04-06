<%--
  Created by IntelliJ IDEA.
  User: liuq
  Date: 16/9/29
  Time: 下午6:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>文档预览</title>
    <script>
        window.contextPath = "<%=request.getContextPath()%>";
    </script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/common/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery.lazy.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/util.js?v=<%=request.getAttribute("sv")%>>"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/word_ppt.js?v=<%=request.getAttribute("sv")%>"></script>
    <link href="<%=request.getContextPath()%>/static/css/main.css" rel="stylesheet">
    <style type="text/css">
        div.lazy {
            background: #FFFFFF url("<%=request.getContextPath()%>/static/images/loading1.gif") no-repeat center;
            height: 500px;
        }

        div.border {
            text-align: center;
            width: 1000px;
            margin: 5px auto;
            border: 1px solid rgb(27, 30, 33)
        }

        div.center {
            text-align: center;
            font-size: 24px;
        }
    </style>
</head>
<body>
<div id="main"></div>
</body>
</html>
