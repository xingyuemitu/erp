package com.exx.dzj.common.export;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.*;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.exx.dzj.bean.SaleDetailReportQuery;
import com.exx.dzj.constant.CommonConstant;
import com.exx.dzj.entity.bean.CustomerQuery;
import com.exx.dzj.entity.bean.StockInfoQuery;
import com.exx.dzj.entity.bean.UserInfoQuery;
import com.exx.dzj.entity.market.*;
import com.exx.dzj.entity.statistics.sales.*;
import com.exx.dzj.entity.user.UserVo;
import com.exx.dzj.enummodel.PayStatusEnum;
import com.exx.dzj.enummodel.SaleListFieldEnum;
import com.exx.dzj.enummodel.SaleSourceEnum;
import com.exx.dzj.enummodel.SalesClassesEnum;
import com.exx.dzj.excepte.ErpException;
import com.exx.dzj.util.DateUtil;
import com.exx.dzj.util.MathUtil;
import com.exx.dzj.util.enums.ExportFileNameEnum;
import com.exx.dzj.util.excel.export.model.*;
import lombok.Cleanup;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FontFamily;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author yangyun
 * @create 2019-04-16-17:21
 */
public class SaleExportUtils extends ExportUtil {

    /**
     * @description 导出, 根据存货
     * @author yangyun
     * @date 2019/4/24 0024
     * @param outputStream
     * @param stockTypeReports
     * @param query
     * @return com.alibaba.excel.ExcelWriter
     */
    public static ExcelWriter inventorySaleExport (ServletOutputStream outputStream, List<StockTypeReport> stockTypeReports, StockInfoQuery query, UserVo userVo) throws IOException {
        int count = 0;
        ExcelWriter writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX);
        if (CollectionUtils.isEmpty(stockTypeReports)){
            return writer;
        }

        Map<Integer, Integer> map = new HashMap<>();
        map.put(0,4000);
        map.put(1,4000);
        map.put(2,4000);
        map.put(3,4000);
        map.put(4,4000);
        map.put(5,4000);
        map.put(6,4000);
        map.put(7,2800);
        map.put(8,2000);
        map.put(9,4000);
        map.put(10,2000);
        map.put(11,2800);
        map.put(12,4000);
        map.put(13,4000);

        String sheetName = ExportFileNameEnum.INVENTORY_SALE_NAME.getValue();
        Sheet sheet = gainSheet(map, sheetName, 1);

        Table title = new Table(1);
        title.setClazz(InventorySaleModel.class);

        writer.write(null, sheet, title);

        List<InventorySaleModel> data1 = new ArrayList<>();
        InventorySaleModel item = new InventorySaleModel();
        item.setStockTypeName("日期: 由" + query.getStartDate());
        item.setVacant("至" + query.getEndDate());
        item.setGrossMargin("打印日期: ");
        item.setGrossRate(DateUtil.getDate(new Date()));
        data1.add(item);
        writer.write(data1, sheet);


        List<InventorySaleModel> dataStock1 = new ArrayList<>();
        for (StockTypeReport stockType : stockTypeReports){
            InventorySaleModel item1 = new InventorySaleModel();

            List<StockInfoReport> stockReportList = stockType.getStockReportList();
            if (CollectionUtils.isEmpty(stockReportList)){
                continue;
            }
            for (StockInfoReport stockinfo : stockReportList){
                List<SaleGoodsReport> saleInfoReports = stockinfo.getSaleInfoReports();

                if (CollectionUtils.isEmpty(saleInfoReports)){
                    continue;
                }

                if (count == CommonConstant.DEFAULT_VALUE_ZERO){
                    item1.setStockTypeName(stockType.getStockTypeName());
                    item1.setVacant("存货编号: " + stockinfo.getStockCode());
                    item1.setCreateTime("存货名称: " + stockinfo.getStockName());
                    item1.setCustName("单位: " + (stockinfo.getMeterUnit() == null ? "" : stockinfo.getMeterUnit()));
                    count ++;
                    dataStock1.add(item1);
                } else {
                    item1 = new InventorySaleModel();
                    item1.setVacant("存货编号: " + stockinfo.getStockCode());
                    item1.setRealName("存货名称: ");
                    item1.setCreateTime(stockinfo.getStockName());
                    item1.setCustName("单位: " + (stockinfo.getMeterUnit() == null ? "" : stockinfo.getMeterUnit()));
                    dataStock1.add(item1);
                }
                for (SaleGoodsReport saleGoods: saleInfoReports){
                    InventorySaleModel itemSaleGoods = new InventorySaleModel();
                    itemSaleGoods.setSaleCode(saleGoods.getSaleCode());
                    itemSaleGoods.setRealName(saleGoods.getRealName());
                    itemSaleGoods.setCreateTime(DateUtil.getDate(saleGoods.getSaleDate()));
                    itemSaleGoods.setStockAddress(saleGoods.getStockAddress());
                    itemSaleGoods.setCustName(saleGoods.getCustName());
                    itemSaleGoods.setGoodsNum(saleGoods.getGoodsNum());
                    itemSaleGoods.setUnitPrice(saleGoods.getUnitPrice());
                    itemSaleGoods.setSalesVolume(saleGoods.getSalesVolume());
                    itemSaleGoods.setStandardBuyPrice(saleGoods.getStandardBuyPrice());
                    itemSaleGoods.setCost(saleGoods.getCost());
                    itemSaleGoods.setGrossMargin(saleGoods.getGrossMargin().toString());
                    itemSaleGoods.setGrossRate(saleGoods.getGrossRate().toString() + "%");
                    dataStock1.add(itemSaleGoods);
                }
                InventorySaleModel subtotal = new InventorySaleModel();
                subtotal.setStockAddress("小计: ");
                subtotal.setGoodsNum(stockinfo.getCountTotal());
                subtotal.setSalesVolume(stockinfo.getSalesTotal());
                subtotal.setCost(stockinfo.getCostTotal());
                subtotal.setGrossMargin(stockinfo.getGrossTotal().toString());
                subtotal.setGrossRate(stockinfo.getGrossRateTotal()+"%");
                dataStock1.add(subtotal);
            }
            count = CommonConstant.DEFAULT_VALUE_ZERO;
            InventorySaleModel summation = new InventorySaleModel();
            summation.setStockAddress("合计: ");
            summation.setGoodsNum(stockType.getCountTotal());
            summation.setSalesVolume(stockType.getSalesTotal());
            summation.setCost(stockType.getCostTotal());
            summation.setGrossMargin(stockType.getGrossTotal().toString());
            summation.setGrossRate(stockType.getGrossRateTotal()+"%");
            dataStock1.add(summation);
        }

