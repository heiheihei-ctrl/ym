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
import com.ruoyi.ym.domain.Lawyer;
import com.ruoyi.ym.service.ILawyerService;

/**
 * 律师信息 Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/ym/lawyer")
public class LawyerController extends BaseController
{
    @Autowired
    private ILawyerService lawyerService;

    @PreAuthorize("@ss.hasPermi('ym:lawyer:list')")
    @GetMapping("/list")
    public TableDataInfo list(Lawyer lawyer)
    {
        startPage();
        List<Lawyer> list = lawyerService.selectLawyerList(lawyer);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ym:lawyer:export')")
    @Log(title = "律师信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Lawyer lawyer)
    {
        List<Lawyer> list = lawyerService.selectLawyerList(lawyer);
        ExcelUtil<Lawyer> util = new ExcelUtil<>(Lawyer.class);
        util.exportExcel(response, list, "律师信息数据");
    }

    @PreAuthorize("@ss.hasPermi('ym:lawyer:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Integer id)
    {
        return success(lawyerService.selectLawyerById(id));
    }

    @PreAuthorize("@ss.hasPermi('ym:lawyer:add')")
    @Log(title = "律师信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Lawyer lawyer)
    {
        return toAjax(lawyerService.insertLawyer(lawyer));
    }

    @PreAuthorize("@ss.hasPermi('ym:lawyer:edit')")
    @Log(title = "律师信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Lawyer lawyer)
    {
        return toAjax(lawyerService.updateLawyer(lawyer));
    }

    @PreAuthorize("@ss.hasPermi('ym:lawyer:remove')")
    @Log(title = "律师信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(lawyerService.deleteLawyerByIds(ids));
    }
}
