package com.example.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Interact;
import com.example.entity.dto.Topic;
import com.example.entity.dto.TopicType;
import com.example.entity.vo.request.AddCommentVO;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.request.TopicUpdateVO;
import com.example.entity.vo.response.*;

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
   void deleteTopic(int id,int uid);
   TopicPreviewVO ResolveToPreview(Topic topic);
   void TopTopic(int tid,boolean status);
   void AdminDeleteTopic(int tid);
   String AdminDeleteComment(int id);
   JSONObject listAllTopicByPage(int page,int size,String keyword);
   void setTopicLocked(int id,boolean locked);
   void setTopicInvisible(int id,boolean invisible);
   List<Topic> listTopicByUser(int uid);
   List<TopicSearchVO> searchTopic(String keyword);
}
