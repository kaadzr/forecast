package com.globus_ltd.forecast_sber_test.data.model.remote;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CloudsResponse implements Serializable {

    @SerializedName("all")
    @Expose
    private int all;

    public CloudsResponse() {
    }

    public int getAll() {
        return all;
    }

    public void setAll(int all) {
        this.all = all;
    }


}
