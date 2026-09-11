package com.ruoyi.ym.mapper;

import java.util.List;
import com.ruoyi.ym.domain.Lawyer;

/**
 * 律师信息 Mapper
 *
 * @author ruoyi
 */
public interface LawyerMapper
{
    Lawyer selectLawyerById(Integer id);

    Lawyer selectLawyerByUserId(Long userId);

    List<Lawyer> selectLawyerList(Lawyer lawyer);

    int insertLawyer(Lawyer lawyer);

    int updateLawyer(Lawyer lawyer);

    int deleteLawyerById(Integer id);

    int deleteLawyerByIds(Integer[] ids);
}
