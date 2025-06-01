package com.example.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
@Data
public class TopicDetailVO {
    int id;
    String title;
    String content;
    Integer type;
    Date time;
    User user;
    Interact interact;
    @Data
   public static class User{
       Integer id;
       String username;
       String avatar;
       String desc;
       Integer gender;
       String phone;
       String email;
       String wx;
       String qq;
   }
   @Data
   @AllArgsConstructor
    public static class Interact{
        Boolean like;
        Boolean collect;
   }
}
