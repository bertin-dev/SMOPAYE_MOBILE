package com.ezpass.smopaye_mobile.web_service_access;

import java.util.List;
import java.util.Map;

/**
 * Me permet de capture les erreurs selon un formatage précis
 *
 * @see ApiError
 */
public class ApiError {

    String message;
    Map<String, List<String>> errors;

    public String getMessage() {
        return message;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }
}
