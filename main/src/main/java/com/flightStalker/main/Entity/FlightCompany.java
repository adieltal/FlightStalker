package com.flightStalker.main.Entity;

public class FlightCompany {

    private String name;
    private String code;
    private String iconUrl;

    public FlightCompany(String name, String code, String iconUrl) {
        this.name = name;
        this.code = code;
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public FlightCompany setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public FlightCompany setCode(String code) {
        this.code = code;
        return this;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public FlightCompany setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
        return this;
    }

    @Override
    public String toString() {
        return "FlightCompany{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                '}';
    }
}
