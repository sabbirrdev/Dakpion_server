package com.company.efood.user.dto;

import lombok.Data;

@Data
public class SSLPaymentRequestDto {
    private String total_amount;
    private String currency = "BDT";
    private String tran_id;
    private String success_url;
    private String fail_url;
    private String cancel_url;
    private String ipn_url;
    private String cus_name;
    private String cus_email;
    private String cus_add1;
    private String cus_city;
    private String cus_postcode;
    private String cus_country = "Bangladesh";
    private String cus_phone;
    private String shipping_method = "NO";
    private String product_name = "Food Order";
    private String product_category = "Food";
    private String product_profile = "general";
}

