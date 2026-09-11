package com.ruoyi.ym.mapper;

import java.util.List;
import com.ruoyi.ym.domain.Certificate;

/**
 * 证书 Mapper
 *
 * @author ruoyi
 */
public interface CertificateMapper
{
    Certificate selectCertificateById(Integer id);

    Certificate selectCertificateByVerificationId(Integer verificationId);

    List<Certificate> selectCertificateList(Certificate certificate);

    int insertCertificate(Certificate certificate);

    int updateCertificate(Certificate certificate);

    int deleteCertificateById(Integer id);

    int deleteCertificateByIds(Integer[] ids);
}
