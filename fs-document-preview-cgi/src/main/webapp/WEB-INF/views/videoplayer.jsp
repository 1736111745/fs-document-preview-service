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
  <title>视频播放</title>
  <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,minimum-scale=1,user-scalable=no">
  <script type="text/javascript" src="<%=request.getContextPath()%>/static/common/jquery-1.11.1.min.js"></script>
  <link href="<%=request.getContextPath()%>/static/common/video-js.css" rel="stylesheet">
  <script src="<%=request.getContextPath()%>/static/common/video.js"></script>
  <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/util.js?v=<%=request.getAttribute("sv")%>"></script>
  <script>
    $(function () {
      var path = getQueryStringByName("path");
      var token = getQueryStringByName("token");
      var domain = document.domain;
      var protocol=location.protocol;
      if (path != "") {
        $("#source").attr("src", protocol+"//" + domain + "/FSC/EM/File/RangeDownloadByStream?path=" + path);
      }
      else if (token != "") {
        $("#source").attr("src", protocol+"//" + domain + "/FSC/EM/File/RangeDownloadByStream?FileToken=" + token);
      }
      videojs('my-player', {
        controls: true,
        autoplay: false,
        preload: 'auto'
      });
    });
  </script>
  <style>
    html,body{
      margin:0;
      padding: 0;
      width: 100%;
      height: 100%;
    }
    .video-js{
      width: 100%;
      height: 100%;
    }
  </style>
</head>
<body>
<video id="my-player" class="video-js vjs-big-play-centered" controls="true">
  <source id="source" src="" type="video/mp4">
</video>
</body>
</html>