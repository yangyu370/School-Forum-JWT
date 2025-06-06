package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.response.NotificationVO;
import com.example.service.NotificationService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    @Resource
    NotificationService notificationService;
    @GetMapping("/list")
    public RestBean<List<NotificationVO>> listNotification(@RequestAttribute("id") int id){
        return RestBean.success(notificationService.findUserNotificaiton(id));
    }
    @GetMapping("/delete")
    public RestBean<Void> deleteNotification(@RequestParam @Min(0) int id,@RequestAttribute ("id") int uid){
        notificationService.deleteUserNotification(id,uid);
        return RestBean.success();
    }
    @GetMapping("/delete-all")
    public RestBean<Void> deleteAllNotification(@RequestAttribute("id") int uid){
        notificationService.deleteUserAllNotification(uid);
        return RestBean.success();
    }
}
