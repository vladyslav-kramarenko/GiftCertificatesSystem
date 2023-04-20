package com.epam.esm.api.controller;

import com.epam.esm.api.util.DemoDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/generate")
public class DemoDataController {
    @Autowired
    private DemoDataGenerator demoDataGenerator;

    @PostMapping("")
    public ResponseEntity<String> generateDemoData(
            @RequestParam(name = "usersCount", defaultValue = "0") int usersCount,
            @RequestParam(name = "tagsCount", defaultValue = "0") int tagsCount,
            @RequestParam(name = "giftCertificatesCount", defaultValue = "0") int giftCertificatesCount) {

        String result = demoDataGenerator.generateDemoData(usersCount, tagsCount, giftCertificatesCount);
        return new ResponseEntity<>("Demo data generated successfully: " + result, HttpStatus.CREATED);
    }
}
