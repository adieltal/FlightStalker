package com.flightStalker.main.Task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightStalker.main.Entity.Flight;
import com.flightStalker.main.Entity.FlightCompany;
import com.flightStalker.main.Entity.RoundTrip;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Worker {

    //    private String url = "";
    private String url = "https://s3.eu-central-1.amazonaws.com/catalogs.hulyo.co.il/catalogs/Production/Flights/v1.4/above199FlightsWebOnly.js";

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public void parseDocument() {

        try {
            ObjectMapper mapper = new ObjectMapper();
            URL urlPath = new URL(url);
            parseCsvFile(urlPath);

            ClassLoader classLoader = getClass().getClassLoader();
            //File file = new File(classLoader.getResource("hulyo.json").getFile());
            File file = new File("hulyo.json");
            InputStream is = new FileInputStream(file);



            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            //Stream<String> stream = Files.lines(Paths.get("hulyo.json"));
            //String jsonText = stream.toString();
            JsonNode rootNode = mapper.readTree(jsonText);
            JsonNode DealCountryNamesNode = rootNode.path("ShowEmptyCatalog");
            System.out.println(DealCountryNamesNode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String convertToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    public void parseCsvFile(URL urlPath) {
        try {
            InputStream in = urlPath.openStream();
            Files.copy(in, Paths.get("hulyo.json"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String parseWithSelenium() {
        ArrayList<RoundTrip> roundTrips = new ArrayList<>();
        try{
            //point at the chrome-driver path
            System.setProperty("webdriver.chrome.driver", "C:\\ProgramData\\chocolatey\\lib\\chromedriver\\tools\\chromedriver.exe");
            //run in headless mode
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
            //start driver
            WebDriver driver = new ChromeDriver(options);
            //connect hulyo website
            driver.get(url);
            String str = driver.getPageSource();

            final String regex = "\\{(.*)\\}";
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(str);

            if (matcher.find()) {
                String strJSON =  matcher.group(0) ;
                ObjectMapper mapper = new ObjectMapper();
                final JsonNode flightsNode = mapper.readTree(strJSON).get("Flights");
                if(flightsNode.isArray()){
                    //iterate over flights array
                    for( final JsonNode roundTripNode : flightsNode){
                        if(isRoundTripValid(roundTripNode)){
                            RoundTrip roundTrip = parseRoundTrip(roundTripNode);
                            roundTrips.add(roundTrip);
                        }
                    }
                    //check results
                    for(RoundTrip roundTrip : roundTrips){
                        System.out.println("RoundTrip: " + roundTrip); //todo generate toString() method for all objects

                    }
                }
            }

        } catch (IOException e){
            e.printStackTrace();

        }

        return roundTrips.toString();
    }

    private boolean isRoundTripValid(JsonNode roundTripNode) {
        return roundTripNode.has("PriceTitle")
                && roundTripNode.has("InboundFlights")
                && roundTripNode.has("OutboundFlights");
    }

    private RoundTrip parseRoundTrip(JsonNode roundTripNode) {
        System.out.println("flight data: " + roundTripNode);
        Flight inboundFlight = parseSingleFlight(roundTripNode.get("InboundFlights").get(0)); //todo add a validation check
        Flight outboundFlight = parseSingleFlight(roundTripNode.get("OutboundFlights").get(0)); //todo add a validation check`
        int price = extractFlightPrice(roundTripNode);
        String countryName = extractCountryName(roundTripNode);
        String destinationName = extractDestinationName(roundTripNode);
        int availableSeats = extractAvailableSeats(roundTripNode);
        long lastCheck = System.currentTimeMillis();
        return new RoundTrip(inboundFlight, outboundFlight, price, countryName, destinationName, lastCheck, availableSeats);
    }

    private String extractDestinationName(JsonNode roundTripNode) {
        return roundTripNode.get("DealDestinationName").textValue();

    }

    private int extractAvailableSeats(JsonNode roundTripNode) {
//        String availableSeats = roundTripNode.has("AvailableSeats") ? roundTripNode.get("AvailableSeats").textValue() : "0";
//        return Integer.parseInt(availableSeats);
        return 0;
    }

    private String extractCountryName(JsonNode roundTripNode) {
        return roundTripNode.get("DealCountryName").textValue();
    }

    private int extractFlightPrice(JsonNode roundTripNode) {
        String price = roundTripNode.get("PriceTitle").textValue();
        return Integer.parseInt(removeDollarSign(price));
    }

    private String removeDollarSign(String price) {
        return price.substring(1);
    }

    private Flight parseSingleFlight(JsonNode flightNode) { //todo validate fields first and then extract values

        FlightCompany flightCompany = extractFlightCompany(flightNode);
        String flightNumber = extractFlightNumber(flightNode);
        String from = extractDepartureAirport(flightNode);
        String  to = extractArrivalAirport(flightNode);
        Date departure = extractDeparture(flightNode);
        Date arrival = extractArrival(flightNode);
        return new Flight(flightCompany,flightNumber,from,to,departure,arrival);
    }

    private FlightCompany extractFlightCompany(JsonNode flightNode) {
        String name = flightNode.get("AirlineName").textValue();
        String code = flightNode.get("AirlineCode").textValue();
        String iconUrl = flightNode.get("AirlineIconUrl").textValue();
        return new FlightCompany(name,code,iconUrl);
    }

    private String extractDepartureAirport(JsonNode flightNode) {
        return flightNode.get("DepartureDestCode").textValue();
    }

    private String extractArrivalAirport(JsonNode flightNode) {
        return flightNode.get("ArrivalDestCode").textValue();
    }

    private Date extractDeparture(JsonNode flightNode){
        return extractFlightDate(flightNode, "DepartureATA");
    }

    private Date extractArrival(JsonNode flightNode){
        return extractFlightDate(flightNode, "ArrivalATA");
    }

    private Date extractFlightDate(JsonNode flightNode, String fieldName) {
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

    private String extractFlightNumber(JsonNode flightNode) {
        return flightNode.get("FlightNumber").textValue();
    }
}