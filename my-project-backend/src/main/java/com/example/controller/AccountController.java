package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.vo.response.AccountVO;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class AccountController {
    @Resource
    AccountService accountService;
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
}
