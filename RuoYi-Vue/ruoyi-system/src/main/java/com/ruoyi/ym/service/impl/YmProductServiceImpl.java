package com.ruoyi.ym.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.ym.domain.YmProduct;
import com.ruoyi.ym.mapper.YmProductMapper;
import com.ruoyi.ym.service.IYmProductService;

@Service
public class YmProductServiceImpl implements IYmProductService
{
    @Autowired
    private YmProductMapper ymProductMapper;

    @Override
    public YmProduct selectYmProductById(Integer id)
    {
        return ymProductMapper.selectYmProductById(id);
    }

    @Override
    public YmProduct selectYmProductByCode(String productCode)
    {
        if (StringUtils.isEmpty(productCode))
        {
            return null;
        }
        return ymProductMapper.selectYmProductByCode(productCode.trim().toLowerCase());
    }

    @Override
    public List<YmProduct> selectYmProductList(YmProduct query)
    {
        return ymProductMapper.selectYmProductList(query);
    }

    @Override
    public List<YmProduct> selectEnabledProductList()
    {
        YmProduct query = new YmProduct();
        query.setStatus(1);
        return ymProductMapper.selectYmProductList(query);
    }

    @Override
    public void assertEnabledProductCode(String productCode)
    {
        YmProduct product = selectYmProductByCode(productCode);
        if (product == null || product.getStatus() == null || product.getStatus() != 1)
        {
            throw new ServiceException("产品类型无效或已禁用，请先在「产品管理」中维护");
        }
    }

    @Override
    public int insertYmProduct(YmProduct product)
    {
        normalizeForSave(product, true);
        if (ymProductMapper.selectYmProductByCode(product.getProductCode()) != null)
        {
            throw new ServiceException("产品编码已存在");
        }
        return ymProductMapper.insertYmProduct(product);
    }

    @Override
    public int updateYmProduct(YmProduct product)
    {
        if (product.getId() == null)
        {
            throw new ServiceException("缺少产品 ID");
        }
        YmProduct existing = ymProductMapper.selectYmProductById(product.getId());
        if (existing == null)
        {
            throw new ServiceException("产品不存在");
        }
        String oldCode = existing.getProductCode();
        normalizeForSave(product, true);
        String newCode = product.getProductCode();
        if (!newCode.equals(oldCode))
        {
            YmProduct dup = ymProductMapper.selectYmProductByCode(newCode);
            if (dup != null && !dup.getId().equals(product.getId()))
            {
                throw new ServiceException("产品编号已存在");
            }
        }
        product.setSortOrder(existing.getSortOrder());
        product.setRemark(existing.getRemark());
        int rows = ymProductMapper.updateYmProduct(product);
        if (rows > 0 && !newCode.equals(oldCode))
        {
            ymProductMapper.updateCertificateProductType(oldCode, newCode);
            ymProductMapper.updateOriginProductType(oldCode, newCode);
            ymProductMapper.updateBatchProductType(oldCode, newCode);
        }
        return rows;
    }

    @Override
    public int deleteYmProductByIds(Integer[] ids)
    {
        if (ids == null || ids.length == 0)
        {
            return 0;
        }
        for (Integer id : ids)
        {
            YmProduct p = ymProductMapper.selectYmProductById(id);
            if (p == null)
            {
                continue;
            }
            int certCnt = ymProductMapper.countCertificateByProductCode(p.getProductCode());
            int originCnt = ymProductMapper.countOriginByProductCode(p.getProductCode());
            if (certCnt > 0 || originCnt > 0)
            {
                throw new ServiceException("产品「" + p.getProductName() + "」已被见证书或产地引用，无法删除");
            }
        }
        return ymProductMapper.deleteYmProductByIds(ids);
    }

    private void normalizeForSave(YmProduct product, boolean isCreate)
    {
        if (isCreate)
        {
            if (StringUtils.isEmpty(product.getProductCode()))
            {
                throw new ServiceException("请填写产品编码");
            }
            String code = product.getProductCode().trim().toLowerCase();
            if (!code.matches("^[a-z][a-z0-9_]{1,31}$"))
            {
                throw new ServiceException("产品编码仅支持小写字母、数字、下划线，且以字母开头");
            }
            product.setProductCode(code);
        }
        if (StringUtils.isEmpty(product.getProductName()))
        {
            throw new ServiceException("请填写产品名称");
        }
        product.setProductName(product.getProductName().trim());
        if (product.getSortOrder() == null)
        {
            product.setSortOrder(0);
        }
        if (product.getStatus() == null)
        {
            product.setStatus(1);
        }
    }
}
