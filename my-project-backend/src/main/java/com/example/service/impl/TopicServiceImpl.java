package com.example.service.impl;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.*;
import com.example.entity.vo.request.AddCommentVO;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.request.TopicUpdateVO;
import com.example.entity.vo.response.CommentVO;
import com.example.entity.vo.response.TopTopicVO;
import com.example.entity.vo.response.TopicDetailVO;
import com.example.entity.vo.response.TopicPreviewVO;
import com.example.mapper.*;
import com.example.service.NotificationService;
import com.example.service.TopicService;
import com.example.utils.CacheUtils;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper,Topic >  implements TopicService {
    @Resource
    TopicTypeMapper topicTypeMapper;
    @Resource
    FlowUtils flowUtils;
    @Resource
    AccountMapper accountMapper;
    @Resource
    AccountDetailsMapper accountDetailsMapper;
    @Resource
    AccountPrivacyMapper accountPrivacyMapper;
    @Resource
    TopicCommentMapper topicCommentMapper;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    private Set<Integer> types;
    @Resource
    CacheUtils cacheUtils;
    @Resource
    NotificationService notificationService;
    @Autowired
    private TopicMapper topicMapper;

    @PostConstruct
    public void initTypes() {
        this.types = this.listTypes().stream().map(TopicType::getId).collect(Collectors.toSet());
    }
    @Override
    public List<TopicType> listTypes() {
        return topicTypeMapper.selectList(null);
    }

    @Override
    public String createTopic(int uid, TopicCreateVO vo) {
        if(!textLimitCheck(vo.getContent(),20000)){
            return "文章内容过长，发文失败!";
        }
        if(!types.contains(vo.getType())) return "文章类型非法";
        String key= Const.FORUM_TOPIC_COUNTER + uid;
        if(!flowUtils.limitPeriodCounterCheck(key,5,3600))
            return "发文频繁,请稍后再试";
        Topic topic=new Topic();
        BeanUtils.copyProperties(vo,topic);
        topic.setContent(vo.getContent().toJSONString());
        topic.setUid(uid);
        topic.setTime(new Date());
        if(this.save(topic)){
            cacheUtils.deleteFromCachePattern(Const.FORUM_TOPIC_PREVIEW_CACHE + "*");
            return null;
        }else{
            return "内部错误,请联系管理员!";
        }
    }
    @Override
    public String updateTopic(int uid, TopicUpdateVO vo) {
        if(!textLimitCheck(vo.getContent(),20000)){
            return "文章内容过长，发文失败!";
        }
        if(!types.contains(vo.getType())) return "文章类型非法";
        baseMapper.update(null,Wrappers.<Topic>update()
                .eq("uid",uid)
                .eq("id",vo.getId()).set("title",vo.getTitle())
                .set("content",vo.getContent().toString())
                .set("type",vo.getType()).set("time",new Date()));
        return null;
    }
    @Override
    public String createComment(int uid, AddCommentVO vo) {
        if(!textLimitCheck(JSONObject.parseObject(vo.getContent()),2000)){
            return "评论内容过长，发表失败!";
        }
        String key=Const.FORUM_TOPIC_COMMENT_COUNTER + uid;
        if(!flowUtils.limitPeriodCounterCheck(key,2,60))
            return "发表评论频繁,请稍后再试";
       TopicComment comment=new TopicComment();
       comment.setUid(uid);
       BeanUtils.copyProperties(vo,comment);
       comment.setTime(new Date());
       topicCommentMapper.insert(comment);
       Topic topic=baseMapper.selectById(vo.getTid());
       Account account=accountMapper.selectById(uid);
       if(vo.getQuote()>0){
           TopicComment quoteComment=topicCommentMapper.selectById(vo.getQuote());
          if(!Objects.equals(account.getId(),quoteComment.getUid())){
              notificationService.addNotification(account.getId(),"你有新的评论回复",account.getUsername()+"回复了你的评论,快去看看吧","success","/index/topic-detail/"+quoteComment.getTid());

          }
       }else if(!Objects.equals(account.getId(),topic.getUid())){
           notificationService.addNotification(topic.getUid(),"你有新的帖子回复",account.getUsername()+"回复了你发表的主题"+topic.getTitle()+"，快去看看吧","success","/index/topic-detail/"+topic.getId());
       }
       return null;
    }

    @Override
    public List<CommentVO> comments(int tid, int pageNumber) {
        Page<TopicComment> page= Page.of(pageNumber, 10);
        topicCommentMapper.selectPage(page,Wrappers.<TopicComment>query().eq("tid",tid));
        return page.getRecords().stream().map(dto->{
            CommentVO vo=new CommentVO();
            BeanUtils.copyProperties(dto,vo);
            if(dto.getQuote()>0){
              TopicComment comment= topicCommentMapper.selectOne(Wrappers.<TopicComment>query()
                      .eq("id",dto.getQuote()).orderByAsc( "time"));
                if(comment!=null){
                    JSONObject object=JSONObject.parseObject(comment.getContent());
                    StringBuilder builder=new StringBuilder();
                    this.shortContent(object.getJSONArray("ops"),builder,ignore->{});
                    vo.setQuote(builder.toString());
                }else{
                    vo.setQuote("评论已删除");
                }

            }
            CommentVO.User user=new CommentVO.User();
            this.fillUserDetailsByPrivacy(user,dto.getUid());
            vo.setUser(user);
            return vo;
        }).toList();
    }
    @Override
    public void deleteComment(int id, int uid) {
        topicCommentMapper.delete(Wrappers.<TopicComment>query().eq("id", id).eq("uid", uid));

    }

    @Override
    public void deleteTopic(int id, int uid) {
        Topic topic = baseMapper.selectOne(Wrappers.<Topic>query()
                .eq("id", id)
                .eq("uid", uid));
        if (topic == null) {
            throw new RuntimeException("帖子不存在或无权删除");
        }
        //删除评论
        DeleteCommentByTid(id);
        //删除提醒
        notificationService.deleteNotificationByTid(id);
        //删除点赞，收藏
        topicMapper.deleteTopicInteractBytid(id,"like");
        topicMapper.deleteTopicInteractBytid(id,"collect");
        // 5. 清除Redis中的互动缓存
        cleanRedisInteract(id);
        //清除缓存
        cacheUtils.deleteFromCachePattern(Const.FORUM_TOPIC_PREVIEW_CACHE + "*");
        //删除帖子
        baseMapper.deleteById(id);
    }
    @Override
    public TopicPreviewVO ResolveToPreview(Topic topic) {
        return this.resolveToPreview(topic);
    }

    @Override
    public void TopTopic(int tid,boolean status) {
        topicMapper.update(null,Wrappers.<Topic>update().eq("id",tid).set("top",status?1:0));
    }

    @Override
    public List<TopicPreviewVO> listTopicByPage(int pageNumber, int type) {
        String key=Const.FORUM_TOPIC_PREVIEW_CACHE+pageNumber+":"+type;
        List<TopicPreviewVO> list=cacheUtils.TakeListFromCache(key,TopicPreviewVO.class);
        if(list != null ){
            return list;
        }
        Page<Topic> page=Page.of(pageNumber,10);
        if(type==0){
           baseMapper.selectPage(page,Wrappers.<Topic>query().orderByDesc("time"));
        }else{
            baseMapper.selectPage(page,Wrappers.<Topic>query().eq("type",type).orderByDesc("time"));
        }
        List<Topic> topics=page.getRecords();
        if(topics.isEmpty()) return null;
        list=topics.stream().map(this::resolveToPreview).toList();
        cacheUtils.saveListToCache(key,list,60);
        return list;
    }

    @Override
    public List<TopTopicVO> listTopTopics() {
        List<Topic> topics=baseMapper.selectList(Wrappers.<Topic>query()
                .select("id","title","time")
                .eq("top",1));
        return topics.stream().map(topic -> {
            TopTopicVO vo=new TopTopicVO();
            BeanUtils.copyProperties(topic,vo);
            return vo;
        }).toList();
    }

    @Override
    public TopicDetailVO getTopicDetail(int tid,int uid) {
        TopicDetailVO vo=new TopicDetailVO();
        Topic topic=baseMapper.selectById(tid);
        if (topic == null) {
            return null;  // 返回 null，让 Controller 层处理
        }
        BeanUtils.copyProperties(topic,vo);
        TopicDetailVO.Interact interact=new TopicDetailVO.Interact(
                hasInteract(tid, uid, "like"),
                hasInteract(tid, uid, "collect")
        );
        vo.setInteract(interact);
        TopicDetailVO.User user=new TopicDetailVO.User();
        vo.setUser(this.fillUserDetailsByPrivacy(user,topic.getUid()));
        vo.setComments(topicCommentMapper.selectCount(Wrappers.<TopicComment>query().eq("tid",tid)));
        return vo;
    }

    @Override
    public void interact(Interact interact, Boolean state) {
        String type=interact.getType();
        synchronized (type.intern()){
            stringRedisTemplate.opsForHash().put(type, interact.toKey(), state.toString());
            this.saveInteractSchedule(type);
        }
    }

    @Override
    public List<TopicPreviewVO> listTopicCollects(int uid) {
        return baseMapper.collectTopic(uid).stream().map(topic->{
            TopicPreviewVO vo=new TopicPreviewVO();
            BeanUtils.copyProperties(topic,vo);
            return vo;
        }).toList();
    }

    private void shortContent(JSONArray ops, StringBuilder previewText, Consumer<Object> imageHandler){
        for(Object op:ops){
            Object insert=JSONObject.from(op).get("insert");
            if(insert instanceof String){
                if(previewText.length()>=300) continue;
                previewText.append(insert);
            }else if(insert instanceof Map<?,?> map){
                Optional.ofNullable(map.get("image"))
                        .ifPresent(imageHandler);
            }
        }
    }


    private final Map<String,Boolean> state=new HashMap<>();
    ScheduledExecutorService service= Executors.newScheduledThreadPool(2);
    private void  saveInteractSchedule(String type){
        if(!state.getOrDefault(type,false)){
            state.put(type,true);
            service.schedule(()->{
                this.saveInteract(type);
                state.put(type,false);
            },3, TimeUnit.SECONDS);
        }
    }
    private void saveInteract(String type) {
        synchronized (type.intern()) {
            List<Interact> check = new LinkedList<>();
            List<Interact> uncheck = new LinkedList<>();
            stringRedisTemplate.opsForHash().entries(type).forEach((k, v) -> {
                if (Boolean.parseBoolean(v.toString())) {
                    check.add(Interact.ParseInteract(k.toString(), type));
                } else {
                    uncheck.add(Interact.ParseInteract(k.toString(), type));
                }
            });
            if (!check.isEmpty()) {
                baseMapper.addInteract(check, type);
            }
            if (!uncheck.isEmpty()) {
                baseMapper.deleteInteract(uncheck, type);
            }
            stringRedisTemplate.delete(type);
        }
    }
    private <T> T fillUserDetailsByPrivacy(T  target,int uid){
        AccountDetails accountDetails=accountDetailsMapper.selectById(uid);
        Account account=accountMapper.selectById(uid);
        AccountPrivacy accountPrivacy=accountPrivacyMapper.selectById(uid);
        String[] ignores = accountPrivacy.hiddenFields();
        BeanUtils.copyProperties(account,target,ignores);
        BeanUtils.copyProperties(accountDetails,target,ignores);
        return target;
    }
    private TopicPreviewVO resolveToPreview(Topic topic){
        TopicPreviewVO vo=new TopicPreviewVO();
        BeanUtils.copyProperties(accountMapper.selectById(topic.getUid()),vo);
        BeanUtils.copyProperties(topic,vo);
        vo.setLike(baseMapper.InteractCount(topic.getId(),"like"));
        vo.setCollect(baseMapper.InteractCount(topic.getId(),"collect"));
        List<String> images=new ArrayList<>();
        StringBuilder previewText=new StringBuilder();
        JSONArray ops= JSON.parseObject(topic.getContent()).getJSONArray("ops");
        for(Object op:ops){
            Object insert=JSONObject.from(op).get("insert");
            if(insert instanceof String){
                if(previewText.length()>=300) continue;
                previewText.append(insert);
            }else if(insert instanceof Map<?,?> map){
                Optional.ofNullable(map.get("image"))
                        .ifPresent(obj->images.add(obj.toString()));
            }
        }
       vo.setText(previewText.length()>300?previewText.toString().substring(0,300):previewText.toString());
        vo.setImages(images);
        return vo;
    }
    private Boolean textLimitCheck(JSONObject object,int max){
        if(object==null)
            return false;
        long length=0;
        for (Object op : object.getJSONArray("ops")) {
            length+=JSONObject.from(op).getString("insert").length();
            if(length>max) return false;
        }

        return true;
    }
    private boolean hasInteract(int tid,int uid,String type){
      String key=tid+":"+uid;
      if(stringRedisTemplate.opsForHash().hasKey(type,key))
          return Boolean.parseBoolean(stringRedisTemplate.opsForHash().get(type, key).toString());
      return baseMapper.userInteractCount(tid,uid,type)>0;
    }
    private void DeleteCommentByTid(int tid){
        topicCommentMapper.delete(Wrappers.<TopicComment>query().eq("tid",tid));
    }
    private void cleanRedisInteract(int tid) {
        cleanRedisInteractByType(tid, "like");
        cleanRedisInteractByType(tid, "collect");
    }
    private void cleanRedisInteractByType(int tid, String type) {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(type);
        List<Object> keysToDelete = new ArrayList<>();
        // 找出所有与该帖子相关的键
        for (Object key : entries.keySet()) {
            String keyStr = key.toString();
            // key格式为 "tid:uid"
            if (keyStr.startsWith(tid + ":")) {
                keysToDelete.add(key);
            }
        }
        // 批量删除
        if (!keysToDelete.isEmpty()) {
            stringRedisTemplate.opsForHash().delete(type, keysToDelete.toArray());
        }
    }
}
