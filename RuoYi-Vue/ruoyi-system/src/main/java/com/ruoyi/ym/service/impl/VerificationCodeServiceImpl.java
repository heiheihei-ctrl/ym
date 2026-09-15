package com.ruoyi.ym.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.ym.domain.VerificationCode;
import com.ruoyi.ym.domain.dto.VerificationCodeBatchRequest;
import com.ruoyi.ym.mapper.VerificationCodeMapper;
import com.ruoyi.ym.service.IYmProductService;
import com.ruoyi.ym.service.IVerificationCodeService;
import com.ruoyi.ym.utils.NanoIdUtils;
import com.ruoyi.ym.utils.YmPublicUrlUtils;
import com.ruoyi.ym.utils.YmQrCodeHelper;
import com.ruoyi.ym.utils.YmVerificationQrZipUtils;
import java.io.IOException;

@Service
public class VerificationCodeServiceImpl implements IVerificationCodeService
{
    private static final int MAX_BATCH = 1000;
    private static final int MAX_COLLISION_RETRY = 8;

    @Autowired
    private VerificationCodeMapper verificationCodeMapper;

    @Autowired
    private YmPublicUrlUtils ymPublicUrlUtils;

    @Autowired
    private IYmProductService ymProductService;

    @Override
    public List<VerificationCode> selectVerificationCodeList(VerificationCode query)
    {
        return verificationCodeMapper.selectVerificationCodeList(query);
    }

    @Override
    public List<VerificationCode> selectUnusedVerificationCodeList(String productType)
    {
        String type = StringUtils.isEmpty(productType) ? null : productType.trim().toLowerCase();
        return verificationCodeMapper.selectUnusedVerificationCodeList(type);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchGenerate(VerificationCodeBatchRequest request)
    {
        int count = request.getCount() == null ? 0 : request.getCount();
        if (count < 1 || count > MAX_BATCH)
        {
            throw new ServiceException("生成数量需在 1～" + MAX_BATCH + " 之间");
        }
        String prefix = normalizeOptionalPrefix(request.getPrefix());
        String productType = normalizeProductType(request.getProductType());
        ymProductService.assertEnabledProductCode(productType);
        String createBy = SecurityUtils.getUsername();
        Date createdAt = new Date();

        List<VerificationCode> list = new ArrayList<>(count);
        List<String> codes = new ArrayList<>(count);
        Set<String> batchSet = new HashSet<>();
        for (int i = 0; i < count; i++)
        {
            String code = nextUniqueCode(prefix, batchSet);
            batchSet.add(code);
            VerificationCode row = new VerificationCode();
            row.setCode(code);
            row.setCodeUrl(generateQrCodeUrl(code));
            row.setProductType(productType);
            row.setCreatedAt(createdAt);
            row.setCreateBy(createBy);
            list.add(row);
            codes.add(code);
        }
        verificationCodeMapper.batchInsertVerificationCode(list);

        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("codes", codes);
        return result;
    }

    private String nextUniqueCode(String prefix, Set<String> batchSet)
    {
        for (int attempt = 0; attempt < MAX_COLLISION_RETRY; attempt++)
        {
            String code = prefix + NanoIdUtils.randomId();
            if (batchSet.contains(code))
            {
                continue;
            }
            if (verificationCodeMapper.selectVerificationCodeByCode(code) == null)
            {
                return code;
            }
        }
        throw new ServiceException("生成随机查询码失败，请重试");
    }

    private String normalizeProductType(String productType)
    {
        if (StringUtils.isEmpty(productType))
        {
            throw new ServiceException("请选择产品类型");
        }
        return productType.trim().toLowerCase();
    }

    /** 可选前缀：仅字母，可为空 */
    private String normalizeOptionalPrefix(String prefix)
    {
        if (StringUtils.isEmpty(prefix))
        {
            return "";
        }
        String p = prefix.trim().toUpperCase();
        if (!p.matches("^[A-Z]{0,10}$"))
        {
            throw new ServiceException("查询码前缀仅限字母（可留空）");
        }
        return p;
    }

    @Override
    public int deleteVerificationCodeByIds(Integer[] ids)
    {
        if (ids != null)
        {
            for (Integer id : ids)
            {
                VerificationCode vc = verificationCodeMapper.selectVerificationCodeById(id);
                if (vc != null && (vc.getBindStatus() != null && vc.getBindStatus() == VerificationCode.BIND_STATUS_BOUND
                        || vc.getCertificateId() != null))
                {
                    throw new ServiceException("查询码已绑定证书，无法删除：" + vc.getCode());
                }
            }
        }
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
}
