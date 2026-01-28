package com.example.controller.admin;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.RestBean;
import com.example.entity.dto.EmailRecord;
import com.example.service.EmailService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/email")
public class EmailAdminController {
    @Resource
    EmailService emailService;

    @GetMapping("/list")
    public RestBean<JSONObject> list(int page, int size){
        JSONObject object = new JSONObject();
        List<EmailRecord> list = emailService.listEmailRecord(page, size);
        object.put("list", list);
        object.put("total", emailService.countEmailRecord());
        return RestBean.success(object);
    }

    @PostMapping("/resend")
    public RestBean<Void> resend(@RequestBody Map<String, Integer> request){
        try {
            Integer id = request.get("id");
            if(id == null) {
                return RestBean.failure(400, "邮件ID不能为空");
            }
            emailService.resendEmail(id);
            return RestBean.success();
        } catch (RuntimeException e) {
            return RestBean.failure(400, e.getMessage());
        }
    }
}
