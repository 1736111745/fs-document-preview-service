<%-- Copyright (c) Microsoft Corporation. All rights reserved. --%>

<%@ Register Tagprefix="Ewa" Namespace="Microsoft.Office.Excel.WebUI"
             Assembly="Microsoft.Office.Excel.WebUI.Internal, Version=16.0.0.0, Culture=neutral, PublicKeyToken=71e9bce111e9429c" %>

<%@ Assembly
        Name="Microsoft.Office.Excel.WebUI.Internal, Version=16.0.0.0, Culture=neutral, PublicKeyToken=71e9bce111e9429c" %>
<%@ Page language="C#" Codebehind="XlViewerInternal.aspx.cs" AutoEventWireup="false"
         Inherits="Microsoft.Office.Excel.WebUI.XlViewerInternal,Microsoft.Office.Excel.WebUI.Internal,Version=16.0.0.0,Culture=neutral,PublicKeyToken=71e9bce111e9429c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" dir="<%= HtmlDirection%>">
<head runat="server">
  <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
  <meta name='viewport' content='width=device-width, initial-scale=1'/>
  <link rel="shortcut icon" type="image/vnd.microsoft.icon" id="m_shortcutIcon"/>
  <script type="text/javascript">
    document.domain = "ceshi112.com"
    window.addEventListener('message', function (event) {
      console.log('received response:  ', event, event.data);
    }, false);
  </script>
  <style type="text/css"> html, body {
      overflow: hidden;
      margin: 0;
      padding: 0;
  }

  body, div.ewaContainer {
      position: absolute;
      top: 0;
      right: 0;
      bottom: 0;
      left: 0;
  }

  div.ewaContainer {
      overflow: hidden;
      top: 0;
  } </style>
  <script type="text/javascript"> window._Stw || (window._Stw = {});
  _Stw.nt = function () {
    var p = window.performance;
    var t = (p && p.now && Math.floor(p.now())) || new Date().getTime();
    return {m: t};
  };
  _Stw.app = new Date();
  _Stw.rap = _Stw.nt();
  _Stw.swc = [];
  _Stw.swpc = {};
  _Stw.ssw = function (o, t) {
    return {s: _Stw.nt(), o: o, t: t};
  };
  _Stw.esw = function (sw) {
    if (sw) {
      sw.e = _Stw.nt();
      _Stw.swc.push(sw);
    }
  };
  _Stw.spsw = function (o, t) {
    if (typeof (_Stw.swpc[o + t]) === 'undefined') {
      _Stw.swpc[o + t] = _Stw.ssw(o, t);
    }
  };
  var bsqmXLSPageLoadStartTime = _Stw.app; </script>
</head>

<body>
<![if gte IE 8]>
<div id="load_back" role="progressbar" aria-label="loadingLabel">
  <div id="loadingLabel" role="alert" aria-live="assertive" aria-atomic="true"
       style="overflow: hidden; max-height: 0px; max-width: 0px; z-index:-1;"></div>
</div>
<div id="load_img" role="presentation" aria-hidden="true">
  <div class="load_center">
    <span role="presentation" align="absmiddle" class="load_logo_xls"></span>
    <br/> <!-- Loading image --> <span role="presentation" align="absmiddle" class="load_splash"></span></div>
</div>
<![endif]>
<script type="text/javascript"> window._Stw && _Stw.spsw("LoadingUIShown", "SplashScreen"); </script>

<script type="text/javascript"> window.g_firstByte = window.__startTime = new Date();
window._Stw && _Stw.spsw("LoadResource", "<%= EwaCoreScriptName %>"); </script>
<asp:Placeholder runat="server" ID="m_ewaParallelScriptLoaderPlaceHolder"/>
<div id="applicationOuterContainer" aria-hidden="true" aria-busy="true">
  <form runat="server">
    <noscript class="<%= EwaRootNoscript %>">
      <%= NoScriptAlertEncoded %>
    </noscript>
    <div id="ewaContainer" class="ewaContainer">
      <Ewa:ExcelWebRendererInternal id="m_excelWebRenderer" runat="server"/>
    </div>
  </form>
</div>
</body>
</html>
