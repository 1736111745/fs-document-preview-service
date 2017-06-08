/**
 * Created by liuq on 16/9/8.
 */
var path = getQueryStringByName("path");
var token = getQueryStringByName("token");
var pageCount = 0;
var sg = "";//安全组
var width = getQueryStringByName("width");
width = width == "" ? 1000 : width;
function getPreviewInfo() {
  $('#divLoading').show();
  $.ajax({
    type: 'get',
    dataType: 'json',
    async: false,
    url: window.contextPath + '/preview/getPreviewInfo?path=' + path + '&token=' + token,
    success: function (data) {
      if (data.canPreview) {
        pageCount = data.pageCount;
        path = data.path;
        sg = data.sg;
        doPreview();
      }
      else {
        var errorMsg = data.errorMsg;
        $("#msg").html(data.errorMsg);
        var encryptImage = window.contextPath + "/static/images/encrypt.png";
        var damageImage = window.contextPath + "/static/images/damage.png";
        if (errorMsg.indexOf("加密") > -1) {
          $('#imgIcon').attr("src", encryptImage);
        }
        else {
          $('#imgIcon').attr("src", damageImage);
        }
      }
    }
  });
}


function doPreview() {
  if (path.toLowerCase().indexOf("txt") >= 0 || path.toLowerCase().indexOf("csv") >= 0) {
    doPreviewTxt();
  }
  else {
    doPreviewOffice();
  }
}

function doPreviewTxt() {
  $.ajax({
    type: 'get',
    dataType: 'json',
    async: false,
    url: window.contextPath + '/preview/getTxtPreviewInfo?path=' + path + "&sg=" + sg,
    success: function (data) {
      var dirName = data.dirName;
      var fileName = data.fileName;
      var url = window.contextPath + '/preview/' + dirName + "/" + fileName;
      location.href = url;
    }
  });
}

function doPreviewOffice() {
  var route = '';
  if (path.toLowerCase().indexOf("xls") >= 0) {
    route = "excel2html";
  }
  else
    route = "pdf2html";
  console.log("route:" + route);
  var url = window.contextPath + '/preview/' + route + '?path=' + path + '&pageCount=' + pageCount + "&sg=" + sg + "&width=" + width + "&rnd=1.0.1";
  location.href = url;
}

//入口
$(document).ready(function () {
  getPreviewInfo();
});