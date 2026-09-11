package com.ruoyi.ym.service;

import java.util.List;
import com.ruoyi.ym.domain.Lawyer;

/**
 * 律师信息 Service
 *
 * @author ruoyi
 */
public interface ILawyerService
{
    Lawyer selectLawyerById(Integer id);

    List<Lawyer> selectLawyerList(Lawyer lawyer);

    int insertLawyer(Lawyer lawyer);

    int updateLawyer(Lawyer lawyer);

    int deleteLawyerByIds(Integer[] ids);
}
