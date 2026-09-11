package com.ruoyi.ym.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.StringUtils;

/**
 * 构建对外可访问的资源 URL（扫码跳转见证书图片等）
 */
@Component
public class YmPublicUrlUtils
{
    @Value("${ruoyi.publicSiteUrl:}")
    private String publicSiteUrl;

    @Value("${ruoyi.publicVerifyUrl:}")
    private String publicVerifyUrl;

    /**
     * 将上传返回路径（如 /profile/upload/...）转为可访问的完整 URL
     */
    public String buildPublicResourceUrl(String resourcePath)
    {
        return joinBase(publicSiteUrl, resourcePath);
    }

    /**
     * 扫码打开的见证书页 URL（scan.html：查看图片 + 下载 PDF 提示）
     */
    public String buildPublicScanPageUrl(String verificationCode)
    {
        if (StringUtils.isEmpty(verificationCode))
        {
            return "";
        }
        String base = StringUtils.isNotEmpty(publicVerifyUrl) ? publicVerifyUrl.trim() : publicSiteUrl.trim();
        if (base.endsWith("/"))
        {
            base = base.substring(0, base.length() - 1);
        }
        String code = URLEncoder.encode(verificationCode.trim(), StandardCharsets.UTF_8);
        if (StringUtils.isEmpty(base))
        {
            return "/scan.html?code=" + code;
        }
        return base + "/scan.html?code=" + code;
    }

    private String joinBase(String baseUrl, String resourcePath)
    {
        if (StringUtils.isEmpty(resourcePath))
        {
            return "";
        }
        String path = resourcePath.trim();
        if (path.startsWith("http://") || path.startsWith("https://"))
        {
            return path;
        }
        if (!path.startsWith("/"))
        {
            path = "/" + path;
        }
        String base = StringUtils.isEmpty(baseUrl) ? "" : baseUrl.trim();
        if (base.endsWith("/"))
        {
            base = base.substring(0, base.length() - 1);
        }
        if (StringUtils.isEmpty(base))
        {
            return path;
        }
        return base + path;
    }
}
