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

    private Integer id;

    /** 提交见证时选择的 verification.id（单码绑定，兼容旧流程） */
    private Integer verificationCodeId;

    /** 代表查询码记录 id（certificates.code） */
    private Integer code;

    /** 所属批次 */
    private Integer batchId;

    /** 产品类型：yangmei 杨梅 / rice 五常大米 */
    @Excel(name = "产品类型", readConverterExp = "yangmei=杨梅,rice=五常大米")
    private String productType;

    /** 批次名称（关联查询） */
    @Excel(name = "批次")
    private String batchName;

    /** 批次号（关联查询） */
    private String batchNo;

    /** 溯源视频（来自批次，关联查询） */
    private String batchVideoUrl;

    @Excel(name = "查询码")
    private String verificationCode;

    /** 扫码二维码图片路径 */
    private String codeUrl;

    @Excel(name = "见证书图片")
    private String certImage;

    /** 状态：0未使用 1未激活 2已激活 3已删除 */
    @Excel(name = "状态", readConverterExp = "0=未使用,1=未激活,2=已激活,3=已删除")
    private Integer status;

    @Excel(name = "产地来源")
    private String origin;

    /** 加工包装厂家（大米） */
    @Excel(name = "加工包装厂家")
    private String processor;

    /** 仓库（大米） */
    @Excel(name = "仓库")
    private String warehouse;

    /** 收割时间（大米） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "收割时间", width = 20, dateFormat = "yyyy-MM-dd")
    private Date harvestDate;

    /** 加工时间（大米） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "加工时间", width = 20, dateFormat = "yyyy-MM-dd")
    private Date processDate;

    private String baseDetailUrl;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "采摘日期", width = 20, dateFormat = "yyyy-MM-dd")
    private Date pickDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "包装日期", width = 20, dateFormat = "yyyy-MM-dd")
    private Date packDate;

    @Excel(name = "见证律师")
    private String lawyerName;

    private String certFile;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "上传时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date uploadAt;

    @Excel(name = "上传IP")
    private String uploadIp;

    @Excel(name = "审核人")
    private String auditBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date auditAt;

    @Excel(name = "驳回原因")
    private String rejectReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    private String ipLog;

    /** 已绑定查询码数量 */
    private Integer boundCodeCount;

    /** 区间绑定起始码（兼容旧入参，非表字段） */
    private String startCode;

    /** 区间绑定结束码（兼容旧入参，非表字段） */
    private String endCode;

    /** 批量绑定的查询码 ID 列表（入参，非表字段） */
    private Integer[] verificationCodeIds;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getVerificationCodeId() { return verificationCodeId; }
    public void setVerificationCodeId(Integer verificationCodeId) { this.verificationCodeId = verificationCodeId; }
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public Integer getBatchId() { return batchId; }
    public void setBatchId(Integer batchId) { this.batchId = batchId; }
    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }
    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getBatchVideoUrl() { return batchVideoUrl; }
    public void setBatchVideoUrl(String batchVideoUrl) { this.batchVideoUrl = batchVideoUrl; }
    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }
    public String getCodeUrl() { return codeUrl; }
    public void setCodeUrl(String codeUrl) { this.codeUrl = codeUrl; }
    public String getCertImage() { return certImage; }
    public void setCertImage(String certImage) { this.certImage = certImage; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getProcessor() { return processor; }
    public void setProcessor(String processor) { this.processor = processor; }
    public String getWarehouse() { return warehouse; }
    public void setWarehouse(String warehouse) { this.warehouse = warehouse; }
    public Date getHarvestDate() { return harvestDate; }
    public void setHarvestDate(Date harvestDate) { this.harvestDate = harvestDate; }
    public Date getProcessDate() { return processDate; }
    public void setProcessDate(Date processDate) { this.processDate = processDate; }
    public String getBaseDetailUrl() { return baseDetailUrl; }
    public void setBaseDetailUrl(String baseDetailUrl) { this.baseDetailUrl = baseDetailUrl; }
    public Date getPickDate() { return pickDate; }
    public void setPickDate(Date pickDate) { this.pickDate = pickDate; }
    public Date getPackDate() { return packDate; }
    public void setPackDate(Date packDate) { this.packDate = packDate; }
    public String getLawyerName() { return lawyerName; }
    public void setLawyerName(String lawyerName) { this.lawyerName = lawyerName; }
    public String getCertFile() { return certFile; }
    public void setCertFile(String certFile) { this.certFile = certFile; }
    public Date getUploadAt() { return uploadAt; }
    public void setUploadAt(Date uploadAt) { this.uploadAt = uploadAt; }
    public String getUploadIp() { return uploadIp; }
    public void setUploadIp(String uploadIp) { this.uploadIp = uploadIp; }
    public String getAuditBy() { return auditBy; }
    public void setAuditBy(String auditBy) { this.auditBy = auditBy; }
    public Date getAuditAt() { return auditAt; }
    public void setAuditAt(Date auditAt) { this.auditAt = auditAt; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public String getIpLog() { return ipLog; }
    public void setIpLog(String ipLog) { this.ipLog = ipLog; }
    public Integer getBoundCodeCount() { return boundCodeCount; }
    public void setBoundCodeCount(Integer boundCodeCount) { this.boundCodeCount = boundCodeCount; }
    public String getStartCode() { return startCode; }
    public void setStartCode(String startCode) { this.startCode = startCode; }
    public String getEndCode() { return endCode; }
    public void setEndCode(String endCode) { this.endCode = endCode; }
    public Integer[] getVerificationCodeIds() { return verificationCodeIds; }
    public void setVerificationCodeIds(Integer[] verificationCodeIds) { this.verificationCodeIds = verificationCodeIds; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("batchId", getBatchId())
            .append("verificationCode", getVerificationCode())
            .append("status", getStatus())
            .append("origin", getOrigin())
            .append("boundCodeCount", getBoundCodeCount())
            .toString();
    }
}
