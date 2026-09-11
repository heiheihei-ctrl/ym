package com.ruoyi.ym.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import com.ruoyi.common.utils.StringUtils;

/**
 * 见证书查询码规范化（公开查验：不区分大小写，O/0 等易混字符纠错）
 */
public final class VerificationCodeUtils
{
    private VerificationCodeUtils()
    {
    }

    public static String normalize(String raw)
    {
        if (StringUtils.isEmpty(raw))
        {
            return "";
        }
        return raw.trim().toUpperCase().replace('O', '0');
    }

    /**
     * 生成待匹配的查询码候选（去重、保持顺序）
     */
    public static List<String> buildCandidates(String raw)
    {
        Set<String> set = new LinkedHashSet<>();
        if (StringUtils.isEmpty(raw))
        {
            return new ArrayList<>();
        }
        String trimmed = raw.trim();
        String upper = trimmed.toUpperCase();
        String oToZero = upper.replace('O', '0');
        String zeroToO = upper.replace('0', 'O');
        set.add(upper);
        set.add(oToZero);
        if (!zeroToO.equals(upper))
        {
            set.add(zeroToO);
        }
        set.add(trimmed);
        set.remove("");
        return new ArrayList<>(set);
    }
}
