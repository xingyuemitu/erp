package com.exx.dzj.entity.customer;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author
 * @Date 2019/1/9 0009 16:22
 * @Description  查询添加
 */
@Data
@ToString
public class CustomerSupplierQuery implements Serializable {

    private int page;

    private int limit;

    private String custName;

    private String linkMan;
}
