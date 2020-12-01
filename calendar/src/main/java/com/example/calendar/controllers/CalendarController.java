package com.example.calendar.controllers;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class CalendarController {

    public String generateFile() throws Exception{
        Connection conn = Jsoup.connect("http://weeia.p.lodz.pl/");
        Document doc = conn.get();
        Elements els = doc.select("td.active");

        Map<String, String> events = new HashMap<>();

        for (Element el : els){
            events.put(el.select("a.active").text(),el.select("div.InnerBox").text());
        }
        Calendar cal = new Calendar();
        cal.getProperties().add(new ProdId("ID"));
        cal.getProperties().add(Version.VERSION_2_0);
        cal.getProperties().add(CalScale.GREGORIAN);

        java.util.Calendar utilCal = java.util.Calendar.getInstance();

        Date date = new Date();

        for (String key : events.keySet()){
            int i =1;
            utilCal.set(java.util.Calendar.MONTH,date.getMonth());
            utilCal.set(java.util.Calendar.DAY_OF_MONTH,((Integer)i).parseInt(key));
            VEvent event = new VEvent(new Date(utilCal.getTime()), events.get(key));
            UUID uuid = UUID.randomUUID();
            String s = uuid.toString();
            Uid uid = new Uid(s);
            event.getProperties().add(uid);
            cal.getComponents().add(event);
        }

        FileOutputStream fileOutputStream = new FileOutputStream("calendar.ics");
        CalendarOutputter calendarOutputter = new CalendarOutputter();
        calendarOutputter.output(cal,fileOutputStream);

        return cal.toString();
    }

    @GetMapping("/generate")
    public String getTestResponse(@RequestParam String testString) throws  Exception{
        generateFile();
        return testString;
    }

    @GetMapping("/calendar")
    public ResponseEntity<Resource>getResponse() throws Exception {
        generateFile();
        Resource resource = loadResources("calendar.ics");
        return new ResponseEntity<Resource>(resource, HttpStatus.OK);
    }

    private Resource loadResources(String filename) throws Exception{
        try{
            Path file = Paths.get(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists()||resource.isReadable()){
                return resource;
            }else{
                throw new Exception();
            }
        }catch (Exception e){
            throw new FileNotFoundException();
        }
    }

}
