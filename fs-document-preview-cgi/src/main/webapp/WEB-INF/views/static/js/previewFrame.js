/**
 * Created by liuq on 2017/7/27.
 */
var sg = getQueryStringByName("sg");
var pageCount = getQueryStringByName("pageCount");
var path = getQueryStringByName("path");
var page = getQueryStringByName("page");

$(function () {
  loadViewPort();
  loadData(page);
  $(window).resize(function () {
    loadViewPort();
  });
})

function loadViewPort() {
  var docWidth = $(window).width();
  var scale = docWidth * 0.96 / width;
  var viewport = document.querySelector("meta[name=viewport]");
  viewport.setAttribute('content', 'initial-scale=' + scale + ', width=' + docWidth + 'px');
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