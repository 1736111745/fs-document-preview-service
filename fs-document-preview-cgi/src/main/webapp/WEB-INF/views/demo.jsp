<%--
  Created by IntelliJ IDEA.
  User: liuq
  Date: 16/8/30
  Time: 上午11:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>文件上传</title>
</head>
<body>
<form action="<%=request.getContextPath()%>/upload" enctype="multipart/form-data" method="post">
    <input type="file" name="file"><input type="submit" value="提交">
</form>
</body>
</html>
