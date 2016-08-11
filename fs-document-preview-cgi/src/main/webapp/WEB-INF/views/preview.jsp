<%--
  Created by IntelliJ IDEA.
  User: liuq
  Date: 16/8/10
  Time: 下午3:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>文档预览</title>
    <script src="http://cdn.besdlab.cn/jquery/2.1.1/jquery.2.1.1.min.js"></script>
    <link href="//netdna.bootstrapcdn.com/bootstrap/3.0.0-rc1/css/bootstrap.min.css" rel="stylesheet">
    <script src="//netdna.bootstrapcdn.com/bootstrap/3.0.0-rc1/js/bootstrap.min.js"></script>
    <script>
       $(function () {
          $("#sl").click(function () {
            $('#file').click();
          })
       })
       function doUpload() {
           var formData = new FormData($( "#f1" )[0]);
           $.ajax({
               url: '/dps/upload' ,
               type: 'POST',
               data: formData,
               async: false,
               cache: false,
               contentType: false,
               processData: false,
               success: function (data) {
                   $('#iframe').attr('src', '/dps/preview/'+data);

               },
               error: function (data) {
                   alert(data);
               }
           });
       }
    </script>
</head>
<body>
<div class="fileupload fileupload-new" data-provides="fileupload" style="margin-left: 20px;margin-top: 20px">
    <form action="/dps/preview" method="post" enctype="multipart/form-data" id="f1">
    <span class="btn btn-primary btn-file"><span class="fileupload-new" id="sl">选择文件</span>
            <input type="file" id="file" name="file" style="display:none">
        </span>
    </form>
    <script type="text/javascript">
        $('#file').change(function() {
           doUpload();
        });
    </script>
    <iframe sr="" id="iframe" width = "100%" height="100%" frameborder="no" scrolling="auto"></iframe>
</div>
</body>
</html>
