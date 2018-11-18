package com.flightStalker.main.Dao;

import com.flightStalker.main.Entity.RoundTrip;
import com.flightStalker.main.Repository.RoundTripRepository;
import com.flightStalker.main.Worker.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoundTripDAO {

    @Autowired
    private RoundTripRepository roundTripRepository;

    public List<RoundTrip> findAll() {
        List<RoundTrip> roundTrips = new ArrayList<>();
        roundTripRepository.findAll().forEach(roundTrip -> roundTrips.add(roundTrip));
        return roundTrips;
    }

    public long count(){
        return roundTripRepository.count();
    }

    public void saveAll(List<RoundTrip> roundTrips){
        roundTripRepository.saveAll(roundTrips);
    }

    public long fetchLastCheck() {
        Long lastCheck = roundTripRepository.findByMaxLastCheck();
        //todo: consider replacing entire validation with isEmptyDB()
        return (lastCheck == null) ? Worker.NOT_INITIALIZED : lastCheck;
    }

    public List<RoundTrip> findLastDeals(long lastCheck) {
        return roundTripRepository.findAllByLastCheck(lastCheck);
    }
}
