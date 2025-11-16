package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Announcement;
import com.example.entity.vo.request.AnnouncementCreateVO;
import com.example.entity.vo.response.AnnouncementVO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface AnnouncementService extends IService<Announcement> {
    String CreateAnnouncement(AnnouncementCreateVO vo,int uid);
    List<AnnouncementVO> listAnnouncement();
}
