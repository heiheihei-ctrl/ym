package com.ruoyi.ym.service;

import java.util.List;
import com.ruoyi.ym.domain.Certificate;
import com.ruoyi.ym.domain.vo.CertificatePublicVerifyVO;

/**
 * 证书 Service
 *
 * @author ruoyi
 */
public interface ICertificateService
{
    Certificate selectCertificateById(Integer id);

    List<Certificate> selectCertificateList(Certificate certificate);

    int insertCertificate(Certificate certificate);

    int updateCertificate(Certificate certificate);

    int deleteCertificateByIds(Integer[] ids);

    /**
     * 公开查验见证书（无需登录）
     */
    CertificatePublicVerifyVO verifyByCodePublic(String code);
}
