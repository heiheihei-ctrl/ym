package com.ruoyi.ym.utils;

import java.io.File;
import java.io.FileOutputStream;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;

/**
 * 生成二维码图片（内容为扫码后要打开的公开查验页 URL）
 */
public final class YmQrCodeHelper
{
    private YmQrCodeHelper()
    {
    }

    /**
     * @param scanTargetUrl 扫码后跳转的地址（公开查验页，带查询码参数）
     * @param fileKey       文件名标识（如查询码 WM202605000001）
     * @return 二维码图片相对路径（存入 verification.code_url）
     */
    public static String saveQrImage(String scanTargetUrl, String fileKey)
    {
        if (StringUtils.isEmpty(scanTargetUrl))
        {
            throw new ServiceException("无法生成二维码：目标地址为空");
        }
        try
        {
            String safeKey = StringUtils.isEmpty(fileKey) ? "cert" : fileKey.replaceAll("[^a-zA-Z0-9_-]", "");
            byte[] png;
            try
            {
                png = QrCodeUtils.generateLabeledQrPngBytes(scanTargetUrl, fileKey);
            }
            catch (IllegalStateException e)
            {
                throw new ServiceException(e.getMessage());
            }
            String uploadDir = RuoYiConfig.getUploadPath();
            String pathName = "qrcode/" + DateUtils.datePath() + "/" + safeKey + ".png";
            File file = FileUploadUtils.getAbsoluteFile(uploadDir, pathName);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists())
            {
                parent.mkdirs();
            }
            try (FileOutputStream fos = new FileOutputStream(file))
            {
                fos.write(png);
            }
            return FileUploadUtils.getPathFileName(uploadDir, pathName);
        }
        catch (Exception e)
        {
            throw new ServiceException("生成二维码失败：" + e.getMessage());
        }
    }
}
