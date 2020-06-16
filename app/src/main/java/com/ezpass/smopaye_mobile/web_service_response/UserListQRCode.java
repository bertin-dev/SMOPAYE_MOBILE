package com.ezpass.smopaye_mobile.web_service_response;

import com.ezpass.smopaye_mobile.Profil_user.Particulier;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserListQRCode {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("category_id")
    @Expose
    private String category_id;
    @SerializedName("particulier")
    @Expose
    List<Particulier> particulier;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public List<Particulier> getParticulier() {
        return particulier;
    }

    public void setParticulier(List<Particulier> particulier) {
        this.particulier = particulier;
    }
}
