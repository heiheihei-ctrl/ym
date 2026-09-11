package com.ruoyi.ym.service;

import java.util.List;
import com.ruoyi.ym.domain.Origin;

/**
 * 产地来源 Service
 *
 * @author ruoyi
 */
public interface IOriginService
{
    Origin selectOriginById(Integer id);

    List<Origin> selectOriginList(Origin origin);

    int insertOrigin(Origin origin);

    int updateOrigin(Origin origin);

    int deleteOriginByIds(Integer[] ids);
}
