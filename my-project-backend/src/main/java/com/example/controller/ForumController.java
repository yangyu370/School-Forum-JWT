package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.TopicType;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.response.TopicTypeVO;
import com.example.entity.vo.response.WeatherVO;
import com.example.service.TopicService;
import com.example.service.WeatherService;
import com.example.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/forum")
public class ForumController {
    @Resource
    WeatherService weatherService;
    @Resource
    TopicService topicService;
    @Resource
    ControllerUtils utils;
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
    public RestBean<Void> createTopic(@RequestBody @Valid TopicCreateVO vo,@RequestAttribute("id") int id) {
        return utils.messageHandler(()-> topicService.createTopic(id,vo));
    }
}