package com.exx.dzj.service.statistics.sales;

import com.exx.dzj.entity.bean.CustomerQuery;
import com.exx.dzj.entity.bean.StockInfoQuery;
import com.exx.dzj.entity.bean.UserInfoQuery;
import com.exx.dzj.entity.customer.CustomerSupplierBean;
import com.exx.dzj.entity.statistics.sales.CustomerReport;
import com.exx.dzj.entity.statistics.sales.StockTypeReport;
import com.exx.dzj.entity.statistics.sales.UserInfoReport;

import java.util.List;

/**
 * @author yangyun
 * @create 2019-04-11-14:16
 */
public interface SaleTicketReportService {

    public List<StockTypeReport> statisticsSaleByInventory(StockInfoQuery query);

    List<UserInfoReport> statisticsSaleBySalesMan (UserInfoQuery query);

    List<CustomerReport> querySalesTicketByCust (CustomerQuery query);
}
