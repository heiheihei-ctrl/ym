package com.ruoyi.ym.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.ym.domain.Certificate;
import com.ruoyi.ym.domain.VerificationCode;
import com.ruoyi.ym.domain.vo.CertificatePublicVerifyVO;
import com.ruoyi.ym.mapper.CertificateMapper;
import com.ruoyi.ym.mapper.VerificationCodeMapper;
import com.ruoyi.ym.service.ICertificateService;
import com.ruoyi.ym.utils.VerificationCodeUtils;

@Service
public class CertificateServiceImpl implements ICertificateService
{
    /** 公开查验：查询码不存在、待激活、已删除等不可展示详情时的统一提示 */
    private static final String PUBLIC_VERIFY_NOT_FOUND_MSG = "未能查到对应的见证书，请核实查询码后重试。";

    @Autowired
    private CertificateMapper certificateMapper;

    @Autowired
    private VerificationCodeMapper verificationCodeMapper;

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
     * 提交见证：将 verification.id 写入 certificates.code
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCertificate(Certificate certificate)
    {
        if (certificate.getVerificationCodeId() == null)
        {
            throw new ServiceException("请选择查询码");
        }
        VerificationCode vc = verificationCodeMapper.selectVerificationCodeById(certificate.getVerificationCodeId());
        if (vc == null)
        {
            throw new ServiceException("查询码不存在");
        }
        Certificate bound = certificateMapper.selectCertificateByVerificationId(vc.getId());
        if (bound != null)
        {
            throw new ServiceException("查询码已被使用：" + vc.getCode());
        }
        certificate.setCode(vc.getId());
        if (certificate.getStatus() == null)
        {
            certificate.setStatus(1);
        }
        if (StringUtils.isEmpty(certificate.getCertFile()))
        {
            throw new ServiceException("请上传见证书 PDF 文件");
        }
        if (StringUtils.isEmpty(certificate.getCertImage()))
        {
            throw new ServiceException("请上传见证书图片");
        }
        if (StringUtils.isEmpty(certificate.getBaseDetailUrl()))
        {
            certificate.setBaseDetailUrl(null);
        }
        // 扫码二维码仅在查询码批量生成时创建，提交见证不再生成
        certificate.setCodeUrl(null);
        int rows = certificateMapper.insertCertificate(certificate);
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
        return certificateMapper.updateCertificate(certificate);
    }

    @Override
    public int deleteCertificateByIds(Integer[] ids)
    {
        return certificateMapper.deleteCertificateByIds(ids);
    }

    @Override
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
        Certificate certificate = certificateMapper.selectCertificateByVerificationId(verification.getId());
        if (certificate != null)
        {
            vo.setStatus(certificate.getStatus());
            vo.setStatusLabel(resolveStatusLabel(certificate.getStatus()));
            if (certificate.getStatus() != null && certificate.getStatus() == 2)
            {
                vo.setVerified(true);
                vo.setResultType("VERIFIED");
                vo.setStatusLabel("已生效");
                vo.setMessage("查验通过，以下为见证书信息");
                fillCertificateDetail(vo, certificate);
            }
            else
            {
                vo.setResultType("NOT_FOUND");
                vo.setMessage(PUBLIC_VERIFY_NOT_FOUND_MSG);
            }
            return vo;
        }
        vo.setResultType("NOT_FOUND");
        vo.setMessage(PUBLIC_VERIFY_NOT_FOUND_MSG);
        return vo;
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

    private void fillCertificateDetail(CertificatePublicVerifyVO vo, Certificate certificate)
    {
        if (StringUtils.isNotEmpty(certificate.getVerificationCode()))
        {
            vo.setCode(certificate.getVerificationCode());
        }
        vo.setOrigin(certificate.getOrigin());
        vo.setPickDate(certificate.getPickDate());
        vo.setPackDate(certificate.getPackDate());
        vo.setLawyerName(certificate.getLawyerName());
        vo.setCertFile(certificate.getCertFile());
        vo.setCertImage(certificate.getCertImage());
        vo.setBaseDetailUrl(certificate.getBaseDetailUrl());
        if (StringUtils.isNotEmpty(certificate.getCodeUrl()))
        {
            vo.setCodeUrl(certificate.getCodeUrl());
        }
        else if (certificate.getCode() != null)
        {
            VerificationCode vc = verificationCodeMapper.selectVerificationCodeById(certificate.getCode());
            if (vc != null)
            {
                vo.setCodeUrl(vc.getCodeUrl());
            }
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
