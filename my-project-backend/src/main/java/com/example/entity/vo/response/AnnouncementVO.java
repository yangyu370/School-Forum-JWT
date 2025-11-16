package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class AnnouncementVO {
    int id;
    String title;
    String content;
    Date time;
    User user;
    @Data
    public static class User{
        Integer id;
        String username;
        String avatar;
    }
}
