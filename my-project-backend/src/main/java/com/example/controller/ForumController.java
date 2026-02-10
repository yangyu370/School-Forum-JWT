package com.example.controller;
import com.alibaba.fastjson2.JSONObject;
import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.dto.Interact;
import com.example.entity.dto.TopicType;
import com.example.entity.vo.request.AddCommentVO;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.request.TopicUpdateVO;
import com.example.entity.vo.response.*;
import com.example.service.AccountService;
import com.example.service.AnnouncementService;
import com.example.service.TopicService;
import com.example.service.WeatherService;
import com.example.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {
    @Resource
    WeatherService weatherService;
    @Resource
    TopicService topicService;
    @Resource
    AccountService accountService;
    @Resource
    ControllerUtils utils;
    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/weather")
    public RestBean<WeatherVO> weather(double longitude, double latitude) {
        WeatherVO vo = weatherService.fetchWeather(longitude, latitude);
        return vo == null ? RestBean.failure(400, "获取地理天气信息失败,请联系管理员") : RestBean.success(vo);
    }

    @GetMapping("/types")
    public RestBean<List<TopicTypeVO>> listType() {
        List<TopicType> list = topicService.listTypes();
        List<TopicTypeVO> vos = list.stream().map(type -> {
            TopicTypeVO vo = new TopicTypeVO();
            BeanUtils.copyProperties(type, vo);
            return vo;
        }).toList();
        return RestBean.success(vos);
    }

    @PostMapping("/create-topic")
    public RestBean<Void> createTopic(@RequestBody @Valid TopicCreateVO vo, @RequestAttribute("id") int id) {
        Account account = accountService.findAccountById(id);
        if(account.isMute()) return RestBean.forbidden("你已被禁言，无法发表主题");
        return utils.messageHandler(() -> topicService.createTopic(id, vo));
    }

    @GetMapping("/list-topic")
    public RestBean<List<TopicPreviewVO>> listTopic(@RequestParam @Min(0) @Max(100) int page,
                                                    @RequestParam @Min(0) int type) {
        return RestBean.success(topicService.listTopicByPage(page + 1, type));
    }

    @GetMapping("/top-topic")
    public RestBean<List<TopTopicVO>> topTopic() {
        return RestBean.success(topicService.listTopTopics());
    }

    @GetMapping("/topic")
    public RestBean<TopicDetailVO> Topic(@RequestParam @Min(0) int tid, @RequestAttribute("id") int id) {
        TopicDetailVO detail = topicService.getTopicDetail(tid, id);
        if (detail == null) {
            return RestBean.failure(404, "帖子不存在或已被删除");
        }
        return RestBean.success(detail);
    }

    @GetMapping("/interact")
    public RestBean<Void> interact(@RequestParam @Min(0) int tid
            , @RequestParam @Pattern(regexp = "(like|collect)") String type,
                                   @RequestParam boolean state, @RequestAttribute("id") int id) {
        topicService.interact(new Interact(tid, id, new Date(), type), state);
        return RestBean.success();
    }

    @GetMapping("/collects")
    public RestBean<List<TopicPreviewVO>> collects(@RequestAttribute("id") int id) {
        return RestBean.success(topicService.listTopicCollects(id));
    }

    @PostMapping("/update-topic")
    public RestBean<Void> updateTopic(@RequestBody @Valid TopicUpdateVO vo, @RequestAttribute("id") int id) {
        return utils.messageHandler(() -> topicService.updateTopic(id, vo));
    }

    @PostMapping("/add-comment")
    public RestBean<Void> addComment(@Valid @RequestBody AddCommentVO vo, @RequestAttribute("id") int id) {
        Account account = accountService.findAccountById(id);
        if(account.isMute()) return RestBean.forbidden("你已被禁言，无法发表评论");
        return utils.messageHandler(() -> topicService.createComment(id, vo));
    }

    @GetMapping("/comments")
    public RestBean<List<CommentVO>> comments(@RequestParam @Min(0) int tid,
                                              @RequestParam @Min(0) int page) {
        return RestBean.success(topicService.comments(tid, page + 1));
    }

    @GetMapping("/delete-comment")
    public RestBean<Void>  DeleteComment(@RequestParam  @Min(0) int id,
                                         @RequestAttribute("id") int uid){
        topicService.deleteComment(id,uid);
        return RestBean.success();
    }
    @GetMapping("/delete-topic")
    public RestBean<Void> DeleteTopic(@RequestParam @Min(0) int id,
                                      @RequestAttribute("id") int uid){
        topicService.deleteTopic(id,uid);
        return RestBean.success();
    }
    @GetMapping("/list-announcement")
    public RestBean<List<AnnouncementVO>> ListAnnouncement(){
        return RestBean.success(announcementService.listAnnouncement());
    }

}