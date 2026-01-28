package com.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.dto.EmailRecord;

import java.util.List;

public interface EmailService {
    void sendVerifyEmail(String type,String email,int code);
    List<EmailRecord> listEmailRecord(int page, int size);
    long countEmailRecord();
    void resendEmail(int id);
}
