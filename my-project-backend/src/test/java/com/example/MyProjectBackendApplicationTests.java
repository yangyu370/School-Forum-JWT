package com.example;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.dto.Account;
import com.example.entity.dto.AccountDetails;
import com.example.entity.dto.AccountPrivacy;
import com.example.entity.dto.Topic;
import com.example.entity.vo.response.TopicDetailVO;
import com.example.entity.vo.response.WeatherVO;
import com.example.mapper.AccountDetailsMapper;
import com.example.mapper.AccountMapper;
import com.example.mapper.AccountPrivacyMapper;
import com.example.mapper.TopicMapper;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@SpringBootTest
class MyProjectBackendApplicationTests {
    @Resource
    RestTemplate rest;
    @Resource
    TopicMapper topicMapper;
    @Resource
    AccountMapper accountMapper;
    @Resource
    AccountPrivacyMapper accountPrivacyMapper;
    @Resource
    AccountDetailsMapper accountDetailsMapper;
    @Test
    void contextLoads() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("123456"));
    }
    @Test
    void test(){
        int uid=9;
        TopicDetailVO target=new TopicDetailVO();
        AccountDetails accountDetails=accountDetailsMapper.selectById(uid);
        Account account=accountMapper.selectById(uid);
        AccountPrivacy accountPrivacy=accountPrivacyMapper.selectById(uid);
        String[] ignores = accountPrivacy.hiddenFields();
        BeanUtils.copyProperties(account,target,ignores);
        BeanUtils.copyProperties(accountDetails,target,ignores);
        System.out.println(target);
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
    private <T> T fillUserDetailsByPrivacy(T  target,int uid){
        AccountDetails accountDetails=accountDetailsMapper.selectById(uid);
        Account account=accountMapper.selectById(uid);
        AccountPrivacy accountPrivacy=accountPrivacyMapper.selectById(uid);
        String[] ignores = accountPrivacy.hiddenFields();
        BeanUtils.copyProperties(account,target,ignores);
        BeanUtils.copyProperties(accountDetails,target,ignores);
        return target;
    }
}
