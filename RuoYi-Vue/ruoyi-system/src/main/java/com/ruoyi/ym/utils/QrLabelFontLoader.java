package com.ruoyi.ym.utils;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

/**
 * 加载二维码标签用中文字体（优先 classpath/fonts，其次 itext-asian 内置字体）
 */
final class QrLabelFontLoader
{
    private static final String[] FONT_RESOURCE_PATHS = {
        "fonts/SourceHanSansCN-Regular.otf",
        "fonts/NotoSansSC-Regular.otf",
        "fonts/DroidSansFallback.ttf",
        "fonts/LabelCN.ttf"
    };

    private static volatile Font baseFont;

    private QrLabelFontLoader()
    {
    }

    static Font getFont(int size)
    {
        if (baseFont == null)
        {
            synchronized (QrLabelFontLoader.class)
            {
                if (baseFont == null)
                {
                    baseFont = loadBaseFont();
                }
            }
        }
        return baseFont.deriveFont(Font.PLAIN, size);
    }

    private static Font loadBaseFont()
    {
        ClassLoader loader = QrLabelFontLoader.class.getClassLoader();
        for (String path : FONT_RESOURCE_PATHS)
        {
            Font font = tryLoad(loader, path);
            if (font != null)
            {
                return font;
            }
        }
        throw new IllegalStateException(
            "无法加载中文字体，请确认 ruoyi-system 模块 resources/fonts/SourceHanSansCN-Regular.otf 已随 jar 打包部署");
    }

    private static Font tryLoad(ClassLoader loader, String path)
    {
        InputStream in = null;
        try
        {
            in = loader.getResourceAsStream(path);
            if (in == null)
            {
                in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            }
            if (in == null)
            {
                return null;
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, in);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font;
        }
        catch (Exception ignored)
        {
            return null;
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (Exception ignored)
                {
                }
            }
        }
    }
}
