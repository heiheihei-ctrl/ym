package com.ruoyi.web.controller.ym;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.ym.domain.Origin;
import com.ruoyi.ym.service.IOriginService;

/**
 * 产地来源 Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/ym/origin")
public class OriginController extends BaseController
{
    @Autowired
    private IOriginService originService;

    @PreAuthorize("@ss.hasPermi('ym:origin:list')")
    @GetMapping("/list")
    public TableDataInfo list(Origin origin)
    {
        startPage();
        List<Origin> list = originService.selectOriginList(origin);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ym:origin:export')")
    @Log(title = "产地来源", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Origin origin)
    {
        List<Origin> list = originService.selectOriginList(origin);
        ExcelUtil<Origin> util = new ExcelUtil<>(Origin.class);
        util.exportExcel(response, list, "产地来源数据");
    }

    @PreAuthorize("@ss.hasPermi('ym:origin:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Integer id)
    {
        return success(originService.selectOriginById(id));
    }

    @PreAuthorize("@ss.hasPermi('ym:origin:add')")
    @Log(title = "产地来源", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Origin origin)
    {
        return toAjax(originService.insertOrigin(origin));
    }

    @PreAuthorize("@ss.hasPermi('ym:origin:edit')")
    @Log(title = "产地来源", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Origin origin)
    {
        return toAjax(originService.updateOrigin(origin));
    }

    @PreAuthorize("@ss.hasPermi('ym:origin:remove')")
    @Log(title = "产地来源", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(originService.deleteOriginByIds(ids));
    }
}
