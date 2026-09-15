package com.ruoyi.ym.utils;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

/**
 * 包装序号（查询码）区间校验
 */
public final class CodeRangeUtils
{
    private CodeRangeUtils()
    {
    }

    public static String[] normalizeRange(String startCode, String endCode)
    {
        if (StringUtils.isEmpty(startCode) || StringUtils.isEmpty(endCode))
        {
            throw new ServiceException("请填写起始与结束查询码");
        }
        String start = startCode.trim().toUpperCase();
        String end = endCode.trim().toUpperCase();
        if (start.length() != end.length())
        {
            throw new ServiceException("起始码与结束码长度须一致");
        }
        if (start.compareTo(end) > 0)
        {
            throw new ServiceException("起始码不能大于结束码");
        }
        String startPrefix = stripTrailingDigits(start);
        String endPrefix = stripTrailingDigits(end);
        if (!startPrefix.equals(endPrefix))
        {
            throw new ServiceException("区间须为同一前缀流水号，如 WM202605000001～WM202605000100");
        }
        return new String[] { start, end };
    }

    private static String stripTrailingDigits(String code)
    {
        int i = code.length();
        while (i > 0 && Character.isDigit(code.charAt(i - 1)))
        {
            i--;
        }
        return code.substring(0, i);
    }
}
