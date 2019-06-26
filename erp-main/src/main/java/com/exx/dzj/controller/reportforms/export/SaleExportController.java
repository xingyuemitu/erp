package com.exx.dzj.controller.reportforms.export;

import com.alibaba.excel.ExcelWriter;
import com.exx.dzj.bean.SaleDetailReportQuery;
import com.exx.dzj.common.export.SaleExportUtils;
import com.exx.dzj.constant.CommonConstant;
import com.exx.dzj.entity.bean.CustomerQuery;
import com.exx.dzj.entity.bean.StockInfoQuery;
import com.exx.dzj.entity.bean.UserInfoQuery;
import com.exx.dzj.entity.market.SaleInfo;
import com.exx.dzj.entity.market.SaleInfoQuery;
import com.exx.dzj.entity.market.SaleListInfo;
import com.exx.dzj.entity.statistics.sales.SaleInfoReport;
import com.exx.dzj.entity.statistics.sales.StockTypeReport;
import com.exx.dzj.entity.user.UserVo;
import com.exx.dzj.excepte.ErpException;
import com.exx.dzj.facade.reportforms.sale.SaleTicketReportFacade;
import com.exx.dzj.facade.user.UserFacade;
import com.exx.dzj.util.enums.ExportFileNameEnum;
import com.exx.dzj.util.excel.ExcelUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    public void exportSaleList (HttpServletRequest request, HttpServletResponse response, @PathVariable("realName") String realName, SaleInfoQuery query){
        try {
            String code = ExcelUtil.getCode();

            List<SaleListInfo> list = null;
            response.setContentType("application/x-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("Sale-" + code + ".xlsx").getBytes(), "ISO-8859-1"));

            ServletOutputStream outputStream = response.getOutputStream();
            XSSFWorkbook writer = null;
            int type = query.getType();
            switch (type){
                case CommonConstant.DEFAULT_VALUE_ONE:
                    list = saleTicketReportFacade.exportSaleList(query);
                    writer = SaleExportUtils.exportSaleList(outputStream, list, query.getFieldList());
//                    writer = SaleExportUtils.exportSaleList2(outputStream, list);
                    break;
                case CommonConstant.DEFAULT_VALUE_TWO:
                    list = saleTicketReportFacade.querySalesListForIds(query);
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

    @GetMapping("exportSaleList2/{realName}")
    public void exportSaleList2 (HttpServletRequest request, HttpServletResponse response, @PathVariable("realName") String realName, SaleInfoQuery query){
        try {
            String code = ExcelUtil.getCode();

            List<SaleListInfo> list = null;
            response.setContentType("application/x-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("Sale-" + code + ".xlsx").getBytes(), "ISO-8859-1"));

            ServletOutputStream outputStream = response.getOutputStream();
            ExcelWriter writer = null;
            int type = query.getType();
            switch (type){
                case CommonConstant.DEFAULT_VALUE_ONE:
                    list = saleTicketReportFacade.exportSaleList(query);
                    writer = SaleExportUtils.exportSaleListTenet2(outputStream, list);
                    break;
                case CommonConstant.DEFAULT_VALUE_TWO:
                    list = saleTicketReportFacade.querySalesListForIds(query);
                    writer = SaleExportUtils.exportSaleList2(outputStream, list);
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
}
