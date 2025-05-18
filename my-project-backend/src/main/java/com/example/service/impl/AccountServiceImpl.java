package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVO;
import com.example.entity.vo.request.ModifyEmailVO;
import com.example.mapper.AccountMapper;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
    @Resource
    PasswordEncoder encoder;
    @Resource
    FlowUtils flowUtils;
    @Resource
    AmqpTemplate amqpTemplate;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account=this.getAccountByNameOrEmail(username);
        if(account==null){
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    public Account getAccountByNameOrEmail(String text){
        return this.query()
                .eq("username",text).or()
                .eq("email",text)
                .one();

    }

    @Override
    public Account findAccountById(int id) {
        return this.query().eq("id", id).one();
    }
    @Override
    public String RegisterEmailVerifyCode(String type, String email, String ip) {
        synchronized (ip.intern()) {
            if(!this.verifyLimit(ip)){
                return "请求频繁,请稍后再试";
            }
            Random random = new Random();
            int code=random.nextInt(899999)+100000;
            Map<String,Object> data=Map.of("type",type,"mail",email,"code",code);
            amqpTemplate.convertAndSend("mail",data);
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA+email,String.valueOf(code),3, TimeUnit.MINUTES);
            return null;
        }
    }

    @Override
    public String RegisterEmailAccount(EmailRegisterVO vo) {
        String email=vo.getEmail();
        String code=stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA+email);
        String username=vo.getUsername();
        if(code==null){
             return "请先获取验证码";
        }
        if(!code.equals(vo.getCode())){
            return "验证码输入错误请重新输入";
        }
        if(this.existsAccountByEmail(email)){
            return "该邮件已被其他用户注册,请更换邮箱";
        }
        if(this.existsAccountByUsername(username)){
            return "此用户名已被其他用户注册,请换个新的";
        }
        String password=encoder.encode(vo.getPassword());
        Account account=new Account(null,username,password,email,"user",new Date());
        if (this.save(account)) {
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA+email);
            return null;
        }else{
            return "内部错误，请联系管理员";
        }
    }

    private boolean verifyLimit(String ip){
        String key = Const.VERIFY_EMAIL_LIMIT+ip;
        return flowUtils.limitOnceCheck(key,60);
    }
    private boolean existsAccountByEmail(String email){
        return this.baseMapper.exists(Wrappers.<Account>query().eq("email",email));
    }
    private boolean existsAccountByUsername(String username){
        return this.baseMapper.exists(Wrappers.<Account>query().eq("username",username));
    }

    @Override
    public String resetConfirm(ConfirmResetVO vo) {
        String email=vo.getEmail();
        String code=stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA+email);
        if(code==null){
            return "请先获取验证码";
        }
        if(!code.equals(vo.getCode())){
            return "验证码错误请重新输入";
        }
        return null;
    }

    @Override
    public String resetEmailAccountPassword(EmailResetVO vo) {
        String email=vo.getEmail();
       String verify=this.resetConfirm(new ConfirmResetVO(email,vo.getCode()));
       if(verify!=null) return verify;
       String password=encoder.encode(vo.getPassword());
       boolean update=this.update().eq("email",email).set("password",password).update();
       if(update){
           stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA+email);
       }
        return null;
    }

    @Override
    public String modifyEmail(int id, ModifyEmailVO vo) {
        String email=vo.getEmail();
        String code=stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA+email);
        if(code==null){
            return "请先获取验证码";
        }else if(!code.equals(vo.getCode())){
            return "验证码错误,请重新输入!";
        }
        stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA+email);
        Account account=this.getAccountByNameOrEmail(email);
        if(account!=null&&account.getId()!=id)
              return "该邮件已被其他用户注册,无法完成此操作,请更换别的邮箱";
        this.update()
                .set("email",email)
                .eq("id", id)
                .update();
        return null;
    }
}
