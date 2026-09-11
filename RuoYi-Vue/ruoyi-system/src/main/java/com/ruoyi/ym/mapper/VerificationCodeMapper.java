package com.ruoyi.ym.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.ym.domain.VerificationCode;

public interface VerificationCodeMapper
{
    VerificationCode selectVerificationCodeById(Integer id);

    VerificationCode selectVerificationCodeByCode(String code);

    VerificationCode selectVerificationCodeByCodeIgnoreCase(String code);

    List<VerificationCode> selectVerificationCodeList(VerificationCode query);

    /** 未被 certificates 绑定的查询码 */
    List<VerificationCode> selectUnusedVerificationCodeList();

    Integer selectMaxMonthlySeq(@Param("monthKey") String monthKey);

    int batchInsertVerificationCode(List<VerificationCode> list);

    int deleteVerificationCodeByIds(Integer[] ids);

    List<VerificationCode> selectVerificationCodeByIds(Integer[] ids);
}
