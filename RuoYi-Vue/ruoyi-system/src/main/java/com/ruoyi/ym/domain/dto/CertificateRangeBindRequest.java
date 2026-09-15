package com.ruoyi.ym.domain.dto;

/**
 * 将多个查询码绑定到见证书
 */
public class CertificateRangeBindRequest
{
    /** 已存在的证书 ID */
    private Integer certificateId;

    /** 要绑定的查询码 ID 列表（推荐） */
    private Integer[] verificationCodeIds;

    /** 兼容旧：起始查询码 */
    private String startCode;

    /** 兼容旧：结束查询码 */
    private String endCode;

    public Integer getCertificateId()
    {
        return certificateId;
    }

    public void setCertificateId(Integer certificateId)
    {
        this.certificateId = certificateId;
    }

    public Integer[] getVerificationCodeIds()
    {
        return verificationCodeIds;
    }

    public void setVerificationCodeIds(Integer[] verificationCodeIds)
    {
        this.verificationCodeIds = verificationCodeIds;
    }

    public String getStartCode()
    {
        return startCode;
    }

    public void setStartCode(String startCode)
    {
        this.startCode = startCode;
    }

    public String getEndCode()
    {
        return endCode;
    }

    public void setEndCode(String endCode)
    {
        this.endCode = endCode;
    }
}
