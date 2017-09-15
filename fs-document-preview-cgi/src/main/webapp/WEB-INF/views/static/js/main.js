/**
 * Created by liuq on 16/9/8.
 */
var path = getQueryStringByName("path");
var token = getQueryStringByName("token");
var sharetoken = getQueryStringByName("shareToken");
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
    url: window.contextPath + '/preview/getPreviewInfo?path=' + path + '&token=' + token + "&width=" + width + "&sharetoken=" + sharetoken,
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
        $("#main").show();
      }
    }
  });
}


function doPreview() {
  if (path.toLowerCase().indexOf("txt") >= 0
    || path.toLowerCase().indexOf("csv") >= 0
    || path.toLowerCase().indexOf("svg") >= 0
    || path.toLowerCase().indexOf("webp")>=0) {
    doPreviewOriginal();
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
  var url = window.contextPath + '/preview/' + route + '?path=' + path + '&pageCount=' + pageCount + "&sg=" + sg + "&width=" + width + "&sharetoken=" + sharetoken + "&rnd=1.0.1";
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
  $("#main").hide();
  if (location.href.indexOf("bysharetoken") > -1) {
    checkShareToken();
  }
  else {
    getPreviewInfo();
  }
});