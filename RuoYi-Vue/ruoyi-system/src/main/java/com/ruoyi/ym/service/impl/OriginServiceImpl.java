package com.ruoyi.ym.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ym.domain.Origin;
import com.ruoyi.ym.mapper.OriginMapper;
import com.ruoyi.ym.service.IOriginService;

/**
 * 产地来源 Service 实现
 *
 * @author ruoyi
 */
@Service
public class OriginServiceImpl implements IOriginService
{
    @Autowired
    private OriginMapper originMapper;

    @Override
    public Origin selectOriginById(Integer id)
    {
        return originMapper.selectOriginById(id);
    }

    @Override
    public List<Origin> selectOriginList(Origin origin)
    {
        return originMapper.selectOriginList(origin);
    }

    @Override
    public int insertOrigin(Origin origin)
    {
        return originMapper.insertOrigin(origin);
    }

    @Override
    public int updateOrigin(Origin origin)
    {
        return originMapper.updateOrigin(origin);
    }

    @Override
    public int deleteOriginByIds(Integer[] ids)
    {
        return originMapper.deleteOriginByIds(ids);
    }
}
