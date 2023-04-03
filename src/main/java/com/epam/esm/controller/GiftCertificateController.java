package com.epam.esm.controller;

import com.epam.esm.exception.ServiceException;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.service.impl.GiftCertificateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/certificates")
public class GiftCertificateController {
    private static final Logger logger = LoggerFactory.getLogger(GiftCertificateServiceImpl.class);
    private final GiftCertificateServiceImpl giftCertificateService;

    @Autowired
    public GiftCertificateController(GiftCertificateServiceImpl giftCertificateService) {
        this.giftCertificateService = giftCertificateService;
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<?> getGiftCertificateById(@PathVariable Long id) {
        try {
            Optional<GiftCertificate> certificate = giftCertificateService.getGiftCertificateById(id);
            if (certificate.isEmpty()) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(certificate.get());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ServiceException se) {
            return ResponseEntity.internalServerError().body(se.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createGiftCertificate(@RequestBody GiftCertificate certificate) {
        try {
            GiftCertificate createdCertificate = giftCertificateService.createGiftCertificate(certificate);
            return ResponseEntity.ok(createdCertificate);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ServiceException se) {
            return ResponseEntity.internalServerError().body(se.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGiftCertificate(@PathVariable Long id,
                                                   @RequestBody GiftCertificate certificate) {
        try {
            Optional<GiftCertificate> updatedCertificate = giftCertificateService.updateGiftCertificate(id, certificate);
            return updatedCertificate
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ServiceException se) {
            return ResponseEntity.internalServerError().body(se.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGiftCertificate(@PathVariable Long id) {
        try {
            boolean isDeleted = giftCertificateService.deleteGiftCertificate(id);
            return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ServiceException se) {
            return ResponseEntity.internalServerError().body(se.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getGiftCertificates() {
        try {
            List<GiftCertificate> certificates = giftCertificateService.getGiftCertificates();
            return ResponseEntity.ok(certificates);
        } catch (ServiceException se) {
            return ResponseEntity.internalServerError().body(se.getMessage());
        }
    }
}
