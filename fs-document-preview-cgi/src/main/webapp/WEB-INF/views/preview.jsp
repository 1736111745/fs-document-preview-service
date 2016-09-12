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
    <link href="<%=request.getContextPath()%>/static/bootstrap-combined.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/static/doc.css?v=1" rel="stylesheet">
    <script>
        window.contextPath = "<%=request.getContextPath()%>";
    </script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/main.js?v=16"></script>
<body class="word-body">
<div id="divPages">
</div>
</body>