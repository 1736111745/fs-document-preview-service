<%@ page import="java.util.UUID" %><%--
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
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>文档</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/yozo/jquery-1.11.1.min.js"></script>
    <script>
        window.contextPath = "<%=request.getContextPath()%>";
    </script>
    <link href="<%=request.getContextPath()%>/static/yozo/style.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/static/yozo/bootstrap.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/static/yozo/bootstrap-responsive.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/static/excel_preview.css?ts=<%=System.currentTimeMillis()%>"
          rel="stylesheet">
    <script type="application/javascript" src="<%=request.getContextPath()%>/static/yozo/bootstrap.min.js"></script>
    <script type="application/javascript"
            src="<%=request.getContextPath()%>/static/excel.js?ts=<%=System.currentTimeMillis()%>"></script>
</head>
<body>
<div id="divLoading">
    <div id="background" class="background"></div>
    <div id="progressBar" class="progressBar"></div>
</div>
<div id="content">
</div>
<div class="fixed_div">
    <div class="btn-group">
        <a type="button" class="btn btn-group-lg" id="linkLast">上一页</a>
        <a type="button" class="btn btn-group-lg" id="linkNext">下一页</a>
        <button type="button" class="btn btn-group-lg" id="paging" disabled></button>
    </div>
</div>
</body>
</html>
