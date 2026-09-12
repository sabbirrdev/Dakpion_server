package com.company.dakpion.base;

import org.springframework.http.MediaType;

public interface BaseConstants {

    String APP_ENDPOINT = "/api/";
    String PUBLIC_ENDPOINT = APP_ENDPOINT + "public/";
    String PRIVATE_ENDPOINT = APP_ENDPOINT + "private/";

    String AUTHENTICATION_ENDPOINT = APP_ENDPOINT + "auth/";
    String AUTHENTICATION_V1_ENDPOINT = APP_ENDPOINT + "v1/auth/";

    String PAGEABLE_DATA_PATH = "pageable-data";

    Integer USER_TYPE_ID_DEV = 0;
    Integer USER_TYPE_ID_ADMIN = 1;
    Integer USER_TYPE_ID_MODERATOR = 2;
    Integer USER_TYPE_ID_USER = 3;

    String OBJECT_ID = "id";
    String OBJECT_SEARCH_VALUE = "value";
    String ACTIVE_PATH = "active";
    String DROPDOWN_LIST_PATH = "dropdown-list";

    String GET_OBJECT_BY_ID = "get-by-id/{" + OBJECT_ID + ":[0-9]*}";
    String EXTERNAL_MEDIA_TYPE = MediaType.APPLICATION_JSON_VALUE;

    // Server Messages
    String SAVE_MESSAGE = "Successfully Saved";
    String SAVE_MESSAGE_BN = "সফলভাবে সংরক্ষণ করা হয়েছে";
    String UPDATE_MESSAGE = "Successfully Updated";
    String UPDATE_MESSAGE_BN = "সফলভাবে আপডেট করা হয়েছে";
    String UPLOAD_MESSAGE = "Successfully Uploaded";
    String UPLOAD_MESSAGE_BN = "সফলভাবে আপলোড করা হয়েছে";
    String DELETE_MESSAGE = "Successfully Deleted";
    String DELETE_MESSAGE_BN = "সফলভাবে ডিলিট করা হয়েছে";
    String DELETE_MESSAGE_FAILED = "Delete Failed";
    String DELETE_MESSAGE_FAILED_BN = "ডিলিট সফল হয়নি";
    String DATA_ALRADY_EXISTS_MESSAGE = "Data already exists!!";
    String DATA_ALRADY_EXISTS_MESSAGE_BN = "তথ্যটি ইতিমধ্যে সংরক্ষিত রয়েছে!!";
    String CHILD_RECORD_FOUND = "Child record found !!";
    String CHILD_RECORD_FOUND_BN = "চাইল্ড রেকর্ড ফাউন্ড !!";
    String PROCESS_COMPLETE = "Process successfully completed !!";
    String PROCESS_COMPLETE_BN = "প্রক্রিয়া সফলভাবে শেষ হয়েছে !!";
    String PROCESS_FAILED = "Processing Failed !!";
    String PROCESS_FAILED_BN = "প্রক্রিয়াকরণ ব্যর্থ হয়েছে !!";
    String SMS_MESSAGE = "SMS Successfully Send";
    String SMS_MESSAGE_BN = "এসএমএস সফলভাবে পাঠানো হয়েছে";
    String INPUT_VALIDATION_MESSAGE = "Input is not valid!";
    String INPUT_VALIDATION_MESSAGE_BN = "ইনপুট বৈধ নয়!";
}