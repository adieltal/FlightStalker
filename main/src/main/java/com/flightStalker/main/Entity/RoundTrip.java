package com.flightStalker.main.Entity;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class RoundTrip {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String countryName;
    private String destinationName;
    @OneToOne(cascade = {CascadeType.ALL})
    private Flight inboundFlight;
    @OneToOne(cascade = {CascadeType.ALL})
    private Flight outboundFlight;
    private int price;
    private long lastCheck;
    private int availableSeats;

    public RoundTrip() {
    }

    public RoundTrip(Flight inboundFlight, Flight outboundFlight, int price, String countryName, String destinationName, long lastCheck, int availableSeats) {

        this.countryName = countryName;
        this.destinationName = destinationName;
        this.inboundFlight = inboundFlight;
        this.outboundFlight = outboundFlight;
        this.price = price;
        this.lastCheck = lastCheck;
        this.availableSeats = availableSeats;

    }

    public Long getId() {
        return id;
    }

    public String getCountryName() {
        return countryName;
    }

    public RoundTrip setCountryName(String countryName) {
        this.countryName = countryName;
        return this;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public RoundTrip setDestinationName(String destinationName) {
        this.destinationName = destinationName;
        return this;
    }

    public Flight getInboundFlight() {
        return inboundFlight;
    }

    public RoundTrip setInboundFlight(Flight inboundFlight) {
        this.inboundFlight = inboundFlight;
        return this;
    }

    public Flight getOutboundFlight() {
        return outboundFlight;
    }

    public RoundTrip setOutboundFlight(Flight outboundFlight) {
        this.outboundFlight = outboundFlight;
        return this;
    }

    public int getPrice() {
        return price;
    }

    public RoundTrip setPrice(int price) {
        this.price = price;
        return this;
    }

    public long getLastCheck() {
        return lastCheck;
    }

    public RoundTrip setLastCheck(long lastCheck) {
        this.lastCheck = lastCheck;
        return this;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public RoundTrip setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
        return this;
    }

    @Override
    public String toString() {
        return "RoundTrip{" +
                "countryName='" + countryName + '\'' +
                ", destinationName='" + destinationName + '\'' +
                ", inboundFlight=" + inboundFlight +
                ", outboundFlight=" + outboundFlight +
                ", price=" + price +
                ", lastCheck=" + lastCheck +
                ", availableSeats=" + availableSeats +
                '}';
    }
}
