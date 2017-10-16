/**
 * Created by liuq on 2017/7/27 v1
 */
var sg = getQueryStringByName("sg");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
var page = getQueryStringByName("page");
var screenWidth = window.screen.availWidth;//屏幕宽度
var screenHeight = window.screen.availHeight;//屏幕高度；
var width = 1000;
var u = navigator.userAgent;
var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
var deviceWidth = screenWidth;
$(function () {
  loadData(page);
  window.addEventListener("orientationchange", function () {
    if (isAndroid) {
      var delay = 1000;
      if (window.DocMeasure.isCurrentFocus(page)) {
        loadViewPort();
      } else {
        window.setTimeout(function () {
          loadViewPort();
        }, delay);
      }
    } else {
      loadViewPort();
    }
  }, true);
});

//计算屏幕真实高宽
function calcScreenRealSize(islandscape) {
  var landscape = typeof islandscape == "undefined" ? isLandscape() : islandscape;
  if (landscape) {//横屏 宽度取大的值
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
  return (window.orientation != 0 && window.orientation != 180)
}

function calcScale(islandscape) {
  var landscape = typeof islandscape == "undefined" ? isLandscape() : islandscape;
  calcScreenRealSize(landscape);
  var height = $("#framePage").height();
  var scale = 1.0;
  if (landscape && path.toLowerCase().indexOf("ppt") >= 0) { //横屏
    if (height > screenHeight) {
      deviceWidth = width * screenHeight / height;
      scale = deviceWidth * 0.99 / width;
    }
    else {
      scale = screenWidth * 0.99 / width;
      deviceWidth = screenWidth;
    }
  }
  else
    scale = screenWidth * 0.99 / width;
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
    $("body").css("margin-top", "0px");
  }
  else {
    $("body").css("margin-top", topMargin);
  }
}


function loadData(i) {
  var url = window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + i + "&pageCount=" + pageCount + "&width=" + width + "&sg=" + sg + "&ver=2.1";
  var iframe = "<iframe id='framePage' src='" + url + "' onload='resize(this)'  scrolling='no' frameborder='0' width='100%'></iframe>";
  $('#main').html(iframe);
}

function resize(obj) {
  var height = $(obj.contentWindow.document).find("div[id='page-container']").height()
  $(obj).height(height);
  $(obj.parentElement).removeClass("lazy");
  //alert("loadViewPort by resize!height:"+height);
  loadViewPort();
}