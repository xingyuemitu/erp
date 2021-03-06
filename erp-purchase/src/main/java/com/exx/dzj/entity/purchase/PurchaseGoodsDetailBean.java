package com.exx.dzj.entity.purchase;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ToString
@EqualsAndHashCode(of = "id")
public class PurchaseGoodsDetailBean implements Serializable {
    private Integer id;

    private String purchaseCode;

    private String stockCode;

    private String stockName;

    private String stockAddressCode;

    private String stockAddress;

    private Double goodsNum;// 采购数量
    private Integer realGoodsNum; // 实际入库数量

    private BigDecimal unitPrice;

    private BigDecimal realSellPrice; // 实际采购价格

    private String priceType;

    private BigDecimal discountRate;

    private BigDecimal discountAmount;

    private BigDecimal purchaseVolume;

    private String remarks;

    private Date createTime;

    private String createUser;

    private Date updateTime;

    private String updateUser;

    // 同仓的商品的总金额
    private BigDecimal sumPrice;

    // 同仓的商品的总数量
    private Double sumNum;
}