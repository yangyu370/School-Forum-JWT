package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.dto.StoreImage;
import com.example.mapper.AccountMapper;
import com.example.mapper.ImageStoreMapper;
import com.example.service.ImageService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import io.minio.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class ImageServiceImpl extends ServiceImpl<ImageStoreMapper, StoreImage> implements ImageService {

    @Resource
    MinioClient client;
    @Resource
    AccountMapper accountMapper;
    @Resource
    FlowUtils flowUtils;

   private SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");

    @Override
    public String uploadAvatar(MultipartFile file, int id) throws IOException {
        String imageName = UUID.randomUUID().toString().replace("-", "")+".jpeg";
        imageName="/avatar/"+imageName;
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket("forum")
                .stream(file.getInputStream(),file.getSize(),-1)
                .object(imageName)
                .build();
        try{
           client.putObject(putObjectArgs);
           String avatar=accountMapper.selectById(id).getAvatar();
           this.DeleteOldAvatar(avatar);
          if(accountMapper.update(null, Wrappers.<Account>update().eq("id",id).set("avatar",imageName))>0){
              return imageName;
          }else{
              return null;
          }
        } catch (Exception e) {
            log.error("头像上传问题："+e.getMessage());
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

    @Override
    public String uploadImage(MultipartFile file, int id) throws Exception {
        String key= Const.FORUM_IMAGE_CACHE+id;
       if(!flowUtils.limitPeriodCounterCheck(key,20,3600)){
           return null;
       }
       String imageName = UUID.randomUUID().toString().replace("-", "")+".jpg";
       Date date=new Date();
       imageName="/cache/"+format.format(date)+"/"+imageName;
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket("forum")
                .stream(file.getInputStream(),file.getSize(),-1)
                .object(imageName)
                .build();
        try{
            client.putObject(putObjectArgs);
            if(this.save(new StoreImage(id,imageName,date))){
                return imageName;
            }else{
                return null;
            }
        } catch (Exception e) {
            log.error("图片上传问题："+e.getMessage());
            return null;
        }
    }
    private void DeleteOldAvatar(String avatar) throws Exception {
        if(avatar==null|| avatar.isEmpty()){
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket("forum")
                    .object(avatar)
                    .build();
            client.removeObject(removeObjectArgs);
        }
    }
}
