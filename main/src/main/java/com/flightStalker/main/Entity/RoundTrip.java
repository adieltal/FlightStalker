package com.flightStalker.main.Entity;

public class RoundTrip {

    private String countryName;
    private String destinationName;
    private Flight inboundFlight;
    private Flight outboundFlight;
    private int price;
    private long lastCheck;
    private int availableSeats;

    public RoundTrip(Flight inboundFlight, Flight outboundFlight, int price, String countryName, String destinationName, long lastCheck, int availableSeats) {

        this.countryName = countryName;
        this.destinationName = destinationName;
        this.inboundFlight = inboundFlight;
        this.outboundFlight = outboundFlight;
        this.price = price;
        this.lastCheck = lastCheck;
        this.availableSeats = availableSeats;

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
