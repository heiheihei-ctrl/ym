package com.ruoyi.ym.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.ym.domain.Lawyer;
import com.ruoyi.ym.mapper.LawyerMapper;
import com.ruoyi.ym.service.ILawyerService;
import com.ruoyi.ym.utils.LawyerNoUtils;

/**
 * 律师信息 Service 实现
 *
 * @author ruoyi
 */
@Service
public class LawyerServiceImpl implements ILawyerService
{
    @Autowired
    private LawyerMapper lawyerMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public Lawyer selectLawyerById(Integer id)
    {
        return lawyerMapper.selectLawyerById(id);
    }

    @Override
    public List<Lawyer> selectLawyerList(Lawyer lawyer)
    {
        return lawyerMapper.selectLawyerList(lawyer);
    }

    @Override
    public int insertLawyer(Lawyer lawyer)
    {
        fillAndCheckLawyerUser(lawyer);
        return lawyerMapper.insertLawyer(lawyer);
    }

    @Override
    public int updateLawyer(Lawyer lawyer)
    {
        fillAndCheckLawyerUser(lawyer);
        return lawyerMapper.updateLawyer(lawyer);
    }

    private void fillAndCheckLawyerUser(Lawyer lawyer)
    {
        if (lawyer.getUserId() == null)
        {
            throw new ServiceException("请选择系统用户");
        }
        SysUser user = sysUserMapper.selectUserById(lawyer.getUserId());
        if (user == null)
        {
            throw new ServiceException("系统用户不存在");
        }
        Lawyer exists = lawyerMapper.selectLawyerByUserId(lawyer.getUserId());
        if (exists != null && (lawyer.getId() == null || !exists.getId().equals(lawyer.getId())))
        {
            throw new ServiceException("用户「" + user.getUserName() + "」已绑定律师信息");
        }
        if (StringUtils.isEmpty(lawyer.getPhone()) && StringUtils.isNotEmpty(user.getPhonenumber()))
        {
            lawyer.setPhone(user.getPhonenumber());
        }
        if (StringUtils.isEmpty(lawyer.getRealName()) && StringUtils.isNotEmpty(user.getNickName()))
        {
            lawyer.setRealName(user.getNickName());
        }
        LawyerNoUtils.validate(lawyer.getLawyerNo());
        lawyer.setLawyerNo(lawyer.getLawyerNo().trim());
    }

    @Override
    public int deleteLawyerByIds(Integer[] ids)
    {
        return lawyerMapper.deleteLawyerByIds(ids);
    }
}
