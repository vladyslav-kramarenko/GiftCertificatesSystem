package com.epam.esm.api.controller;

import com.epam.esm.core.service.impl.DemoDataGeneratorService;
import com.epam.esm.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/generate")
public class DemoDataController {

    private final DemoDataGeneratorService demoDataGenerator;

    @Autowired
    public DemoDataController(DemoDataGeneratorService demoDataGenerator) {
        this.demoDataGenerator = Objects.requireNonNull(demoDataGenerator, "DemoDataGenerator must be initialised");
    }

    @PostMapping("")
    public ResponseEntity<String> generateDemoData(
            @RequestParam(name = "usersCount", defaultValue = "0") int usersCount,
            @RequestParam(name = "tagsCount", defaultValue = "0") int tagsCount,
            @RequestParam(name = "giftCertificatesCount", defaultValue = "0") int giftCertificatesCount,
            @RequestParam(name = "ordersCount", defaultValue = "0") int ordersCount,
            @RequestParam(name = "emailsCount", defaultValue = "0") int emailsCount
    ) throws ServiceException {

        String result = demoDataGenerator.generateDemoData(
                usersCount, tagsCount, giftCertificatesCount, ordersCount, emailsCount
        );
        return new ResponseEntity<>("Demo data generated successfully: " + result, HttpStatus.CREATED);
    }
}
