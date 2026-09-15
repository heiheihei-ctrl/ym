package com.ruoyi.ym.domain.dto;

public class VerificationCodeBatchRequest
{
    /** 产品编码，如 yangmei / rice */
    private String productType;

    private String prefix;
    private Integer count;

    public String getProductType()
    {
        return productType;
    }

    public void setProductType(String productType)
    {
        this.productType = productType;
    }

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
