package com.ruoyi.ym.mapper;

import com.ruoyi.ym.domain.YmSiteConfig;

public interface YmSiteConfigMapper
{
    YmSiteConfig selectSiteConfig();

    int updateSiteConfig(YmSiteConfig config);

    int insertSiteConfig(YmSiteConfig config);
}
