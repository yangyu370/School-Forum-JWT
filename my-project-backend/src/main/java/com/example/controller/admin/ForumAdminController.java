package com.example.controller.admin;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.RestBean;
import com.example.entity.dto.Topic;
import com.example.entity.vo.response.TopicPreviewVO;
import com.example.service.TopicService;
import com.example.utils.CacheUtils;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

//todo 4.筛选查看不同板块的帖子
@RestController
@RequestMapping("/api/admin/forum")
public class ForumAdminController {
    @Resource
    TopicService topicService;
    @Resource
    CacheUtils cacheUtils;
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
    @PostMapping("/set-top")
    public RestBean<Void> topTopic(int tid,boolean status){
        Topic topic=topicService.getById(tid);
        if(topic==null){
            return RestBean.failure(404,"该帖子不存在或已删除");
        }
        topicService.TopTopic(tid,status);
        cacheUtils.deleteFromCachePattern(Const.FORUM_TOPIC_PREVIEW_CACHE + "*");
        return RestBean.success();
    }
    @GetMapping("/Admin-deleteTopic")
    public RestBean<Void> AdminDeleteTopic(int tid){
        try {
            topicService.AdminDeleteTopic(tid);
            return RestBean.success();
        } catch (RuntimeException e) {
            return RestBean.failure(400, e.getMessage());
        }
    }
}
