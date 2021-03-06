package com.exx.dzj.service.purchasereceipts;

import com.exx.dzj.entity.purchase.PurchaseGoodsDetailBean;
import com.exx.dzj.entity.purchase.PurchaseReceiptsDetailsBean;

import java.util.List;

/**
 * @author yangyun
 * @create 2019-01-12-16:06
 */
public interface PurchaseReceiptsService {

    void batchInsertPurchaseReceipts(List<PurchaseReceiptsDetailsBean> purchaseReceiptsDetailsBeans);

    void batchUpdatePurchaseReceiptsDetails(List<PurchaseReceiptsDetailsBean> purchaseReceiptsDetailsBeans);

    void batchDeletePurchaseReceipts(List<Integer> receiptIds);

    List<PurchaseReceiptsDetailsBean> queryPurchaseReceviptDetailList(String purchaseCode);

    void financeCheckPurchaseTicet (List<Integer> ids);

    void warehouseCheckPurchaseTicet (List<Integer> ids);

    List<PurchaseGoodsDetailBean> queryPurchaseGoodsDetail(List<Integer> ids);
}
