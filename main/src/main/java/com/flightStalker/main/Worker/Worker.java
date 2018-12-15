package com.flightStalker.main.Worker;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightStalker.main.Dao.RoundTripDAO;
import com.flightStalker.main.Entity.RoundTrip;
import com.flightStalker.main.utils.ParseUtils;
import org.apache.catalina.connector.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@Service
public class Worker {
    public static final long NOT_INITIALIZED = 0;
    private String URL_SOURCE = "https://s3.eu-central-1.amazonaws.com/catalogs.hulyo.co.il/catalogs/Production/Flights/v1.4/above199FlightsWebOnly.js";

    @Autowired
    private RoundTripDAO roundTripDAO;

    private final Logger log = LoggerFactory.getLogger(Worker.class);
    private static long lastCheck;

    public Worker() {
        this.lastCheck = NOT_INITIALIZED;
    }

    // TODO: enable scheduled on production mode
    // @Scheduled(cron = "0 0/15 * * * *")
    public void parseDocument() {
        ArrayList<RoundTrip> roundTrips = null;
        log.info("Start scheduled task parse");
        try {
            String response = httpSend();
            Worker.lastCheck = System.currentTimeMillis();
            roundTrips= ParseUtils.buildRoundTrips(response, Worker.lastCheck);
            roundTripDAO.saveAll(roundTrips);
        } catch (Exception ex) {
            log.error(ex.toString());
        }
    }
    // String response = httpSend("finnovest-static/res/recommendedSecurities/recommended_securities.js");


    private String httpSend() throws IOException {
        String res;
        URL url = new URL(this.URL_SOURCE);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Accept-Encoding", "gzip");
        con.setRequestMethod("GET");

        InputStream is;
        if (con.getResponseCode() == Response.SC_OK) {
            is = new GZIPInputStream(con.getInputStream());
        } else {
            is = con.getErrorStream();
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
            res = in.lines().collect(Collectors.joining());
        }
        log.debug("response = " + res);
        return res;
    }


    public long testH2() {
        return roundTripDAO.count();
    }

    public List<RoundTrip> getLastDeals() {
        //todo find a better way to get last roundTrips fast - current solution is to fetch all elements with max last check
        //todo possible solution: hold lastCheck as a field and update its value each time (another possible solution: caching)
        List<RoundTrip> lastDeals = roundTripDAO.findLastDeals(getLastCheck());
        writeToFile(lastDeals);
        return lastDeals;
    }

    private void writeToFile(List<RoundTrip> lastDeals) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(new File("demo.json"), lastDeals);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<RoundTrip> getDemo() {
        List<RoundTrip> myObjects = null;
        File file = new File("demo.json");
        try {
            InputStream inJson = new FileInputStream(file);
            ObjectMapper mapper = new ObjectMapper();
            myObjects = mapper.readValue(inJson, new TypeReference<List<RoundTrip>>() {
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return myObjects;
    }

    public long getLastCheck() {
        if (Worker.lastCheck == Worker.NOT_INITIALIZED) {
            Worker.lastCheck = roundTripDAO.fetchLastCheck();
        }
        return Worker.lastCheck;
    }

}