package com.ruoyi.ym.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.ym.domain.VerificationCode;
import com.ruoyi.ym.domain.dto.VerificationCodeBatchRequest;
import com.ruoyi.ym.mapper.VerificationCodeMapper;
import com.ruoyi.ym.service.IVerificationCodeService;
import com.ruoyi.ym.utils.YmPublicUrlUtils;
import com.ruoyi.ym.utils.YmQrCodeHelper;
import com.ruoyi.ym.utils.YmVerificationQrZipUtils;

@Service
public class VerificationCodeServiceImpl implements IVerificationCodeService
{
    private static final int MAX_BATCH = 1000;

    /** 查询码流水号位数（前缀 + yyyyMM + 流水号） */
    private static final int SEQ_DIGIT_LENGTH = 6;

    @Autowired
    private VerificationCodeMapper verificationCodeMapper;

    @Autowired
    private YmPublicUrlUtils ymPublicUrlUtils;

    @Override
    public List<VerificationCode> selectVerificationCodeList(VerificationCode query)
    {
        return verificationCodeMapper.selectVerificationCodeList(query);
    }

    @Override
    public List<VerificationCode> selectUnusedVerificationCodeList()
    {
        return verificationCodeMapper.selectUnusedVerificationCodeList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchGenerate(VerificationCodeBatchRequest request)
    {
        String prefix = normalizePrefix(request.getPrefix());
        int count = request.getCount() == null ? 0 : request.getCount();
        if (count < 1 || count > MAX_BATCH)
        {
            throw new ServiceException("生成数量需在 1～" + MAX_BATCH + " 之间");
        }

        String monthKey = prefix + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        Integer maxSeq = verificationCodeMapper.selectMaxMonthlySeq(monthKey);
        int start = maxSeq == null ? 0 : maxSeq;
        long maxAllowed = (long) Math.pow(10, SEQ_DIGIT_LENGTH) - 1;
        if ((long) start + count > maxAllowed)
        {
            throw new ServiceException("本月流水号已达上限（" + SEQ_DIGIT_LENGTH + " 位，最大 " + maxAllowed + "）");
        }

        List<VerificationCode> list = new ArrayList<>(count);
        List<String> codes = new ArrayList<>(count);
        for (int i = 1; i <= count; i++)
        {
            String code = monthKey + String.format("%0" + SEQ_DIGIT_LENGTH + "d", start + i);
            if (verificationCodeMapper.selectVerificationCodeByCode(code) != null)
            {
                throw new ServiceException("查询码已存在：" + code);
            }
            VerificationCode row = new VerificationCode();
            row.setCode(code);
            row.setCodeUrl(generateQrCodeUrl(code));
            list.add(row);
            codes.add(code);
        }
        verificationCodeMapper.batchInsertVerificationCode(list);

        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("codes", codes);
        return result;
    }

    @Override
    public int deleteVerificationCodeByIds(Integer[] ids)
    {
        return verificationCodeMapper.deleteVerificationCodeByIds(ids);
    }

    @Override
    public byte[] downloadVerificationQrZip(Integer[] ids)
    {
        if (ids == null || ids.length == 0)
        {
            throw new ServiceException("请选择要下载的查询码");
        }
        List<VerificationCode> list = verificationCodeMapper.selectVerificationCodeByIds(ids);
        try
        {
            return YmVerificationQrZipUtils.buildZip(list);
        }
        catch (IOException e)
        {
            throw new ServiceException("打包下载失败：" + e.getMessage());
        }
    }

    private String generateQrCodeUrl(String codeLabel)
    {
        String scanUrl = ymPublicUrlUtils.buildPublicScanPageUrl(codeLabel);
        if (StringUtils.isEmpty(scanUrl))
        {
            throw new ServiceException("请配置 ruoyi.publicVerifyUrl（扫码页根地址）以生成二维码");
        }
        return YmQrCodeHelper.saveQrImage(scanUrl, codeLabel);
    }

    private String normalizePrefix(String prefix)
    {
        if (StringUtils.isEmpty(prefix))
        {
            throw new ServiceException("查询码前缀不能为空");
        }
        String p = prefix.trim().toUpperCase();
        if (!p.matches("^[A-Z]+$"))
        {
            throw new ServiceException("查询码前缀仅限字母，如 WM");
        }
        return p;
    }
}
