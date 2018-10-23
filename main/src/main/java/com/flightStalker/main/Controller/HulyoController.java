package com.flightStalker.main.Controller;

import com.flightStalker.main.Task.Worker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HulyoController {

    @GetMapping("/")
    public String scrape(){
        Worker worker = new Worker();
        return worker.parseWithSelenium();
    }

}
