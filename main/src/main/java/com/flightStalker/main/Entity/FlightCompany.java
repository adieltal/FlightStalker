package com.flightStalker.main.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FlightCompany {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String name;
    private String code;
    private String iconUrl;

    public FlightCompany() {
    }

    public FlightCompany(String name, String code, String iconUrl) {
        this.name = name;
        this.code = code;
        this.iconUrl = iconUrl;
    }

    public Long getId() {
        return id;
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
