package com.ezpass.smopaye_mobile.web_service_access;

import java.util.List;
import java.util.Map;

/**
 * Me permet de capture les erreurs lors de l'enregistrement ou l'auto-enregistrement selon un formatage précis
 *
 * @see ApiErrorRegister
 */
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
