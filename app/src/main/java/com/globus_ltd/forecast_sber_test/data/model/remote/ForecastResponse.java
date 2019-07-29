package com.globus_ltd.forecast_sber_test.data.model.remote;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ForecastResponse implements Serializable {

    @SerializedName("coord")
    @Expose
    private CoordResponse coord;
    @SerializedName("weather")
    @Expose
    private List<WeatherResponse> weather = null;
    @SerializedName("base")
    @Expose
    private String base;
    @SerializedName("main")
    @Expose
    private MainDataResponse main;
    @SerializedName("visibility")
    @Expose
    private int visibility;
    @SerializedName("wind")
    @Expose
    private WindResponse wind;
    @SerializedName("clouds")
    @Expose
    private CloudsResponse clouds;
    @SerializedName("dt")
    @Expose
    private int dt;
    @SerializedName("sys")
    @Expose
    private SysDataResponse sys;
    @SerializedName("timezone")
    @Expose
    private int timezone;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("cod")
    @Expose
    private int cod;

    public ForecastResponse() {
    }

    public CoordResponse getCoord() {
        return coord;
    }

    public void setCoord(CoordResponse coord) {
        this.coord = coord;
    }

    public List<WeatherResponse> getWeather() {
        return weather;
    }

    public void setWeather(List<WeatherResponse> weather) {
        this.weather = weather;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public MainDataResponse getMain() {
        return main;
    }

    public void setMain(MainDataResponse main) {
        this.main = main;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public WindResponse getWind() {
        return wind;
    }

    public void setWind(WindResponse wind) {
        this.wind = wind;
    }

    public CloudsResponse getClouds() {
        return clouds;
    }

    public void setClouds(CloudsResponse clouds) {
        this.clouds = clouds;
    }

    public int getDt() {
        return dt;
    }

    public void setDt(int dt) {
        this.dt = dt;
    }

    public SysDataResponse getSys() {
        return sys;
    }

    public void setSys(SysDataResponse sys) {
        this.sys = sys;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }
}