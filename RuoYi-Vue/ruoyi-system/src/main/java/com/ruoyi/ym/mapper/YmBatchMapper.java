package com.ruoyi.ym.mapper;

import java.util.List;
import com.ruoyi.ym.domain.YmBatch;

public interface YmBatchMapper
{
    YmBatch selectYmBatchById(Integer id);

    YmBatch selectYmBatchByBatchNo(String batchNo);

    List<YmBatch> selectYmBatchList(YmBatch query);

    int insertYmBatch(YmBatch batch);

    int updateYmBatch(YmBatch batch);

    int deleteYmBatchByIds(Integer[] ids);
}
