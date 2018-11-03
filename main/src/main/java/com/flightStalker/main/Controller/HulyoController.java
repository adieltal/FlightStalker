package com.flightStalker.main.Controller;

import com.flightStalker.main.Entity.RoundTrip;
import com.flightStalker.main.Task.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HulyoController {


    @Autowired
    private Worker worker;

    @GetMapping("/")
    public String scrape(){
        //todo should be replaced with scheduled task
        return worker.parseWithSelenium();
    }

    @GetMapping("/getLastDeals")
    public List<RoundTrip> getLastDeals(){
            return worker.getLastDeals();
    }

    @GetMapping("/test")
    public long testH2(){
        return worker.testH2();
    }

}
