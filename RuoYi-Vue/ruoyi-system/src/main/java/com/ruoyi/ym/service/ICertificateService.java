package com.ruoyi.ym.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.ym.domain.Certificate;
import com.ruoyi.ym.domain.dto.CertificateRangeBindRequest;
import com.ruoyi.ym.domain.vo.CertificatePublicVerifyVO;

/**
 * 证书 Service
 */
public interface ICertificateService
{
    Certificate selectCertificateById(Integer id);

    List<Certificate> selectCertificateList(Certificate certificate);

    int insertCertificate(Certificate certificate);

    int updateCertificate(Certificate certificate);

    int deleteCertificateByIds(Integer[] ids);

    /**
     * 将包装序号区间绑定到已有见证书
     */
    Map<String, Object> bindCodeRange(CertificateRangeBindRequest request);

    /**
     * 公开查验见证书（无需登录），并记录扫码指纹
     */
    CertificatePublicVerifyVO verifyByCodePublic(String code);
}
