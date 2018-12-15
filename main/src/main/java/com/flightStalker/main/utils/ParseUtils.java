package com.flightStalker.main.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightStalker.main.Entity.Flight;
import com.flightStalker.main.Entity.FlightCompany;
import com.flightStalker.main.Entity.RoundTrip;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ParseUtils {
    private static final String ROUND_TRIP_URL_PREFIX = "https://www.hulyo.co.il/flightDetails/";

    public static ArrayList<RoundTrip> buildRoundTrips(String response, long lastCheck) throws IOException {
        ArrayList<RoundTrip> roundTrips = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        final JsonNode flightsNode = mapper.readTree(response).get("Flights");

        //iterate over flights array
        for (final JsonNode roundTripNode : flightsNode) {
            if (isRoundTripValid(roundTripNode)) {
                RoundTrip roundTrip = parseRoundTrip(roundTripNode, lastCheck);
                roundTrips.add(roundTrip);
            }
        }
        return roundTrips;
    }

    private static boolean isRoundTripValid(JsonNode roundTripNode) {
        return roundTripNode.has("PriceTitle")
                && roundTripNode.has("InboundFlights")
                && roundTripNode.has("OutboundFlights");
    }

    private static RoundTrip parseRoundTrip(JsonNode roundTripNode, long lastCheck) {
//        System.out.println("flight data: " + roundTripNode);
        Flight inboundFlight = parseSingleFlight(roundTripNode.get("InboundFlights").get(0)); //todo add a validation check
        Flight outboundFlight = parseSingleFlight(roundTripNode.get("OutboundFlights").get(0)); //todo add a validation check`
        String flightUrl = extractFlightUrl(roundTripNode);
        int price = extractFlightPrice(roundTripNode);
        String countryName = extractCountryName(roundTripNode);
        String destinationName = extractDestinationName(roundTripNode);
        int availableSeats = extractAvailableSeats(roundTripNode);
        return new RoundTrip(inboundFlight, outboundFlight, flightUrl, price, countryName, destinationName, lastCheck, availableSeats);
    }

    private static String extractFlightUrl(JsonNode roundTripNode) {
        return ROUND_TRIP_URL_PREFIX + roundTripNode.get("Id").asInt();
    }

    private static String extractDestinationName(JsonNode roundTripNode) {
        return roundTripNode.get("DealDestinationName").textValue();

    }

    private static int extractAvailableSeats(JsonNode roundTripNode) {
        return roundTripNode.get("AvailableSeats").asInt();
    }

    private static String extractCountryName(JsonNode roundTripNode) {
        return roundTripNode.get("DealCountryName").textValue();
    }

    private static int extractFlightPrice(JsonNode roundTripNode) {
        String price = roundTripNode.get("PriceTitle").textValue();
        return Integer.parseInt(removeDollarSign(price));
    }

    private static String removeDollarSign(String price) {
        return price.substring(1);
    }

    private static Flight parseSingleFlight(JsonNode flightNode) { //todo validate fields first and then extract values

        FlightCompany flightCompany = extractFlightCompany(flightNode);
        String flightNumber = extractFlightNumber(flightNode);
        String from = extractDepartureAirport(flightNode);
        String to = extractArrivalAirport(flightNode);
        Date departure = extractDeparture(flightNode);
        Date arrival = extractArrival(flightNode);
        return new Flight(flightCompany, flightNumber, from, to, departure, arrival);
    }

    private static FlightCompany extractFlightCompany(JsonNode flightNode) {
        String name = flightNode.get("AirlineName").textValue();
        String code = flightNode.get("AirlineCode").textValue();
        String iconUrl = flightNode.get("AirlineIconUrl").textValue();
        return new FlightCompany(name, code, iconUrl);
    }

    private static String extractDepartureAirport(JsonNode flightNode) {
        return flightNode.get("DepartureDestCode").textValue();
    }

    private static String extractArrivalAirport(JsonNode flightNode) {
        return flightNode.get("ArrivalDestCode").textValue();
    }

    private static Date extractDeparture(JsonNode flightNode) {
        return extractFlightDate(flightNode, "DepartureATA");
    }

    private static Date extractArrival(JsonNode flightNode) {
        return extractFlightDate(flightNode, "ArrivalATA");
    }

    private static Date extractFlightDate(JsonNode flightNode, String fieldName) {
        String flightDateStr = flightNode.get(fieldName).textValue();
        SimpleDateFormat format = new SimpleDateFormat("DD/MM/yy HH:mm");
        Date flightDate = null;
        try {
            flightDate = format.parse(flightDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flightDate;
    }

    private static String extractFlightNumber(JsonNode flightNode) {
        return flightNode.get("FlightNumber").textValue();
    }


}
