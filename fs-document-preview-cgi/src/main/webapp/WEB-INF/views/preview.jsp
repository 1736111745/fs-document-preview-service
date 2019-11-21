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
  <script>
    window.contextPath = "/dps";
  </script>
  <script type="text/javascript" src="https://a9.fspage.com/FSR/fs-dps/static/common/jquery-1.11.1.min.js"></script>
  <script type="text/javascript" src="https://a9.fspage.com/FSR/fs-dps/static/js/util.js?v=<%=request.getAttribute("sv")%>"></script>
  <%--<script type="text/javascript"--%>
          <%--src="/dps/static/js/main.js?v=<%=request.getAttribute("sv")%>"></script>--%>
  <script type="text/javascript" src="https://a9.fspage.com/FSR/fs-dps/static/js/main.js?v=<%=request.getAttribute("sv")%>"></script>
  <link href="https://a9.fspage.com/FSR/fs-dps/static/css/main.css?v=<%=request.getAttribute("sv")%>" rel="stylesheet">
  <style>
    html, body {
      margin: 0;
      padding: 0;
      width: 100%;
      height: 100%;
    }

    .main {
      width: 640px;
      height: 530px;
      border-radius: 3px;
      background-color: #ffffff;
      margin: 30px auto;
    }

    .main .img-center {
      width: 176px;
      height: 192px;
      margin: 0 auto;
    }

    .main .msg {
      width: 189px;
      height: 40px;
      text-align: center;
      color: #bdbdbd;
      margin: 0 auto;
    }
  </style>
<body>
<div class="main" id="main">
  <div class="img-center">
    <img id="imgIcon" src="https://a9.fspage.com/FSR/fs-dps/static/images/encrypt.png" width="176px" height="192px"/>
  </div>
  <div class="msg" id="msg"></div>
</div>
</body>