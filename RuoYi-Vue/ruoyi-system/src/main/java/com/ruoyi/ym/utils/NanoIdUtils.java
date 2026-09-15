package com.ruoyi.ym.utils;

import java.security.SecureRandom;

/**
 * 生成不可猜测的查询码（类 NanoID，大写字母+数字，避开易混字符）
 */
public final class NanoIdUtils
{
    /** 去掉 0/O/1/I 等易混字符 */
    private static final char[] ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();

    private static final SecureRandom RANDOM = new SecureRandom();

    /** 随机段默认长度 */
    public static final int DEFAULT_SIZE = 12;

    private NanoIdUtils()
    {
    }

    public static String randomId()
    {
        return randomId(DEFAULT_SIZE);
    }

    public static String randomId(int size)
    {
        if (size < 8 || size > 32)
        {
            throw new IllegalArgumentException("size must be 8..32");
        }
        char[] buf = new char[size];
        byte[] bytes = new byte[size];
        RANDOM.nextBytes(bytes);
        for (int i = 0; i < size; i++)
        {
            buf[i] = ALPHABET[(bytes[i] & 0xFF) % ALPHABET.length];
        }
        return new String(buf);
    }
}
