<%--
  Created by IntelliJ IDEA.
  User: liuq
  Date: 2017/7/27
  Time: 10:58
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
  <script type="text/javascript" src="https://a9.fspage.com/FSR/fs-dps/static/common/jquery-1.11.1.min.js"></script>
  <script type="text/javascript" src="https://a9.fspage.com/FSR/fs-dps/static/common/jquery.lazy.min.js"></script>
  <script type="text/javascript"
          src="https://a9.fspage.com/FSR/fs-dps/static/js/util.js?v=<%=request.getAttribute("sv")%>"></script>
  <script type="text/javascript" src="https://a9.fspage.com/FSR/fs-dps/static/js/previewFrame.js?v=<%=request.getAttribute("sv")%>")></script>
  <script>
    window.contextPath = "/dps"
  </script>
  <style type="text/css">

    div.border {
      text-align: center;
      width: 1000px;
      margin: 5px auto;
      /*background-color: #ffffff;*/
      /*box-shadow: 0 2px 14px 0 rgba(0, 0, 0, 0.15);*/
    }
  </style>
  <title>文档预览</title>
</head>
<body bgcolor="#807F7F">
<div id="main" class="border" style="margin:auto auto;overflow: hidden;">
</div>
</body>
</html>