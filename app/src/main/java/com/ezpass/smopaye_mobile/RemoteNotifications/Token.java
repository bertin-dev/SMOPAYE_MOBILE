package com.ezpass.smopaye_mobile.RemoteNotifications;

/**
 * Structure du model de la classe Token pour le chat
 *
 * @see Token
 */
public class Token {

    private String token;

    public Token() {
    }

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
