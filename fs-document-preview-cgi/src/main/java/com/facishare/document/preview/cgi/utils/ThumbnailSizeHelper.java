package com.facishare.document.preview.cgi.utils;

import com.facishare.document.preview.cgi.model.ImageSize;
import com.fxiaoke.common.image.SimpleImageInfo;

import java.io.File;
import java.io.IOException;

/**
 * 算法参考微信缩略图的处理
 * a，图片宽或者高均小于或等于1280时图片尺寸保持不变，但仍然经过图片压缩处理，得到小文件的同尺寸图片
 * b，宽或者高大于1280，但是图片宽度高度比小于或等于2，则将图片宽或者高取大的等比压缩至1280
 * c，宽或者高大于1280，但是图片宽高比大于2时，并且宽以及高均大于1280，则宽或者高取小的等比压缩至1280
 * d，宽或者高大于1280，但是图片宽高比大于2时，并且宽或者高其中一个小于1280，则压缩至同尺寸的小文件图片
 */
public class ThumbnailSizeHelper {
    public static ImageSize getProcessedSize(String imageFilePath) throws IOException {
        SimpleImageInfo pngImageInfo = new SimpleImageInfo(new File(imageFilePath));
        int originalWidth = pngImageInfo.getWidth();
        int originalHeight = pngImageInfo.getHeight();
        int width = originalWidth, height = originalHeight;
        if (originalHeight > 1280 || originalWidth > 1280) {
            if (originalWidth / (originalHeight * 1.0f) <= 2) {
                if (originalWidth > originalHeight) {
                    originalWidth = 1280;
                    width = originalWidth;
                    height = width * originalHeight / originalWidth;

                } else {
                    originalHeight = 1280;
                    height = originalHeight;
                    width = originalWidth * height / originalHeight;
                }
            } else {
                if (originalHeight > 1280 && originalWidth > 1280) {
                    if (originalHeight <= originalWidth) {
                        originalHeight = 1280;
                        height = originalHeight;
                        width = originalWidth * height / originalHeight;
                    } else {
                        originalWidth = 1280;
                        width = originalWidth;
                        height = width * originalHeight / originalWidth;
                    }
                } else if (originalHeight < 1280) {
                    height = originalHeight;
                    width = originalWidth * height / originalHeight;
                } else if (originalWidth < 1280) {
                    width = originalWidth;
                    height = width * originalHeight / originalWidth;
                }
            }
        }
        return new ImageSize(width, height);
    }
}
