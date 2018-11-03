package com.flightStalker.main.Entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Flight {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @ManyToOne(cascade = {CascadeType.ALL})
    private FlightCompany flightCompany;
    private String flightNumber;
    private String departureAirport;
    private String arrivalAirport;
    private Date departure;
    private Date arrival;

    public Flight() {
    }

    public Flight(FlightCompany flightCompany, String flightNumber, String departureAirport, String arrivalAirport, Date departure, Date arrival) {

        this.flightCompany = flightCompany;
        this.flightNumber = flightNumber;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departure = departure;
        this.arrival = arrival;
    }

    public Long getId() {
        return id;
    }

    public FlightCompany getFlightCompany() {
        return flightCompany;
    }

    public Flight setFlightCompany(FlightCompany flightCompany) {
        this.flightCompany = flightCompany;
        return this;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public Flight setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
        return this;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public Flight setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
        return this;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public Flight setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
        return this;
    }


    public Date getDeparture() {
        return departure;
    }

    public Flight setDeparture(Date departure) {
        this.departure = departure;
        return this;
    }

    public Date getArrival() {
        return arrival;
    }

    public Flight setArrival(Date arrival) {
        this.arrival = arrival;
        return this;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flightCompany=" + flightCompany +
                ", flightNumber='" + flightNumber + '\'' +
                ", departureAirport='" + departureAirport + '\'' +
                ", arrivalAirport='" + arrivalAirport + '\'' +
                ", departure=" + departure +
                ", arrival=" + arrival +
                '}';
    }
}
