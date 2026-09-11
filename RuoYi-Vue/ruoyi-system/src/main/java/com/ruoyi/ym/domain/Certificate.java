package com.ruoyi.ym.domain;



import java.io.Serializable;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.apache.commons.lang3.builder.ToStringStyle;

import com.ruoyi.common.annotation.Excel;



/**

 * 证书对象 certificates

 */

public class Certificate implements Serializable

{

    private static final long serialVersionUID = 1L;



    /** 主键 */

    private Integer id;



    /** 提交见证时选择的 verification.id（接口传参，写入 certificates.code） */

    private Integer verificationCodeId;



    /** 查询码记录 id（certificates.code，关联 verification.id） */

    private Integer code;



    /** 查询码文本（关联查询 verification.code，非 certificates 表字段） */

    @Excel(name = "查询码")

    private String verificationCode;



    /** 扫码二维码图片路径（列表/详情来自关联查询码 verification.code_url） */

    private String codeUrl;



    /** 见证书图片路径（certificates.cert_image，扫码跳转目标） */

    @Excel(name = "见证书图片")

    private String certImage;



    /** 状态：0未使用 1未激活 2已激活 3已删除 */

    @Excel(name = "状态", readConverterExp = "0=未使用,1=未激活,2=已激活,3=已删除")

    private Integer status;



    /** 产地来源 */

    @Excel(name = "产地来源")

    private String origin;



    /** 基地详情链接（扫码页「查看该基地详情」） */

    private String baseDetailUrl;



    /** 采摘日期 */

    @JsonFormat(pattern = "yyyy-MM-dd")

    @Excel(name = "采摘日期", width = 20, dateFormat = "yyyy-MM-dd")

    private Date pickDate;



    /** 包装日期 */

    @JsonFormat(pattern = "yyyy-MM-dd")

    @Excel(name = "包装日期", width = 20, dateFormat = "yyyy-MM-dd")

    private Date packDate;



    /** 见证律师姓名 */

    @Excel(name = "见证律师")

    private String lawyerName;



    /** 证书照片/文件路径 */

    private String certFile;



    /** 上传时间 */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

    @Excel(name = "上传时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")

    private Date uploadAt;



    /** 上传者IP */

    @Excel(name = "上传IP")

    private String uploadIp;



    /** 审核人 */

    @Excel(name = "审核人")

    private String auditBy;



    /** 审核时间 */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")

    private Date auditAt;



    /** 驳回原因 */

    @Excel(name = "驳回原因")

    private String rejectReason;



    /** 创建时间 */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")

    private Date createdAt;



    /** 查询IP日志（防滥用） */

    private String ipLog;



    public Integer getId()

    {

        return id;

    }



    public void setId(Integer id)

    {

        this.id = id;

    }



    public Integer getVerificationCodeId()

    {

        return verificationCodeId;

    }



    public void setVerificationCodeId(Integer verificationCodeId)

    {

        this.verificationCodeId = verificationCodeId;

    }



    public Integer getCode()

    {

        return code;

    }



    public void setCode(Integer code)

    {

        this.code = code;

    }



    public String getVerificationCode()

    {

        return verificationCode;

    }



    public void setVerificationCode(String verificationCode)

    {

        this.verificationCode = verificationCode;

    }



    public String getCodeUrl()

    {

        return codeUrl;

    }



    public void setCodeUrl(String codeUrl)

    {

        this.codeUrl = codeUrl;

    }



    public Integer getStatus()

    {

        return status;

    }



    public void setStatus(Integer status)

    {

        this.status = status;

    }



    public String getOrigin()

    {

        return origin;

    }



    public void setOrigin(String origin)

    {

        this.origin = origin;

    }



    public String getBaseDetailUrl()

    {

        return baseDetailUrl;

    }



    public void setBaseDetailUrl(String baseDetailUrl)

    {

        this.baseDetailUrl = baseDetailUrl;

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



    public Date getUploadAt()

    {

        return uploadAt;

    }



    public void setUploadAt(Date uploadAt)

    {

        this.uploadAt = uploadAt;

    }



    public String getUploadIp()

    {

        return uploadIp;

    }



    public void setUploadIp(String uploadIp)

    {

        this.uploadIp = uploadIp;

    }



    public String getAuditBy()

    {

        return auditBy;

    }



    public void setAuditBy(String auditBy)

    {

        this.auditBy = auditBy;

    }



    public Date getAuditAt()

    {

        return auditAt;

    }



    public void setAuditAt(Date auditAt)

    {

        this.auditAt = auditAt;

    }



    public String getRejectReason()

    {

        return rejectReason;

    }



    public void setRejectReason(String rejectReason)

    {

        this.rejectReason = rejectReason;

    }



    public Date getCreatedAt()

    {

        return createdAt;

    }



    public void setCreatedAt(Date createdAt)

    {

        this.createdAt = createdAt;

    }



    public String getIpLog()

    {

        return ipLog;

    }



    public void setIpLog(String ipLog)

    {

        this.ipLog = ipLog;

    }



    @Override

    public String toString()

    {

        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)

            .append("id", getId())

            .append("code", getCode())

            .append("verificationCode", getVerificationCode())

            .append("status", getStatus())

            .append("origin", getOrigin())

            .append("baseDetailUrl", getBaseDetailUrl())

            .append("pickDate", getPickDate())

            .append("packDate", getPackDate())

            .append("lawyerName", getLawyerName())

            .append("certFile", getCertFile())

            .append("certImage", getCertImage())

            .append("uploadAt", getUploadAt())

            .append("uploadIp", getUploadIp())

            .append("auditBy", getAuditBy())

            .append("auditAt", getAuditAt())

            .append("rejectReason", getRejectReason())

            .append("createdAt", getCreatedAt())

            .append("ipLog", getIpLog())

            .toString();

    }

}

