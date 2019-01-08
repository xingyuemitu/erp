package com.exx.dzj.service.customer;

import com.exx.dzj.entity.customer.CustomerSupplierBean;
import com.exx.dzj.entity.customer.CustomerSupplierInfo;
import com.exx.dzj.entity.customer.CustomerSupplierModel;
import com.exx.dzj.result.Result;

import java.util.List;

/**
 * @Author
 * @Date 2019/1/5 0005 16:29
 * @Description 客户或供应商 service
 */
public interface CustomerService {

    /**
     * 查询 客户或供应商列表数据
     * @return
     */
    Result queryCustomerSupplierList(int pageNum, int pageSize);

    /**
     * 查询 客户或供应商详细信息数据
     * @param custCode
     * @return
     */
    CustomerSupplierInfo queryCustomerSupplierInfo(String custCode);

    /**
     * 保存 客户或供应商基础信息数据
     * @param bean
     */
    void saveCustomerSupplier(CustomerSupplierBean bean);

    /**
     * 修改 客户或供应商基础信息数据
     * @param bean
     */
    void modifyCustomerSupplier(CustomerSupplierBean bean);

    /**
     * 删除 客户或供应商数据
     * @param custCodes
     */
    void delCustomerSupplier(String custCodes, int isEnable);
}