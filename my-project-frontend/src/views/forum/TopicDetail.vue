<script setup lang="ts">
import {useRoute} from "vue-router";
import router from "@/router";
import {
  apiForumCommentDelete,
  apiForumComments,
  apiForumInteract,
  apiForumTopic,
  apiForumUpdateTopic
} from "@/net/api/forum";

import {reactive} from "vue";
import {
  ArrowLeft,
  BellFilled,
  ChatSquare,
  CircleCheck,
  Delete,
  EditPen,
  Female,
  Male,
  Plus,
  Star
} from "@element-plus/icons-vue";
import { QuillDeltaToHtmlConverter } from 'quill-delta-to-html';
import Card from "@/components/Card.vue";
import TopicTag from "@/components/TopicTag.vue";
import InteractButton from "@/components/InteractButton.vue";
import {ElMessage} from "element-plus";
import {ref} from "vue";
import {useStore} from "@/store";
import TopicEditor from "@/components/TopicEditor.vue";
import TopicCommentEditor from "@/components/TopicCommentEditor.vue";
const store = useStore();
const route = useRoute();
const tid=route.params.tid
const topic=reactive({
  data:null,
  like:false,
  collect:false,
  comments:null,
  page:1
})
const  edit=ref(false);
const  comment=reactive({
  show:false,
  text:'',
  quote: -1
})
const init = () => apiForumTopic(tid, data => {
  topic.data = data
  topic.like = data.interact.like
  topic.collect = data.interact.collect
  loadComments(1)
})
init()
function ConvertToHtml(content){
  if (!topic.data || !topic.data.content) return ''
  const ops = JSON.parse(content).ops
  const converter = new QuillDeltaToHtmlConverter(ops, {inlineStyles: true });
  return converter.convert();
}
function interact(type, message) {
  apiForumInteract(tid, type, topic, message)
}
function updateTopic(editor) {
  apiForumUpdateTopic({
    id: tid,
    type: editor.type.id,
    title: editor.title,
    content: editor.text
  }, () => {
    ElMessage.success('帖子内容更新成功！')
    edit.value = false
    init()
  })
}
function loadComments(page) {
  topic.comments = null
  topic.page = page
  apiForumComments(tid, page - 1, data => topic.comments = data)
}
function onCommentAdd(){
  comment.show=false
  loadComments(1)
}
function deleteComment(id) {
  apiForumCommentDelete(id, () => {
    ElMessage.success('删除评论成功！')
    loadComments(topic.page)
  })
}
</script>

