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
  <link href="//vjs.zencdn.net/4.10/video-js.css" rel="stylesheet">
  <script src="//vjs.zencdn.net/4.10/video.js"></script>
  <script src="https://cdn.bootcss.com/jquery/1.11.3/jquery.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/util.js?v=<%=request.getAttribute("sv")%>"></script>
  <script>
    $(function () {
      var path = getQueryStringByName("path");
      var token = getQueryStringByName("token");
      var domain = document.domain;
      if (path != "") {
        $("#source").src("https://" + domain + "/FSC/EM/File/RangeDownloadByStream?path=" + path);
      }
      else if (token != "") {
        $("#source").src("https://" + domain + "/FSC/EM/File/RangeDownloadByStream?FileToken=" + token);
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