package com.example.calendar.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalendarController {

    @GetMapping("/calendar")
    public String getResponse(@RequestParam String testString){
        return testString;
    }
}
