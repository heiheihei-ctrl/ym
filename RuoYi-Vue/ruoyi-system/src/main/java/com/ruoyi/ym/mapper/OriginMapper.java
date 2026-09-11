package com.ruoyi.ym.mapper;

import java.util.List;
import com.ruoyi.ym.domain.Origin;

/**
 * 产地来源 Mapper
 *
 * @author ruoyi
 */
public interface OriginMapper
{
    Origin selectOriginById(Integer id);

    List<Origin> selectOriginList(Origin origin);

    int insertOrigin(Origin origin);

    int updateOrigin(Origin origin);

    int deleteOriginById(Integer id);

    int deleteOriginByIds(Integer[] ids);
}