<template>
  <div class="topic-page" v-if="topic.data">
     <div class="topic-main" style="position: sticky;top: 0;z-index: 10">
       <card style="display: flex;width: 100%">
           <el-button type="info" size="small" round @click="router.push('/index')">
             <el-icon><arrow-left/></el-icon>
             返回列表
           </el-button>
           <div style="text-align: center;flex: 1;font-weight: bold;font-size: 20px">
             <topic-tag :type="topic.data.type"></topic-tag>
             <span style="margin-left: 5px;transform: translateY(3px) ">{{topic.data.title}}</span>
           </div>
       </card>
     </div>
    <div class="topic-main">
       <div class="topic-main-left">
            <el-avatar :src="store.avatarUserUrl(topic.data.user.avatar)" :size="60"/>
            <div style="display: flex;margin-left: 65px;align-items: center;">
                <div style="font-size:18px;font-weight: bold;">{{topic.data.user.username}}</div>
              <div style="margin-left:5px;transform: translateY(3px)" >
                <span style="color: hotpink" v-if="topic.data.user.gender === 1">
              <el-icon><Female/></el-icon>
              </span>
                <span style="color: dodgerblue" v-if="topic.data.user.gender === 0">
              <el-icon><Male/></el-icon>
              </span>
              </div>
            </div>
           <div class="desc">
             {{topic.data.user.email}}
           </div>
          <el-divider style="margin:10px 0"/>
          <div style="text-align: left;margin: 0">
              <div class="desc">微信号：{{topic.data.user.wx|| ' 已隐藏或未填写'}}</div>
              <div class="desc">手机号:{{topic.data.user.phone || ' 已隐藏或未填写'}}</div>
              <div class="desc">QQ号:{{topic.data.user.qq || ' 已隐藏或未填写'}}</div>
          </div>
         <el-divider style="margin:5px 0"/>
         <div class="desc" style="margin: 5px 0">{{topic.data.user.desc}}</div>
       </div>
      <div class="topic-main-right" >
           <div class="topic-content" v-html="ConvertToHtml(topic.data.content)"></div>
           <div>
             <div style="font-size: 13px;color: grey;text-align: center;margin-top:40px">
                发帖时间:{{new Date(topic.data.time).toLocaleString()}}
             </div>
           </div>
           <div style="text-align: right;margin-top: 30px">
             <interact-button check-name="编辑帖子"  color="dodgerblue" :checked="true" @check="edit=true" style="margin-right: 20px" v-if="store.user.id===topic.data.user.id">
               <el-icon><EditPen/></el-icon>
             </interact-button>
               <interact-button name="点赞" check-name="已点赞" color="pink" @check="interact('like','点赞')"
                                v-model:checked="topic.like">
                  <el-icon><CircleCheck/></el-icon>
               </interact-button>
             <interact-button name="收藏" check-name="已收藏" style="margin-left: 20px" color="orange" @check="interact('collect','收藏')"
                              v-model:checked="topic.collect">
               <el-icon><Star/></el-icon>
             </interact-button>
           </div>
      </div>
    </div>
    <transition name="el-fade-in-linear" mode="out-in">
        <div v-if="topic.comments">
          <div class="topic-main" style="margin-top: 10px" v-for="item in topic.comments">
            <div class="topic-main-left">
              <el-avatar :src="store.avatarUserUrl(item.user.avatar)" :size="60"/>
              <div style="display: flex;margin-left: 65px;align-items: center;">
                <div style="font-size:18px;font-weight: bold;">{{item.user.username}}</div>
                <div style="margin-left:5px;transform: translateY(3px)" >
                <span style="color: hotpink" v-if="item.user.gender === 1">
              <el-icon><Female/></el-icon>
              </span>
                  <span style="color: dodgerblue" v-if="item.user.gender === 0">
              <el-icon><Male/></el-icon>
              </span>
                </div>
              </div>
              <div class="desc">
                {{item.user.email}}
              </div>
              <el-divider style="margin:10px 0"/>
              <div style="text-align: left;margin: 0">
                <div class="desc">微信号：{{item.user.wx|| ' 已隐藏或未填写'}}</div>
                <div class="desc">手机号:{{item.user.phone || ' 已隐藏或未填写'}}</div>
                <div class="desc">QQ号:{{item.user.qq || ' 已隐藏或未填写'}}</div>
              </div>
            </div>
            <div class="topic-main-right" >
              <div style="font-size: 13px;color: grey;margin-top:10px">
                评论 时间:{{new Date(item.time).toLocaleString()}}
              </div>
              <div v-if="item.quote" class="comment-quote">
                回复:{{item.quote}}
              </div>
              <div class="topic-content" v-html="ConvertToHtml(item.content)"></div>
              <div style="text-align: right">
                <el-link :icon="ChatSquare" @click="comment.show = true;comment.quote = item"
                         type="info">&nbsp;回复评论</el-link>
                <el-link :icon="Delete" type="danger" v-if="item.user.id === store.user.id"
                         style="margin-left: 20px" @click="deleteComment(item.id)">&nbsp;删除评论</el-link>
              </div>

            </div>
          </div>
           <div style="width: fit-content;margin: 20px auto">
             <el-pagination background layout="prev, pager, next"
                            v-model:current-page="topic.page" @current-change="loadComments"
                            :total="topic.data.comments" :page-size="10" hide-on-single-page
             />
           </div>
        </div>
    </transition>
    <topic-editor :show="edit" @close="edit=false" v-if="topic.data&&store.forum.types "
    :default-type="topic.data.type" :default-text="topic.data.content" :default-title="topic.data.title" submit-button="更新帖子内容 " :submit="updateTopic" />
    <topic-comment-editor :show="comment.show" @close="comment.show = false" :tid="tid"
                          :quote="comment.quote" @comment="onCommentAdd"/>
    <div class="add-comment" @click="comment.show=true;comment.quote=null">
        <el-icon style="margin-top:17px"><Plus/></el-icon>
    </div>
  </div>
</template>

<style scoped>
.comment-quote {
  font-size: 13px;
  color: grey;
  background-color: rgba(94, 94, 94, 0.1);
  padding: 10px;
  margin-top: 10px;
  border-radius: 5px;
}
.add-comment{
  position: fixed;
  bottom: 60px;
  right:20px;
  width:60px;
  height: 60px;
  border-radius: 50%;
  font-size: 25px;
  color: var(--el-color-primary);
  text-align: center;
  line-height: 40px;
  background: var(--el-bg-color-overlay);
  box-shadow: var(--el-box-shadow-lighter);
  &:hover{
    background: var(--el-border-color-extra-light);
    cursor: pointer;

  }
}
.topic-page {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 10px 0;
}
.topic-main {
  display: flex;
  border-radius: 7px;
  margin:0 auto;
  background-color: var(--el-bg-color);
  width: 1100px;
  .topic-main-left{
    text-align: center;
    width:200px;
    padding:10px;
    border-right: solid 1px var(--el-border-color);
  }
  .topic-main-right{
    width:900px;
    padding:10px 20px;
    display: flex;
    flex-direction: column;
    .topic-content{
      font-size: 14px;
      opacity: 0.8;
      line-height: 18px;
      flex:1;
    }
  }
}
.desc{
  font-size: 12px;
  color: grey;
}
</style>