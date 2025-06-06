package com.example.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Interact;
import com.example.entity.dto.Topic;
import com.example.entity.dto.TopicType;
import com.example.entity.vo.request.AddCommentVO;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.request.TopicUpdateVO;
import com.example.entity.vo.response.CommentVO;
import com.example.entity.vo.response.TopTopicVO;
import com.example.entity.vo.response.TopicDetailVO;
import com.example.entity.vo.response.TopicPreviewVO;

import java.util.List;

public interface TopicService extends IService<Topic> {
   List<TopicType> listTypes();
   String createTopic(int uid, TopicCreateVO vo);
   List<TopicPreviewVO> listTopicByPage(int page,int type);
   List<TopTopicVO> listTopTopics();
   TopicDetailVO getTopicDetail(int tid,int uid);
   void  interact(Interact interact,Boolean state);
   List<TopicPreviewVO> listTopicCollects(int uid);
   String updateTopic(int uid, TopicUpdateVO vo);
   String createComment(int uid, AddCommentVO vo);
   List<CommentVO> comments(int tid,int pageNumber);
   void deleteComment(int id,int uid);
}
