<%--
  Created by IntelliJ IDEA.
  User: liuq
  Date: 16/9/1
  Time: 下午2:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta Name="viewport" content="width=device-width, initial-scale=1.0">
  <script>
    window.contextPath = "<%=request.getContextPath()%>";
  </script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/static/common/jquery-1.11.1.min.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/util.js?v=<%=request.getAttribute("sv")%>"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/main.js?v=<%=request.getAttribute("sv")%>"></script>
  <link href="<%=request.getContextPath()%>/static/css/main.css?v=<%=request.getAttribute("sv")%>" rel="stylesheet">
  <style>
    div.main {
      width: 640px;
      height: 530px;
      border-radius: 3px;
      background-color: #ffffff;
      margin: 50%;
    }
    div.msg
    {
      width: 189px;
      height: 40px;
      text-align: center;
      color: #bdbdbd;
    }
  </style>
<body>
<div class="main">
  <div style="margin: 0 auto"><img  id="imgIcon" src="<%=request.getContextPath()%>/static/images/nosurport.png" width="176px" height="192px"/></div>
  <div class="msg" id="msg"></div>
</div>
</body>