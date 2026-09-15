package com.ruoyi.ym.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.ym.domain.VerificationCode;

public interface VerificationCodeMapper
{
    VerificationCode selectVerificationCodeById(Integer id);

    VerificationCode selectVerificationCodeByCode(String code);

    VerificationCode selectVerificationCodeByCodeIgnoreCase(String code);

    List<VerificationCode> selectVerificationCodeList(VerificationCode query);

    /** 未绑定证书的查询码 */
    List<VerificationCode> selectUnusedVerificationCodeList(@Param("productType") String productType);

    Integer selectMaxMonthlySeq(@Param("monthKey") String monthKey);

    int batchInsertVerificationCode(List<VerificationCode> list);

    int deleteVerificationCodeByIds(Integer[] ids);

    List<VerificationCode> selectVerificationCodeByIds(Integer[] ids);

    /**
     * 按查询码文本闭区间选取（同长度、字典序）
     */
    List<VerificationCode> selectByCodeRange(@Param("startCode") String startCode, @Param("endCode") String endCode);

    int bindCertificateRange(@Param("certificateId") Integer certificateId,
            @Param("startCode") String startCode,
            @Param("endCode") String endCode,
            @Param("boundAt") Date boundAt);

    int countBoundInRange(@Param("startCode") String startCode, @Param("endCode") String endCode);

    int bindCertificateByIds(@Param("certificateId") Integer certificateId,
            @Param("ids") List<Integer> ids,
            @Param("boundAt") Date boundAt);

    int countBoundByIds(@Param("ids") List<Integer> ids);

    int countByCertificateId(Integer certificateId);

    int clearBindingByCertificateIds(Integer[] certificateIds);
}
