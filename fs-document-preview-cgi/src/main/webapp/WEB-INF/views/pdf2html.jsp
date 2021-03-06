<%--
  Created by IntelliJ IDEA.
  User: liuq
  Date: 2017/2/20
  Time: 16:37
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
  <%--<script type="text/javascript"--%>
          <%--src="/dps/static/js/pdf2html.js?v=<%=request.getAttribute("sv")%>"></script>--%>
  <script type="text/javascript"
          src="https://a9.fspage.com/FSR/fs-dps/static/js/pdf2html.js?v=<%=request.getAttribute("sv")%>"></script>
  <script>
    window.contextPath = "/dps";
  </script>
  <style type="text/css">
    div.lazy {
      background: #FFFFFF url("https://a9.fspage.com/FSR/fs-dps/static/images/loading.gif") no-repeat center;
      height: 500px;
    }

    div.border {
      text-align: center;
      width: ${width}px;
      margin: 5px auto;
      background-color: #ffffff;
      box-shadow: 0 2px 14px 0 rgba(0, 0, 0, 0.15);
      overflow: hidden;
    }

    div.center {
      text-align: center;
      font-size: 1em;
      margin-bottom: 10px;
      margin-top: 10px;
    }

    .sure-wrapper {
      width: 1000px;
      margin: 20px auto;
    }

    .sure-button {
      margin: 10px;
    }

    .weui-btn {
      position: relative;
      display: block;
      margin-left: auto;
      margin-right: auto;
      padding-left: 14px;
      padding-right: 14px;
      box-sizing: border-box;
      font-size: 18px;
      text-align: center;
      text-decoration: none;
      color: #FFFFFF;
      line-height: 2.55555556;
      border-radius: 5px;
      -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
      overflow: hidden;
    }

    .weui-btn_primary {
      background-color: #1AAD19;
    }

    .weui-btn:after {
      content: " ";
      width: 200%;
      height: 200%;
      position: absolute;
      top: 0;
      left: 0;
      border: 1px solid rgba(0, 0, 0, 0.2);
      -webkit-transform: scale(0.5);
      transform: scale(0.5);
      -webkit-transform-origin: 0 0;
      transform-origin: 0 0;
      box-sizing: border-box;
      border-radius: 10px;
    }

  </style>
  <title>文档预览</title>
</head>
<body style="background-color: #ccc">
<div id="main">
</div>
<div id="sure-wrapper" class="sure-wrapper">
  <a href="javascript:;" id="jumpUrl" class="weui-btn weui-btn_primary sure-button">确认对账</a>
</div>
</body>
</html>
