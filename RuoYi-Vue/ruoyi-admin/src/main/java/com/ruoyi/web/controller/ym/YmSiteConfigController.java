package com.ruoyi.web.controller.ym;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.ym.domain.YmSiteConfig;
import com.ruoyi.ym.service.IYmSiteConfigService;

/**
 * 见证书查询页系统配置
 */
@RestController
@RequestMapping("/ym/siteConfig")
public class YmSiteConfigController extends BaseController
{
    @Autowired
    private IYmSiteConfigService siteConfigService;

    /** 公开读取（verify 页） */
    @Anonymous
    @GetMapping("/public")
    public AjaxResult publicConfig()
    {
        return success(siteConfigService.getSiteConfig());
    }

    @PreAuthorize("@ss.hasPermi('ym:siteConfig:query')")
    @GetMapping
    public AjaxResult getConfig()
    {
        return success(siteConfigService.getSiteConfig());
    }

    @PreAuthorize("@ss.hasPermi('ym:siteConfig:edit')")
    @Log(title = "系统配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult save(@RequestBody YmSiteConfig config)
    {
        return toAjax(siteConfigService.saveSiteConfig(config));
    }
}
