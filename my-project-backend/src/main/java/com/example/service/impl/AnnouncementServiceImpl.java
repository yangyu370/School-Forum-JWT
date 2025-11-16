package com.example.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.dto.Announcement;
import com.example.entity.vo.request.AnnouncementCreateVO;
import com.example.entity.vo.response.AnnouncementVO;
import com.example.mapper.AnnouncementMapper;
import com.example.service.AccountService;
import com.example.service.AnnouncementService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    private final AccountService accountService;

    public AnnouncementServiceImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String CreateAnnouncement(AnnouncementCreateVO vo,int uid) {
        if(!textLimitCheck(vo.getContent(),2000)) return "内容过长，发表失败";
        Announcement announcement=new Announcement();
        BeanUtils.copyProperties(vo,announcement);
        announcement.setUid(uid);
        announcement.setContent(vo.getContent().toJSONString());
        announcement.setTime(new Date());
        if(this.save(announcement))
            return null;
        else
            return "内部错误请联系管理员";
    }

    @Override
    public List<AnnouncementVO> listAnnouncement() {
        return this.list(Wrappers.<Announcement>query().orderByDesc("time"))
                .stream()
                .map(announcement -> {
                    AnnouncementVO vo=new AnnouncementVO();
                    BeanUtils.copyProperties(announcement,vo);
                    Account account=accountService.getById(announcement.getUid());
                    AnnouncementVO.User user=new AnnouncementVO.User();
                    BeanUtils.copyProperties(account,user);
                    vo.setUser(user);
                    return vo;
                }).toList();
    }

    private Boolean textLimitCheck(JSONObject object, int max){
        if(object==null)
            return false;
        long length=0;
        for (Object op : object.getJSONArray("ops")) {
            length+=JSONObject.from(op).getString("insert").length();
            if(length>max) return false;
        }
        return true;
    }
}
