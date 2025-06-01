<script setup lang="ts">
import {useRoute} from "vue-router";
import router from "@/router";
import {get,post} from "@/net"
import axios from "axios";
import {reactive} from "vue";
import {ArrowLeft, BellFilled, CircleCheck, EditPen, Female, Male, Star} from "@element-plus/icons-vue";
import { QuillDeltaToHtmlConverter } from 'quill-delta-to-html';
import {computed} from "vue";
import Card from "@/components/Card.vue";
import TopicTag from "@/components/TopicTag.vue";
import InteractButton from "@/components/InteractButton.vue";
import {ElMessage} from "element-plus";
import {ref} from "vue";
import {useStore} from "@/store";
import TopicEditor from "@/components/TopicEditor.vue";
const store = useStore();
const route = useRoute();
const tid=route.params.tid
const topic=reactive({
  data:null,
  like:false,
  collect:false,
  comments:[]
})
const  edit=ref(false);
const init=( )=>get(`api/forum/topic?tid=${tid}`,data=>{
  topic.data=data
  topic.like=data.interact.like
  topic.collect=data.interact.collect
});
init()
const content = computed(()  => {
  if (!topic.data || !topic.data.content) return ''
  const ops = JSON.parse(topic.data.content).ops
  const converter = new QuillDeltaToHtmlConverter(ops, {inlineStyles: true });
  return converter.convert();
})
function interact(type,message){
  get(`api/forum/interact?tid=${tid}&type=${type}&state=${!topic[type]}`,()=>{
    topic[type] = !topic[type];
    if(topic[type])
      ElMessage.success(`${message}成功`)
    else
      ElMessage.error(`已取消${message}`)
  })
}
function updateTopic(editor){
  post('api/forum/update', {
     id:tid,
    type:editor.type.id,
    title:editor.title,
    content:editor.text,
  },( )=>{
     ElMessage.success("帖子内容更新成功")
     edit.value = false
    init()
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
            <el-avatar :src="axios.defaults.baseURL+'/images'+topic.data.user.avatar" :size="60"/>
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
           <div class="topic-content" v-html="content"></div>
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
    <topic-editor :show="edit" @close="edit=false" v-if="topic.data&&store.forum.types "
    :default-type="topic.data.type" :default-text="topic.data.content" :default-title="topic.data.title" submit-button="更新帖子内容 " :submit="updateTopic" />
  </div>
</template>

<style scoped>
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