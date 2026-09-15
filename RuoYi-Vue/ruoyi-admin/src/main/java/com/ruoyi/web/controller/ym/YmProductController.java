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
import com.ruoyi.ym.domain.YmProduct;
import com.ruoyi.ym.service.IYmProductService;

@RestController
@RequestMapping("/ym/product")
public class YmProductController extends BaseController
{
    @Autowired
    private IYmProductService ymProductService;

    @PreAuthorize("@ss.hasPermi('ym:product:list')")
    @GetMapping("/list")
    public TableDataInfo list(YmProduct query)
    {
        startPage();
        return getDataTable(ymProductService.selectYmProductList(query));
    }

    /** 下拉选项：见证书、产地等页面使用 */
    @GetMapping("/options")
    public AjaxResult options()
    {
        return success(ymProductService.selectEnabledProductList());
    }

    @PreAuthorize("@ss.hasPermi('ym:product:export')")
    @Log(title = "产品管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, YmProduct query)
    {
        List<YmProduct> list = ymProductService.selectYmProductList(query);
        ExcelUtil<YmProduct> util = new ExcelUtil<>(YmProduct.class);
        util.exportExcel(response, list, "产品数据");
    }

    @PreAuthorize("@ss.hasPermi('ym:product:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Integer id)
    {
        return success(ymProductService.selectYmProductById(id));
    }

    @PreAuthorize("@ss.hasPermi('ym:product:add')")
    @Log(title = "产品管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody YmProduct product)
    {
        return error("产品由系统维护，不可新增");
    }

    @PreAuthorize("@ss.hasPermi('ym:product:edit')")
    @Log(title = "产品管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody YmProduct product)
    {
        return toAjax(ymProductService.updateYmProduct(product));
    }

    @PreAuthorize("@ss.hasPermi('ym:product:remove')")
    @Log(title = "产品管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return error("产品由系统维护，不可删除");
    }
}
