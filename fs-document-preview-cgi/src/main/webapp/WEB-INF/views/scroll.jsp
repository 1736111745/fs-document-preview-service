<%--
  Created by IntelliJ IDEA.
  User: liuq
  Date: 16/8/31
  Time: 下午1:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta Name="viewport" content="width=device-width, initial-scale=1.0">
    <title>文档预览</title>
    <link rel="stylesheet" href="script/bootstrap.min.css">
    <link rel="stylesheet" href="script/style1.css">
    <script src="script/jquery-1.11.1.min.js"></script>
</head>
<STYLE>
    #container {
        min-width: 400px;
        min-height: 100px;
        border: 2px dotted #000;
        -moz-border-radius: 8px;
    }

    .top-word {
        overflow: hidden;
    }

    .top-word .rb, .top-word .lb, .top-word .rb_bottom, .top-word .lb_bottom {
        height: 25px;
        width: 25px;
    }

    .rb {
        border-right: 1px solid #a9a9a9;
        border-bottom: 1px solid #a9a9a9;
        float: left;
    }

    .lb {
        border-left: 1px solid #a9a9a9;
        border-bottom: 1px solid #a9a9a9;
        float: right;
    }

    .rb_bottom {
        border-right: 1px solid #a9a9a9;
        border-top: 1px solid #a9a9a9;
        float: left;
    }

    .lb_bottom {
        border-left: 1px solid #a9a9a9;
        border-top: 1px solid #a9a9a9;
        float: right;
    }</STYLE>
<body class="word-body">
<script>
    $(function () {
        $(window).bind('scroll', function () {
            show()
        });
        var i = 1;

        function show() {
            var widthlist = '{"data": [{'
                    + '     "index":  "1" ,'
                    + '     "width": "793" '
                    + ' },{'
                    + '     "index":  "2" ,'
                    + '     "width": "793" '
                    + ' },{'
                    + '     "index":  "3" ,'
                    + '     "width": "793" '
                    + ' },{'
                    + '     "index":  "4" ,'
                    + '     "width": "793" '
                    + ' },{'
                    + '     "index":  "5" ,'
                    + '     "width": "793" '
                    + ' }'
                    + ']'
                    + '}';
            var a = $.parseJSON(widthlist);
            datas = a.data;
            if ($(window).scrollTop() + $(window).height() >= $(document).height() * 0.75) {
                i++;
                if (i < datas.length) {
                    $(".word-body").append('<DIV class="container-fluid container-fluid-content">'
                            + '<DIV class="row-fluid">'
                            + '<DIV class="span12">'
                            + '<DIV class="word-page" STYLE="max-width:' + datas[i].width + 'px">'
                            + '<DIV class="word-content">'
                            + '<embed src="script/svg/' + (i + 1) + '.svg" width="100%" height="100%" type="image/svg+xml"></embed>'
                            + '</DIV>'
                            + '</DIV>'
                            + '</DIV>'
                            + '</DIV>'
                            + '</DIV>');
                }
            }
        }
    });</script>
<DIV class="navbar navbar-inverse navbar-fixed-top">
    <DIV class="navbar-inner">
        <DIV class="container-fluid">
            <a class="brand lnk-file-title" STYLE="text-decoration: none;" TITLE="a.doc">a.doc</a>
            <DIV class="nav-collapse collapse">
                <ul class="nav word-tab-title"></ul>
            </DIV>
        </DIV>
    </DIV>
</DIV>
<DIV class="container-fluid container-fluid-content">
    <DIV class="row-fluid">
        <DIV class="span12">
            <DIV class="word-page" STYLE="max-width:793px">
                <DIV class="word-content">
                    <embed src="script/svg/1.svg" width="100%" height="100%" type="image/svg+xml"></embed>
                </DIV>
            </DIV>
        </DIV>
    </DIV>
</DIV>
<DIV class="container-fluid container-fluid-content">
    <DIV class="row-fluid">
        <DIV class="span12">
            <DIV class="word-page" STYLE="max-width:793px">
                <DIV class="word-content">
                    <embed src="script/svg/2.svg" width="100%" height="100%" type="image/svg+xml"></embed>
                </DIV>
            </DIV>
        </DIV>
    </DIV>
</DIV>
</body>
</html>

