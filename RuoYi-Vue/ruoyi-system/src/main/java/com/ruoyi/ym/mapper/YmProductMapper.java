package com.ruoyi.ym.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.ym.domain.YmProduct;

public interface YmProductMapper
{
    YmProduct selectYmProductById(Integer id);

    YmProduct selectYmProductByCode(@Param("productCode") String productCode);

    List<YmProduct> selectYmProductList(YmProduct query);

    int insertYmProduct(YmProduct product);

    int updateYmProduct(YmProduct product);

    int deleteYmProductByIds(Integer[] ids);

    int countCertificateByProductCode(@Param("productCode") String productCode);

    int countOriginByProductCode(@Param("productCode") String productCode);

    int countBatchByProductType(@Param("productCode") String productCode);

    int updateCertificateProductType(@Param("oldCode") String oldCode, @Param("newCode") String newCode);

    int updateOriginProductType(@Param("oldCode") String oldCode, @Param("newCode") String newCode);

    int updateBatchProductType(@Param("oldCode") String oldCode, @Param("newCode") String newCode);
}
