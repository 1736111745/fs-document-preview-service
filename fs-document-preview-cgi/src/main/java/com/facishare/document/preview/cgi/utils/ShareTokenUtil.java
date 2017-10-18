package com.facishare.document.preview.cgi.utils;

import com.facishare.document.preview.cgi.model.ShareTokenParamInfo;
import com.github.autoconf.ConfigFactory;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import org.apache.commons.lang3.math.NumberUtils;


/**
 * Created by liuq on 2017/8/30.
 */
public class ShareTokenUtil {
  private static long sharedTokenExpMills = 3600000;
  static {
    ConfigFactory.getInstance()
                 .getConfig("fs-fsc-cgi-config", config -> sharedTokenExpMills = config.getInt("shared_token_exp_mills",3600000));
  }
  public static ShareTokenParamInfo convertToken2ParamInfo(String shareToken) {
    boolean isValidToken = true;
    if (Strings.isNullOrEmpty(shareToken)) {
      isValidToken = false;
    }
    String avatarDetail = AES256Utils.decode(shareToken);
    String[] details = avatarDetail.split(":");
    if (details.length < 3) {
      isValidToken = false;
    }
    String ea = details[0];
    int employeeId = NumberUtils.toInt(details[1], 1000);
    String path = details[2];
    String securityGroup = null;
    if (details.length > 3) {
      securityGroup = details[3];
    }
    long createTime;
    if (details.length > 4) {
      createTime = Long.valueOf(details[4]);
      if (System.currentTimeMillis() - createTime > sharedTokenExpMills) {
        isValidToken = false;
      }
    }
    if (isValidToken) {
      ShareTokenParamInfo shareTokenParamInfo = new ShareTokenParamInfo();
      shareTokenParamInfo.setEa(ea);
      shareTokenParamInfo.setEmployeeId(employeeId);
      shareTokenParamInfo.setPath(path);
      shareTokenParamInfo.setSecurityGroup(securityGroup);
      return shareTokenParamInfo;
    } else {
      return null;
    }
  }

  public static void main(String[] args) {

    String ea = "2";
    int employeeId = 10000;
    String path = "N_201708_31_83dbd03f1316468ab24078db428de4b0.pptx";
    String aes = AES256Utils.encode(Joiner.on(':').join(ea, employeeId, path, "", System.currentTimeMillis()));
    System.out.println(aes);
  }
}
