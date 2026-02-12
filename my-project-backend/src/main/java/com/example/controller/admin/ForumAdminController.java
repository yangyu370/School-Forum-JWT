package com.example.controller.admin;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.entity.PageRestBean;
import com.example.entity.RestBean;
import com.example.entity.dto.Topic;
import com.example.entity.vo.request.AnnouncementCreateVO;
import com.example.entity.vo.response.TopicPreviewVO;
import com.example.service.AnnouncementService;
import com.example.service.TopicService;
import com.example.utils.CacheUtils;
import com.example.utils.Const;
import com.example.utils.ControllerUtils;
import com.example.utils.ProhibitedUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//todo 4.筛选查看不同板块的帖子
@RestController
@RequestMapping("/api/admin/forum")
public class ForumAdminController {
    @Resource
    ControllerUtils utils;
    @Resource
    TopicService topicService;
    @Resource
    AnnouncementService announcementService;
    @Resource
    CacheUtils cacheUtils;
    @Resource
    ProhibitedUtils prohibitedUtils;
    @GetMapping("/list")
    public PageRestBean<TopicPreviewVO> list(@RequestParam int page,@RequestParam int size){
       JSONObject result=topicService.listAllTopicByPage(page,size);
       return PageRestBean.success(result.getJSONArray("list").toList(TopicPreviewVO.class),
               result.getIntValue("total"),
               page);
    }
    @PostMapping("/set-top")
    public RestBean<Void> topTopic(@RequestBody JSONObject json){
        Integer tid = json.getInteger("tid");
        Boolean status = json.getBoolean("status");
        Topic topic = topicService.getById(tid);
        if(topic == null){
            return RestBean.failure(404, "该帖子不存在或已删除");
        }
        topicService.TopTopic(tid, status);
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
    @GetMapping("/Admin-deleteComment")
    public RestBean<Void> AdminDeleteComment(@RequestParam int id){
        String str=topicService.AdminDeleteComment(id);
        if(str != null){
            if(str.contains("不存在")){
                return RestBean.failure(404, str);
            } else if(str.contains("管理员") || str.contains("无权")){
                return RestBean.failure(403, str);
            } else {
                return RestBean.failure(400, str);
            }
        }
        return RestBean.success();
    }
   @PostMapping("/create-announcement")
    public RestBean<Void> createAnnouncement(@RequestBody AnnouncementCreateVO vo, @RequestAttribute("id") int uid){
       return utils.messageHandler(()->announcementService.CreateAnnouncement(vo,uid));
   }
    @PostMapping("/locked")
    public RestBean<Void> setLocked(@RequestBody JSONObject object){
        topicService.setTopicLocked(
                object.getIntValue("id"),
                object.getBooleanValue("status")
        );
        cacheUtils.deleteFromCachePattern(Const.FORUM_TOPIC_PREVIEW_CACHE + "*");
        return RestBean.success();
    }
    @PostMapping("/invisible")
    public RestBean<Void> setInvisible(@RequestBody JSONObject object){
        topicService.setTopicInvisible(
                object.getIntValue("id"),
                object.getBooleanValue("status")
        );
        cacheUtils.deleteFromCachePattern(Const.FORUM_TOPIC_PREVIEW_CACHE + "*");
        return RestBean.success();
    }
    @GetMapping("/prohibited-list")
    public RestBean<List<String>> getProhibitedList(){
        return RestBean.success(prohibitedUtils.getProhibitedWords());
    }
    @PostMapping("/prohibited-save")
    public RestBean<Void> saveProhibitedList(@RequestBody JSONArray array){
        prohibitedUtils.saveProhibitedWords(array.toList(String.class));
        return RestBean.success();
    }
 }
