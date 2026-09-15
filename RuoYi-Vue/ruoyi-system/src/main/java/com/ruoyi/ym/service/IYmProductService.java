package com.ruoyi.ym.service;

import java.util.List;
import com.ruoyi.ym.domain.YmProduct;

public interface IYmProductService
{
    YmProduct selectYmProductById(Integer id);

    YmProduct selectYmProductByCode(String productCode);

    List<YmProduct> selectYmProductList(YmProduct query);

    List<YmProduct> selectEnabledProductList();

    void assertEnabledProductCode(String productCode);

    int insertYmProduct(YmProduct product);

    int updateYmProduct(YmProduct product);

    int deleteYmProductByIds(Integer[] ids);
}
