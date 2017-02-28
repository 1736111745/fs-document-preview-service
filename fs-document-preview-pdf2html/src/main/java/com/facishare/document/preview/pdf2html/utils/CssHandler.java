package com.facishare.document.preview.pdf2html.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuq on 2017/2/21.
 */
@UtilityClass
public class CssHandler {

    public String reWrite(String cssContent, int pageIndex) throws IOException {
        String _temp_cssContent = cssContent;
        String regex = "\\.\\w+\\{.*}$";
        String idStr = "#pf" + pageIndex;
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(cssContent);
        List<String> w0List = Lists.newArrayList();
        List<String> h0List = Lists.newArrayList();
        while (matcher.find()) {
            String style = matcher.group();
            if (style.startsWith(".w0")) {
                w0List.add(style);
            } else if (style.startsWith(".h0")) {
                h0List.add(style);
            }
            String newStyle = idStr + " " + style;
            _temp_cssContent = _temp_cssContent.replace(style, newStyle);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(_temp_cssContent);
        String w0 = w0List.size() > 0 ? w0List.get(0) : "";
        String m_w0 = w0List.size() > 1 ? w0List.get(1) : "";
        String h0 = h0List.size() > 0 ? h0List.get(0) : "";
        String m_h0 = h0List.size() > 1 ? h0List.get(1) : "";
        if (!Strings.isNullOrEmpty(w0)) {
            w0 = w0.replace(".w0", ".ww" + pageIndex);
            stringBuilder.append("\r\n" + w0 + "\r\n");
        }
        if (!Strings.isNullOrEmpty(h0)) {
            h0 = h0.replace(".h0", ".hh" + pageIndex);
            stringBuilder.append(h0 + "\r\n");
        }
        stringBuilder.append("@media print{\r\n");
        if (!Strings.isNullOrEmpty(m_w0)) {
            m_w0 = m_w0.replace(".w0", ".ww" + pageIndex);
            stringBuilder.append(m_w0 + "\r\n");
        }
        if (!Strings.isNullOrEmpty(m_h0)) {
            m_h0 = m_h0.replace(".h0", ".hh" + pageIndex);
            stringBuilder.append(m_h0 + "\r\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

}
