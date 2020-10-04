package com.ezpass.smopaye_mobile.web_service_access;

import java.util.List;
import java.util.Map;

public class ApiErrorRegister {

    String message;
    Map<String, List<String>> data;

    public String getMessage() {
        return message;
    }

    public Map<String, List<String>> getData() {
        return data;
    }
}
