package com.example.controller.admin;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.dto.AccountDetails;
import com.example.entity.dto.AccountPrivacy;
import com.example.entity.vo.response.AccountVO;
import com.example.service.AccountDetailsService;
import com.example.service.AccountPrivacyService;
import com.example.service.AccountService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/admin/user")
public class AccountAdminController {
    @Resource
    AccountService accountService;
    @Resource
    AccountDetailsService accountDetailsService;
    @Resource
    AccountPrivacyService accountPrivacyService;
    @Resource
    StringRedisTemplate template;
    @Value("${spring.security.jwt.expire}")
    private int expire;
    @GetMapping("/list")
    public RestBean<JSONObject> list(@RequestParam int page,@RequestParam int size,
                                     @RequestParam(required = false) String keyword){
        JSONObject object=new JSONObject();
        Page<Account> accountPage=accountService.page(Page.of(page,size), Wrappers
                .<Account>query().eq(keyword!=null,"id",keyword).or()
                .like(keyword!=null,"username","%"+keyword+"%"));
        List<AccountVO> list=accountPage
                .getRecords()
                .stream()
                .map(a->{
                    AccountVO vo=new AccountVO();
                    BeanUtils.copyProperties(a,vo);
                    return vo;
                })
                .toList();
        object.put("total",accountPage.getTotal());
        object.put("list",list);
        return RestBean.success(object);
    }
    @GetMapping("/detail")
    public RestBean<JSONObject> detail(int id){
        JSONObject obj=new JSONObject();
        obj.put("detail",accountDetailsService.findAccountDetailsById(id));
        obj.put("privacy",accountPrivacyService.getPrivacy(id));
        return RestBean.success(obj);
    }
    @PostMapping("/save")
    public RestBean<Void> saveAccount(@RequestBody JSONObject object,@RequestAttribute(Const.ATTR_USER_ID) int uid ){
         int id=object.getInteger("id");
         if(uid == id){
             return RestBean.failure(400,"无法修改自己账号密码");
         }
         Account account=accountService.findAccountById(id);
         Account save=object.toJavaObject(Account.class);
         HandleBaned(account,save);
         BeanUtils.copyProperties(save,account,"password","registerTime");
         accountService.saveOrUpdate(account);
         AccountDetails details = accountDetailsService.findAccountDetailsById(id);
         AccountDetails saveDetails = object.getJSONObject("detail").toJavaObject(AccountDetails.class);
         BeanUtils.copyProperties(saveDetails, details);
         accountDetailsService.saveOrUpdate(details);
         AccountPrivacy privacy = accountPrivacyService.getPrivacy(id);
         AccountPrivacy savePrivacy = object.getJSONObject("privacy").toJavaObject(AccountPrivacy.class);
         BeanUtils.copyProperties(savePrivacy, privacy);
        accountPrivacyService.saveOrUpdate(savePrivacy);
         return RestBean.success();
    }
    @PostMapping("/modify-password")
    public RestBean<Void> changePassword(@RequestBody JSONObject object){
        accountService.modifyPassword(object.getInteger("id"),object.getString("newPassword"));
        return RestBean.success();
    }


    private void HandleBaned(Account old,Account current){
           String key= Const.BANNED_BLOCK+old.getId();
           if(!old.isBanned()&&current.isBanned()){
              template.opsForValue().set(key,"true",expire, TimeUnit.HOURS);
           }else if(old.isBanned()&&!current.isBanned()){
               template.delete(key);
           }
    }
}
