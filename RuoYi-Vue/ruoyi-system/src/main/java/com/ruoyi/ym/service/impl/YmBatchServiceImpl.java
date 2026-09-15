package com.ruoyi.ym.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.ym.domain.YmBatch;
import com.ruoyi.ym.mapper.YmBatchMapper;
import com.ruoyi.ym.service.IYmBatchService;
import com.ruoyi.ym.service.IYmProductService;

@Service
public class YmBatchServiceImpl implements IYmBatchService
{
    @Autowired
    private YmBatchMapper ymBatchMapper;

    @Autowired
    private IYmProductService ymProductService;

    @Override
    public YmBatch selectYmBatchById(Integer id)
    {
        return ymBatchMapper.selectYmBatchById(id);
    }

    @Override
    public List<YmBatch> selectYmBatchList(YmBatch query)
    {
        return ymBatchMapper.selectYmBatchList(query);
    }

    @Override
    public int insertYmBatch(YmBatch batch)
    {
        validate(batch, true);
        if (batch.getStatus() == null)
        {
            batch.setStatus(1);
        }
        if (ymBatchMapper.selectYmBatchByBatchNo(batch.getBatchNo()) != null)
        {
            throw new ServiceException("批次号已存在：" + batch.getBatchNo());
        }
        return ymBatchMapper.insertYmBatch(batch);
    }

    @Override
    public int updateYmBatch(YmBatch batch)
    {
        validate(batch, false);
        YmBatch existing = ymBatchMapper.selectYmBatchByBatchNo(batch.getBatchNo());
        if (existing != null && !existing.getId().equals(batch.getId()))
        {
            throw new ServiceException("批次号已存在：" + batch.getBatchNo());
        }
        return ymBatchMapper.updateYmBatch(batch);
    }

    @Override
    public int deleteYmBatchByIds(Integer[] ids)
    {
        return ymBatchMapper.deleteYmBatchByIds(ids);
    }

    private void validate(YmBatch batch, boolean creating)
    {
        if (StringUtils.isEmpty(batch.getBatchNo()))
        {
            throw new ServiceException("请填写批次号");
        }
        batch.setBatchNo(batch.getBatchNo().trim());
        if (StringUtils.isEmpty(batch.getName()))
        {
            throw new ServiceException("请填写批次名称");
        }
        batch.setName(batch.getName().trim());

        String type = batch.getProductType() == null ? "yangmei" : batch.getProductType().trim().toLowerCase();
        ymProductService.assertEnabledProductCode(type);
        batch.setProductType(type);

        if (StringUtils.isEmpty(batch.getOrigin()))
        {
            throw new ServiceException("请选择产地");
        }

        if ("rice".equals(type))
        {
            if (StringUtils.isEmpty(batch.getProcessor()))
            {
                throw new ServiceException("请填写加工包装厂家");
            }
            if (StringUtils.isEmpty(batch.getWarehouse()))
            {
                throw new ServiceException("请填写仓库");
            }
            if (batch.getHarvestDate() == null)
            {
                throw new ServiceException("请选择收割时间");
            }
            if (batch.getProcessDate() == null)
            {
                throw new ServiceException("请选择加工时间");
            }
            batch.setPickDate(null);
        }
        else
        {
            if (batch.getPickDate() == null)
            {
                throw new ServiceException("请选择采摘日期");
            }
            batch.setProcessor(null);
            batch.setWarehouse(null);
            batch.setHarvestDate(null);
            batch.setProcessDate(null);
        }

        if (batch.getVideoUrl() != null)
        {
            batch.setVideoUrl(batch.getVideoUrl().trim());
            if (batch.getVideoUrl().isEmpty())
            {
                batch.setVideoUrl(null);
            }
        }
        if (!creating && batch.getId() == null)
        {
            throw new ServiceException("缺少批次 ID");
        }
    }
}
