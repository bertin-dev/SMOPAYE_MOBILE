package com.ezpass.smopaye_mobile.RemoteNotifications;

/**
 * Structure du model de la classe Sender
 *
 * @see Sender
 */

public class Sender {

    public Data data;
    public String to;

    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }
}
