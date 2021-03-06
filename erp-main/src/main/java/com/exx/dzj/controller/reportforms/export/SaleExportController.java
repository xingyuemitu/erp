package com.exx.dzj.controller.reportforms.export;

import com.alibaba.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.exx.dzj.annotation.DataPermission;
import com.exx.dzj.bean.SaleDetailReportQuery;
import com.exx.dzj.common.export.SaleExportUtils;
import com.exx.dzj.constant.CommonConstant;
import com.exx.dzj.entity.bean.CustomerQuery;
import com.exx.dzj.entity.bean.StockInfoQuery;
import com.exx.dzj.entity.bean.UserInfoQuery;
import com.exx.dzj.entity.market.SaleInfo;
import com.exx.dzj.entity.market.SaleInfoQuery;
import com.exx.dzj.entity.market.SaleListInfo;
import com.exx.dzj.entity.purchase.PurchaseListInfo;
import com.exx.dzj.entity.statistics.sales.StockTypeReport;
import com.exx.dzj.entity.statistics.sales.VIPCustomerLevelReport;
import com.exx.dzj.entity.user.UserVo;
import com.exx.dzj.facade.homepage.HomePageFacade;
import com.exx.dzj.facade.reportforms.sale.SaleTicketReportFacade;
import com.exx.dzj.facade.user.UserFacade;
import com.exx.dzj.query.QueryGenerator;
import com.exx.dzj.util.enums.ExportFileNameEnum;
import com.exx.dzj.util.excel.ExcelUtil;
import lombok.Cleanup;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author yangyun
 * @create 2019-04-16-16:16
 */
@RestController
@RequestMapping("saleexport/")
public class SaleExportController {

    @Autowired
    private SaleTicketReportFacade inventoryReportFacade;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private SaleTicketReportFacade saleTicketReportFacade;

