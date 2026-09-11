package com.ruoyi.web.controller.ym;

import java.util.List;

import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.ym.domain.Certificate;
import com.ruoyi.ym.domain.VerificationCode;
import com.ruoyi.ym.domain.dto.VerificationCodeBatchRequest;
import com.ruoyi.ym.service.ICertificateService;
import com.ruoyi.ym.service.IVerificationCodeService;

/**
 * 证书 Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/ym/certificate")
public class CertificateController extends BaseController
{
    @Autowired
    private ICertificateService certificateService;

    @Autowired
    private IVerificationCodeService verificationCodeService;

    /* ---------- 查询码 verification 表 ---------- */

    @PreAuthorize("@ss.hasPermi('ym:verificationCode:list')")
    @GetMapping("/verificationCode/list")
    public TableDataInfo verificationCodeList(VerificationCode query)
    {
        startPage();
        return getDataTable(verificationCodeService.selectVerificationCodeList(query));
    }

    @PreAuthorize("@ss.hasPermi('ym:verificationCode:list') or @ss.hasPermi('ym:certificate:add')")
    @GetMapping("/verificationCode/unusedList")
    public AjaxResult verificationCodeUnusedList()
    {
        return success(verificationCodeService.selectUnusedVerificationCodeList());
    }

    @PreAuthorize("@ss.hasPermi('ym:verificationCode:generate')")
    @Log(title = "批量生成查询码", businessType = BusinessType.INSERT)
    @PostMapping("/verificationCode/batchGenerate")
    public AjaxResult verificationCodeBatchGenerate(@RequestBody VerificationCodeBatchRequest request)
    {
        return success(verificationCodeService.batchGenerate(request));
    }

    @PreAuthorize("@ss.hasPermi('ym:verificationCode:remove')")
    @Log(title = "查询码", businessType = BusinessType.DELETE)
    @DeleteMapping("/verificationCode/{ids}")
    public AjaxResult verificationCodeRemove(@PathVariable Integer[] ids)
    {
        return toAjax(verificationCodeService.deleteVerificationCodeByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('ym:verificationCode:list')")
    @Log(title = "查询码", businessType = BusinessType.EXPORT)
    @GetMapping("/verificationCode/downloadZip")
    public void verificationCodeDownloadZip(HttpServletResponse response, @RequestParam("ids") String ids) throws Exception
    {
        Integer[] idArr = Convert.toIntArray(ids);
        if (idArr == null || idArr.length == 0)
        {
            throw new ServiceException("请选择要下载的查询码");
        }
        byte[] data = verificationCodeService.downloadVerificationQrZip(idArr);
        String filename = "扫码二维码_" + DateUtils.dateTimeNow() + ".zip";
        FileUtils.setAttachmentResponseHeader(response, filename);
        response.setContentType("application/zip");
        IOUtils.write(data, response.getOutputStream());
    }

    /* ---------- 证书 ---------- */

    /**
     * 公开查验见证书（匿名访问）
     */
    @Anonymous
    @GetMapping("/public/verify")
    public AjaxResult publicVerify(@RequestParam("code") String code)
    {
        return success(certificateService.verifyByCodePublic(code));
    }

    @PreAuthorize("@ss.hasPermi('ym:certificate:list')")
    @GetMapping("/list")
    public TableDataInfo list(Certificate certificate)
    {
        startPage();
        List<Certificate> list = certificateService.selectCertificateList(certificate);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ym:certificate:export')")
    @Log(title = "证书管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Certificate certificate)
    {
        List<Certificate> list = certificateService.selectCertificateList(certificate);
        ExcelUtil<Certificate> util = new ExcelUtil<>(Certificate.class);
        util.exportExcel(response, list, "证书数据");
    }

    @PreAuthorize("@ss.hasPermi('ym:certificate:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Integer id)
    {
        return success(certificateService.selectCertificateById(id));
    }

    @PreAuthorize("@ss.hasPermi('ym:certificate:add')")
    @Log(title = "证书管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Certificate certificate)
    {
        return toAjax(certificateService.insertCertificate(certificate));
    }

    @PreAuthorize("@ss.hasPermi('ym:certificate:edit')")
    @Log(title = "证书管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Certificate certificate)
    {
        return toAjax(certificateService.updateCertificate(certificate));
    }

    @PreAuthorize("@ss.hasPermi('ym:certificate:remove')")
    @Log(title = "证书管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(certificateService.deleteCertificateByIds(ids));
    }
}
