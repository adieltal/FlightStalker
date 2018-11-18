package com.flightStalker.main.Worker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightStalker.main.Entity.Flight;
import com.flightStalker.main.Entity.FlightCompany;
import com.flightStalker.main.Entity.RoundTrip;
import com.flightStalker.main.Dao.RoundTripDAO;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Worker {

    @Autowired
    private RoundTripDAO roundTripDAO;

    public static final long NOT_INITIALIZED = 0;
    private static long lastCheck = NOT_INITIALIZED;
    private String url = "https://s3.eu-central-1.amazonaws.com/catalogs.hulyo.co.il/catalogs/Production/Flights/v1.4/above199FlightsWebOnly.js";
    private static final Logger log = LoggerFactory.getLogger(Worker.class);
    private final String ROUND_TRIP_URL_PREFIX = "https://www.hulyo.co.il/flightDetails/";

// todo: enable scheduled on production mode
//    @Scheduled(cron = "0 0/15 * * * *")
    public void parseWithSelenium() {
        log.info("Start scheduled task parseWithSelenium");
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
                    Worker.lastCheck = System.currentTimeMillis();
                    for( final JsonNode roundTripNode : flightsNode){
                        if(isRoundTripValid(roundTripNode)){
                            RoundTrip roundTrip = parseRoundTrip(roundTripNode, Worker.lastCheck);
                            roundTrips.add(roundTrip);
                        }
                    }
                }
            }

        } catch (IOException e){
            e.printStackTrace();

        }
        roundTripDAO.saveAll(roundTrips);
        //return roundTrips.toString();
    }

    private boolean isRoundTripValid(JsonNode roundTripNode) {
        return roundTripNode.has("PriceTitle")
                && roundTripNode.has("InboundFlights")
                && roundTripNode.has("OutboundFlights");
    }

    private RoundTrip parseRoundTrip(JsonNode roundTripNode, long lastCheck) {
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

    private String extractFlightUrl(JsonNode roundTripNode) {
        return ROUND_TRIP_URL_PREFIX + roundTripNode.get("Id").asInt();
    }

    private String extractDestinationName(JsonNode roundTripNode) {
        return roundTripNode.get("DealDestinationName").textValue();

    }

    private int extractAvailableSeats(JsonNode roundTripNode) {
        return roundTripNode.get("AvailableSeats").asInt();
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

    public long getLastCheck() {
        if(Worker.lastCheck == Worker.NOT_INITIALIZED){
            Worker.lastCheck = roundTripDAO.fetchLastCheck();
        }
        return Worker.lastCheck;
    }
    public long testH2() {
        return roundTripDAO.count();
    }

    public List<RoundTrip> getLastDeals() {
        //todo find a better way to get last roundTrips fast - current solution is to fetch all elements with max last check
        //todo possible solution: hold lastCheck as a field and update its value each time (another possible solution: caching)
        return roundTripDAO.findLastDeals(getLastCheck());
    }
//     ************************** previous implementation ***********************************************************************
//     //todo try to avoid high latency  by not using selenium
//    public void parseDocument() {
//
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            URL urlPath = new URL(url);
//            parseCsvFile(urlPath);
//
//            ClassLoader classLoader = getClass().getClassLoader();
//            //File file = new File(classLoader.getResource("hulyo.json").getFile());
//            File file = new File("hulyo.json");
//            InputStream is = new FileInputStream(file);
//
//
//
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//            String jsonText = readAll(rd);
//            //Stream<String> stream = Files.lines(Paths.get("hulyo.json"));
//            //String jsonText = stream.toString();
//            JsonNode rootNode = mapper.readTree(jsonText);
//            JsonNode DealCountryNamesNode = rootNode.path("ShowEmptyCatalog");
//            System.out.println(DealCountryNamesNode);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static String convertToUTF8(String s) {
//        String out;
//        try {
//            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
//        } catch (java.io.UnsupportedEncodingException e) {
//            return null;
//        }
//        return out;
//    }
//
//    public void parseCsvFile(URL urlPath) {
//        try {
//            InputStream in = urlPath.openStream();
//            Files.copy(in, Paths.get("hulyo.json"), StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private String readAll(Reader rd) throws IOException {
//        StringBuilder sb = new StringBuilder();
//        int cp;
//        while ((cp = rd.read()) != -1) {
//            sb.append((char) cp);
//        }
//        return sb.toString();
//    }


}