package com.ruoyi.ym.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ym.domain.YmSiteConfig;
import com.ruoyi.ym.mapper.YmSiteConfigMapper;
import com.ruoyi.ym.service.IYmSiteConfigService;

@Service
public class YmSiteConfigServiceImpl implements IYmSiteConfigService
{
    @Autowired
    private YmSiteConfigMapper siteConfigMapper;

    @Override
    public YmSiteConfig getSiteConfig()
    {
        YmSiteConfig config = siteConfigMapper.selectSiteConfig();
        if (config == null)
        {
            config = defaultConfig();
            siteConfigMapper.insertSiteConfig(config);
        }
        return config;
    }

    @Override
    public int saveSiteConfig(YmSiteConfig config)
    {
        config.setId(1);
        YmSiteConfig existing = siteConfigMapper.selectSiteConfig();
        if (existing == null)
        {
            return siteConfigMapper.insertSiteConfig(config);
        }
        return siteConfigMapper.updateSiteConfig(config);
    }

    private YmSiteConfig defaultConfig()
    {
        YmSiteConfig config = new YmSiteConfig();
        config.setId(1);
        config.setFirmName("杭州市某某律师事务所");
        config.setQueryTopTip("本系统由律师事务所提供见证服务，证书信息真实可靠");
        config.setQueryBottomTip("扫码查证 · 谨防假冒");
        config.setPhone("0571-XXXXXXXX");
        config.setAddress("浙江省杭州市西湖区XX路XX号XX大厦XX楼");
        return config;
    }
}
