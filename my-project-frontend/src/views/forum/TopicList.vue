<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import {get} from "@/net/index.js";
import LightCard from "@/components/LightCard.vue";
import {
  ArrowRightBold,
  Calendar, CircleCheck,
  Clock,
  CollectionTag,
  Compass,
  Document,
  Edit,
  EditPen, FolderOpened,
  Link,
  Microphone,
  Picture, Star
} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import { ElMessage } from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";
import {useStore} from "@/store/index.js";
import ColorDot from "@/components/ColorDot.vue";
import {watch} from "vue";
import router from "@/router/index.js";
import TopicTag from "@/components/TopicTag.vue";
import TopicCollectList from "@/components/TopicCollectList.vue";
import {apiForumTopicList, apiForumTopTopics, apiForumWeather} from "@/net/api/forum.js";
import AnnouncementList from "@/components/AnnouncementList.vue";
const store = useStore();
const topics=reactive({
  list:[],
  type:0,
  page:0,
  end: false,
  top:[]
})
watch(()=>topics.type ,()=>{
  resetList()
},{immediate:true});


const today = computed(() => {
  const date = new Date()
  return `${date.getFullYear()} 年 ${date.getMonth() + 1} 月 ${date.getDate()} 日`
})
function updateList(){
  if(topics.end) return
  apiForumTopicList(topics.page, topics.type, data => {
    if(data) {
      data.forEach(d => topics.list.push(d))
      topics.page++
    }
    if(!data || data.length < 10)
      topics.end = true
  })
}
updateList();
function onTopicCreate(){
  editor.value=false;
  resetList()
}
function resetList(){
  topics.page=0;
  topics.list = [];
  topics.end=false;
  updateList()
}
const ip = ref('')
const weather = reactive({
  location: {},
  now: {},
  hourly: [],
  success: false
})
// 获取IP地址
onMounted(async () => {
  try {
    const response = await fetch('https://ipinfo.io/json')
    const data = await response.json()
    ip.value = data.ip
  } catch (err) {
    console.error('获取 IP 失败:', err)
  }
})

// 获取天气信息
navigator.geolocation.getCurrentPosition(position => {
  const longitude = position.coords.longitude
  const latitude = position.coords.latitude
  apiForumWeather(longitude, latitude, data => {
    Object.assign(weather, data)
    weather.success = true
  })
}, error => {
  console.info(error)
  ElMessage.warning('位置信息获取超时，请检测网络设置')
  apiForumWeather(116.40529, 39.90499, data => {
    Object.assign(weather, data)
    weather.success = true
  })
}, {
  timeout: 3000,
  enableHighAccuracy: true
})
onMounted(() => {
  apiForumTopTopics(data => topics.top = data)
})

const editor=ref(false);
const collects=ref(false);
const announcement=ref(false)
</script>

