<%--
  Created by IntelliJ IDEA.
  User: liuq
  Date: 2017/3/25
  Time: 23:23
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
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/common/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery.lazy.min.js"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/static/js/util.js?v=<%=request.getAttribute("sv")%>"></script>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/static/js/pdfjs.js?v=<%=request.getAttribute("sv")%>"></script>
    <script>
        window.contextPath = "<%=request.getContextPath()%>";
    </script>
    <title>pdf文档预览</title>
    <style type="text/css">
        <%--div.lazy {--%>
            <%--background: #FFFFFF url("<%=request.getContextPath()%>/static/images/loading1.gif") no-repeat center;--%>
            <%--height: 500px;--%>
        <%--}--%>

        div.border {
            text-align: center;
            width: 1000px;
            margin: 5px auto;
            /*border: 1px solid rgb(27, 30, 33)*/
        }

        div.center {
            text-align: center;
            font-size: 24px;
        }
    </style>
</head>
<body>
<div id="main">
</div>
</body>
</html>