        double sum = stockTypeReports.stream().mapToDouble(StockTypeReport::getCountTotal).sum(); // 数量总计
        BigDecimal salesTotal = stockTypeReports.stream().map(StockTypeReport::getSalesTotal).reduce(BigDecimal.ZERO, BigDecimal::add);// 销售额总计
        BigDecimal costTotal = stockTypeReports.stream().map(StockTypeReport::getCostTotal).reduce(BigDecimal.ZERO, BigDecimal::add); // 成本总计
        BigDecimal grossTotal = stockTypeReports.stream().map(StockTypeReport::getGrossTotal).reduce(BigDecimal.ZERO, BigDecimal::add); // 毛利总计
        BigDecimal rateTotal = MathUtil.keepTwoBigdecimal(grossTotal, costTotal, CommonConstant.DEFAULT_VALUE_FOUR);//毛利率总计
        InventorySaleModel summation = new InventorySaleModel();
        summation.setStockAddress("总计: ");
        summation.setGoodsNum(sum);
        summation.setSalesVolume(salesTotal);
        summation.setCost(costTotal);
        summation.setGrossMargin(grossTotal.toString());
        summation.setGrossRate(rateTotal.toString() + "");
        dataStock1.add(summation);

        writer.write(dataStock1, sheet);



        List<InventorySaleModel> dataEnd = new ArrayList<>();
        InventorySaleModel itemEnd = new InventorySaleModel();
        dataEnd.add(itemEnd);
        writer.write(dataEnd, sheet);
        itemEnd.setStockTypeName("公司名称: 正诚文化");
        itemEnd.setCreateTime("操作员: " + userVo.getRealName());
        writer.write(dataEnd, sheet);

