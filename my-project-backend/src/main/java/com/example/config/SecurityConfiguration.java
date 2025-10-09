package com.example.config;

import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.vo.response.AuthorizeVO;
import com.example.filter.JwtAuthorizeFilter;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration {
    @Resource
    JwtUtils utils;
    @Resource
    JwtAuthorizeFilter jwtAuthorizeFilter;
    @Resource
    AccountService accountservice;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf ->conf
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole(Const.ROLE_ADMIN)
                        .anyRequest().hasAnyRole(Const.ROLE_DEFAULT, Const.ROLE_ADMIN)
                )
                .formLogin(
                        conf->conf
                                .loginProcessingUrl("/api/auth/login")
                                .successHandler(this::onAuthenticationSuccess)
                                .failureHandler(this::onAuthenticationFailure)
                                .usernameParameter("username")
                                .passwordParameter("password")
                )
                .logout(conf->conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess))
                .exceptionHandling(conf->conf
                        .authenticationEntryPoint(this::onUnAuthorized)
                        .accessDeniedHandler(this::onAccessDeny)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf ->conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    public void onAccessDeny(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.forbidden(e.getMessage()).asJsonString());
    }
    public void onUnAuthorized(HttpServletRequest request,
                               HttpServletResponse response,
                               AuthenticationException exception) throws IOException {
               response.setContentType("application/json;charset=utf-8");
               response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());

    }
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        User user = (User) authentication.getPrincipal();
        Account account=accountservice.getAccountByNameOrEmail(user.getUsername());
        String token=utils.createJwt(user,account.getId(),account.getUsername());
        AuthorizeVO authorizeVO=new AuthorizeVO();
        BeanUtils.copyProperties(account,authorizeVO);
        authorizeVO.setToken(token);
        authorizeVO.setExpire(utils.expireTime());
//        authorizeVO.setRole(account.getRole());
//        authorizeVO.setUsername(account.getUsername());
        response.getWriter().write(RestBean.success(authorizeVO).asJsonString());
    }
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException{
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.unauthorized( exception.getMessage()).asJsonString());

    }
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
       response.setContentType("application/json;charset=utf-8");
      PrintWriter writer = response.getWriter();
      String authorization = request.getHeader("Authorization");
      if(utils.invalidJwt(authorization)) {
          writer.write(RestBean.success().asJsonString());
      }else{
         writer.write(RestBean.failure(400,"退出登陆失败").asJsonString());
      }
    }
}
