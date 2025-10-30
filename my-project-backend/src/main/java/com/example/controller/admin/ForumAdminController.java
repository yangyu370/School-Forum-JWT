package com.example.controller.admin;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.RestBean;
import com.example.entity.dto.Topic;
import com.example.entity.vo.response.TopicPreviewVO;
import com.example.service.TopicService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//todo 1.管理员删除普通用户的帖子,批量删除
//todo 2.管理员设置置顶帖子
//todo 3.查看论坛中的所有的帖子
//todo 4.筛选查看不同板块的帖子
@RestController
@RequestMapping("/api/admin/forum")
public class ForumAdminController {
    @Resource
    TopicService topicService;
    @GetMapping("/list")
    public RestBean<JSONObject> list(int page,int size){
        JSONObject obj=new JSONObject();
        QueryWrapper<Topic> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("time");
        Page<Topic> topicPage = topicService.page(Page.of(page, size), wrapper);
        List<TopicPreviewVO> list=topicPage.getRecords()
                .stream()
                .map(topic -> topicService.ResolveToPreview(topic))
                .toList();
        obj.put("list",list);
        obj.put("total",topicService.count());
        return RestBean.success(obj);
    }
}
