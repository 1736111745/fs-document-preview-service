var sg = getQueryStringByName("sg");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
var filePathList = [];//已经转换完毕的页码.html
var loadedList = [];//用户已经滑动过的页码
var pageLoadedList = [];//用户已经加载的页码
var timeout = 100000;
var width = parseInt(getQueryStringByName("width"));
$(function () {
  loadViewPort();
  checkConvertTimeout();
  checkPdf2HtmlStatus();
  loadAllPages();
  checkConvertStatus();
  checkPageLoaded();
  //ios的旋转方法和android不一样
  console.log(navigator.userAgent);
  if (/(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent)) {
    console.log("this is apple device!")
    $(window).on("orientationchange", function (event) {
      console.log('orientationchange: ' + event.orientation);
      loadViewPort();
    });
  }
  else {
    $(window).resize(function () {
      loadViewPort();
    });
  }
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
  var docWidth = $(window).width();
  var scale = docWidth * 0.96 / width;
  var viewport = document.querySelector("meta[name=viewport]");
  viewport.setAttribute('content', 'initial-scale=' + scale + ', width=' + docWidth + 'px');
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
  if ($.inArray(htmlName, filePathList) >= 0) {
    var url = window.contextPath + '/preview/getFilePath?path=' + path + '&page=' + i + "&pageCount=" + pageCount + "&sg=" + sg + "&ver=2.1";
    var iframeId = 'frame' + i;
    var divPageId = 'divPage' + i;
    var element = $('#' + divPageId);
    if ($('#' + iframeId).length == 0) {
      $("<iframe id='" + iframeId + "' src='" + url + "' onload='resize(this)' onresize='resize(this)' scrolling='no' frameborder='0' width='100%'></iframe>").prependTo(element);
      element.load();
      if ($.inArray(i, pageLoadedList) == -1) {
        pageLoadedList.push(i);
      }
    }
  }
}

function resize(obj) {
  var height = $(obj.contentWindow.document).find("div[id='page-container']").height()
  $(obj).height(height + 20);
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
  var url = window.contextPath + '/preview/checkPdf2HtmlStatus?path=' + path + "&sg=" + sg + "&width=" + width;
  $.get(url);
}

function queryPdf2HtmlStatus() {
  var flag = false;//是否转换完毕
  var url = window.contextPath + '/preview/queryPdf2HtmlStatus?path=' + path + "&sg=" + sg
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
      if ($.inArray(htmlName, filePathList) == -1) {
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


