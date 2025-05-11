package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account getAccountByNameOrEmail(String text);
    String RegisterEmailVerifyCode(String type,String email,String ip);

}
