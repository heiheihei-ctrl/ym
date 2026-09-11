package com.ruoyi.ym.domain;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;

/**
 * 产地来源对象 origins
 *
 * @author ruoyi
 */
public class Origin implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 产地地址（省市区+详细地址拼接，供见证书下拉等使用） */
    @Excel(name = "产地地址")
    private String name;

    /** 排序序号 */
    @Excel(name = "排序序号")
    private Integer sortOrder;

    /** 状态（1启用 0禁用） */
    @Excel(name = "状态", readConverterExp = "0=禁用,1=启用")
    private Integer status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("createdAt", getCreatedAt())
            .toString();
    }
}
