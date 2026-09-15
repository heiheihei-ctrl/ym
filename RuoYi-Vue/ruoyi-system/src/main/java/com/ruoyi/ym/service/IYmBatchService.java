package com.ruoyi.ym.service;

import java.util.List;
import com.ruoyi.ym.domain.YmBatch;

public interface IYmBatchService
{
    YmBatch selectYmBatchById(Integer id);

    List<YmBatch> selectYmBatchList(YmBatch query);

    int insertYmBatch(YmBatch batch);

    int updateYmBatch(YmBatch batch);

    int deleteYmBatchByIds(Integer[] ids);
}