        return writer;
    }

    /**
     * @description 导出, 根据销售员
     * @author yangyun
     * @date 2019/4/24 0024
     * @param outputStream
     * @param mapData
     * @param query
     * @return com.alibaba.excel.ExcelWriter
     */
    public static ExcelWriter salesManSaleExport (ServletOutputStream outputStream, Map<String, Object> mapData, UserInfoQuery query, UserVo userVo){
        int count = 0;
        ExcelWriter writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX);

        Map<Integer, Integer> map = new HashMap<>();
        map.put(0,4000);
        map.put(1,4000);
        map.put(2,4000);
        map.put(3,4000);
        map.put(4,4000);
        map.put(5,4000);
        map.put(6,4000);
        map.put(7,2800);
        map.put(8,2000);
        map.put(9,4000);
        map.put(10,2000);
        map.put(11,2800);
        map.put(12,4000);
        map.put(13,4000);

        String sheetName = ExportFileNameEnum.INVENTORY_SALE_NAME.getValue();
        Sheet sheet = gainSheet(map, sheetName, 1);

        Table title = new Table(1);
        title.setClazz(SalesManSaleModel.class);
        writer.write(null, sheet, title);

        List<SalesManSaleModel> data1 = new ArrayList<>();
        SalesManSaleModel item = new SalesManSaleModel();
        item.setCreateTime("日期: 由" + query.getStartDate());
        item.setSaleCode("至" + query.getEndDate());
        item.setGrossMargin("打印日期: ");
        item.setGrossRate(DateUtil.getDate(new Date()));
        data1.add(item);
        writer.write(data1, sheet);

        List<SalesManSaleModel> content = new ArrayList<>();

        List<UserInfoReport> userInfoReports = (List<UserInfoReport>)mapData.get("userInfoReports");

        for (UserInfoReport ur : userInfoReports){

            List<SaleInfoReport> saleInfoList = ur.getSaleInfoList();
            if (CollectionUtils.isEmpty(saleInfoList)){
                continue;
            }

            SalesManSaleModel saleModel = new SalesManSaleModel();
            saleModel.setCreateTime("销售员编号: ");
            saleModel.setSaleCode(ur.getUserCode());
            saleModel.setCustName("销售员名称: ");
            saleModel.setBackAmount(ur.getSalesmanCode() + ur.getRealName());
            content.add(saleModel);

            for (SaleInfoReport sr : saleInfoList){
                List<SaleGoodsReport> saleGoodsReportList = sr.getSaleGoodsReportList();

                SalesManSaleModel saleModel1 = null;

                for (SaleGoodsReport sgr : saleGoodsReportList) {
                    if (count == CommonConstant.DEFAULT_VALUE_ZERO){
                        saleModel1 = new SalesManSaleModel();
                        saleModel1.setCreateTime(DateUtil.getDate(sr.getSaleDate()));
                        saleModel1.setSaleCode(sr.getSaleCode());
                        saleModel1.setCustName(sr.getCustCode() + sr.getCustName());
                        saleModel1.setBackAmount(sr.getCollectedAmountTotal().toString());
                        saleModel1.setStockName(sgr.getStockName());
                        saleModel1.setGoodsNum(sgr.getGoodsNum().toString());
                        saleModel1.setUnitPrice(sgr.getUnitPrice().toString());
                        saleModel1.setSalesVolume(sgr.getSalesVolume().toString());
                        saleModel1.setCost(sgr.getStandardBuyPrice().toString());
                        saleModel1.setSumCost(sgr.getCost().toString());
                        saleModel1.setGrossMargin(sgr.getGrossMargin().toString());
                        saleModel1.setGrossRate(sgr.getGrossRate().toString());
                        count ++;
                    } else {
                        saleModel1 = new SalesManSaleModel();
                        saleModel1.setStockName(sgr.getStockName());
                        saleModel1.setGoodsNum(sgr.getGoodsNum().toString());
                        saleModel1.setUnitPrice(sgr.getUnitPrice().toString());
                        saleModel1.setSalesVolume(sgr.getSalesVolume().toString());
                        saleModel1.setCost(sgr.getStandardBuyPrice().toString());
                        saleModel1.setSumCost(sgr.getCost().toString());
                        saleModel1.setGrossMargin(sgr.getGrossMargin().toString());
                        saleModel1.setGrossRate(sgr.getGrossRate().toString());
                    }
                    content.add(saleModel1);
                }
                if (saleGoodsReportList.size() > CommonConstant.DEFAULT_VALUE_ONE){
                    saleModel1 = new SalesManSaleModel();
                    saleModel1.setCustName("小计: ");
                    saleModel1.setBackAmount(sr.getCollectedAmountTotal().toString());
                    saleModel1.setGoodsNum(sr.getSumGoodsNum() + "");
                    saleModel1.setSalesVolume(sr.getSumSaleVolume().toString());
                    saleModel1.setSumCost(sr.getSumCost().toString());
                    saleModel1.setGrossMargin(sr.getSumGrossMargin().toString());
                    saleModel1.setGrossRate(sr.getSumGrossRate().toString());
                    content.add(saleModel1);
                }
                count = CommonConstant.DEFAULT_VALUE_ZERO;
            }

            saleModel = new SalesManSaleModel();
            saleModel.setCustName("合计: ");
            saleModel.setBackAmount(ur.getBackAmountTotal().toString());
            saleModel.setGoodsNum(ur.getTotalGoodsNum() + "");
            saleModel.setSalesVolume(ur.getTotalSaleVolume().toString());
            saleModel.setSumCost(ur.getTotalCost().toString());
            saleModel.setGrossMargin(ur.getTotalGrossMargin().toString());
            saleModel.setGrossRate(ur.getTotalGrossRate().toString());
            content.add(saleModel);
        }

        item = new SalesManSaleModel();
        item.setCustName("合计: ");
        item.setBackAmount(mapData.get("backAmountTotal").toString());
        item.setGoodsNum(mapData.get("totalGoodsNum").toString());
        item.setSalesVolume(mapData.get("totalSaleVolume").toString());
        item.setSumCost(mapData.get("totalCost").toString());
        item.setGrossMargin(mapData.get("totalGrossMargin").toString());
        item.setGrossRate(mapData.get("totalGrossRate").toString());
        content.add(item);

        writer.write(content, sheet);

        List<SalesManSaleModel> dataEnd = new ArrayList<>();
        SalesManSaleModel itemEnd = new SalesManSaleModel();
        writer.write(dataEnd, sheet);

        dataEnd.add(itemEnd);
        itemEnd.setCreateTime("公司名称: 正诚文化");
        itemEnd.setCustName("操作员: " + userVo.getRealName());
        writer.write(dataEnd, sheet);

        return writer;
    }

    public static ExcelWriter customerSaleExport (ServletOutputStream outputStream, Map<String, Object> mapData, CustomerQuery query, UserVo userVo){
        int count = 0;
        ExcelWriter writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX);

        Map<Integer, Integer> map = new HashMap<>();
        map.put(0,4000);
        map.put(1,4000);
        map.put(2,4000);
        map.put(3,2000);
        map.put(4,2800);
        map.put(5,4000);
        map.put(6,4000);
        map.put(7,2000);
        map.put(8,4000);
        map.put(9,4000);
        map.put(10,4000);

        String sheetName = ExportFileNameEnum.CUSTOMER_SALE_NAME.getValue();
        Sheet sheet = gainSheet(map, sheetName, 1);

        Table title = new Table(1);
        title.setClazz(CustomerSaleModel.class);
        writer.write(null, sheet, title);

        List<CustomerSaleModel> data1 = new ArrayList<>();
        CustomerSaleModel item = new CustomerSaleModel();
        item.setCreateTime("日期: 由" + query.getStartDate());
        item.setSaleCode("至" + query.getEndDate());
        item.setGrossMargin("打印日期: ");
        item.setGrossRate(DateUtil.getDate(new Date()));
        data1.add(item);
        writer.write(data1, sheet);

        List<CustomerSaleModel> content = new ArrayList<>();

        List<CustomerReport> customerSupplierList = (List<CustomerReport>)mapData.get("customerReports");

        for (CustomerReport cs : customerSupplierList){

            List<SaleInfoReport> saleInfoReportList = cs.getSaleInfoReportList();

            if (CollectionUtils.isEmpty(saleInfoReportList)){
                continue;
            }

            CustomerSaleModel csm = new CustomerSaleModel();
            csm.setCreateTime("客户编号: " + cs.getCustCode());
            csm.setStockName("客户名称: " + cs.getCustName());
            content.add(csm);

            for (SaleInfoReport sir : saleInfoReportList){

                List<SaleGoodsReport> saleGoodsReports = sir.getSaleGoodsReportList();

                for (SaleGoodsReport sgr : saleGoodsReports){
                    if (count == CommonConstant.DEFAULT_VALUE_ZERO){
                        csm =  new CustomerSaleModel();
                        csm.setCreateTime(DateUtil.getDate(sir.getSaleDate()));
                        csm.setSaleCode(sir.getSaleCode());
                        csm.setStockName(sgr.getStockName());
                        csm.setGoodsNum(sgr.getGoodsNum().toString());
                        csm.setUnitPrice(sgr.getUnitPrice().toString());
                        csm.setSalesVolume(sgr.getSalesVolume().toString());
                        csm.setCost(sgr.getStandardBuyPrice().toString());
                        csm.setSumCost(sgr.getCost().toString());
                        csm.setGrossMargin(sgr.getGrossMargin().toString());
                        csm.setGrossRate(sgr.getGrossRate().toString());
                        count ++;
                    } else {
                        csm =  new CustomerSaleModel();
                        csm.setStockName(sgr.getStockName());
                        csm.setGoodsNum(sgr.getGoodsNum().toString());
                        csm.setUnitPrice(sgr.getUnitPrice().toString());
                        csm.setSalesVolume(sgr.getSalesVolume().toString());
                        csm.setCost(sgr.getStandardBuyPrice().toString());
                        csm.setSumCost(sgr.getCost().toString());
                        csm.setGrossMargin(sgr.getGrossMargin().toString());
                        csm.setGrossRate(sgr.getGrossRate().toString());
                    }
                    content.add(csm);
                }

                if (saleGoodsReports.size() > CommonConstant.DEFAULT_VALUE_ONE){
                    csm =  new CustomerSaleModel();
                    csm.setStockName("小计: ");
                    csm.setGoodsNum(sir.getSumGoodsNum() + "");
                    csm.setSalesVolume(sir.getSumSaleVolume().toString());
                    csm.setSumCost(sir.getSumCost().toString());
                    csm.setGrossMargin(sir.getSumGrossMargin().toString());
                    csm.setGrossRate(sir.getSumGrossRate().toString());
                    content.add(csm);
                }
                count = CommonConstant.DEFAULT_VALUE_ZERO;
            }

            csm =  new CustomerSaleModel();
            csm.setStockName("合计: ");
            csm.setGoodsNum(cs.getTotalGoodsNum() + "");
            csm.setSalesVolume(cs.getTotalSaleVolume().toString());
            csm.setSumCost(cs.getTotalCost().toString());
            csm.setGrossMargin(cs.getTotalGrossMargin().toString());
            csm.setGrossRate(cs.getTotalGrossRate().toString());
            content.add(csm);
        }

        CustomerSaleModel csmTotal =  new CustomerSaleModel();
        csmTotal.setStockName("总计: ");
        csmTotal.setGoodsNum(mapData.get("totalGoodsNum").toString());
        csmTotal.setSalesVolume(mapData.get("totalSaleVolume").toString());
        csmTotal.setSumCost(mapData.get("totalCost").toString());
        csmTotal.setGrossMargin(mapData.get("totalGrossMargin").toString());
        csmTotal.setGrossRate(mapData.get("totalGrossRate").toString());
        content.add(csmTotal);

        writer.write(content, sheet);

        List<CustomerSaleModel> dataEnd = new ArrayList<>();
        CustomerSaleModel itemEnd = new CustomerSaleModel();
        writer.write(dataEnd, sheet);

        dataEnd.add(itemEnd);
        itemEnd.setCreateTime("公司名称: 正诚文化");
        itemEnd.setStockName("操作员: " + userVo.getRealName());
        writer.write(dataEnd, sheet);

        return writer;
    }

    /**
     * @description 销售员提成统计导出
     * @author yangyun
     * @date 2019/4/30 0030
     * @param outputStream
     * @param mapData
     * @param query
     * @return com.alibaba.excel.ExcelWriter
     */
    public static ExcelWriter SaleManDeductionExport (ServletOutputStream outputStream, Map<String, Object> mapData, UserInfoQuery query, UserVo userVo){
        int count = 0;
        ExcelWriter writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX);

        Map<Integer, Integer> map = new HashMap<>();
        map.put(0,4000);
        map.put(1,4000);
        map.put(2,4000);
        map.put(3,2000);
        map.put(4,2800);
        map.put(5,4000);
        map.put(6,4000);
        map.put(7,2000);
        map.put(8,4000);
        map.put(9,4000);


        String sheetName = ExportFileNameEnum.SALEMAN_SALE_DEDUCTION.getValue();
        Sheet sheet = gainSheet(map, sheetName, 1);

        Table title = new Table(1);
        title.setClazz(SaleDeductionModel.class);
        writer.write(null, sheet, title);


        List<SaleDeductionModel> data1 = new ArrayList<>();
        SaleDeductionModel item = new SaleDeductionModel();
