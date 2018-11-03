package com.flightStalker.main.dao;

import com.flightStalker.main.Entity.RoundTrip;
import com.flightStalker.main.Repository.RoundTripRepository;
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
}
