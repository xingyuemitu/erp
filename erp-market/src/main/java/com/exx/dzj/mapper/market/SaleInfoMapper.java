package com.exx.dzj.mapper.market;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exx.dzj.entity.market.*;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;

import java.math.BigDecimal;
import java.util.List;

public interface SaleInfoMapper extends BaseMapper<SaleInfo> {
    long countByExample(SaleInfoExample example);

    int deleteByExample(SaleInfoExample example);

    int deleteByPrimaryKey(Integer id);

    @Override
    int insert(SaleInfo record);

    int insertSelective(SaleInfo record);

    List<SaleInfo> querySalesTicketList(SaleInfoQuery query);

    List<SaleInfo> getSalesTicketList(@Param("query") SaleInfoQuery query, @Param("ew") Wrapper<T> queryWrapper);

    SaleInfo selectByPrimaryKey(Integer id);

    List<SaleInfo> printSalesTicket(@Param("saleCodes") List<String> saleCodes);

    int updateByExampleSelective(@Param("record") SaleInfo record, @Param("example") SaleInfoExample example);

    int updateByExample(@Param("record") SaleInfo record, @Param("example") SaleInfoExample example);

    int updateByPrimaryKeySelective(SaleInfo record);

    int updateByPrimaryKey(SaleInfo record);

    void logisticsInfoDel(@Param("id") Integer id);

    List<SaleInfo> querySumSalesOnDay();

    List<SaleInfo> querySumSalesOnMonth();

    List<SaleInfo> queryAdditionalSumSalesOnDay();

    List<SaleInfo> queryAdditionalSumSalesOnMonth();

    List<SaleInfo> querySumSalesOnYear();

    List<SaleInfo> querySalesTop(@Param("type") String type);

    List<SaleInfo> salesUncollectedTop();

    List<SaleInfo> querySalesTicketTop();

    List<SaleInfo> queryCustomerSalesToday (SaleInfo saleInfo);

    List<SaleGoodsTop> querySaleGoodsTop(@Param("type") String type);

    List<SaleInfo> querySubordinateCompanySelect();

    int querySaleBySaleCode(@Param("saleCode") String saleCode);

    void updateSalesmanSubordinateCompany(SaleInfo saleInfo);

    List<SaleListInfo> querySalesListForIds(SaleInfoQuery query);

    List<SaleListInfo> exportSaleList(SaleInfoQuery query);

    int updatReceiptStatus(SaleInfo saleInfo);

    void updateReceivableAccoun(@Param("saleCode") String saleCode, @Param("receivableAccoun") BigDecimal receivableAccoun);

    void batchInsertLogistics(List<LogisticsInfo> list);
}