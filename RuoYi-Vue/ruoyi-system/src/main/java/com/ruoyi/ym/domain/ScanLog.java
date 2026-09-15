package com.ruoyi.ym.domain;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 扫码指纹 ym_scan_logs
 */
public class ScanLog implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer verificationId;
    private String code;
    private String scanIp;
    private String userAgent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date scannedAt;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Integer getVerificationId()
    {
        return verificationId;
    }

    public void setVerificationId(Integer verificationId)
    {
        this.verificationId = verificationId;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getScanIp()
    {
        return scanIp;
    }

    public void setScanIp(String scanIp)
    {
        this.scanIp = scanIp;
    }

    public String getUserAgent()
    {
        return userAgent;
    }

    public void setUserAgent(String userAgent)
    {
        this.userAgent = userAgent;
    }

    public Date getScannedAt()
    {
        return scannedAt;
    }

    public void setScannedAt(Date scannedAt)
    {
        this.scannedAt = scannedAt;
    }
}
