/**
 * Created by liuq on 16/9/8.
 */
var path = getQueryStringByName("path");
var token = getQueryStringByName("token");
var name = getQueryStringByName("name");
var sharetoken = getQueryStringByName("shareToken");
var jumpUrl = getQueryStringByName("jumpUrl");
var pageCount = 0;
var sg = ""//安全组
var width = getQueryStringByName("width");
var pdfConvertType = 0;
var txtAndImageExt = ".sql|.txt|.js|.css|.json|.csv|.svg|.webp|.jpeg|.jpg|.png|.bmp|.gif";
width = width == "" ? 1000 : width;


function getPreviewInfo() {
  $('#divLoading').show();
  $.ajax({
    type: 'get',
    dataType: 'json',
    async: false,
    url: window.contextPath + '/preview/getPreviewInfo?path=' + path + '&token=' + token + "&width=" + width + "&sharetoken=" + sharetoken + "&sg=" + sg,
    success: function (data) {
      if (data.canPreview) {
        pageCount = data.pageCount;
        path = data.path;
        sg = data.sg;
        pdfConvertType = data.pdfConvertType;
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
        $("#main").show();
      }
    }
  });
}


function doPreview() {
  var ext = getFileExt(path.toLowerCase());
  if (txtAndImageExt.indexOf(ext) >= 0) {
    doPreviewOriginal();
  }
  else if (path.toLowerCase().indexOf("mp4") >= 0) {
    location.href = window.contextPath + "/preview/videoplayer?path=" + path + "&token=" + token;
  }
  else {
    doPreviewOffice();
  }
}

function doPreviewOriginal() {
  $.ajax({
    type: 'get',
    dataType: 'json',
    async: false,
    url: window.contextPath + '/preview/getOriginalPreviewInfo?path=' + path + "&sg=" + sg + "&sharetoken=" + sharetoken,
    success: function (data) {
      var dirName = data.dirName;
      var fileName = data.fileName;
      var ea = data.ea;
      var url = window.contextPath + '/preview/' + dirName + "/" + fileName + "?sharetoken=" + sharetoken;
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
  var url = window.contextPath + '/preview/' + route + '?path=' + path + '&pageCount=' + pageCount + "&pdfConvertType=" + pdfConvertType + "&sg=" + sg + "&width=" + width + "&sharetoken=" + sharetoken + "&jumpUrl=" + jumpUrl;
  location.href = url;
}

function checkShareToken() {
  if (sharetoken != "") {
    $.ajax({
      type: 'get',
      dataType: 'json',
      async: false,
      url: window.contextPath + '/share/preview/parseShareToken?shareToken=' + sharetoken,
      success: function (d) {
        if (d.success) {
          path = d.data.path;
          sg = d.data.securityGroup;
          getPreviewInfo();
        }
        else {
          var errorMsg = "参数错误";
          $("#msg").html(errorMsg);
          var damageImage = window.contextPath + "/static/images/damage.png";
          $('#imgIcon').attr("src", damageImage);
          $("#main").show();
        }
      }
    });
  }
  else {
    var errorMsg = "参数错误";
    $("#msg").html(errorMsg);
    var damageImage = window.contextPath + "/static/images/damage.png";
    $('#imgIcon').attr("src", damageImage);
    $("#main").show();
  }
}


//入口
$(document).ready(function () {
  //兼容path不带扩展名，取name的扩展名
  if (path != "") {
    var ext = getFileExt(path);
    if (ext == null) {
      path = path + getFileExt(name);
    }
  }
  $("#main").hide();
  if (location.href.indexOf("bysharetoken") > -1) {
    checkShareToken();
  }
  else {
    if (path.toLowerCase().indexOf("mp4") >= 0 || name.toLowerCase().indexOf("mp4") >= 0) {
      location.href = window.contextPath + "/preview/videoplayer?path=" + path + "&token=" + token;
    }
    else {
      getPreviewInfo();
    }
  }
});