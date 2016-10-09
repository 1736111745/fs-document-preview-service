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
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/common/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/util.js?ts=<%=System.currentTimeMillis()%>"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/main.js?ts=<%=System.currentTimeMillis()%>"></script>
    <link href="<%=request.getContextPath()%>/static/css/main.css" rel="stylesheet">
<style>

</style>
<body>
<div id="divLoading">
    <div id="background" class="background"></div>
    <div id="progressBar" class="progressBar"></div>
</div>
</body>