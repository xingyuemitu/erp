package com.exx.dzj.mapper.stock;

import com.exx.dzj.entity.stock.StockNumPrice;
import com.exx.dzj.entity.stock.StockNumPriceExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StockNumPriceMapper {
    long countByExample(StockNumPriceExample example);

    int insertSelective(StockNumPrice record);

    List<StockNumPrice> selectByExample(StockNumPriceExample example);

    StockNumPrice selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") StockNumPrice record, @Param("example") StockNumPriceExample example);

    int updateByPrimaryKeySelective(StockNumPrice record);
}