package com.example.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AIService {
    SseEmitter chatWithAI(JSONArray content);
}
