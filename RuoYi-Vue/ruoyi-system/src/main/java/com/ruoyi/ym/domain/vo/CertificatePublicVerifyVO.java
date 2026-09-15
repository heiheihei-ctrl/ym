package com.ruoyi.ym.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * 见证书公开查验结果（含扫码指纹与色码）
 */
public class CertificatePublicVerifyVO
{
    /** 是否可展示证书详情（已激活） */
    private boolean verified;

    /** NOT_FOUND / VERIFIED */
    private String resultType;

    private String message;

    private String code;

    private Integer status;

    private String statusLabel;

    private String origin;

    /** yangmei / rice */
    private String productType;

    /** 产品名称（来自产品管理） */
    private String productName;

    private String processor;

    private String warehouse;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date harvestDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date processDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date pickDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date packDate;

    private String lawyerName;

    private String certFile;

    private String certImage;

    private String codeUrl;

    private String baseDetailUrl;

    /** 批次名称 */
    private String batchName;

    private String batchNo;

    /** 溯源/跟采视频 */
    private String videoUrl;

    /** GREEN / YELLOW / RED */
    private String codeColor;

    /** 含本次的扫码次数 */
    private Long scanCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date firstScanAt;

    /** 是否首次扫码 */
    private boolean firstScan;

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public String getResultType() { return resultType; }
    public void setResultType(String resultType) { this.resultType = resultType; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getStatusLabel() { return statusLabel; }
    public void setStatusLabel(String statusLabel) { this.statusLabel = statusLabel; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProcessor() { return processor; }
    public void setProcessor(String processor) { this.processor = processor; }
    public String getWarehouse() { return warehouse; }
    public void setWarehouse(String warehouse) { this.warehouse = warehouse; }
    public Date getHarvestDate() { return harvestDate; }
    public void setHarvestDate(Date harvestDate) { this.harvestDate = harvestDate; }
    public Date getProcessDate() { return processDate; }
    public void setProcessDate(Date processDate) { this.processDate = processDate; }
    public Date getPickDate() { return pickDate; }
    public void setPickDate(Date pickDate) { this.pickDate = pickDate; }
    public Date getPackDate() { return packDate; }
    public void setPackDate(Date packDate) { this.packDate = packDate; }
    public String getLawyerName() { return lawyerName; }
    public void setLawyerName(String lawyerName) { this.lawyerName = lawyerName; }
    public String getCertFile() { return certFile; }
    public void setCertFile(String certFile) { this.certFile = certFile; }
    public String getCertImage() { return certImage; }
    public void setCertImage(String certImage) { this.certImage = certImage; }
    public String getCodeUrl() { return codeUrl; }
    public void setCodeUrl(String codeUrl) { this.codeUrl = codeUrl; }
    public String getBaseDetailUrl() { return baseDetailUrl; }
    public void setBaseDetailUrl(String baseDetailUrl) { this.baseDetailUrl = baseDetailUrl; }
    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public String getCodeColor() { return codeColor; }
    public void setCodeColor(String codeColor) { this.codeColor = codeColor; }
    public Long getScanCount() { return scanCount; }
    public void setScanCount(Long scanCount) { this.scanCount = scanCount; }
    public Date getFirstScanAt() { return firstScanAt; }
    public void setFirstScanAt(Date firstScanAt) { this.firstScanAt = firstScanAt; }
    public boolean isFirstScan() { return firstScan; }
    public void setFirstScan(boolean firstScan) { this.firstScan = firstScan; }
}
