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
    <link href="<%=request.getContextPath()%>/static/yozo/bootstrap-responsive.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/static/yozo/doc.css?v=1.0.2" rel="stylesheet">
    <script>
        window.contextPath = "<%=request.getContextPath()%>";
    </script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/yozo/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/jquery.lazyload.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/main.js?v=1.1.0"></script>
<style>
    .content {
        margin-bottom: 15px;
        border: 1px solid #D3D3D3;
    }</style>
<body>
<div id="divPages">
</div>
</body>