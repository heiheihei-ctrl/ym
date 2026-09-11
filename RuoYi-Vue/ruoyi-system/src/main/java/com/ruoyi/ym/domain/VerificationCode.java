package com.ruoyi.ym.domain;

import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;

/**
 * 查询码 verification 表（id / code）
 */
public class VerificationCode implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Integer id;

    @Excel(name = "查询码")
    private String code;

    /** 扫码二维码图片路径 */
    private String codeUrl;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCodeUrl()
    {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl)
    {
        this.codeUrl = codeUrl;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("codeUrl", getCodeUrl())
            .toString();
    }
}
