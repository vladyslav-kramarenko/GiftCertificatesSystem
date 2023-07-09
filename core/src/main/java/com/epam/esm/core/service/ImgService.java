package com.epam.esm.core.service;

import com.epam.esm.core.exception.ServiceException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;


public interface ImgService {

    Resource loadImageAsResource(String imagePath) throws ServiceException;

    String saveImage(MultipartFile image) throws ServiceException;
    void deleteImage(String imagePath) throws ServiceException;
}
