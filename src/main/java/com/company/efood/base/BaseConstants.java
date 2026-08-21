package com.company.efood.base;


import org.springframework.http.MediaType;

/**
 * @version 1.0.0
 * @Author Md. Sabbir Hossain
 * @Email sabbirr883@gmail.com
 * @Since March 1, 2024
 */

public interface BaseConstants {

    String APP_ENDPOINT = "/api/";
    String PUBLIC_ENDPOINT = APP_ENDPOINT + "public/";
    String PRIVET_ENDPOINT = APP_ENDPOINT + "private/";
    String SYSTEM_ADMIN_END_POINT = PRIVET_ENDPOINT + "sya/";
    String SELLER_END_POINT = PRIVET_ENDPOINT + "seller/";
    String CUSTOMER_END_POINT = PRIVET_ENDPOINT + "customer/";
    String RAIDER_END_POINT = PRIVET_ENDPOINT + "raider/";

    String SHOP_END_POINT = "shop/";
    String BRANCH_END_POINT = "branch";
    String PRODUCT_END_POINT = "product";
    String CATEGORY_END_POINT =  "category";

    String AUTHENTICATION_END_POINT = APP_ENDPOINT + "/auth";

//    String SELLER_AUTHENTICATION_END_POINT = AUTHENTICATION_END_POINT + "seller/";
//    String RAIDER_AUTHENTICATION_END_POINT = AUTHENTICATION_END_POINT + "raider/";
//    String CUSTOMER_AUTHENTICATION_END_POINT = AUTHENTICATION_END_POINT + "customer/";

    String SELLER_PUBLIC_END_POINT = PUBLIC_ENDPOINT + "seller/";
    String RAIDER_PUBLIC_END_POINT = PUBLIC_ENDPOINT + "raider/";
    String CUSTOMER_PUBLIC_END_POINT = PUBLIC_ENDPOINT + "customer/";

    String AUTHENTICATION_ENDPOINT = APP_ENDPOINT + "auth/";
    String PAGEABLE_DATA_PATH ="pageable-data";
    Integer USER_TYPE_ID_DEV = 0;
    Integer USER_TYPE_ID_ADMIN = 1;
    Integer USER_TYPE_ID_SELLER = 2;
    Integer USER_TYPE_ID_RAIDER = 3;
    Integer USER_TYPE_ID_CUSTOMER = 4;


    String OBJECT_ID = "id";

    String OBJECT_SEARCH_VALUE = "value";
    String ACTIVE_PATH = "active";
    String DROPDOWN_LIST_PATH = "dropdown-list";
    String SUB_CATEGORY = "sub_category_";

    String GET_OBJECT_BY_ID = "get-by-id/{" + OBJECT_ID + ":[0-9]*}";
    String EXTERNAL_MEDIA_TYPE = MediaType.APPLICATION_JSON_VALUE;

    //===============================================================
    //                      Server Message
    //===============================================================
    String SAVE_MESSAGE = "Successfully Saved";
    String SAVE_MESSAGE_BN = "à¦¸à¦«à¦²à¦­à¦¾à¦¬à§‡ à¦¸à¦‚à¦°à¦•à§�à¦·à¦£ à¦•à¦°à¦¾ à¦¹à¦¯à¦¼à§‡à¦›à§‡";
    String UPDATE_MESSAGE = "Successfully Updated";
    String UPDATE_MESSAGE_BN = "à¦¸à¦«à¦²à¦­à¦¾à¦¬à§‡ à¦†à¦ªà¦¡à§‡à¦Ÿ à¦•à¦°à¦¾ à¦¹à¦¯à¦¼à§‡à¦›à§‡";
    String UPLOAD_MESSAGE = "Successfully Uploaded";
    String UPLOAD_MESSAGE_BN = "à¦¸à¦«à¦²à¦­à¦¾à¦¬à§‡ à¦†à¦ªà¦²à§‹à¦¡ à¦•à¦°à¦¾ à¦¹à¦¯à¦¼à§‡à¦›à§‡";
    String DELETE_MESSAGE = "Successfully Deleted";
    String DELETE_MESSAGE_BN = "à¦¸à¦«à¦²à¦­à¦¾à¦¬à§‡ à¦¡à¦¿à¦²à¦¿à¦Ÿ à¦•à¦°à¦¾ à¦¹à§Ÿà§‡à¦›à§‡";
    String DELETE_MESSAGE_FAILED = "Delete Failed";
    String DELETE_MESSAGE_FAILED_BN = "à¦¡à¦¿à¦²à¦¿à¦Ÿ à¦¸à¦«à¦² à¦¹à§Ÿà¦¨à¦¿";
    String DATA_ALRADY_EXISTS_MESSAGE = "Data already exists!!";
    String DATA_ALRADY_EXISTS_MESSAGE_BN = "à¦¤à¦¥à§�à¦¯à¦Ÿà¦¿ à¦‡à¦¤à¦¿à¦®à¦§à§�à¦¯à§‡ à¦¸à¦‚à¦°à¦•à§�à¦·à¦¿à¦¤ à¦°à§Ÿà§‡à¦›à§‡!!";
    String CHILD_RECORD_FOUND = "Child record found !!";
    String CHILD_RECORD_FOUND_BN = "à¦šà¦¾à¦‡à¦²à§�à¦¡ à¦°à§‡à¦•à¦°à§�à¦¡ à¦«à¦¾à¦‰à¦¨à§�à¦¡ !!";
    String PROCESS_COMPLETE = "Process successfully completed !!";
    String PROCESS_COMPLETE_BN = "à¦ªà§�à¦°à¦•à§�à¦°à¦¿à§Ÿà¦¾ à¦¸à¦«à¦²à¦­à¦¾à¦¬à§‡ à¦¶à§‡à¦· à¦¹à§Ÿà§‡à¦›à§‡  !!";
    String PROCESS_FAILED = "Processing Failed !!";
    String PROCESS_FAILED_BN = "à¦ªà§�à¦°à¦•à§�à¦°à¦¿à¦¯à¦¼à¦¾à¦•à¦°à¦£ à¦¬à§�à¦¯à¦°à§�à¦¥ à¦¹à¦¯à¦¼à§‡à¦›à§‡  !!";
    String SMS_MESSAGE = "SMS Successfully Send";
    String SMS_MESSAGE_BN = "à¦�à¦¸à¦�à¦®à¦�à¦¸ à¦¸à¦«à¦²à¦­à¦¾à¦¬à§‡ à¦ªà¦¾à¦ à¦¾à¦¨à§‹ à¦¹à¦¯à¦¼à§‡à¦›à§‡";
    String INPUT_VALIDATION_MESSAGE = "Input is not valid!";
    String INPUT_VALIDATION_MESSAGE_BN = "à¦‡à¦¨à¦ªà§�à¦Ÿ à¦¬à§ˆà¦§ à¦¨à¦¯à¦¼!";


}