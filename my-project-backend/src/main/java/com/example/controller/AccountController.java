package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.dto.AccountDetails;
import com.example.entity.dto.AccountPrivacy;
import com.example.entity.vo.request.ChangePasswordVO;
import com.example.entity.vo.request.DetailsSaveVO;
import com.example.entity.vo.request.ModifyEmailVO;
import com.example.entity.vo.request.PrivacySaveVO;
import com.example.entity.vo.response.AccountDetailsVO;
import com.example.entity.vo.response.AccountPrivacyVO;
import com.example.entity.vo.response.AccountVO;
import com.example.service.AccountDetailsService;
import com.example.service.AccountPrivacyService;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class AccountController {
    @Resource
    AccountService accountService;
    @Resource
    AccountDetailsService accountDetailsService;
    @Resource
    AccountPrivacyService accountPrivacyService;
    @GetMapping("/info")
   public RestBean<AccountVO> info(@RequestAttribute("id") int id){
         Account account=accountService.findAccountById(id);
         if(account==null){
          return  RestBean.failure(500,"内部错误请联系管理员");
         }
         AccountVO vo=new AccountVO();
         BeanUtils.copyProperties(account,vo);
         return RestBean.success(vo);
    }
    @GetMapping("/details")
    public RestBean<AccountDetailsVO> details(@RequestAttribute("id") int id){
        AccountDetails details= Optional
                .ofNullable(accountDetailsService.findAccountDetailsById(id))
                .orElseGet(AccountDetails::new);
        AccountDetailsVO vo=new AccountDetailsVO();
        BeanUtils.copyProperties(details,vo);
        return RestBean.success(vo);
    }
    @PostMapping("/save-details")
    public RestBean<Void> savedetails(@RequestAttribute("id") int id,@RequestBody @Valid DetailsSaveVO vo){
      boolean success=accountDetailsService.saveAccountDetails(id,vo);
      return success ? RestBean.success() : RestBean.failure(400,"用户名已被注册，请重新更换");
    }
    @PostMapping("/modify-email")
    public RestBean<Void> modifyEmail(@RequestAttribute("id") int id
            ,@RequestBody @Valid ModifyEmailVO vo){
        String result= accountService.modifyEmail(id,vo);
        return result==null ? RestBean.success():RestBean.failure(400,result);
    }
    @PostMapping("/change-password")
    public RestBean<Void>  changePassword(@RequestAttribute("id") int id,
                                          @RequestBody @Valid ChangePasswordVO vo){
        String result= accountService.changePassword(id,vo);
        return result==null ? RestBean.success():RestBean.failure(400,result);
    }
    @PostMapping("/save-privacy")
    public RestBean<Void> savePrivacy(@RequestAttribute("id") int id, @RequestBody @Valid PrivacySaveVO vo){
             accountPrivacyService.savePrivacy(id,vo);
             return RestBean.success();
    }
    @GetMapping("/privacy")
    public RestBean<AccountPrivacyVO>  privacy(@RequestAttribute("id") int id){
         AccountPrivacy accountprivacy=accountPrivacyService.getPrivacy(id);
         AccountPrivacyVO vo=new AccountPrivacyVO();
         BeanUtils.copyProperties(accountprivacy,vo);
         return RestBean.success(vo);
    }
}
