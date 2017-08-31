/**
 * Created by liuq on 2017/7/27.
 */
var sg = getQueryStringByName("sg");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
var page = getQueryStringByName("page");
var screenWidth = window.screen.width;//屏幕宽度
var screenHeight = window.screen.height;//屏幕高度；
var ua = navigator.userAgent;
var isIOS = /iphone|ipod|ipad/ig.test(ua);
$(function () {
  loadViewPort();
  loadData(page);
  window.addEventListener("orientationchange", function () {
    //alert("isIOS:"+isIOS);
    if (isIOS) {
      if (window.orientation != 0) {//横屏 宽度取大的值
        screenWidth = Math.max(window.screen.width, window.screen.height);
        screenHeight = Math.min(window.screen.width, window.screen.height);
      }
      else {//竖屏 宽度取晓得值
        screenWidth = Math.min(window.screen.width, window.screen.height);
        screenHeight = Math.max(window.screen.width, window.screen.height);
      }
    }
    else {
      screenWidth = $(window).width();
      screenHeight = $(window).height();
    }
    window.setTimeout(function () {
      loadViewPort();
    },200);
  }, true);
})


function loadViewPort() {
  //alert("width:"+width+",screenWidth:"+screenWidth);
  var scale = screenWidth * 0.99 / width;
  //alert("w:" + screenWidth + "/h:" + screenHeight + "/" + scale);
  var viewport = document.querySelector("meta[name=viewport]");
  viewport.content = 'width=' + screenWidth + ',initial-scale=' + scale;
  //alert(document.querySelector("meta[name=viewport]").getAttribute('content'));

}


function loadData(i) {
  var url = window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + i + "&pageCount=" + pageCount + "&sg=" + sg + "&ver=2.1";
  var iframe = "<iframe id='framePage' src='" + url + "' onload='resize(this)' onresize='resize(this)' scrolling='no' frameborder='0' width='100%'></iframe>";
  $('#main').html(iframe);
}

function resize(obj) {
  var height = $(obj.contentWindow.document).find("div[id='page-container']").height()
  $(obj).height(height + 20);
  $(obj.parentElement).removeClass("lazy");
}