//        item.setSaleMan("日期: 由" + query.getStartDate());
//        item.setSaleGoodsNum("至" + query.getEndDate());
//        item.setGrossMargin("打印日期: ");
//        item.setGrossRate(DateUtil.getDate(new Date()));
//        data1.add(item);
        writer.write(data1, sheet);

        List<SaleDeductionReport> saleDeductionReports = (List<SaleDeductionReport>)mapData.get("saleDeductionReports");

        List<SaleDeductionModel> content = new ArrayList<>();

        SaleDeductionModel sdm = null;

        for (SaleDeductionReport sdr : saleDeductionReports){
            sdm = new SaleDeductionModel();
            sdm.setSaleMan(sdr.getUserCode() + sdr.getRealName());
            sdm.setSaleGoodsNum(sdr.getSumGoodsNum());
            sdm.setSaleVolume(sdr.getSumSaleVolume().doubleValue());
            sdm.setSaleCost(sdr.getSumSaleCost().doubleValue());
            sdm.setGrossMargin(sdr.getSumGrossMargin().doubleValue());
            sdm.setGrossRate((sdr.getGrossRate() == null ? BigDecimal.ZERO : sdr.getGrossRate()).doubleValue());
            sdm.setFee(sdr.getSumCost().doubleValue());
            sdm.setPureProfit(sdr.getPureProfit().doubleValue());
            sdm.setCommissionRate((sdr.getCommissionRate() == null ? 0 : sdr.getCommissionRate()) + "%");
            sdm.setCommission(sdr.getCommission().doubleValue());
            content.add(sdm);
        }

        sdm = new SaleDeductionModel();
        sdm.setSaleMan("合计: ");
        sdm.setSaleGoodsNum((Double) mapData.get("sumGoodsNum"));
        sdm.setSaleVolume(((BigDecimal)mapData.get("sumSaleVolume")).doubleValue());
        sdm.setSaleCost(((BigDecimal)mapData.get("sumSaleCost")).doubleValue());
        sdm.setGrossMargin(((BigDecimal)mapData.get("sumGrossMargin")).doubleValue());
        sdm.setFee(((BigDecimal)mapData.get("sumCost")).doubleValue());
        sdm.setPureProfit(((BigDecimal)mapData.get("sumPureProfit")).doubleValue());
        sdm.setCommission(((BigDecimal)mapData.get("sumCommission")).doubleValue());

        content.add(sdm);
        writer.write(content, sheet);

        List<SaleDeductionModel> dataEnd = new ArrayList<>();
        SaleDeductionModel itemEnd = new SaleDeductionModel();
//        dataEnd.add(itemEnd);
        writer.write(dataEnd, sheet);
        itemEnd.setSaleMan("公司名称: 正诚文化");
