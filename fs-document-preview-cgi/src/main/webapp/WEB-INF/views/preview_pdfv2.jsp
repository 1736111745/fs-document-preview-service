<%--
  Created by IntelliJ IDEA.
  User: liuq
  Date: 2017/2/20
  Time: 16:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta Name="viewport" content="width=device-width, initial-scale=1.0">
    <title>文档预览</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/base.min.css"/>
    <link rel="stylesheet"
          href="<%=request.getContextPath()%>/static/css/fancy.min.css?v=<%=request.getAttribute("sv")%>"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/common/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery.lazy.min.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/static/js/util.js?v=<%=request.getAttribute("sv")%>"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/static/js/pdf2html.js?v=<%=request.getAttribute("sv")%>"></script>
    <script>
        window.contextPath = "<%=request.getContextPath()%>";
    </script>
    <style type="text/css">
        div.lazy {
            width:98%;
            height: 300px;
            background: url("<%=request.getContextPath()%>/static/images/loading.gif") no-repeat center;
        }
    </style>
    <title>pdf文档预览</title>
</head>
<body>
<div class="loading"></div>
<div id="page-container">
</div>
</body>
</html>
