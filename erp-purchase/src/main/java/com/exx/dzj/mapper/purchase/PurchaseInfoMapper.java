package com.exx.dzj.mapper.purchase;

import com.exx.dzj.entity.purchase.PurchaseInfo;
import com.exx.dzj.entity.purchase.PurchaseInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PurchaseInfoMapper {
    long countByExample(PurchaseInfoExample example);

    int deleteByExample(PurchaseInfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(PurchaseInfo record);

    int insertSelective(PurchaseInfo record);

    List<PurchaseInfo> selectByExample(PurchaseInfoExample example);

    PurchaseInfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") PurchaseInfo record, @Param("example") PurchaseInfoExample example);

    int updateByExample(@Param("record") PurchaseInfo record, @Param("example") PurchaseInfoExample example);

    int updateByPrimaryKeySelective(PurchaseInfo record);

    int updateByPrimaryKey(PurchaseInfo record);
}