    @GetMapping("exportinventorysale/{userCode}")
    public void exportInventorySale (HttpServletRequest request, HttpServletResponse response, @PathVariable("userCode") String userCode, StockInfoQuery query){

        List<StockTypeReport> stockTypeReports = inventoryReportFacade.statisticsSaleByInventory(query);
        try {
            String code = ExcelUtil.getCode();
            response.setContentType("application/x-excel");

            String value = ExportFileNameEnum.INVENTORY_SALE_NAME.getValue();

            response.setHeader("Content-Disposition", "attachment;filename=" + new String((value + "-" + code +".xlsx").getBytes(), "ISO-8859-1"));

            ServletOutputStream outputStream = response.getOutputStream();
            UserVo userVo = userFacade.queryUserBean(userCode);
            ExcelWriter excelWriter = SaleExportUtils.inventorySaleExport(outputStream, stockTypeReports, query, userVo);
            excelWriter.finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @description 销售单根据销售员统计导出
     * @author yangyun
     * @date 2019/4/24 0024
     * @param response
     * @param query
     * @return void
     */
    @GetMapping("exportsalesmansale/{userCode}")
    public void exportSalesManSale (HttpServletResponse response, @PathVariable("userCode") String userCode, UserInfoQuery query){
        try {
            Map<String, Object> mapData = inventoryReportFacade.statisticsSaleBySalesMan(query);
            String code = ExcelUtil.getCode();
            String value = ExportFileNameEnum.SALEMAN_SALE_NAME.getValue();

            response.setContentType("application/x-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String((value + "-" + code +".xlsx").getBytes(), "ISO-8859-1"));

            ServletOutputStream outputStream = response.getOutputStream();

            UserVo userVo = userFacade.queryUserBean(userCode);

            ExcelWriter writer = SaleExportUtils.salesManSaleExport(outputStream, mapData, query, userVo);
            writer.finish();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * @description 销售单根据客户导出
     * @author yangyun
     * @date 2019/4/24 0024
     * @param response
     * @param query
     * @return void
     */
    @GetMapping("exportcustomersale/{userCode}")
    public void exportCustomerSale (HttpServletResponse response,@PathVariable("userCode") String userCode, CustomerQuery query){
        try {
            Map<String, Object> mapData = inventoryReportFacade.statisticsSalesTicketByCust(query);
            String code = ExcelUtil.getCode();
            String value = ExportFileNameEnum.CUSTOMER_SALE_NAME.getValue();

            response.setContentType("application/x-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String((value + "-" + code +".xlsx").getBytes(), "ISO-8859-1"));

            ServletOutputStream outputStream = response.getOutputStream();

            UserVo userVo = userFacade.queryUserBean(userCode);


            ExcelWriter writer = SaleExportUtils.customerSaleExport(outputStream, mapData, query, userVo);

            writer.finish();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * @description  销售员提成统计导出
     * @author yangyun
     * @date 2019/4/30 0030
     * @param response
     * @param query
     * @return void
     */
    @GetMapping("exportsalededuction/{userCode}")
    public void exportSaleDeduction (HttpServletResponse response, @PathVariable("userCode") String userCode, UserInfoQuery query){
        try {
            Map<String, Object> mapData = inventoryReportFacade.statisticSalesDeductionBySaleman(query);

            String code = ExcelUtil.getCode();
            String value = ExportFileNameEnum.SALEMAN_SALE_DEDUCTION.getValue();

            response.setContentType("application/x-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String((value + "-" + code + ".xlsx").getBytes(), "ISO-8859-1"));

            ServletOutputStream outputStream = response.getOutputStream();
            UserVo userVo = userFacade.queryUserBean(userCode);
            ExcelWriter writer = SaleExportUtils.SaleManDeductionExport(outputStream, mapData, query, userVo);

            writer.finish();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * @description 销售单列表导出 1 列表样式导出 2 详情导出
     * @author yangyun
     * @date 2019/6/13 0013
     * @param request
     * @param response
     * @param realName
     * @param query
     * @return void
     */
    @GetMapping("exportSaleList/{realName}")
    @DataPermission(pageComponent="sale/saleticket/saleTicketList")
    public void exportSaleList (HttpServletRequest request, HttpServletResponse response, @PathVariable("realName") String realName, SaleInfoQuery query){
        try {
            String code = ExcelUtil.getCode();

            List<SaleListInfo> list = null;
            response.setContentType("application/x-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("Sale-" + code + ".xlsx").getBytes(), "ISO-8859-1"));

            ServletOutputStream outputStream = response.getOutputStream();
            XSSFWorkbook writer = null;

            SaleInfo saleInfo = new SaleInfo();
            saleInfo.setSaleReceiptsDetailsList(null);
            saleInfo.setSaleGoodsDetailBeanList(null);
            // 查询条件
            QueryWrapper<SaleInfo> queryWrapper = QueryGenerator.initQueryWrapper(saleInfo, request.getParameterMap());

            int type = query.getType();
            switch (type){
                case CommonConstant.DEFAULT_VALUE_ONE:
                    list = saleTicketReportFacade.exportSaleList(query, queryWrapper);
                    writer = SaleExportUtils.exportSaleList(outputStream, list, query.getFieldList());
//                    writer = SaleExportUtils.exportSaleList2(outputStream, list);
                    break;
                case CommonConstant.DEFAULT_VALUE_TWO:
                    list = saleTicketReportFacade.querySalesListForIds(query, queryWrapper);
//                    writer = SaleExportUtils.exportSaleListTenet(outputStream, list, query.getFieldList());
                    SaleExportUtils.exportSaleListTenet2(outputStream, list);
                    break;
                default:

                    break;
            }

            writer.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @DataPermission(pageComponent="sale/saleticket/saleTicketList")
    @GetMapping("exportSaleList2/{realName}")
    public void exportSaleList2 (HttpServletRequest request, HttpServletResponse response, @PathVariable("realName") String realName, SaleInfoQuery query){
        try {
            String code = ExcelUtil.getCode();

            List<SaleListInfo> list = null;
            response.setContentType("application/x-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("Sale-" + code + ".xlsx").getBytes(), "ISO-8859-1"));

            SaleInfo saleInfo = new SaleInfo();
            saleInfo.setSaleReceiptsDetailsList(null);
            saleInfo.setSaleGoodsDetailBeanList(null);
            // 查询条件
            QueryWrapper<SaleInfo> queryWrapper = QueryGenerator.initQueryWrapper(saleInfo, request.getParameterMap());

            ServletOutputStream outputStream = response.getOutputStream();
            ExcelWriter writer = null;
            int type = query.getType();
            switch (type){
                case CommonConstant.DEFAULT_VALUE_ONE:
                    list = saleTicketReportFacade.exportSaleList(query, queryWrapper);
                    writer = SaleExportUtils.exportSaleListTenet2(outputStream, list);
                    break;
                case CommonConstant.DEFAULT_VALUE_TWO:
                    list = saleTicketReportFacade.querySalesListForIds(query, queryWrapper);
                    writer = SaleExportUtils.exportSaleList2(outputStream, list);
                    break;
                case CommonConstant.DEFAULT_VALUE_THREE:
                    list = saleTicketReportFacade.querySalesListForIds2(query, queryWrapper);
                    writer = SaleExportUtils.exportSaleFinanceList(outputStream, list);
                    break;
                default:

                    break;
            }

            writer.finish();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    /**
     * @description 销售单明细导出
     * @author yangyun
     * @date 2019/6/26 0026
     * @param request
     * @param response
     * @param realName
     * @param query
     * @return void
     */
    @GetMapping("exportsaledetail/{realName}")
    public void exportSaleDetail (HttpServletRequest request, HttpServletResponse response, @PathVariable("realName") String realName, SaleDetailReportQuery query){
        ExcelWriter writer = null;

        String code = ExcelUtil.getCode();
        try {
            Map<String, Object> map = saleTicketReportFacade.querySaleDetailList(query);

            response.setContentType("application/x-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("SaleDetail-" + code + ".xlsx").getBytes(), "ISO-8859-1"));
            ServletOutputStream outputStream = response.getOutputStream();

            writer = SaleExportUtils.exportSaleDetail(outputStream, map, realName, query);

            writer.finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @description: 销售件数导出
     * @author yangyun
     * @date 2019/8/20 0020
     * @param response
     * @return void
     */
    @GetMapping("exportsaleticketcount")
    public void exportSaleTicketCount (HttpServletResponse response, SaleInfoQuery query){
        try {
            @Cleanup ServletOutputStream outputStream = null;
            String code = ExcelUtil.getCode();
            response.setContentType("application/x-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("SaleTicketCount-" + code + ".xlsx").getBytes(), "ISO-8859-1"));

            outputStream = response.getOutputStream();

            List<SaleInfo> list = saleTicketReportFacade.querySalesTicketCount(query);
            SaleExportUtils.exportSaleTicketCount(outputStream, list);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @description , @PathVariable("userCode") String userCode, @PathVariable("realName") String realName
     * @author yangyun
     * @date 2019/8/21 0021
     * @param response
     * @return void
     */
    @GetMapping("exportsalesmanvipcustomerdetail")
    public void exportSalesManVipCustomerDetail (HttpServletResponse response){
        try {

            List<VIPCustomerLevelReport> list = new ArrayList<>();
            @Cleanup ServletOutputStream outputStream = null;
            @Cleanup XSSFWorkbook workbook = null;
            XSSFSheet sheet =null;
            String code = ExcelUtil.getCode();
            response.setContentType("application/x-excel");

            outputStream = response.getOutputStream();
            List<VIPCustomerLevelReport> data = saleTicketReportFacade.querySalesmanInfo();
            String name = null;
            workbook = new XSSFWorkbook();
            for (VIPCustomerLevelReport temp : data) {
                // 销售员对应 vip 客户数据
                list = saleTicketReportFacade.querySaleVipCustomerDetail(temp.getUserCode());
                if (list.size() > 0) {
                    Map<String, List<VIPCustomerLevelReport>> collect = list.stream().collect(Collectors.groupingBy(VIPCustomerLevelReport::getCustGrade));
                    response.setHeader("Content-Disposition", "attachment;filename=" + new String(("vip客户" + ".xlsx").getBytes(), "ISO-8859-1"));
                    name = StringUtils.equals(temp.getRealName(), "") ? temp.getUserCode() : temp.getRealName().replace("/", "");
                    sheet = workbook.createSheet(name);
                    SaleExportUtils.exportSalesManVipCustomerDetail(outputStream, collect, sheet);
                }

//                TimeUnit.SECONDS.sleep(2);
            }
            workbook.write(outputStream);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
