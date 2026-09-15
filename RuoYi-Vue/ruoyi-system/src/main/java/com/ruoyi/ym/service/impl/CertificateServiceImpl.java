package com.ruoyi.ym.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.ym.domain.Certificate;
import com.ruoyi.ym.domain.ScanLog;
import com.ruoyi.ym.domain.VerificationCode;
import com.ruoyi.ym.domain.YmBatch;
import com.ruoyi.ym.domain.dto.CertificateRangeBindRequest;
import com.ruoyi.ym.domain.vo.CertificatePublicVerifyVO;
import com.ruoyi.ym.mapper.CertificateMapper;
import com.ruoyi.ym.mapper.ScanLogMapper;
import com.ruoyi.ym.mapper.VerificationCodeMapper;
import com.ruoyi.ym.mapper.YmBatchMapper;
import com.ruoyi.ym.service.ICertificateService;
import com.ruoyi.ym.service.IYmProductService;
import com.ruoyi.ym.domain.YmProduct;
import com.ruoyi.ym.utils.CodeRangeUtils;
import com.ruoyi.ym.utils.VerificationCodeUtils;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class CertificateServiceImpl implements ICertificateService
{
    private static final String PUBLIC_VERIFY_NOT_FOUND_MSG = "未能查到对应的见证书，请核实查询码后重试。";

    /** 非首次且扫码次数达到该阈值显示红码 */
    private static final long RED_SCAN_THRESHOLD = 10;

    @Autowired
    private CertificateMapper certificateMapper;

    @Autowired
    private VerificationCodeMapper verificationCodeMapper;

    @Autowired
    private YmBatchMapper ymBatchMapper;

    @Autowired
    private ScanLogMapper scanLogMapper;

    @Autowired
    private IYmProductService ymProductService;

    @Override
    public Certificate selectCertificateById(Integer id)
    {
        return certificateMapper.selectCertificateById(id);
    }

    @Override
    public List<Certificate> selectCertificateList(Certificate certificate)
    {
        return certificateMapper.selectCertificateList(certificate);
    }

    /**
     * 提交见证：支持单码或多选查询码绑定，并关联批次
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCertificate(Certificate certificate)
    {
        validateCertFiles(certificate);
        applyFromBatch(certificate);

        List<Integer> bindIds = resolveBindIds(certificate);
        if (bindIds.isEmpty())
        {
            throw new ServiceException("请选择要绑定的查询码");
        }

        if (certificate.getStatus() == null)
        {
            certificate.setStatus(1);
        }
        if (StringUtils.isEmpty(certificate.getBaseDetailUrl()))
        {
            certificate.setBaseDetailUrl(null);
        }
        certificate.setCodeUrl(null);
        normalizeProductFields(certificate);

        ensureCodesUnbound(bindIds, certificate.getProductType());
        certificate.setCode(bindIds.get(0));
        int rows = certificateMapper.insertCertificate(certificate);
        Date now = new Date();
        int updated = verificationCodeMapper.bindCertificateByIds(certificate.getId(), bindIds, now);
        if (updated != bindIds.size())
        {
            throw new ServiceException("查询码绑定失败，请重试");
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCertificate(Certificate certificate)
    {
        certificate.setCodeUrl(null);
        if (certificate.getBaseDetailUrl() != null)
        {
            certificate.setBaseDetailUrl(certificate.getBaseDetailUrl().trim());
        }
        if (certificate.getBatchId() != null)
        {
            applyFromBatch(certificate);
        }
        normalizeProductFields(certificate);
        return certificateMapper.updateCertificate(certificate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCertificateByIds(Integer[] ids)
    {
        if (ids != null && ids.length > 0)
        {
            verificationCodeMapper.clearBindingByCertificateIds(ids);
        }
        return certificateMapper.deleteCertificateByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> bindCodeRange(CertificateRangeBindRequest request)
    {
        if (request == null || request.getCertificateId() == null)
        {
            throw new ServiceException("请指定见证书");
        }
        Certificate cert = certificateMapper.selectCertificateById(request.getCertificateId());
        if (cert == null)
        {
            throw new ServiceException("见证书不存在");
        }

        List<Integer> bindIds = new ArrayList<>();
        if (request.getVerificationCodeIds() != null && request.getVerificationCodeIds().length > 0)
        {
            for (Integer id : request.getVerificationCodeIds())
            {
                if (id != null && !bindIds.contains(id))
                {
                    bindIds.add(id);
                }
            }
        }
        else if (StringUtils.isNotEmpty(request.getStartCode()) || StringUtils.isNotEmpty(request.getEndCode()))
        {
            // 兼容旧流水号区间（历史数据）
            String[] range = CodeRangeUtils.normalizeRange(request.getStartCode(), request.getEndCode());
            List<VerificationCode> codes = verificationCodeMapper.selectByCodeRange(range[0], range[1]);
            if (codes == null || codes.isEmpty())
            {
                throw new ServiceException("区间内没有查询码");
            }
            for (VerificationCode c : codes)
            {
                bindIds.add(c.getId());
            }
        }
        if (bindIds.isEmpty())
        {
            throw new ServiceException("请选择要绑定的查询码");
        }

        ensureCodesUnbound(bindIds, cert.getProductType());
        Date now = new Date();
        int updated = verificationCodeMapper.bindCertificateByIds(cert.getId(), bindIds, now);
        if (updated != bindIds.size())
        {
            throw new ServiceException("查询码绑定失败，请重试");
        }
        if (cert.getCode() == null)
        {
            Certificate patch = new Certificate();
            patch.setId(cert.getId());
            patch.setCode(bindIds.get(0));
            certificateMapper.updateCertificate(patch);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("certificateId", cert.getId());
        result.put("boundCount", updated);
        return result;
    }

    private List<Integer> resolveBindIds(Certificate certificate)
    {
        List<Integer> bindIds = new ArrayList<>();
        if (certificate.getVerificationCodeIds() != null)
        {
            for (Integer id : certificate.getVerificationCodeIds())
            {
                if (id != null && !bindIds.contains(id))
                {
                    bindIds.add(id);
                }
            }
        }
        if (bindIds.isEmpty() && certificate.getVerificationCodeId() != null)
        {
            bindIds.add(certificate.getVerificationCodeId());
        }
        if (bindIds.isEmpty() && (StringUtils.isNotEmpty(certificate.getStartCode())
                || StringUtils.isNotEmpty(certificate.getEndCode())))
        {
            String[] range = CodeRangeUtils.normalizeRange(certificate.getStartCode(), certificate.getEndCode());
            List<VerificationCode> codes = verificationCodeMapper.selectByCodeRange(range[0], range[1]);
            if (codes != null)
            {
                for (VerificationCode c : codes)
                {
                    bindIds.add(c.getId());
                }
            }
        }
        return bindIds;
    }

    private void ensureCodesUnbound(List<Integer> bindIds, String expectedProductType)
    {
        int alreadyBound = verificationCodeMapper.countBoundByIds(bindIds);
        if (alreadyBound > 0)
        {
            throw new ServiceException("所选查询码中有 " + alreadyBound + " 个已绑定，请重新选择");
        }
        List<VerificationCode> found = verificationCodeMapper.selectVerificationCodeByIds(bindIds.toArray(new Integer[0]));
        if (found == null || found.size() != bindIds.size())
        {
            throw new ServiceException("存在无效查询码，请刷新后重试");
        }
        String expected = StringUtils.isEmpty(expectedProductType) ? "yangmei" : expectedProductType.trim().toLowerCase();
        for (VerificationCode vc : found)
        {
            String codeType = StringUtils.isEmpty(vc.getProductType()) ? "yangmei" : vc.getProductType().trim().toLowerCase();
            if (!expected.equals(codeType))
            {
                throw new ServiceException("查询码「" + vc.getCode() + "」属于其他产品类型，与见证书不匹配");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CertificatePublicVerifyVO verifyByCodePublic(String code)
    {
        CertificatePublicVerifyVO vo = new CertificatePublicVerifyVO();
        if (StringUtils.isEmpty(code))
        {
            vo.setResultType("NOT_FOUND");
            vo.setMessage("请输入见证书查询码");
            return vo;
        }
        VerificationCode verification = findVerificationByCandidates(code);
        if (verification == null)
        {
            vo.setResultType("NOT_FOUND");
            vo.setMessage(PUBLIC_VERIFY_NOT_FOUND_MSG);
            return vo;
        }
        vo.setCode(verification.getCode());

        Certificate certificate = resolveCertificate(verification);
        if (certificate == null || certificate.getStatus() == null || certificate.getStatus() != 2)
        {
            vo.setResultType("NOT_FOUND");
            vo.setMessage(PUBLIC_VERIFY_NOT_FOUND_MSG);
            if (certificate != null)
            {
                vo.setStatus(certificate.getStatus());
                vo.setStatusLabel(resolveStatusLabel(certificate.getStatus()));
            }
            return vo;
        }

        // 记录扫码指纹（时间 + IP）
        recordScan(verification);

        long scanCount = scanLogMapper.countByVerificationId(verification.getId());
        Date firstScanAt = scanLogMapper.selectFirstScanAt(verification.getId());
        boolean first = scanCount <= 1;

        vo.setVerified(true);
        vo.setResultType("VERIFIED");
        vo.setStatus(certificate.getStatus());
        vo.setStatusLabel("已生效");
        vo.setScanCount(scanCount);
        vo.setFirstScanAt(firstScanAt);
        vo.setFirstScan(first);
        vo.setCodeColor(resolveCodeColor(scanCount));
        if (first)
        {
            vo.setMessage("正品鉴证通过，以下为见证书与溯源信息");
        }
        else
        {
            vo.setMessage("该码已被扫描 " + scanCount + " 次，首次扫码时间："
                    + (firstScanAt != null ? firstScanAt.toString() : "-"));
        }
        fillCertificateDetail(vo, certificate, verification);
        return vo;
    }

    private Certificate resolveCertificate(VerificationCode verification)
    {
        if (verification.getCertificateId() != null)
        {
            Certificate byBind = certificateMapper.selectCertificateById(verification.getCertificateId());
            if (byBind != null)
            {
                return byBind;
            }
        }
        return certificateMapper.selectCertificateByVerificationId(verification.getId());
    }

    private void recordScan(VerificationCode verification)
    {
        ScanLog log = new ScanLog();
        log.setVerificationId(verification.getId());
        log.setCode(verification.getCode());
        log.setScanIp(IpUtils.getIpAddr());
        HttpServletRequest request = ServletUtils.getRequest();
        if (request != null)
        {
            String ua = request.getHeader("User-Agent");
            if (ua != null && ua.length() > 480)
            {
                ua = ua.substring(0, 480);
            }
            log.setUserAgent(ua);
        }
        log.setScannedAt(new Date());
        scanLogMapper.insertScanLog(log);

        // 同步简要指纹到证书 ip_log（最近一次）
        Certificate cert = resolveCertificate(verification);
        if (cert != null)
        {
            String line = log.getScannedAt() + "|" + log.getScanIp();
            String prev = cert.getIpLog();
            String merged = StringUtils.isEmpty(prev) ? line : (prev + ";" + line);
            if (merged.length() > 2000)
            {
                merged = merged.substring(merged.length() - 2000);
            }
            Certificate patch = new Certificate();
            patch.setId(cert.getId());
            patch.setIpLog(merged);
            certificateMapper.updateCertificate(patch);
        }
    }

    private String resolveCodeColor(long scanCount)
    {
        if (scanCount <= 1)
        {
            return "GREEN";
        }
        if (scanCount >= RED_SCAN_THRESHOLD)
        {
            return "RED";
        }
        return "YELLOW";
    }

    private void normalizeProductFields(Certificate certificate)
    {
        String type = certificate.getProductType() == null ? "yangmei" : certificate.getProductType().trim().toLowerCase();
        ymProductService.assertEnabledProductCode(type);
        certificate.setProductType(type);
        if (!"rice".equals(type) && certificate.getPackDate() == null)
        {
            throw new ServiceException("请填写包装日期");
        }
    }

    /** 见证书溯源信息以批次为准（大米厂家/仓库等在批次维护） */
    private void applyFromBatch(Certificate certificate)
    {
        if (certificate.getBatchId() == null)
        {
            throw new ServiceException("请选择所属批次");
        }
        YmBatch batch = ymBatchMapper.selectYmBatchById(certificate.getBatchId());
        if (batch == null)
        {
            throw new ServiceException("批次不存在");
        }
        if (batch.getStatus() != null && batch.getStatus() == 0)
        {
            throw new ServiceException("批次已禁用：" + batch.getName());
        }
        String batchType = batch.getProductType() == null ? "yangmei" : batch.getProductType().trim().toLowerCase();
        ymProductService.assertEnabledProductCode(batchType);
        if (StringUtils.isNotEmpty(certificate.getProductType())
                && !batchType.equalsIgnoreCase(certificate.getProductType().trim()))
        {
            throw new ServiceException("所选批次与产品类型不一致");
        }
        certificate.setProductType(batchType);
        certificate.setOrigin(batch.getOrigin());
        if (StringUtils.isEmpty(certificate.getLawyerName()) && StringUtils.isNotEmpty(batch.getLawyerName()))
        {
            certificate.setLawyerName(batch.getLawyerName());
        }
        if ("rice".equals(batchType))
        {
            certificate.setProcessor(batch.getProcessor());
            certificate.setWarehouse(batch.getWarehouse());
            certificate.setHarvestDate(batch.getHarvestDate());
            certificate.setProcessDate(batch.getProcessDate());
            certificate.setPickDate(null);
            certificate.setPackDate(null);
            certificate.setBaseDetailUrl(null);
        }
        else
        {
            certificate.setPickDate(batch.getPickDate());
            certificate.setProcessor(null);
            certificate.setWarehouse(null);
            certificate.setHarvestDate(null);
            certificate.setProcessDate(null);
        }
    }

    private void validateCertFiles(Certificate certificate)
    {
        if (StringUtils.isEmpty(certificate.getCertFile()))
        {
            throw new ServiceException("请上传见证书 PDF 文件");
        }
        if (StringUtils.isEmpty(certificate.getCertImage()))
        {
            throw new ServiceException("请上传见证书图片");
        }
    }

    private VerificationCode findVerificationByCandidates(String raw)
    {
        for (String candidate : VerificationCodeUtils.buildCandidates(raw))
        {
            VerificationCode vc = verificationCodeMapper.selectVerificationCodeByCodeIgnoreCase(candidate);
            if (vc != null)
            {
                return vc;
            }
        }
        return null;
    }

    private void fillCertificateDetail(CertificatePublicVerifyVO vo, Certificate certificate, VerificationCode verification)
    {
        if (StringUtils.isNotEmpty(certificate.getVerificationCode()))
        {
            vo.setCode(certificate.getVerificationCode());
        }
        if (verification != null && StringUtils.isNotEmpty(verification.getCode()))
        {
            vo.setCode(verification.getCode());
        }
        YmBatch batch = certificate.getBatchId() != null
                ? ymBatchMapper.selectYmBatchById(certificate.getBatchId()) : null;
        String productType = certificate.getProductType();
        if (batch != null && StringUtils.isNotEmpty(batch.getProductType()))
        {
            productType = batch.getProductType();
        }
        vo.setProductType(productType);
        YmProduct product = ymProductService.selectYmProductByCode(productType);
        if (product != null)
        {
            vo.setProductName(product.getProductName());
        }
        if (batch != null)
        {
            vo.setOrigin(batch.getOrigin());
            vo.setLawyerName(StringUtils.isNotEmpty(certificate.getLawyerName())
                    ? certificate.getLawyerName() : batch.getLawyerName());
            vo.setBatchName(batch.getName());
            vo.setBatchNo(batch.getBatchNo());
            vo.setVideoUrl(batch.getVideoUrl());
            if ("rice".equals(batch.getProductType()))
            {
                vo.setProcessor(batch.getProcessor());
                vo.setWarehouse(batch.getWarehouse());
                vo.setHarvestDate(batch.getHarvestDate());
                vo.setProcessDate(batch.getProcessDate());
                vo.setPickDate(null);
                vo.setPackDate(null);
                vo.setBaseDetailUrl(null);
            }
            else
            {
                vo.setPickDate(batch.getPickDate());
                vo.setPackDate(certificate.getPackDate());
                vo.setBaseDetailUrl(certificate.getBaseDetailUrl());
            }
        }
        else
        {
            vo.setOrigin(certificate.getOrigin());
            vo.setProcessor(certificate.getProcessor());
            vo.setWarehouse(certificate.getWarehouse());
            vo.setHarvestDate(certificate.getHarvestDate());
            vo.setProcessDate(certificate.getProcessDate());
            vo.setPickDate(certificate.getPickDate());
            vo.setPackDate(certificate.getPackDate());
            vo.setLawyerName(certificate.getLawyerName());
            vo.setBaseDetailUrl(certificate.getBaseDetailUrl());
            vo.setBatchName(certificate.getBatchName());
            vo.setBatchNo(certificate.getBatchNo());
            vo.setVideoUrl(certificate.getBatchVideoUrl());
        }
        vo.setCertFile(certificate.getCertFile());
        vo.setCertImage(certificate.getCertImage());
        if (StringUtils.isNotEmpty(verification.getCodeUrl()))
        {
            vo.setCodeUrl(verification.getCodeUrl());
        }
        else if (StringUtils.isNotEmpty(certificate.getCodeUrl()))
        {
            vo.setCodeUrl(certificate.getCodeUrl());
        }
    }

    private String resolveStatusLabel(Integer status)
    {
        if (status == null)
        {
            return "未知";
        }
        return switch (status)
        {
            case 0 -> "未使用";
            case 1 -> "未激活";
            case 2 -> "已激活";
            case 3 -> "已删除";
            default -> "未知";
        };
    }
}
