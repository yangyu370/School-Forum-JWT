package com.example.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class FlowUtils {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    private static final LimitAction defaultAction = overclock -> !overclock;
    public boolean limitOnceCheck(String key,int blockTime) {
       if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
           return false;
       }else{
           stringRedisTemplate.opsForValue().set(key,"",blockTime, TimeUnit.SECONDS);
       }
       return true;
    }
   /**
   * 针对于在时间段内多次请求限制，如3秒内20次请对
   * @param counterKey 计数键
   * @param frequency 请求频率
   * @param period 计数周期
   * @retumn  是否通过检查
   */
    public boolean limitPeriodCounterCheck(String counterKey,int frequency,int period) {
         return this.internalCheck(counterKey, frequency, period, defaultAction);
    }
    private boolean internalCheck(String key, int frequency, int period, LimitAction action){
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            Long value = Optional.ofNullable(stringRedisTemplate.opsForValue().increment(key)).orElse(0L);
            return action.run(value > frequency);
        } else {
            stringRedisTemplate.opsForValue().set(key, "1", period, TimeUnit.SECONDS);
            return true;
        }
    }
    /*
     *
     * 内部使用，限制行为与策略
     */
    private interface LimitAction {
        boolean run(boolean overclock);
    }
}
