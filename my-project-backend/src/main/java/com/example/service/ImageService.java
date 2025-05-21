package com.example.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;

public interface ImageService {
    String upload(MultipartFile file,int id) throws IOException;
     void fetchImageFromMinio(OutputStream stream,String image) throws Exception;
}
