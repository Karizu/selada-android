package com.boardinglabs.mireta.selada.component.network.entities;

import java.util.List;

public class Transaction extends BaseEntity {
    public String transaction_code;
    public String stock_location_id;
    public String manual_transaction_code;
    public int payment_type;
    public int payment_method;
    public int status;
    public String total_price;
    public String total_discount;
    public String aggregator_id;
    public StockLocation stock_location;
    public SumQty sum_qty;
    public SumSalesPrice sum_sales_price;
    public List<Item> transactionItems;

    public class SumQty{
        public String transaction_id;
        public String sum_qty;
    }
    public class SumSalesPrice{
        public String transaction_id;
        public String sum_sales_price;
    }
}
