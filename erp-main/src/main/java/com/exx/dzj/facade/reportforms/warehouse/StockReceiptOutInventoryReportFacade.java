package com.exx.dzj.facade.reportforms.warehouse;

import com.exx.dzj.constant.CommonConstant;
import com.exx.dzj.entity.bean.StockInfoQuery;
import com.exx.dzj.entity.statistics.warehouse.StockReceiptOutReport;
import com.exx.dzj.service.statistics.warehouse.StockReceiptOutInventoryReportService;
import com.exx.dzj.util.MathUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName StockReceiptOutInventoryReportFacade
 * @Description:
 * @Author yangyun
 * @Date 2019/9/9 0009 18:01
 * @Version 1.0
 **/
@Component
public class StockReceiptOutInventoryReportFacade {

    @Autowired
    private StockReceiptOutInventoryReportService stockReceiptOutInventoryService;

    public Map<String, Object> queryReceiptOutInventoryList (StockInfoQuery query){
        List<StockReceiptOutReport> colloct = stockReceiptOutInventoryService.queryReceiptOutInventoryList(query);

        String startDate = query.getStartDate();

        BigDecimal beginningCost = BigDecimal.ZERO;

        // 结存数量和成本
        Double resMinInventory = 0.0;
        BigDecimal resCost = BigDecimal.ZERO;

        for (StockReceiptOutReport temp : colloct){
            if (StringUtils.isBlank(startDate)){
                temp.setBeginningCost(BigDecimal.ZERO).setBeginningMinInventory(Double.valueOf(CommonConstant.DEFAULT_VALUE_ZERO))
                        .setBeginningPrice(BigDecimal.ZERO);
            } else {
                beginningCost = temp.getAvgPrice().multiply(new BigDecimal(temp.getBeginningMinInventory()));
                temp.setBeginningCost(MathUtil.keepTwoBigdecimal(beginningCost, new BigDecimal(1), CommonConstant.DEFAULT_VALUE_FOUR));
                temp.setBeginningPrice(temp.getAvgPrice());
            }
            resMinInventory = temp.getBeginningMinInventory() + temp.getReceiptInventoryNum() - temp.getOutInventoryNum();
            resCost = temp.getBeginningPrice().multiply(new BigDecimal(resMinInventory));
            temp.setMinInventory(resMinInventory).setCost(MathUtil.keepTwoBigdecimal(resCost, new BigDecimal(1), CommonConstant.DEFAULT_VALUE_FOUR));
            temp.setOutCost(MathUtil.keepTwoBigdecimal(temp.getOutCost(), new BigDecimal(1), CommonConstant.DEFAULT_VALUE_FOUR)).
                    setReceiptCost(MathUtil.keepTwoBigdecimal(temp.getReceiptCost(), new BigDecimal(1), CommonConstant.DEFAULT_VALUE_FOUR));
        }

        LinkedHashMap<String, List<StockReceiptOutReport>> linkedMap = new LinkedHashMap<>();

        List<StockReceiptOutReport> list = null;
        for (StockReceiptOutReport temp: colloct){
            list = linkedMap.get(temp.getStockClassName()) == null ? null : linkedMap.get(temp.getStockClassName());
            if (list == null){
                list = new ArrayList<>();
                linkedMap.put(temp.getStockClassName(), list);
            }
            list.add(temp);

        }

        StockReceiptOutReport sum = new StockReceiptOutReport();
        double sumReNum = colloct.stream().mapToDouble(StockReceiptOutReport::getReceiptInventoryNum).sum();
        BigDecimal sumReCost = colloct.stream().map(StockReceiptOutReport::getReceiptCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        double sumOutInventoryNum = colloct.stream().mapToDouble(StockReceiptOutReport::getOutInventoryNum).sum();
        BigDecimal sumOutCost = colloct.stream().map(StockReceiptOutReport::getOutCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        double sumMinInventory = colloct.stream().mapToDouble(StockReceiptOutReport::getMinInventory).sum();
        BigDecimal sumCost = colloct.stream().map(StockReceiptOutReport::getCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        double beginSum = colloct.stream().mapToDouble(StockReceiptOutReport::getBeginningMinInventory).sum();
        BigDecimal BeginSumCost = colloct.stream().map(StockReceiptOutReport::getBeginningCost).reduce(BigDecimal.ZERO, BigDecimal::add);

        sum.setReceiptInventoryNum(sumReNum).setReceiptCost(MathUtil.keepTwoBigdecimal(sumReCost, new BigDecimal(1), CommonConstant.DEFAULT_VALUE_FOUR)).setOutInventoryNum(sumOutInventoryNum).
                setOutCost(MathUtil.keepTwoBigdecimal(sumOutCost, new BigDecimal(1), CommonConstant.DEFAULT_VALUE_FOUR)).setMinInventory(sumMinInventory).setCost(MathUtil.keepTwoBigdecimal(sumCost, new BigDecimal(1), CommonConstant.DEFAULT_VALUE_FOUR)).setMeterUnit("总计: ")
                .setBeginningMinInventory(beginSum).setBeginningCost(MathUtil.keepTwoBigdecimal(BeginSumCost, new BigDecimal(1), CommonConstant.DEFAULT_VALUE_FOUR));


        Collection<List<StockReceiptOutReport>> values = linkedMap.values();
        double reNum = 0.0;
        double outNum = 0.0;
        double minInventory = 0.0;
        BigDecimal reCost = BigDecimal.ZERO;
        BigDecimal outCost = BigDecimal.ZERO;
        BigDecimal cost = BigDecimal.ZERO;
        double beginningSumNum = 0.0; // 期初总计数量
        BigDecimal beginningSumCost = BigDecimal.ZERO; // 期初成本总计
        StockReceiptOutReport res = null;
        for (List<StockReceiptOutReport> data : values){
            res = new StockReceiptOutReport();
            reNum = data.stream().mapToDouble(StockReceiptOutReport::getReceiptInventoryNum).sum();
            reCost = data.stream().map(StockReceiptOutReport::getReceiptCost).reduce(BigDecimal.ZERO, BigDecimal::add);
            outNum = data.stream().mapToDouble(StockReceiptOutReport::getOutInventoryNum).sum();
            outCost = data.stream().map(StockReceiptOutReport::getOutCost).reduce(BigDecimal.ZERO, BigDecimal::add);
            minInventory = data.stream().mapToDouble(StockReceiptOutReport::getMinInventory).sum();
            cost = data.stream().map(StockReceiptOutReport::getCost).reduce(BigDecimal.ZERO, BigDecimal::add);
            beginningSumNum = data.stream().mapToDouble(StockReceiptOutReport::getBeginningMinInventory).sum();
            beginningSumCost = data.stream().map(StockReceiptOutReport::getBeginningCost).reduce(BigDecimal.ZERO, BigDecimal::add);
            res.setReceiptInventoryNum(reNum).setReceiptCost(MathUtil.keepTwoBigdecimal(reCost, new BigDecimal(1), CommonConstant.DEFAULT_VALUE_FOUR)).
                    setOutInventoryNum(outNum).setBeginningMinInventory(beginningSumNum)
                    .setBeginningCost(MathUtil.keepTwoBigdecimal(beginningSumCost, new BigDecimal(1), CommonConstant.DEFAULT_VALUE_FOUR)).
                    setOutCost(MathUtil.keepTwoBigdecimal(outCost, new BigDecimal(1), CommonConstant.DEFAULT_VALUE_FOUR)).
                    setMinInventory(MathUtil.keepTwoBigdecimal(new BigDecimal(minInventory), new BigDecimal(1), CommonConstant.DEFAULT_VALUE_FOUR).doubleValue()).
                    setCost(MathUtil.keepTwoBigdecimal(cost, new BigDecimal(1), CommonConstant.DEFAULT_VALUE_FOUR)).setMeterUnit("合计: ");
            data.add(res);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("data", linkedMap);
        map.put("len", linkedMap == null ? 0 : linkedMap.size());
        map.put("sum", sum);

        return map;
    }

}
