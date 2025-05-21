package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.entity.dto.Account;
import com.example.mapper.AccountMapper;
import com.example.service.ImageService;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Resource
    MinioClient client;
    @Resource
    AccountMapper accountMapper;
    @Override
    public String upload(MultipartFile file, int id) throws IOException {
        String imageName = UUID.randomUUID().toString().replace("-", "")+".jpeg";
        imageName="/avatar/"+imageName;
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket("forum")
                .stream(file.getInputStream(),file.getSize(),-1)
                .object(imageName)
                .build();
        try{
           client.putObject(putObjectArgs);
          if(accountMapper.update(null, Wrappers.<Account>update().eq("id",id).set("avatar",imageName))>0){
              return imageName;
          }else{
              return null;
          }
        } catch (Exception e) {
            log.error("图片上传问题："+e.getMessage());
            return null;
        }
    }

    @Override
    public void fetchImageFromMinio(OutputStream stream, String image) throws Exception {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket("forum")
                .object(image)
                .build();
        GetObjectResponse response=client.getObject(getObjectArgs);
        IOUtils.copy(response,stream);
    }
}
