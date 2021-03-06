var sg = getQueryStringByName("sg");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
var jumpUrl = getQueryStringByName("jumpUrl");
var pdfConvertType = getQueryStringByName("pdfConvertType");
var filePathList = [];//已经转换完毕的页码.html
var loadedList = [];//用户已经滑动过的页码
var pageLoadedList = [];//用户已经加载的页码
var timeout = 100000;
var width = parseInt(getQueryStringByName("width"));
var sharetoken = getQueryStringByName("shareToken");
var screenWidth = window.screen.width;//屏幕宽度
var screenHeight = window.screen.height;//屏幕高度；
var ua = navigator.userAgent;
var isIOS = /iphone|ipod|ipad/ig.test(ua);
$(function () {
  if (jumpUrl != '') {
    jumpUrl = decodeURIComponent(jumpUrl);
    $("#jumpUrl").attr("href", jumpUrl);
    $("#sure-wrapper").show();
  }
  else {
    $("#sure-wrapper").hide();
  }
  checkConvertTimeout();
  checkPdf2HtmlStatus();
  loadAllPages();
  checkConvertStatus();
  checkPageLoaded();
  loadViewPort();
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
    }, 200);
  }, true);
});


function loadAllPages() {
  for (var i = 0; i < pageCount; i++) {
    var div = $("<div id='divPage" + i + "' class='lazy border' data-page-no='" + i + "' data-loader='pageLoader'>");
    var nav = $("<div class='center'><span>- " + (i + 1) + "页 / " + pageCount + "页 -</span></div>");
    $('#main').append(div).append(nav);
    loadPageLoader();
  }
}


function loadViewPort() {
  //alert("width:"+width+",screenWidth:"+screenWidth);
  var scale = screenWidth * 0.96 / width;
  //alert("w:" + screenWidth + "/h:" + screenHeight + "/" + scale);
  var viewport = document.querySelector("meta[name=viewport]");
  viewport.content = 'width=' + screenWidth + ',initial-scale=' + scale;
  //alert(document.querySelector("meta[name=viewport]").getAttribute('content'));

}

function loadPageLoader() {
  $('.lazy').lazy({
    pageLoader: function (element) {
      var pageIndex = parseInt(element.attr("data-page-no"));
      if ($.inArray(pageIndex, loadedList) == -1) {
        loadedList.push(pageIndex);
      }
      loadData(pageIndex);
      console.log("page " + pageIndex + " loaded!")
    }
  });
}

var idChkPageLoaded;

//定时检查用户滑动过的页码，如果用户页码都加载完毕了就停止检查
function checkPageLoaded() {
  idChkPageLoaded = setInterval(function () {
    for (var i = 0; i < loadedList.length; i++) {
      var index = loadedList[i];
      loadData(index);
    }
    if (pageLoadedList.length == pageCount) {
      console.log("all page loaded!")
      clearInterval(idChkPageLoaded);
    }
  }, 100);
}

function loadData(i) {
  var htmlName = (i + 1) + ".html";
  var imageName = (i + 1) + ".png";
  var resultName = pdfConvertType == 0 ? htmlName : imageName;
  if ($.inArray(resultName, filePathList) >= 0) {
    var url = window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + i + "&pageCount=" + pageCount + "&sg=" + sg + "&width=" + width + "&sharetoken=" + sharetoken + "&ver=2.1";
    var iframeId = 'frame' + i;
    var divPageId = 'divPage' + i;
    var element = $('#' + divPageId);
    if ($('#' + iframeId).length == 0) {
      $("<iframe id='" + iframeId + "' src='" + url + "' onload='resize(this)' scrolling='no' frameborder='0' width='100%'></iframe>").prependTo(element);
      element.load();
      if ($.inArray(i, pageLoadedList) == -1) {
        pageLoadedList.push(i);
      }
    }
  }
}

function resize(obj) {
  var height = $(obj.contentWindow.document).find("div[id='page-container']").height()
  if (pdfConvertType == 1) {
    height = $(obj.contentWindow.document).find("img").height();
  }
  $(obj).height(height);
  $(obj.parentElement).removeClass("lazy");
}

var idChkConvertStatus;

//定时监测转换状态，当全部转换完毕停止检测
function checkConvertStatus() {
  idChkConvertStatus = setInterval(function () {
    if (queryPdf2HtmlStatus()) {
      clearInterval(idChkConvertStatus);
    }
  }, 500);
}

function checkPdf2HtmlStatus() {
  var url = window.contextPath + '/preview/checkPdf2HtmlStatus?path=' + path + "&sg=" + sg + "&width=" + width + "&sharetoken=" + sharetoken;
  $.get(url);
}

function queryPdf2HtmlStatus() {
  var flag = false;//是否转换完毕
  var url = window.contextPath + '/preview/queryPdf2HtmlStatus?path=' + path + "&sg=" + sg + "&width=" + width + "&sharetoken=" + sharetoken;
  $.ajax({
    type: 'get',
    dataType: 'json',
    async: false,
    url: url,
    success: function (data) {
      filePathList = data.list;
      flag = (filePathList.length == pageCount);
    }
  });
  return flag;
}

var idChkConvertTimeout;//超时后还没有加载完毕就提示预览超时。同时停止查询轮询和检测页码轮询方法。
function checkConvertTimeout() {
  idChkConvertTimeout = setTimeout(function () {
    clearInterval(idChkConvertStatus);
    for (var i = 0; i < pageCount; i++) {
      var htmlName = (i + 1) + ".html";
      var imageName = (i + 1) + ".png";
      var resultName = pdfConvertType == 0 ? htmlName : imageName;
      if ($.inArray(resultName, filePathList) == -1) {
        var iframeId = 'frame' + i;
        var divPageId = 'divPage' + i;
        var element = $('#' + divPageId);
        if ($('#' + iframeId).length == 0) {
          var spanMsg = $("<span>该页面暂时无法预览，请稍后刷新重试！</span>");
          element.removeClass("lazy");
          element.append(spanMsg).load();
        }
      }
    }
  }, timeout)
}


