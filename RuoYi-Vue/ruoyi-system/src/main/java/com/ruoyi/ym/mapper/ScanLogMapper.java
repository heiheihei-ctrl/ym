package com.ruoyi.ym.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.ym.domain.ScanLog;

public interface ScanLogMapper
{
    int insertScanLog(ScanLog log);

    long countByVerificationId(Integer verificationId);

    Date selectFirstScanAt(Integer verificationId);

    List<ScanLog> selectRecentByVerificationId(@Param("verificationId") Integer verificationId, @Param("limit") int limit);
}
