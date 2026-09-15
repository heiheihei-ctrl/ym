package com.ruoyi.ym.domain;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;

/**
 * 查询码 verification 表
 */
public class VerificationCode implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 绑定状态：未绑定 */
    public static final int BIND_STATUS_UNBOUND = 0;
    /** 绑定状态：已绑定证书 */
    public static final int BIND_STATUS_BOUND = 1;

    private Integer id;

    @Excel(name = "查询码")
    private String code;

    /** 扫码二维码图片路径 */
    private String codeUrl;

    /** 产品编码 yangmei / rice */
    @Excel(name = "产品类型")
    private String productType;

    /** 绑定的见证书 ID */
    private Integer certificateId;

    /**
     * 0未绑定 1已绑定
     */
    @Excel(name = "绑定状态", readConverterExp = "0=未绑定,1=已绑定")
    private Integer bindStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date boundAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @Excel(name = "创建人")
    private String createBy;

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

    public String getProductType()
    {
        return productType;
    }

    public void setProductType(String productType)
    {
        this.productType = productType;
    }

    public Integer getCertificateId()
    {
        return certificateId;
    }

    public void setCertificateId(Integer certificateId)
    {
        this.certificateId = certificateId;
    }

    public Integer getBindStatus()
    {
        return bindStatus;
    }

    public void setBindStatus(Integer bindStatus)
    {
        this.bindStatus = bindStatus;
    }

    public Date getBoundAt()
    {
        return boundAt;
    }

    public void setBoundAt(Date boundAt)
    {
        this.boundAt = boundAt;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public String getCreateBy()
    {
        return createBy;
    }

    public void setCreateBy(String createBy)
    {
        this.createBy = createBy;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("codeUrl", getCodeUrl())
            .append("certificateId", getCertificateId())
            .append("bindStatus", getBindStatus())
            .append("boundAt", getBoundAt())
            .toString();
    }
}
