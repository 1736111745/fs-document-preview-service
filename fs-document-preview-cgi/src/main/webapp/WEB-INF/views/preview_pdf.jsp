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
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/common/jquery.lazyload.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/util.js?v=<%=request.getAttribute("sv")%>"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/pdf.js?v=<%=request.getAttribute("sv")%>"></script>
    <link href="<%=request.getContextPath()%>/static/css/main.css" rel="stylesheet">
</head>
<body>
<div id="divPages"></div>
</body>
</html>
