package com.ruoyi.ym.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * 见证书公开查验结果
 */
public class CertificatePublicVerifyVO
{
    /** 是否可展示证书详情（已激活） */
    private boolean verified;

    /** NOT_FOUND / CODE_ONLY / PENDING / VERIFIED */
    private String resultType;

    private String message;

    private String code;

    private Integer status;

    private String statusLabel;

    private String origin;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date pickDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date packDate;

    private String lawyerName;

    private String certFile;

    /** 见证书图片（移动端预览） */
    private String certImage;

    private String codeUrl;

    /** 基地详情链接 */
    private String baseDetailUrl;

    public boolean isVerified()
    {
        return verified;
    }

    public void setVerified(boolean verified)
    {
        this.verified = verified;
    }

    public String getResultType()
    {
        return resultType;
    }

    public void setResultType(String resultType)
    {
        this.resultType = resultType;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public String getStatusLabel()
    {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel)
    {
        this.statusLabel = statusLabel;
    }

    public String getOrigin()
    {
        return origin;
    }

    public void setOrigin(String origin)
    {
        this.origin = origin;
    }

    public Date getPickDate()
    {
        return pickDate;
    }

    public void setPickDate(Date pickDate)
    {
        this.pickDate = pickDate;
    }

    public Date getPackDate()
    {
        return packDate;
    }

    public void setPackDate(Date packDate)
    {
        this.packDate = packDate;
    }

    public String getLawyerName()
    {
        return lawyerName;
    }

    public void setLawyerName(String lawyerName)
    {
        this.lawyerName = lawyerName;
    }

    public String getCertFile()
    {
        return certFile;
    }

    public void setCertFile(String certFile)
    {
        this.certFile = certFile;
    }

    public String getCertImage()
    {
        return certImage;
    }

    public void setCertImage(String certImage)
    {
        this.certImage = certImage;
    }

    public String getCodeUrl()
    {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl)
    {
        this.codeUrl = codeUrl;
    }

    public String getBaseDetailUrl()
    {
        return baseDetailUrl;
    }

    public void setBaseDetailUrl(String baseDetailUrl)
    {
        this.baseDetailUrl = baseDetailUrl;
    }
}
