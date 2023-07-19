package com.epam.esm.api.controller;

import com.epam.esm.api.ErrorResponse;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.service.ImgService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

import static com.epam.esm.api.util.Constants.ERROR_NO_CONTENT;

@RestController
@RequestMapping("/image")
@Validated
public class ImageController {
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    private final ImgService imgService;

    public ImageController(ImgService imgService) {
        this.imgService = Objects.requireNonNull(imgService, "ImgService must be initialised");
    }

    @PostMapping("/")
    public ResponseEntity<?> saveImage(@RequestParam("file") MultipartFile file) throws ServiceException {
        String path = imgService.saveImage(file);
        return ResponseEntity.ok(path);
    }

    @GetMapping("/{path}")
    public ResponseEntity<?> getImage(@PathVariable("path") @NotNull String path, HttpServletRequest request){
        logger.debug("request: " + request);
        try {
            Resource file = imgService.loadImageAsResource(path);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ErrorResponse("Requested resource not found", ERROR_NO_CONTENT));
        }
    }
}
