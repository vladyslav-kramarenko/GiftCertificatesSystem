package com.epam.esm.core.service.impl;

import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.service.ImgService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImgServiceImpl implements ImgService {
    private static final Logger logger = LoggerFactory.getLogger(ImgServiceImpl.class);
    private Path fileStorageLocation;

    @Value("${file.upload-dir}")
    private String fileUploadDir;

    @PostConstruct
    public void initFileUploadDir() throws ServiceException {
        this.fileStorageLocation = Paths.get(fileUploadDir).toAbsolutePath().normalize();
        logger.info("fileStorageLocation = "+fileStorageLocation);
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception ex) {
            throw new ServiceException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }


    @Override
    public Resource loadImageAsResource(String imagePath) throws ServiceException {
        try {
            logger.debug("imagePath = "+imagePath);
            Path filePath = this.fileStorageLocation.resolve(imagePath).normalize();
            logger.debug("filePath = "+filePath);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                logger.error("File not found "+imagePath);
                throw new ServiceException("File not found " + imagePath);
            }
        } catch (Exception ex) {
            logger.error("Could not load file "+imagePath);
            throw new ServiceException("Could not load file" + imagePath);
        }
    }

    @Override
    public String saveImage(MultipartFile image) throws ServiceException {
        try {
            String originalFilename = Objects.requireNonNull(image.getOriginalFilename());
            logger.debug("originalFilename = "+originalFilename);

            String newFilename = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(originalFilename);
            logger.debug("newFilename = "+newFilename);

            Path targetLocation = this.fileStorageLocation.resolve(newFilename);
            logger.debug("targetLocation = "+targetLocation);

            Files.copy(image.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return newFilename;
        } catch (Exception ex) {
            throw new ServiceException("Could not store file " + image.getOriginalFilename(), ex);
        }
    }

    @Override
    public void deleteImage(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
               logger.error("File name is empty");
            } else {
                logger.debug("imagePath = "+imagePath);
                Path targetPath = this.fileStorageLocation.resolve(imagePath).normalize();
                logger.debug("targetPath = "+targetPath);
                Files.delete(targetPath);
            }
        } catch (NoSuchFileException x) {
            logger.error("%s: no such" + " file or directory%n"+imagePath);
        } catch (DirectoryNotEmptyException x) {
            logger.error("%s not empty%n"+imagePath);
        } catch (IOException x) {
            logger.error(String.valueOf(x));
        }
    }
}
