package com.ruoyi.ym.utils;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

/**
 * 律师执业证号校验（17位数字）
 */
public final class LawyerNoUtils
{
    private LawyerNoUtils()
    {
    }

    public static void validate(String lawyerNo)
    {
        if (StringUtils.isEmpty(lawyerNo))
        {
            throw new ServiceException("执业证号不能为空");
        }
        String no = lawyerNo.trim();
        if (!no.matches("\\d{17}"))
        {
            throw new ServiceException("执业证号须为17位数字");
        }
    }
}
