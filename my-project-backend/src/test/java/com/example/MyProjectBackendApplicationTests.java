package com.example;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.vo.response.WeatherVO;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@SpringBootTest
class MyProjectBackendApplicationTests {
    @Resource
    RestTemplate rest;
    @Test
    void contextLoads() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("123456"));
        long date = System.currentTimeMillis();
        System.out.println(date);
        Date date1 = new Date();
        System.out.println(date1);
    }
    private JSONObject decompressStingToJson(byte[] data){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data));
            byte[] buffer = new byte[1024];
            int read;
            while ((read = gzip.read(buffer)) != -1)
                stream.write(buffer, 0, read);
            gzip.close();
            stream.close();
            return JSONObject.parseObject(stream.toString());
        } catch (IOException e) {
            return null;
        }

    }
}
