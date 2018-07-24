/**
 * Created by liuq on 16/9/29.
 */
//获取queryString的值
function getQueryStringByName(name) {
  var result = location.search.match(new RegExp("[\?\&]" + name + "=([^\&]+)", "i"));
  if (result == null || result.length < 1) {
    return "";
  }
  return result[1];
}

String.prototype.endWith = function (str) {
  if (str == null || str == "" || this.length == 0 || str.length > this.length)
    return false;
  if (this.substring(this.length - str.length) == str)
    return true;
  else
    return false;
  return true;
}

function getFileExt(file_name) {
  var index1 = file_name.lastIndexOf(".");
  var index2 = file_name.length;
  return file_name.substring(index1, index2);//
}