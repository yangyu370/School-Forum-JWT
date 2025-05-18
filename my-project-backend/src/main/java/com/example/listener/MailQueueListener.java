package com.example.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues="mail")
public class MailQueueListener {
    @Resource
    JavaMailSender sender;
    @Value("${spring.mail.username}")
    String username;
    @RabbitHandler
    public void sendMessage(Map<String,Object> data){
        String mail=data.get("mail").toString();
        Integer code=(Integer)data.get("code");
        String type=(String)data.get("type");
        SimpleMailMessage message=switch(type){
            case "register" -> createMessage("欢迎注册网站","您的邮件注册验证码为"+code+"有效时间为3分钟，为了保障你的安全请勿向他人泄漏您的验证码",mail);
            case "reset" ->createMessage("重置密码","您好你正在进行重置密码操作"+code+"有效时间3分钟，如非本人请无视",mail);
            case "modify" ->createMessage("修改验证邮件","您好您正在进行修改新的绑定的电子邮件操作，验证码为"+code+"有效时间3分钟，如非本人操作，请无视",mail);
            default -> null;
        };
        if(message==null) return;
        sender.send(message);
    }
    private SimpleMailMessage createMessage(String title,String content,String email){
          SimpleMailMessage message = new SimpleMailMessage();
          message.setSubject(title);
          message.setText(content);
          message.setTo(email);
          message.setFrom(username);
          return message;
    }
}
