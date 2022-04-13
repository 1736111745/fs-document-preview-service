package com.facishare.document.preview.convert.office.service;

import com.facishare.document.preview.convert.office.utils.GetConvertResult;
import com.facishare.document.preview.convert.office.utils.GetPageInfo;
import com.facishare.document.preview.convert.office.utils.GetResponse;
import com.facishare.document.preview.convert.office.utils.ParameterCalibration;
import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Andy
 */
@Getter
@Service
public class GetUtilObject {

  @Resource
  private GetResponse response;

  @Resource
  private GetConvertResult convertResult;

  @Resource
  private ParameterCalibration parameterCalibration;

  @Resource
  private GetPageInfo getPageInfo;

}
