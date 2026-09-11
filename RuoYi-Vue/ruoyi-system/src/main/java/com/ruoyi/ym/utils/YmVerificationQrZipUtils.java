package com.ruoyi.ym.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.ImageUtils;
import com.ruoyi.ym.domain.VerificationCode;

/**
 * 查询码扫码二维码多选打包 ZIP
 */
public final class YmVerificationQrZipUtils
{
    private YmVerificationQrZipUtils()
    {
    }

    public static byte[] buildZip(List<VerificationCode> rows) throws IOException
    {
        if (rows == null || rows.isEmpty())
        {
            throw new ServiceException("未找到所选查询码");
        }
        int fileCount = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos, StandardCharsets.UTF_8))
        {
            for (VerificationCode row : rows)
            {
                fileCount += addQrCode(zos, row);
            }
            zos.finish();
        }
        if (fileCount == 0)
        {
            throw new ServiceException("所选查询码没有可下载的二维码图片");
        }
        return baos.toByteArray();
    }

    private static int addQrCode(ZipOutputStream zos, VerificationCode row) throws IOException
    {
        String baseName = safeFileName(StringUtils.isNotEmpty(row.getCode())
            ? row.getCode()
            : ("查询码_" + row.getId()));
        String entryName = baseName + extOrDefault(row.getCodeUrl(), ".png");
        return putFile(zos, entryName, row.getCodeUrl());
    }

    private static int putFile(ZipOutputStream zos, String entryName, String resourcePath) throws IOException
    {
        if (StringUtils.isEmpty(resourcePath))
        {
            return 0;
        }
        byte[] data = ImageUtils.readFile(resourcePath);
        if (data == null || data.length == 0)
        {
            return 0;
        }
        ZipEntry entry = new ZipEntry(entryName);
        zos.putNextEntry(entry);
        zos.write(data);
        zos.closeEntry();
        return 1;
    }

    private static String safeFileName(String name)
    {
        return name.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private static String extOrDefault(String path, String defaultExt)
    {
        if (StringUtils.isEmpty(path))
        {
            return defaultExt;
        }
        int dot = path.lastIndexOf('.');
        if (dot < 0 || dot >= path.length() - 1)
        {
            return defaultExt;
        }
        String ext = path.substring(dot).toLowerCase();
        if (ext.length() > 8)
        {
            return defaultExt;
        }
        return ext;
    }
}
