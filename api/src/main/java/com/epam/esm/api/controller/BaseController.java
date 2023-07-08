package com.epam.esm.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class BaseController {

    @GetMapping("/")
    public ResponseEntity<String> helloWorld(
    ) {
        return ResponseEntity.ok("Hello World!");
    }

    @GetMapping("/validate-token")
    public ResponseEntity<String> validateToken(
    ) {
        return ResponseEntity.ok("Token is valid!");
    }
}
