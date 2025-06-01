package com.example.filter;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Synchronized;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import com.example.utils.Const;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Order(Const.ORDER_LIMIT)
public class FLowLimitFilter extends HttpFilter {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String ip = request.getRemoteAddr();
        if(this.trycount(ip)){
            chain.doFilter(request, response);
        }else{
            this.writeBlockMessage(response);
        }
    }
    private void writeBlockMessage(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("操作频繁请稍后再试");
    }
    private boolean trycount(String ip){
        synchronized (ip.intern()) {
            if(stringRedisTemplate.hasKey(Const.FLOW_LIMIT_BLOCK + ip))
                return false;
            return this.limitPeriodBlock(ip);
        }
    }
    private boolean limitPeriodBlock(String ip){
        if (stringRedisTemplate.hasKey(Const.FLOW_LIMIT_COUNTER + ip)) {
            Long increment = Optional.ofNullable(stringRedisTemplate.opsForValue().increment(Const.FLOW_LIMIT_COUNTER+ip)).orElse(0L);
            if(increment > 50){
                stringRedisTemplate.opsForValue().set(Const.FLOW_LIMIT_BLOCK+ip, "", 180, TimeUnit.SECONDS);
                return false; // 当请求次数超过阈值时，返回false阻止请求
            }
        } else {
            stringRedisTemplate.opsForValue().set(Const.FLOW_LIMIT_COUNTER+ip, "1", 3, TimeUnit.SECONDS);
        }
        return true;
    }
}