<template>
  <div style="display: flex;margin:20px auto;gap: 20px;max-width: 1100px">
    <div style="flex: 1">
         <light-card>
            <div class="create-topic" @click="editor=true">
              <el-icon><EditPen/></el-icon>
               点击发表主题...
            </div>
           <div style="margin-top: 10px;display: flex;gap: 13px;font-size: 18px;color: grey">
             <el-icon><Edit /></el-icon>
             <el-icon><Document /></el-icon>
             <el-icon><Compass /></el-icon>
             <el-icon><Picture /></el-icon>
             <el-icon><Microphone /></el-icon>
           </div>
         </light-card>
      <light-card style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
        <div v-for="item in topics.top" class="top-topic" @click="() => router.push('/index/topic-detail/'+item.id)">
          <el-tag type="info" size="small">置顶</el-tag>
          <div>{{item.title}}</div>
          <div>{{new Date(item.time).toLocaleDateString()}}</div>
        </div>
      </light-card>
         <light-card style="margin-top: 10px; display: flex;gap: 7px" >
            <div :class="`type-select-card ${topics.type===item.id ? 'active' : ''}`"
                 @click="topics.type=item.id"
                 v-for="item in store.forum.types">
                 <color-dot :color="item.color "/>
              <span style="margin-left: 5px">{{item.name}}</span>
            </div>
         </light-card>
         <transition name="el-fade-in" mode="out-in">
           <div v-if="topics.list?.length">
             <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px"
                  v-infinite-scroll="updateList"
                  :infinite-scroll-disabled="topics.end"
                  :infinite-scroll-distance="10">
               <light-card  v-for="item in topics.list" :key="item" class="topic-card" @click="() => router.push('/index/topic-detail/'+item.id)">
                 <div style="display: flex">
                   <div>
                     <el-avatar :size="30" :src="store.avatarUserUrl(item.avatar)"/>
                   </div>
                   <div style="margin-left: 7px;transform: translateY(-2px)">
                     <div style="font-size: 13px;font-weight: bold">{{item.username}}</div>
                     <div style="font-size: 12px;color: grey ">
                       <el-icon><Clock/></el-icon>
                       <div style="margin-left: 2px;display: inline-block;transform: translateY(-2px)">{{new Date(item.time).toLocaleString()}}</div>
                     </div>
                   </div>
                 </div>
                 <div style="display:flex">
                  <topic-tag :type="item.type"/>
                   <span style="font-weight: bold;margin-left: 7px;transform: translateY(-1.5px)">{{item.title}}</span>
                 </div>
                 <div class="topic-content">{{item.text}}</div>
                 <div style="display: grid;grid-template-columns: repeat(3,1fr);grid-gap: 10px">
                   <el-image class="topic-image" v-for="img in item.images" :src="img"  fit="cover"></el-image>
                 </div>
                 <div style="display: flex;gap: 20px;font-size:13px;margin-top: 10px;opacity: 0.8">
                   <div>
                      <el-icon style="vertical-align: middle"><CircleCheck/></el-icon>{{item.like}}点赞
                   </div>
                   <div>
                     <el-icon style="vertical-align: middle"><Star/></el-icon>{{item.collect}}收藏
                   </div>
                 </div>
               </light-card>
             </div>
           </div>
         </transition>
    </div>
     <div style="width: 280px">
       <div style="position: sticky;top: 20px">
         <light-card>
             <div class="collect-list-button" @click="collects=true">
                 <span><el-icon><FolderOpened/></el-icon>查看我的收藏 </span>
                 <el-icon style="transform: translateY(3px)"><ArrowRightBold/></el-icon>
             </div>
         </light-card>
         <light-card style="margin-top: 10px">
             <div class="announcement-list-button" @click="announcement=true">
               <span><el-icon><CollectionTag/></el-icon>论坛公告</span>
               <el-icon style="transform: translateY(3px);"><ArrowRightBold/></el-icon>
             </div>
             <el-divider style="margin:10px 0"/>
             <div style="font-size: 14px;margin:10px;color:grey">
                卢本伟没有开挂，卢本伟表演了十七张牌被秒杀上央视的精彩表演
             </div>
         </light-card>
         <light-card style="margin-top: 10px;">
              <div>
                <el-icon><Calendar/></el-icon>
                天气信息
                <el-divider style="margin:10px 0"/>
                <weather :data="weather"/>
              </div>
         </light-card>
         <light-card style="margin-top: 10px">
           <div class="info-text">
             <div>当前日期</div>
             <div>{{today}}</div>
           </div>
           <div class="info-text">
             <div>当前ip地址</div>
             <div>{{ip}}</div>
           </div>
         </light-card>
         <div style="color: grey;font-size: 14px;margin-top: 20px">
           <el-icon><Link/></el-icon>
           友情链接
           <el-divider style="margin:10px 0"/>
         </div>
         <div style="display: grid;grid-template-columns: repeat(2,1fr);grid-gap: 10px;margin-top: 10px">
           <div class="friend-link">
             <el-image style="height: 100%" src="https://element-plus.org/images/js-design-banner.jpg"/>
           </div>
           <div class="friend-link">
             <el-image style="height: 100%" src="https://element-plus.org/images/vform-banner.png"/>
           </div>
         </div>
       </div>
     </div>
     <div>
        <topic-editor :show="editor" @close="editor=false" @success="onTopicCreate"/>
        <topic-collect-list :show="collects" @close="collects=false"/>
        <announcement-list :show="announcement" @close="announcement=false"/>
     </div>
  </div>
</template>

<style lang="less" scoped>
.collect-list-button{
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  transition: .3s;
  &:hover{
    cursor: pointer;
    opacity: 0.8;
  }
}
.announcement-list-button{
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  transition: .3s;
  &:hover{
    cursor: pointer;
    opacity: 0.8;
  }
}
.top-topic {
  display: flex;

  div:first-of-type {
    font-size: 14px;
    margin-left: 10px;
    font-weight: bold;
    opacity: 0.8;
    transition: color .3s;

    &:hover {
      color: grey;
    }
  }

  div:nth-of-type(2) {
    flex: 1;
    color: grey;
    font-size: 13px;
    text-align: right;
  }

  &:hover {
    cursor: pointer;
  }
}
.type-select-card{
  background-color: #f5f5f5;
  padding:2px 7px;
  font-size: 14px;
  border-radius: 3px;
  box-sizing: border-box;
  transition:background-color 0.3s;
  &.active{
     border: solid 1px #ead4c4;
  }
  &:hover{
    cursor: pointer;
    background: #dadada;
  }
}

.info-text {
  display: flex;
  justify-content: space-between;
  color: grey;
  font-size: 14px;
}
.friend-link {
  border-radius: 5px;
  overflow: auto;
}
.create-topic {
  background-color: #efefef;
  color: grey;
  border-radius: 5px;
  height: 40px;
  font-size: 14px;
  line-height: 40px;
  padding: 0 10px;
  &:hover {
    cursor: pointer;
  }
}
.dark {
  .create-topic{
    background-color: #232323;
  }
  .type-select-card{
     background-color: #282828;
    &.active{
      border: solid 1px #64594b;
    }
    &:active{
      border: solid 1px #5e5e5e;
    }
  }
}
.topic-card {
  padding: 15px;
  transition: scale .3s;
  &:hover{
    scale:1.015;
    cursor: pointer;
  }
  .topic-content {
    font-size: 13px;
    color: grey;
    margin:6px 0;
    display:-webkit-box;
    -webkit-box-orient:vertical;
    -webkit-line-clamp: 3;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .topic-type{
      display: inline-block;
      border-radius: 5px;
      font-size: 12px;
      padding:0 12px;
      border: 0.5px grey solid;
      height: 18px;
      margin-right: 5px;
  }
  .topic-image{
     width: 100%;
    height: 100%;
    max-height: 110px;
    border-radius: 5px;
  }
}
</style>