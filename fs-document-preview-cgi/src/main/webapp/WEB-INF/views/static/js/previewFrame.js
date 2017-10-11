/**
 * Created by liuq on 2017/7/27
 */
var sg = getQueryStringByName("sg");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
var page = getQueryStringByName("page");
var screenWidth = window.screen.availWidth;//屏幕宽度
var screenHeight = window.screen.availHeight;//屏幕高度；
var width = 1000;
var deviceWidth = screenWidth;
$(function () {
  loadData(page);
  calcScreenRealSize();
  window.addEventListener("orientationchange", function () {
    calcScreenRealSize();
    window.setTimeout(function () {
      loadViewPort();
    }, 200);
  }, true);
});

//计算屏幕真实高宽
function calcScreenRealSize() {
  if (isLandscape()) {//横屏 宽度取大的值
    screenWidth = Math.max(window.screen.width, window.screen.height);
    screenHeight = Math.min(window.screen.width, window.screen.height);
  }
  else {//竖屏 宽度取小得值
    screenWidth = Math.min(window.screen.width, window.screen.height);
    screenHeight = Math.max(window.screen.width, window.screen.height);
  }
}

//是否横屏
function isLandscape() {
  return (window.orientation != 0 || window.orientation != 180)
}

function calcScale() {
  var height = $("#framePage").height();
  var scale = 1.0;
  if (isLandscape() && path.toLowerCase().indexOf("ppt") >= 0) { //横屏
    if (height > screenHeight) {
      deviceWidth = width * screenHeight / height;
      scale = deviceWidth * 0.96 / width;
    }
    else {
      scale = screenWidth * 0.96 / width;
      deviceWidth = screenWidth;
    }
  }
  else
    scale = screenWidth * 0.96 / width;
  return scale;
}

function loadViewPort() {
  var scale = calcScale();
  var viewport = document.querySelector("meta[name=viewport]");
  viewport.content = 'width=' + deviceWidth + ',initial-scale=' + scale;
  var topMargin = (screenHeight - document.body.offsetHeight * scale) * 0.5;
  topMargin = topMargin < 0 ? 0 : topMargin / scale;
  topMargin = topMargin + 'px';
  if (isLandscape() && path.toLowerCase().indexOf("ppt") >= 0) {
    $("body").css("margin-top", "8px");
  }
  else {
    $("body").css("margin-top", topMargin);
  }
}


function loadData(i) {
  var url = window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + i + "&pageCount=" + pageCount + "&width=" + width + "&sg=" + sg + "&ver=2.1";
  var iframe = "<iframe id='framePage' src='" + url + "' onload='resize(this)' onresize='resize(this)' scrolling='no' frameborder='0' width='100%'></iframe>";
  $('#main').html(iframe);
}

function resize(obj) {
  var height = $(obj.contentWindow.document).find("div[id='page-container']").height()
  $(obj).height(height);
  $(obj.parentElement).removeClass("lazy");
  loadViewPort();
}