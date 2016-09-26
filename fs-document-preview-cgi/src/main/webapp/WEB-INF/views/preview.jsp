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
    <script>
        window.contextPath = "<%=request.getContextPath()%>";
    </script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/yozo/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/jquery.lazyload.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/main.js?ts=<%=System.currentTimeMillis()%>"></script>
<style>
    .content {
        margin-bottom: 15px;
        border: 1px solid #D3D3D3;
    }
    .background {
        display: block;
        width: 100%;
        height: 100%;
        opacity: 0.4;
        filter: alpha(opacity=40);
        background: white;
        position: absolute;
        top: 0;
        left: 0;
        z-index: 2000;
    }

    .progressBar {
        background: url('<%=request.getContextPath()%>/static/loading.gif') no-repeat center center;
        display: block;
        width: 32px;
        height: 32px;
        top: 50%;
        left: 50%;
        margin-left: -16px;
        margin-top: -16px;
        text-align: left;
        line-height: 27px;
        font-weight: bold;
        position: absolute;
        z-index: 2001;
    }
</style>
<body>
<div id="divLoading">
    <div id="background" class="background"></div>
    <div id="progressBar" class="progressBar"></div>
</div>
<div id="divPages">
</div>
</body>