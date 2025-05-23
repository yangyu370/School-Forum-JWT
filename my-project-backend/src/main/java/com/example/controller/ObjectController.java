package com.example.controller;


import com.example.entity.RestBean;
import com.example.service.ImageService;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController

public class ObjectController {

    @Resource
    ImageService imageService;

    @GetMapping("/images/**")
    public void imageFetch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Type", "image/jpg");
        this.fetchImage(request, response);
    }
    private void fetchImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String imagePath=request.getServletPath().substring(7);
        ServletOutputStream out = response.getOutputStream();
        if(imagePath.length()<=13){
            out.println(RestBean.failure(404,"图片消失啦").toString());
        } else{
            try{
                imageService.fetchImageFromMinio(out,imagePath);
                response.setHeader("Cache-Control","max-age=2592000");
                response.setHeader("Content-Type","image/jpg");
            }catch(ErrorResponseException e){
                if(e.response().code()==404){
                    response.setStatus(404);
                    out.println(RestBean.failure(404,"图片消失啦").toString());
                }else{
                    log.error("从minio获取图片出现异常"+e.response().code()+":"+e.getMessage());
                }
            }

        }
    }
}
