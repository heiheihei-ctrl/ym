package com.ruoyi.ym.service;

import com.ruoyi.ym.domain.YmSiteConfig;

public interface IYmSiteConfigService
{
    YmSiteConfig getSiteConfig();

    int saveSiteConfig(YmSiteConfig config);
}
