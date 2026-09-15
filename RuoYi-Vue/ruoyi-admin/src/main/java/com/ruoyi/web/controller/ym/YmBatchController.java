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
import com.ruoyi.ym.domain.YmBatch;
import com.ruoyi.ym.service.IYmBatchService;

/**
 * 杨梅批次（产地跟采）
 */
@RestController
@RequestMapping("/ym/batch")
public class YmBatchController extends BaseController
{
    @Autowired
    private IYmBatchService ymBatchService;

    @PreAuthorize("@ss.hasPermi('ym:batch:list')")
    @GetMapping("/list")
    public TableDataInfo list(YmBatch query)
    {
        startPage();
        return getDataTable(ymBatchService.selectYmBatchList(query));
    }

    @PreAuthorize("@ss.hasPermi('ym:batch:export')")
    @Log(title = "批次管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, YmBatch query)
    {
        List<YmBatch> list = ymBatchService.selectYmBatchList(query);
        ExcelUtil<YmBatch> util = new ExcelUtil<>(YmBatch.class);
        util.exportExcel(response, list, "批次数据");
    }

    @PreAuthorize("@ss.hasPermi('ym:batch:query') or @ss.hasPermi('ym:certificate:add') or @ss.hasPermi('ym:certificate:edit')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Integer id)
    {
        return success(ymBatchService.selectYmBatchById(id));
    }

    @PreAuthorize("@ss.hasPermi('ym:batch:add')")
    @Log(title = "批次管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YmBatch batch)
    {
        return toAjax(ymBatchService.insertYmBatch(batch));
    }

    @PreAuthorize("@ss.hasPermi('ym:batch:edit')")
    @Log(title = "批次管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YmBatch batch)
    {
        return toAjax(ymBatchService.updateYmBatch(batch));
    }

    @PreAuthorize("@ss.hasPermi('ym:batch:remove')")
    @Log(title = "批次管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(ymBatchService.deleteYmBatchByIds(ids));
    }
}
