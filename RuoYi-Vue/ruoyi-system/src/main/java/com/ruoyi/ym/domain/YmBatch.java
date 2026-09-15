package com.ruoyi.ym.domain;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;

/**
 * 杨梅采摘/鉴证批次 ym_batches
 */
public class YmBatch implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Integer id;

    @Excel(name = "批次号")
    private String batchNo;

    @Excel(name = "批次名称")
    private String name;

    /** 产品编码：yangmei / rice */
    @Excel(name = "产品类型")
    private String productType;

    @Excel(name = "产地")
    private String origin;

    @Excel(name = "加工包装厂家")
    private String processor;

    @Excel(name = "仓库")
    private String warehouse;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "收割时间", width = 20, dateFormat = "yyyy-MM-dd")
    private Date harvestDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "加工时间", width = 20, dateFormat = "yyyy-MM-dd")
    private Date processDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "采摘日期", width = 20, dateFormat = "yyyy-MM-dd")
    private Date pickDate;

    /** 跟采/溯源视频 */
    private String videoUrl;

    @Excel(name = "跟采律师")
    private String lawyerName;

    private String remark;

    /** 1启用 0禁用 */
    @Excel(name = "状态", readConverterExp = "0=禁用,1=启用")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getBatchNo()
    {
        return batchNo;
    }

    public void setBatchNo(String batchNo)
    {
        this.batchNo = batchNo;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getProductType()
    {
        return productType;
    }

    public void setProductType(String productType)
    {
        this.productType = productType;
    }

    public String getOrigin()
    {
        return origin;
    }

    public void setOrigin(String origin)
    {
        this.origin = origin;
    }

    public String getProcessor()
    {
        return processor;
    }

    public void setProcessor(String processor)
    {
        this.processor = processor;
    }

    public String getWarehouse()
    {
        return warehouse;
    }

    public void setWarehouse(String warehouse)
    {
        this.warehouse = warehouse;
    }

    public Date getHarvestDate()
    {
        return harvestDate;
    }

    public void setHarvestDate(Date harvestDate)
    {
        this.harvestDate = harvestDate;
    }

    public Date getProcessDate()
    {
        return processDate;
    }

    public void setProcessDate(Date processDate)
    {
        this.processDate = processDate;
    }

    public Date getPickDate()
    {
        return pickDate;
    }

    public void setPickDate(Date pickDate)
    {
        this.pickDate = pickDate;
    }

    public String getVideoUrl()
    {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl)
    {
        this.videoUrl = videoUrl;
    }

    public String getLawyerName()
    {
        return lawyerName;
    }

    public void setLawyerName(String lawyerName)
    {
        this.lawyerName = lawyerName;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("batchNo", getBatchNo())
            .append("name", getName())
            .append("origin", getOrigin())
            .append("pickDate", getPickDate())
            .append("videoUrl", getVideoUrl())
            .append("lawyerName", getLawyerName())
            .append("remark", getRemark())
            .append("status", getStatus())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
