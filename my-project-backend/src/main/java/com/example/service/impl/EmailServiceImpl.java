package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.dto.EmailRecord;
import com.example.mapper.EmailRecordMapper;
import com.example.service.EmailService;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {
    @Resource
    AmqpTemplate amqpTemplate;
    @Resource
    EmailRecordMapper recordMapper;
    @Override
    public void sendVerifyEmail(String type, String email, int code) {
        EmailRecord emailRecord=switch(type){
            case "register" -> new EmailRecord(email, "欢迎注册网站",
                    "您的邮件注册验证码为"+code+"有效时间为3分钟，为了保障你的安全请勿向他人泄漏您的验证码");
            case "reset" -> new EmailRecord(email,"重置密码",
                    "您好你正在进行重置密码操作"+code+"有效时间3分钟，如非本人请无视");
            case "modify" ->new EmailRecord(email,"修改验证邮件",
                    "您好您正在进行修改新的绑定的电子邮件操作，验证码为"+code+"有效时间3分钟，如非本人操作，请无视");
            default ->throw new IllegalArgumentException(type);
        };
         recordMapper.insert(emailRecord);
         amqpTemplate.convertAndSend("mail",emailRecord);
    }

    @Override
    public List<EmailRecord> listEmailRecord(int page, int size) {
        QueryWrapper<EmailRecord> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("time");
        Page<EmailRecord> emailPage = recordMapper.selectPage(Page.of(page, size), wrapper);
        return emailPage.getRecords();
    }

    @Override
    public long countEmailRecord() {
        return recordMapper.selectCount(Wrappers.emptyWrapper());
    }

    @Override
    public void resendEmail(int id) {
        EmailRecord record = recordMapper.selectById(id);
        if(record == null) {
            throw new RuntimeException("邮件记录不存在");
        }
        // 重置状态为发送中
        record.setStatus(0);
        recordMapper.updateById(record);
        // 重新发送到队列
        amqpTemplate.convertAndSend("mail", record);
    }
}
