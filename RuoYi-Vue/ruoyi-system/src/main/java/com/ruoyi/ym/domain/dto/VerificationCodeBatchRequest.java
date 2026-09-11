package com.ruoyi.ym.domain.dto;

public class VerificationCodeBatchRequest
{
    private String prefix;
    private Integer count;

    public String getPrefix()
    {
        return prefix;
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public Integer getCount()
    {
        return count;
    }

    public void setCount(Integer count)
    {
        this.count = count;
    }
}
