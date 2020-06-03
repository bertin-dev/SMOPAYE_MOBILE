package com.ezpass.smopaye_mobile.web_service_response.Recharge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("flow")
    @Expose
    private String flow;
    @SerializedName("channel_ussd")
    @Expose
    private String channel_ussd;
    @SerializedName("channel_name")
    @Expose
    private String channel_name;
    @SerializedName("channel")
    @Expose
    private String channel;
    @SerializedName("paymentId")
    @Expose
    private String paymentId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public String getChannel_ussd() {
        return channel_ussd;
    }

    public void setChannel_ussd(String channel_ussd) {
        this.channel_ussd = channel_ussd;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
