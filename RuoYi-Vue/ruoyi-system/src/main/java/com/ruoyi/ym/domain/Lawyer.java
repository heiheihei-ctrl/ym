package com.ruoyi.ym.domain;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;

/**
 * 律师信息对象 lawyers
 *
 * @author ruoyi
 */
public class Lawyer implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 系统用户ID（关联 sys_user） */
    private Long userId;

    /** 系统用户账号（关联 sys_user.user_name，非 lawyers 表字段） */
    @Excel(name = "系统用户")
    private String username;

    /** 用户昵称（关联查询） */
    private String nickName;

    /** 手机号 */
    @Excel(name = "手机号")
    private String phone;

    /** 律师真实姓名 */
    @Excel(name = "真实姓名")
    private String realName;

    /** 律师执业证号 */
    @Excel(name = "执业证号")
    private String lawyerNo;

    /** 状态（1启用 0禁用） */
    @Excel(name = "状态", readConverterExp = "0=禁用,1=启用")
    private Integer status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getRealName()
    {
        return realName;
    }

    public void setRealName(String realName)
    {
        this.realName = realName;
    }

    public String getLawyerNo()
    {
        return lawyerNo;
    }

    public void setLawyerNo(String lawyerNo)
    {
        this.lawyerNo = lawyerNo;
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
            .append("userId", getUserId())
            .append("username", getUsername())
            .append("nickName", getNickName())
            .append("phone", getPhone())
            .append("realName", getRealName())
            .append("lawyerNo", getLawyerNo())
            .append("status", getStatus())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
