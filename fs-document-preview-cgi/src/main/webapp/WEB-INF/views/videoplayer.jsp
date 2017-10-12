<%--
  Created by IntelliJ IDEA.
  User: liuq
  Date: 2017/10/12
  Time: 10:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML>
<html>
<head>
  <title>Video.js Test Suite</title>
  <script type="text/javascript" src="<%=request.getContextPath()%>/static/common/jquery-1.11.1.min.js"></script>
  <link href="<%=request.getContextPath()%>/static/common/video-js.css" rel="stylesheet">
  <script src="<%=request.getContextPath()%>/static/common/video.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/util.js?v=<%=request.getAttribute("sv")%>"></script>
  <script>
    $(function () {
      var path = getQueryStringByName("path");
      var token = getQueryStringByName("token");
      var domain = document.domain;
      if (path != "") {
        $("#source").attr("src", "https://" + domain + "/FSC/EM/File/RangeDownloadByStream?path=" + path);
      }
      else if (token != "") {
        $("#source").attr("src", "https://" + domain + "/FSC/EM/File/RangeDownloadByStream?FileToken=" + token);
      }
      videojs('my-player', {
        controls: true,
        autoplay: true,
        preload: 'auto'
      });
    });
  </script>
</head>
<body>
<video id="my-player" class="video-js" width="500" height="500" controls="true">
  <source id="source" src="" type="video/mp4">
</video>
</body>
</html>