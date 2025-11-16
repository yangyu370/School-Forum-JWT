package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_announcement")
public class Announcement {
    @TableId(type = IdType.AUTO)
    Integer id;
    String title;
    String content;
    int uid;
    Date time;
}

