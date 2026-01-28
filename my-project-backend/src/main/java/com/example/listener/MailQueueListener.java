package com.example.listener;

import com.example.entity.dto.EmailRecord;
import com.example.mapper.EmailRecordMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;
@Slf4j
@Component
@RabbitListener(queues="mail")
public class MailQueueListener {
    @Resource
    JavaMailSender sender;
    @Value("${spring.mail.username}")
    String username;
    @Resource
    EmailRecordMapper recordMapper;
    @RabbitHandler
    public void sendMessage(EmailRecord record){
           try{
               sender.send(createMessage(record));
               record.setStatus(1);
               recordMapper.updateById(record);
               log.info("邮件发送成功.邮件记录ID: {}, 邮件接收人: {}",record.getId(),record.getEmail());
           }catch (Exception e){
               log.error("邮件发送失败，邮件记录ID: {}, 邮件接收人: {}",record.getId(),record.getEmail());
               throw e;
           }
    }
    private SimpleMailMessage createMessage(EmailRecord record){
          SimpleMailMessage message = new SimpleMailMessage();
          message.setSubject(record.getTitle());
          message.setText(record.getContent());
          message.setTo(record.getEmail());
          message.setFrom(username);
          return message;
    }
}
