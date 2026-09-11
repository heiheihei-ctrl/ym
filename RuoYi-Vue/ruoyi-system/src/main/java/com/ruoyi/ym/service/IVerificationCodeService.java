package com.ruoyi.ym.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.ym.domain.VerificationCode;
import com.ruoyi.ym.domain.dto.VerificationCodeBatchRequest;

public interface IVerificationCodeService
{
    List<VerificationCode> selectVerificationCodeList(VerificationCode query);

    List<VerificationCode> selectUnusedVerificationCodeList();

    Map<String, Object> batchGenerate(VerificationCodeBatchRequest request);

    int deleteVerificationCodeByIds(Integer[] ids);

    /** 多选打包下载扫码二维码（ZIP） */
    byte[] downloadVerificationQrZip(Integer[] ids);
}