//        itemEnd.setSaleVolume("操作员: " + userVo.getRealName());
        writer.write(dataEnd, sheet);

        return writer;
    }

    /**
     * @description 销售单列表导出
     * @author yangyun
     * @date 2019/6/17 0017
     * @param outputStream
     * @param list
     * @param fieldList
     * @return com.alibaba.excel.ExcelWriter
     */
    public static XSSFWorkbook exportSaleList (ServletOutputStream outputStream, List<SaleListInfo> list, List<String> fieldList){
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("销售单");

        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth(20);

        // 获取标题字段
        List<String> titleNameList = getTitleNameList(fieldList);

        // 标题
        XSSFRow row = sheet.createRow(0);
        XSSFCell titleCell;

        for (int i = 0; i < titleNameList.size(); i++){
            titleCell = row.createCell(i);
            titleCell.setCellValue(titleNameList.get(i));
        }

        // 具体数据
        Iterator<SaleListInfo> it = list.iterator();

        int index = 0;
        Field field;
        XSSFCell cell;
        String fieldName;
        String getMethodName;

        Field[] fields = SaleListInfo.class.getDeclaredFields();

        while (it.hasNext()){
            index ++;
            row = sheet.createRow(index);

            for (int i = 0; i < fields.length; i++){
                cell = row.createCell(i);
                field = fields[i];
                fieldName = field.getName();
                getMethodName = "get" + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);
                System.out.println(fieldName + "++++++++++" + getMethodName);
            }

        }


        return workbook;
    }

    /**
     * @description 用户选择导出字段
     * @author yangyun
     * @date 2019/6/17 0017
     * @param fieldList
     * @return java.util.List<java.lang.String>
     */
    private static List<String> getTitleNameList (List<String> fieldList){
        List<String> titleNameList = new ArrayList<>();
        fieldList.stream().forEach(
                key -> {
                    Arrays.stream(SaleListFieldEnum.values()).forEach(
                            temp -> {
                                if (StringUtils.equals(key, temp.getKey())){
                                    titleNameList.add(temp.getName());
                                }
                            }
                    );
                }
        );

        return titleNameList;
    }

    /**
     * @description 销售单详细导出
     * @author yangyun
     * @date 2019/6/17 0017
     * @param outputStream
     * @param list
     * @param fieldList
     * @return com.alibaba.excel.ExcelWriter
     */
    public static ExcelWriter exportSaleListTenet (ServletOutputStream outputStream, List<SaleListInfo> list, List<String> fieldList){
        ExcelWriter writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX);
        String sheetName = "销售单列表";
        Sheet sheet = new Sheet(1,0);
        sheet.setSheetName(sheetName);

        Table title = new Table(1);
        title.setClazz(SaleListTenetModel.class);
        writer.write(null, sheet, title);

        List<SaleListTenetModel> content = new ArrayList<>();
        SaleListTenetModel model = null;

        List<LogisticsInfo> logisticsLsit = null;
        StringBuilder logisticsCompamyName = null;
        StringBuilder freihtCode = null;
        StringBuilder deliverGoodsTime = null;
        StringBuilder selectorPhoneNum = null;
        StringBuilder remark = null;

        for (SaleListInfo info : list){
            model = new SaleListTenetModel();
            model.setSaleCode(info.getSaleCode());
            model.setSaleDate(info.getSaleDate());
            model.setCustCode(info.getCustCode());
            model.setCustName(info.getCustName());
            model.setCurrency(info.getCurrency());
            model.setExchangeRate(formateBigdecimal(info.getExchangeRate()));
            model.setReceivableAccoun(info.getReceivableAccoun().doubleValue());
            model.setPaymentStatus(PayStatusEnum.getPayStatusEnumByKey(info.getPaymentStatus()).getValue());
            model.setIsReceipt(formateBigdecimal(SalesClassesEnum.getSalesClassesEnum(info.getIsReceipt()).getValue()));
            model.setSalesmanName(info.getSalesmanName());
            model.setCollectionUserName(info.getCollectionUserName());
            model.setSaleRemark(info.getSaleRemark());
            model.setDeliveryAddress(info.getDeliveryAddress());

            logisticsLsit = info.getLogisticsLsit();
            if (!CollectionUtils.isEmpty(logisticsLsit)){
                logisticsCompamyName = new StringBuilder("");
                freihtCode = new StringBuilder("");
                deliverGoodsTime = new StringBuilder("");
                selectorPhoneNum = new StringBuilder("");
                remark = new StringBuilder("");
                for (LogisticsInfo li : logisticsLsit){
                    logisticsCompamyName.append(li.getLogisticsCompamyName() + ";");
                    freihtCode.append(li.getFreihtCode() + ";");
                    deliverGoodsTime.append(li.getDeliverGoodsTime() + ";");
                    selectorPhoneNum.append(li.getSelectorPhoneNum() + ";");
                    remark.append(li.getRemark() + ";");
                }
                model.setLogisticsCompamyName(logisticsCompamyName.toString());
                model.setFreihtCode(freihtCode.toString());
                model.setDeliverGoodsTime(deliverGoodsTime.toString());
                model.setSelectorPhoneNum(selectorPhoneNum.toString());
                model.setRemark(remark.toString());

            }
            model.setSaleProject(info.getSaleProject());
            model.setDiscountAmount((info.getDiscountAmount()).doubleValue());
            model.setCollectionTerms(info.getCollectionTerms());
            model.setAccountPeriod(formateBigdecimal(info.getAccountPeriod()));
            model.setSubordinateCompanyName(formateBigdecimal(info.getSubordinateCompanyName()));
            model.setSubordinateCompanyName(info.getSubordinateCompanyName());


            content.add(model);
        }

        writer.write(content, sheet);

        return writer;
    }

    /**
     * @description 销售单详情导出
     * @author yangyun
     * @date 2019/6/17 0017
     * @param outputStream
     * @param list
     * @return com.alibaba.excel.ExcelWriter
     */
    @Deprecated
    public static ExcelWriter exportSaleListTenet2 (ServletOutputStream outputStream, List<SaleListInfo> list){
        ExcelWriter writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX);
        String sheetName = "销售单列表";
        Sheet sheet = new Sheet(1,0);
        sheet.setSheetName(sheetName);

        Table title = new Table(1);
        title.setClazz(SaleListTenetModel.class);
        writer.write(null, sheet, title);

        List<SaleListTenetModel> content = new ArrayList<>();
        SaleListTenetModel model = null;

        List<LogisticsInfo> logisticsLsit = null;
        StringBuilder logisticsCompamyName = null;
        StringBuilder freihtCode = null;
        StringBuilder deliverGoodsTime = null;
        StringBuilder selectorPhoneNum = null;
        StringBuilder remark = null;

        for (SaleListInfo info : list){
            model = new SaleListTenetModel();
            model.setSaleCode(info.getSaleCode());
            model.setSaleDate(info.getSaleDate());
            model.setCustCode(info.getCustCode());
            model.setCustName(info.getCustName());
            model.setDeliverRemark(info.getDeliverRemark());
            model.setCustPhoneNum(info.getCustPhoneNum());
            model.setCurrency(info.getCurrency());
            model.setExchangeRate(formateBigdecimal(info.getExchangeRate()));
            model.setReceivableAccoun(info.getReceivableAccoun().doubleValue());
            model.setPaymentStatus(PayStatusEnum.getPayStatusEnumByKey(info.getPaymentStatus()).getValue());
            model.setIsReceipt(formateBigdecimal(SalesClassesEnum.getSalesClassesEnum(info.getIsReceipt()).getValue()));
            model.setSalesmanName(info.getSalesmanName());
            model.setCollectionUserName(info.getCollectionUserName());
            model.setSumCollectedAmount(info.getSumCollectedAmount().doubleValue());
            model.setUnCollectedAmount((info.getReceivableAccoun().subtract(info.getSumCollectedAmount())).doubleValue());
            model.setSaleRemark(info.getSaleRemark());
            model.setDeliveryAddress(info.getDeliveryAddress());

            logisticsLsit = info.getLogisticsLsit();
            if (!CollectionUtils.isEmpty(logisticsLsit)){
                logisticsCompamyName = new StringBuilder("");
                freihtCode = new StringBuilder("");
                deliverGoodsTime = new StringBuilder("");
                selectorPhoneNum = new StringBuilder("");
                remark = new StringBuilder("");
                for (LogisticsInfo li : logisticsLsit){
                    logisticsCompamyName.append(li.getLogisticsCompamyName() + ";");
                    freihtCode.append(li.getFreihtCode() + ";");
                    deliverGoodsTime.append(li.getDeliverGoodsTime() + ";");
                    selectorPhoneNum.append(li.getSelectorPhoneNum() + ";");
                    remark.append(li.getRemark() + ";");
                }
                model.setLogisticsCompamyName(logisticsCompamyName.toString());
                model.setFreihtCode(freihtCode.toString());
                model.setDeliverGoodsTime(deliverGoodsTime.toString());
                model.setSelectorPhoneNum(selectorPhoneNum.toString());
                model.setRemark(remark.toString());

            }
            model.setSaleProject(info.getSaleProject());
            model.setDiscountAmount((info.getDiscountAmount()).doubleValue());
            model.setCollectionTerms(info.getCollectionTerms());
            model.setAccountPeriod(formateBigdecimal(info.getAccountPeriod()));
            model.setSubordinateCompanyName(formateBigdecimal(info.getSubordinateCompanyName()));
            model.setSubordinateCompanyName(info.getSubordinateCompanyName());
            model.setSaleSource(SaleSourceEnum.getSaleSourceEnum(info.getSaleSource()).getValue());
            content.add(model);
        }

        writer.write(content, sheet);

        return writer;
    }

    public static ExcelWriter exportSaleList3 (ServletOutputStream outputStream, List<SaleListInfo> list){
        ExcelWriter writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX);
        String sheetName = "销售单";
        Sheet sheet = new Sheet(1,0);
        sheet.setSheetName(sheetName);

        Table title = new Table(1);
        title.setClazz(SaleListModel.class);
        writer.write(null, sheet, title);

        List<SaleListModel> content = new ArrayList<>();
        SaleListModel model = null;

        for (SaleListInfo sli : list){
            model = new SaleListModel();
            model.setSaleCode(sli.getSaleCode());
            model.setSaleDate(sli.getSaleDate());
            model.setPaymentStatus(PayStatusEnum.getPayStatusEnumByKey(sli.getPaymentStatus()).getValue());
            model.setCustName(sli.getCustName());
            model.setCurrency(sli.getCurrency());
            model.setExchangeRate(formateBigdecimal(sli.getExchangeRate()));
            model.setSalesmanName(sli.getSalesmanName());
            model.setSaleProject(sli.getSaleProject());
            model.setDeliveryAddress(sli.getDeliveryAddress());
            model.setSaleRemark(sli.getSaleRemark());
            model.setIsReceipt(SalesClassesEnum.getSalesClassesEnum(sli.getIsReceipt()).getValue());
            model.setSubordinateCompanyName(sli.getSubordinateCompanyName());
            model.setDiscountAmount(sli.getDiscountAmount().doubleValue());
            model.setCollectedAmount(sli.getCollectedAmount().doubleValue());
            model.setCollectionAccount(sli.getCollectionAccount());
            model.setPaymentMethod(sli.getPaymentMethod());
            model.setCollectionTerms(sli.getCollectionTerms());
            model.setAccountPeriod(formateBigdecimal(sli.getAccountPeriod()));
            model.setCollectionUserName(sli.getCollectionUserName());
            model.setDueDate(sli.getDueDate());
            model.setStockName(sli.getStockName());
            model.setStockAddress(sli.getStockAddress());
            model.setGoodsNum(sli.getGoodsNum().doubleValue());
            model.setUnitPrice(sli.getUnitPrice().doubleValue());
            model.setDiscountRate(formateBigdecimal(sli.getDiscountRate()) + "%");
            model.setGoodsDiscountAmount(formateBigdecimal(sli.getGoodsDiscountAmount()));
            model.setSalesVolume(sli.getSalesVolume().doubleValue());
            model.setGoodsRemark(sli.getGoodsRemark());

            content.add(model);
        }

        writer.write(content, sheet);

        return writer;
    }

    /**
     * @description 销售单列表导出
     * @author yangyun
     * @date 2019/6/17 0017
     * @param outputStream
     * @param list
     * @return com.alibaba.excel.ExcelWriter
     */
    @Deprecated
    public static ExcelWriter exportSaleList2 (ServletOutputStream outputStream, List<SaleListInfo> list){
        ExcelWriter writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX);
        String sheetName = "销售单";
        Sheet sheet = new Sheet(1,0);
        sheet.setSheetName(sheetName);

        Table title = new Table(1);
        title.setClazz(SaleListModel.class);
        writer.write(null, sheet, title);

        List<SaleListModel> content = new ArrayList<>();
        SaleListModel model = null;

        int receiptSize = 0;
        int goodsSize = 0;
        int froNum = 0;
        SaleReceiptsDetails receiptsDetails = null;
        SaleGoodsDetailBean goodsDetailBean =  null;

        for (SaleListInfo sli : list){
            model = new SaleListModel();
            setBasicModel(model, sli);

            model.setSalesSumVolume(sli.getReceivableAccoun().doubleValue());
            model.setCollectionTerms(sli.getCollectionTerms());
            content.add(model);
            // 收款信息
            List<SaleReceiptsDetails> saleReceiptsDetailsList = sli.getSaleReceiptsDetailsList();

            //商品信息
            List<SaleGoodsDetailBean> saleGoodsDetailBeanList = sli.getSaleGoodsDetailBeanList();

            receiptSize = saleReceiptsDetailsList.size();
            goodsSize = saleGoodsDetailBeanList.size();
            froNum = goodsSize >= receiptSize ? goodsSize : receiptSize;

            if (froNum > CommonConstant.DEFAULT_VALUE_ONE){ // 说明商品详情和收款详情中至少有一个记录数多余 1 条
                SaleListModel model1 = null;
                for (int i = 0; i < froNum; i++){
                    if (goodsSize > i){
                        goodsDetailBean = saleGoodsDetailBeanList.get(i);
                    } else {
                        goodsDetailBean = null;
                    }
                    if (receiptSize > i){
                        receiptsDetails = saleReceiptsDetailsList.get(i);
                    } else {
                        receiptsDetails = null;
                    }
                    if (i == 0){
                        setModelValue(model, receiptsDetails, goodsDetailBean);
                    } else {
                        model1 = new SaleListModel();
                        setBasicModel(model1, sli);

                        setModelValue(model1, receiptsDetails, goodsDetailBean);
                        content.add(model1);
                    }
                    receiptsDetails = null;
                }
            } else { // 说明商品详情和收款详情最多都只有一条记录
                if (goodsSize > 0){
                    goodsDetailBean = saleGoodsDetailBeanList.get(0);
                }
                if (receiptSize > 0){

                    receiptsDetails = saleReceiptsDetailsList.get(0);
                }
                setModelValue(model, receiptsDetails, goodsDetailBean);
                receiptsDetails = null;
                goodsDetailBean = null;
            }
        }

        writer.write(content, sheet);

        return writer;
    }

    public static ExcelWriter exportSaleFinanceList (ServletOutputStream outputStream, List<SaleListInfo> list){
        ExcelWriter writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX);
        String sheetName = "销售单";
        Sheet sheet = new Sheet(1,0);
        sheet.setSheetName(sheetName);

        Table title = new Table(1);
        title.setClazz(SaleFinanceListModel.class);
        writer.write(null, sheet, title);

        List<SaleFinanceListModel> content = new ArrayList<>();
        SaleFinanceListModel model = null;

        int receiptSize = 0;
        int goodsSize = 0;
        int froNum = 0;
        SaleReceiptsDetails receiptsDetails = null;
        SaleGoodsDetailBean goodsDetailBean =  null;

        for (SaleListInfo sli : list){
            model = new SaleFinanceListModel();
            setBasicModel2(model, sli);

            content.add(model);
            // 收款信息
            List<SaleReceiptsDetails> saleReceiptsDetailsList = sli.getSaleReceiptsDetailsList();

            //商品信息
            List<SaleGoodsDetailBean> saleGoodsDetailBeanList = sli.getSaleGoodsDetailBeanList();

            receiptSize = saleReceiptsDetailsList.size();
            goodsSize = saleGoodsDetailBeanList.size();
            froNum = goodsSize >= receiptSize ? goodsSize : receiptSize;

            if (froNum > CommonConstant.DEFAULT_VALUE_ONE){ // 说明商品详情和收款详情中至少有一个记录数多余 1 条
                SaleFinanceListModel model1 = null;
                for (int i = 0; i < froNum; i++){
                    if (goodsSize > i){
                        goodsDetailBean = saleGoodsDetailBeanList.get(i);
                    } else {
                        goodsDetailBean = null;
                    }
                    if (receiptSize > i){
                        receiptsDetails = saleReceiptsDetailsList.get(i);
                    } else {
                        receiptsDetails = null;
                    }
                    if (i == 0){
                        setModelValue2(model, goodsDetailBean);
                    } else {
                        model1 = new SaleFinanceListModel();
                        setBasicModel2(model1, sli);

                        setModelValue2(model1, goodsDetailBean);
                        content.add(model1);
                    }
                }
            } else { // 说明商品详情和收款详情最多都只有一条记录
                if (goodsSize > 0){
                    goodsDetailBean = saleGoodsDetailBeanList.get(0);
                }
                if (receiptSize > 0){

                    receiptsDetails = saleReceiptsDetailsList.get(0);
                }
                setModelValue2(model, goodsDetailBean);
                receiptsDetails = null;
                goodsDetailBean = null;
            }
        }

        writer.write(content, sheet);

        return writer;
    }

    private static String formateBigdecimal (Object b){
        if (b == null){
            return "0";
        }
        return b.toString();
    }

    private static void setModelValue (SaleListModel model, SaleReceiptsDetails receiptsDetails, SaleGoodsDetailBean goodsDetailBean){
        if (receiptsDetails != null){
            model.setCollectedAmount(receiptsDetails.getCollectedAmount().doubleValue());
            model.setCollectionAccount(receiptsDetails.getCollectionAccount());
            model.setPaymentMethod(receiptsDetails.getPaymentMethod());
        }

        if (goodsDetailBean != null){
            model.setStockName(goodsDetailBean.getStockName());
            model.setStockAddress(goodsDetailBean.getStockAddress());
            model.setGoodsNum(goodsDetailBean.getGoodsNum().doubleValue());
            model.setUnitPrice(goodsDetailBean.getRealSellPrice().doubleValue());
            model.setDiscountRate(formateBigdecimal(goodsDetailBean.getDiscountRate()) + "%");
            model.setGoodsDiscountAmount(formateBigdecimal(goodsDetailBean.getDiscountAmount()));
            model.setSalesVolume(goodsDetailBean.getSalesVolume().doubleValue());
            model.setGoodsRemark(goodsDetailBean.getRemarks());
        }
    }

    private static void setModelValue2 (SaleFinanceListModel model, SaleGoodsDetailBean goodsDetailBean){
        if (goodsDetailBean != null){
            model.setStockName(goodsDetailBean.getStockName());
            model.setGoodsNum(goodsDetailBean.getGoodsNum());
            model.setUnitPrice(goodsDetailBean.getRealSellPrice().doubleValue());
            model.setSalesVolume(goodsDetailBean.getSalesVolume().doubleValue());
            model.setGoodsRemark(goodsDetailBean.getRemarks());
        }
    }

    private static void setBasicModel (SaleListModel model1, SaleListInfo sli){
        model1.setSaleCode(sli.getSaleCode());
        model1.setSaleDate(sli.getSaleDate());
        model1.setPaymentStatus(PayStatusEnum.getPayStatusEnumByKey(sli.getPaymentStatus()).getValue());
        model1.setCustName(sli.getCustName());
        model1.setCustPhoneNum(sli.getCustPhoneNum());
        model1.setCurrency(sli.getCurrency());
        model1.setExchangeRate(formateBigdecimal(sli.getExchangeRate()));
        model1.setSalesmanName(sli.getSalesmanName());
        model1.setSaleProject(sli.getSaleProject());
        model1.setDeliveryAddress(sli.getDeliveryAddress());
        model1.setSaleRemark(sli.getSaleRemark());
        model1.setIsReceipt(SalesClassesEnum.getSalesClassesEnum(sli.getIsReceipt()).getValue());
        model1.setSubordinateCompanyName(sli.getSubordinateCompanyName());
        model1.setDiscountAmount(sli.getDiscountAmount().doubleValue());
        model1.setAccountPeriod(formateBigdecimal(sli.getAccountPeriod()));
        model1.setCollectionUserName(sli.getCollectionUserName());
        model1.setDueDate(sli.getDueDate());
        model1.setDeliverRemark(sli.getDeliverRemark());
        model1.setLogistic(sli.getLogistic());
    }

    private static void setBasicModel2 (SaleFinanceListModel model1, SaleListInfo sli){
        model1.setSaleDate(sli.getSaleDate());
        model1.setPaymentStatus(PayStatusEnum.getPayStatusEnumByKey(sli.getPaymentStatus()).getValue());
        model1.setCustName(sli.getCustName());
        model1.setSaleRemark(sli.getSaleRemark());
    }

    public static ExcelWriter exportSaleDetail (ServletOutputStream outputStream, Map<String, Object> map, String realName, SaleDetailReportQuery query){
        ExcelWriter writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX);

        List<SaleInfoReport> data = (List<SaleInfoReport>)map.get("listData");
        String sheetName = "销售明细表";

        Map<Integer, Integer> mapStyle = new HashMap<>();
        mapStyle.put(0,4000);
        mapStyle.put(1,4000);
        mapStyle.put(2,4000);
        mapStyle.put(3,4000);
        mapStyle.put(4,6000);
        mapStyle.put(5,4000);
        mapStyle.put(6,4000);
        mapStyle.put(7,4000);
        mapStyle.put(8,4000);


        Sheet sheet = gainSheet(mapStyle, sheetName, 1);
        sheet.setSheetName(sheetName);

        Table title = new Table(1);
        title.setClazz(SaleDetailModel.class);
        writer.write(null, sheet, title);

        List<SaleDetailModel> data1 = new ArrayList<>();
        SaleDetailModel item = new SaleDetailModel();
        item.setSaleDate("日期: 由" + query.getStartDate());
        item.setSaleCode("至" + query.getEndDate());
        item.setSaleVolume("打印日期: " + DateUtil.getDate(new Date()));
        data1.add(item);
        writer.write(data1, sheet);

        List<SaleDetailModel> content = new ArrayList<>();

        SaleDetailModel model = null;
        List<SaleGoodsReport> saleGoodsReportList = null;
        SaleGoodsReport goods = null;

        for (SaleInfoReport s : data){
            saleGoodsReportList = s.getSaleGoodsReportList();

            model = new SaleDetailModel();
            model.setSaleDate(DateUtil.getDate(s.getSaleDate()));
            model.setSaleCode(s.getSaleCode());
            model.setCustomer(s.getCustCode() + s.getCustName());
            model.setSalesman(s.getUserCode() + s.getRealName());
            content.add(model);

            if (saleGoodsReportList.size() > CommonConstant.DEFAULT_VALUE_ONE){//此时商品数大于一个
                for (int i = 0; i < saleGoodsReportList.size(); i++){
                    goods = saleGoodsReportList.get(i);
                    if (i == CommonConstant.DEFAULT_VALUE_ZERO){
                        setSaleDetailModelValue(model, goods);
                    } else {
                        SaleDetailModel gg = new SaleDetailModel();
                        setSaleDetailModelValue(gg, goods);
                        content.add(gg);
                    }
                }
            } else {

                if (saleGoodsReportList.size() > CommonConstant.DEFAULT_VALUE_ZERO){
                    goods = saleGoodsReportList.get(0);
                    setSaleDetailModelValue(model, goods);
                }
            }

            //  小计
            SaleDetailModel subtotal = new SaleDetailModel();
            subtotal.setStockAddress("小计: ");
            subtotal.setGoodsNum(String.valueOf(s.getSumGoodsNum()));
            subtotal.setSaleVolume(formateBigdecimal(s.getSumSaleVolume()));
            content.add(subtotal);
        }



        writer.write(content, sheet);

        List<SaleDetailModel> dataEnd = new ArrayList<>();
        // 合计
        SaleDetailModel total = new SaleDetailModel();
        total.setStockAddress("合计: ");
        total.setGoodsNum(String.valueOf(map.get("sum")));
        total.setSaleVolume(formateBigdecimal(map.get("reduce")));
        dataEnd.add(total);

        SaleDetailModel itemEnd = new SaleDetailModel();
        dataEnd.add(itemEnd);
        itemEnd.setSaleDate("公司名称: 正诚文化");
        itemEnd.setSaleVolume("操作员: " + realName);
        writer.write(dataEnd, sheet);
        return writer;
    }

    private static void setSaleDetailModelValue(SaleDetailModel model, SaleGoodsReport goods){
        model.setStockName(goods.getStockCode() + goods.getStockName());
        model.setStockAddress(goods.getStockAddress());
        model.setGoodsNum(String.valueOf(goods.getGoodsNum()));
        model.setUnitPrice(formateBigdecimal(goods.getUnitPrice()));
        model.setSaleVolume(formateBigdecimal(goods.getSalesVolume()));
    }


    public static ExcelWriter exportSaleGoodsNum (ServletOutputStream outputStream, Map<String, Object> map, String realName, SaleDetailReportQuery query){
        ExcelWriter writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX);

        List<SaleInfoReport> data = (List<SaleInfoReport>)map.get("listData");
        String sheetName = "销售明细表";

        Map<Integer, Integer> mapStyle = new HashMap<>();
        mapStyle.put(0,4000);
        mapStyle.put(1,4000);
        mapStyle.put(2,4000);
        mapStyle.put(3,4000);
        mapStyle.put(4,6000);
        mapStyle.put(5,4000);
        mapStyle.put(6,4000);
        mapStyle.put(7,4000);
        mapStyle.put(8,4000);


        Sheet sheet = gainSheet(mapStyle, sheetName, 1);
        sheet.setSheetName(sheetName);

        Table title = new Table(1);
        title.setClazz(SaleDetailModel.class);
        writer.write(null, sheet, title);

        List<SaleDetailModel> data1 = new ArrayList<>();
        SaleDetailModel item = new SaleDetailModel();
        item.setSaleDate("日期: 由" + query.getStartDate());
        item.setSaleCode("至" + query.getEndDate());
        item.setSaleVolume("打印日期: " + DateUtil.getDate(new Date()));
        data1.add(item);
        writer.write(data1, sheet);

        List<SaleDetailModel> content = new ArrayList<>();

        SaleDetailModel model = null;
        List<SaleGoodsReport> saleGoodsReportList = null;
        SaleGoodsReport goods = null;

        for (SaleInfoReport s : data){
            saleGoodsReportList = s.getSaleGoodsReportList();

            model = new SaleDetailModel();
            model.setSaleDate(DateUtil.getDate(s.getSaleDate()));
            model.setSaleCode(s.getSaleCode());
            model.setCustomer(s.getCustCode() + s.getCustName());
            model.setSalesman(s.getUserCode() + s.getRealName());
            content.add(model);

            if (saleGoodsReportList.size() > CommonConstant.DEFAULT_VALUE_ONE){//此时商品数大于一个
                for (int i = 0; i < saleGoodsReportList.size(); i++){
                    goods = saleGoodsReportList.get(i);
                    if (i == CommonConstant.DEFAULT_VALUE_ZERO){
                        setSaleDetailModelValue(model, goods);
                    } else {
                        SaleDetailModel gg = new SaleDetailModel();
                        setSaleDetailModelValue(gg, goods);
                        content.add(gg);
                    }
                }
            } else {

                if (saleGoodsReportList.size() > CommonConstant.DEFAULT_VALUE_ZERO){
                    goods = saleGoodsReportList.get(0);
                    setSaleDetailModelValue(model, goods);
                }
            }

            //  小计
            SaleDetailModel subtotal = new SaleDetailModel();
            subtotal.setStockAddress("小计: ");
            subtotal.setGoodsNum(String.valueOf(s.getSumGoodsNum()));
            subtotal.setSaleVolume(formateBigdecimal(s.getSumSaleVolume()));
            content.add(subtotal);
        }



        writer.write(content, sheet);

        List<SaleDetailModel> dataEnd = new ArrayList<>();
        // 合计
        SaleDetailModel total = new SaleDetailModel();
        total.setStockAddress("合计: ");
        total.setGoodsNum(String.valueOf(map.get("sum")));
        total.setSaleVolume(formateBigdecimal(map.get("reduce")));
        dataEnd.add(total);

        SaleDetailModel itemEnd = new SaleDetailModel();
        dataEnd.add(itemEnd);
        itemEnd.setSaleDate("公司名称: 正诚文化");
        itemEnd.setSaleVolume("操作员: " + realName);
        writer.write(dataEnd, sheet);
        return writer;
    }

    public static void exportSaleTicketCount (ServletOutputStream outputStream, List<SaleInfo> list){
        try {
            @Cleanup XSSFWorkbook workbook = null;
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("今日件数");

            // 设置表格默认列宽度为15个字节
            sheet.setDefaultColumnWidth(40);

            // 标题
            String[] title = {"销售员编号", "销售员", "今日销售件数"};

            // 标题
            XSSFRow row = sheet.createRow(0);
            XSSFCell titleCell;

            for (int i = 0; i < title.length; i++) {
                titleCell = row.createCell(i);
                titleCell.setCellValue(title[i]);
            }

            int count = 0;
            int index = 0;
            XSSFCell cell = null;
            for (SaleInfo s : list){
                index ++;
                row = sheet.createRow(index);

                // 创建单元格
                cell = row.createCell(count ++);
                cell.setCellValue(s.getSalesmanCode());
                cell = row.createCell(count ++);
                cell.setCellValue(s.getRealName());
                cell = row.createCell(count ++);
                cell.setCellValue(s.getCountTicket());
                count = 0;
            }

            workbook.write(outputStream);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void exportSalesManVipCustomerDetail(ServletOutputStream outputStream, Map<String, List<VIPCustomerLevelReport>> map, XSSFSheet sheet) {
        try {


            // 设置表格默认列宽度为15个字节
            sheet.setDefaultColumnWidth(40);

            // 标题
            String[] title = {"客户", "客户等级", "累计金额", "购买次数"};

            XSSFRow row = sheet.createRow(0);

            XSSFCell titleCell;
            XSSFCell cell = null;

            for (int i = 0; i < title.length; i++) {
                titleCell = row.createCell(i);
                titleCell.setCellValue(title[i]);
            }



            int index = 1;
            index = aa("钻石客户", map, sheet, row, index, cell);
            index++;
            index = aa("铂金客户", map, sheet, row, index, cell);
            index++;
            aa("黄金客户", map, sheet, row, index, cell);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static int aa (String name, Map<String, List<VIPCustomerLevelReport>> map, XSSFSheet sheet, XSSFRow row, int index, XSSFCell cell){
        List<VIPCustomerLevelReport> data = map.get(name);
        row = sheet.createRow(index);
        cell = row.createCell(0);
        cell.setCellValue(name);

        cell = row.createCell(1);
        if (data ==  null ){
            cell.setCellValue(0);

        } else {
            cell.setCellValue(data.size());
            for (VIPCustomerLevelReport temp : data) {
                row = sheet.createRow(++index);

                cell = row.createCell(0);
                cell.setCellValue(temp.getCustName());

                cell = row.createCell(1);
                cell.setCellValue(temp.getCustGrade());

                cell = row.createCell(2);
                cell.setCellValue(temp.getSaleVolume().doubleValue());

                cell = row.createCell(3);
                cell.setCellValue(temp.getBuyCount());
            }
        }
        return index;
    }
}