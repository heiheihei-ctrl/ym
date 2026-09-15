package com.ruoyi.ym.domain;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 见证书查询页系统配置 site_config（单条 id=1）
 */
public class YmSiteConfig implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Integer id;

    /** 律所名称 */
    private String firmName;

    /** 查询页顶部提示语 */
    private String queryTopTip;

    /** 查询页底部提示语 */
    private String queryBottomTip;

    /** 联系电话 */
    private String phone;

    /** 律所地址 */
    private String address;

    /** 扫码证书页自动跳转地址（空则不跳转） */
    private String redirectUrl;

    /** 跳转等待秒数，默认 6 */
    private Integer redirectDelaySeconds;

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

    public String getFirmName()
    {
        return firmName;
    }

    public void setFirmName(String firmName)
    {
        this.firmName = firmName;
    }

    public String getQueryTopTip()
    {
        return queryTopTip;
    }

    public void setQueryTopTip(String queryTopTip)
    {
        this.queryTopTip = queryTopTip;
    }

    public String getQueryBottomTip()
    {
        return queryBottomTip;
    }

    public void setQueryBottomTip(String queryBottomTip)
    {
        this.queryBottomTip = queryBottomTip;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getRedirectUrl()
    {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl)
    {
        this.redirectUrl = redirectUrl;
    }

    public Integer getRedirectDelaySeconds()
    {
        return redirectDelaySeconds;
    }

    public void setRedirectDelaySeconds(Integer redirectDelaySeconds)
    {
        this.redirectDelaySeconds = redirectDelaySeconds;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }
}
