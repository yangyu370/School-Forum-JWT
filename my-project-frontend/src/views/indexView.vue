<script setup>
import { ref,reactive,onMounted,inject} from 'vue';
import {
  Bell,
  ChatDotSquare, Collection, DataLine,
  Document, Files,
  Location, Lock, Monitor,
  Notification, Operation, Position, School, Search,
  Umbrella, User, Check, Moon, Sunny, CoffeeCup
} from '@element-plus/icons-vue'
import LightCard from "@/components/LightCard.vue";
import UserInfo from "@/components/UserInfo.vue";
import {apiNotificationDelete, apiNotificationDeleteAll, apiNotificationList} from "@/net/api/user.js";
import {apiForumTypes, apiTopicSearch} from "@/net/api/forum.js";
import AIChatWindow from "@/components/AIChatWindow.vue";
import TopicTag from "@/components/TopicTag.vue";
import {useStore} from "@/store/index.js";
import router from "@/router/index.js";

const store = useStore()
const userMenu = [
  {
    title: '校园论坛', icon: Location, sub: [
      { title: '帖子广场', icon: ChatDotSquare, index: '/index' },
      { title: '失物招领', icon: Bell },
      { title: '校园活动', icon: Notification },
      { title: '表白墙', icon: Umbrella },
      { title: '海文考研', icon: School }
    ]
  }, {
    title: '探索与发现', icon: Position, sub: [
      { title: '成绩查询', icon: Document },
      { title: '班级课程表', icon: Files },
      { title: '教务通知', icon: Monitor },
      { title: '在线图书馆', icon: Collection },
      { title: '预约教室', icon: DataLine }
    ]
  }, {
    title: '个人设置', icon: Operation, sub: [
      { title: '个人信息设置', icon: User, index: '/index/user-setting' },
      { title: '个人帖子管理', icon:  CoffeeCup, index: '/index/forum-setting' },
      { title: '账号安全设置', icon: Lock, index: '/index/privacy-setting' }
    ]
  }
]
const notification=ref([])
const loadNotification =
    () => apiNotificationList(data => notification.value = data)
loadNotification()
function confirmNotification(id, url) {
  apiNotificationDelete(id, () => {
    loadNotification()
    window.open(url)
  })
}
function deleteAllNotification() {
  apiNotificationDeleteAll(loadNotification)
}
const loading=inject("userLoading")
const searchInput=reactive({
  type:"1",
  text: ''
})
const isDark = ref(false)

onMounted(() => {
  // 从 localStorage 读取暗黑模式设置
  const savedMode = localStorage.getItem('dark-mode')
  isDark.value = savedMode === 'true'
  updateDarkClass()
})

const updateDarkClass = () => {
  const html = document.documentElement
  if (isDark.value) {
    html.classList.add('dark')
  } else {
    html.classList.remove('dark')
  }
}

const toggleDarkMode = () => {
  isDark.value = !isDark.value
  updateDarkClass()
  // 保存到 localStorage
  localStorage.setItem('dark-mode', isDark.value.toString())
}
const searchTopic = (keyword, callback) => {
  if(!keyword) {
    callback([])
    return
  }
  apiTopicSearch(keyword, data => {
    callback(data)
  })
}
const toTopic =({id})=>{
  if(!id) return
  router.push('/index/topic-detail/'+id)
}
apiForumTypes(data => {
  const array = []
  array.push({name: '全部', id: 0, color: 'linear-gradient(45deg, white, red, orange, gold, green, blue)'})
  data.forEach(d => array.push(d))
  store.forum.types = array
})
</script>
<template>
    <div  class="main-content" v-loading="loading" element-loading-text="正在进入,请稍后..">
      <AIChatWindow/>
      <el-container style="height: 100%" v-if="!loading">
        <el-header class="main-content-header">
          <el-image class="logo" src="/images/icon.png"></el-image>
          <div style="flex: 1;padding: 0 20px;margin-top: 10px;text-align: center">
             <el-autocomplete v-model="searchInput.text" style="width: 100%;max-width: 500px"
                              fit-input-width
                              placeholder="搜索论坛相关内容..."
                              @select="toTopic"
                              :fetch-suggestions="searchTopic">
                <template #prefix>
                   <el-icon>
                     <Search/>
                   </el-icon>
                </template>
               <template #default="{ item }">
                   <div class="search-item">
                      <div class="title" v-if="item.highlight.title">
                        <topic-tag :type="item.type"/>
                        <span v-html="item.highlight.title"></span>
                      </div>
                     <div class="title" v-else>
                       <topic-tag :type="item.type"/>
                       <span>{{item.title}}</span>
                     </div>
                      <div class="desc" v-if="item.highlight.intro" v-html="item.highlight.intro"></div>
                      <div class="desc" v-else>{{item.intro}}</div>
                   </div>
               </template>
               <template #append>
                 <el-select v-model="searchInput.type" style="width: 120px">
                  <el-option value="1" label="帖子广场"/>
                 <el-option value="2" label="校园活动"/>
                 <el-option value="3" label="表白墙"/>
                 <el-option value="4" label="教务通知"/>
                 </el-select>
               </template>
             </el-autocomplete>
          </div>
          <user-info>
            <!-- 暗黑模式切换按钮 -->
            <div class="theme-toggle" @click="toggleDarkMode" style="margin-right: 15px">
              <el-icon>
                <Moon v-if="!isDark"/>
                <Sunny v-else/>
              </el-icon>
              <div style="font-size: 10px">{{ isDark ? '浅色' : '深色' }}</div>
            </div>
            
            <el-popover placement="bottom" :width="350" trigger="click">
              <template #reference>
                <el-badge style="margin-right: 15px" is-dot :hidden="!notification.length ">
                  <div class="notification">
                    <el-icon><Bell/></el-icon>
                    <div style="font-size: 10px">消息提醒 </div>
                  </div>
                </el-badge>
              </template>
              <el-empty :image-size="80" description="暂无未读消息" v-if="!notification.length"/>
              <el-scrollbar :max-height="500" v-else>
                <light-card v-for="item in notification" class="notification-item" @click="confirmNotification(item.id,item.url)">
                  <div>
                    <el-tag :type="item.type">消息</el-tag>&nbsp;
                    <span style="font-weight: bold">{{item.title}}</span>
                  </div>
                  <el-divider style="margin:7px 0 3px 0"/>
                  <div style="font-size: 13px;opacity: 0.8 ;color: grey">
                    {{item.content}}
                  </div>
                </light-card>
                <div>
                  <el-button size="small" type="info" :icon="Check" @click=" deleteAllNotification" style="width: 100%">清除全部未读消息</el-button>
                </div>
              </el-scrollbar>
            </el-popover>
          </user-info>
        </el-header>
        <el-container>
          <el-aside width="230px">
            <el-scrollbar style="height: calc(100vh - 55px)">
              <el-menu
                  router
                  :default-active="$route.path"
                  :default-openeds="['1', '2', '3']"
                  style="min-height: calc(100vh - 55px)">
                <el-sub-menu :index="(index + 1).toString()"
                             v-for="(menu, index) in userMenu">
                  <template #title>
                    <el-icon>
                      <component :is="menu.icon"/>
                    </el-icon>
                    <span><b>{{ menu.title }}</b></span>
                  </template>
                  <el-menu-item :index="subMenu.index" v-for="subMenu in menu.sub">
                    <template #title>
                      <el-icon>
                        <component :is="subMenu.icon"/>
                      </el-icon>
                      {{ subMenu.title }}
                    </template>
                  </el-menu-item>
                </el-sub-menu>
              </el-menu>

            </el-scrollbar>
          </el-aside>
          <el-main class="main-content-page">
               <el-scrollbar style="height: calc(100vh - 55px)">
                    <router-view v-slot="{Component}">
                        <transition name="el-fade-in-linear" mode="out-in">
                              <component :is="Component" style="height: 100%"></component>
                        </transition>
                    </router-view>
               </el-scrollbar>
          </el-main>
        </el-container>
      </el-container>
    </div>
</template>
<style lang="less" scoped>
.search-item{
  line-height: 1.5;
  padding: 10px 0;
  :deep(em) {
    color: #1a1a1a;
    background-color: yellow;
    font-style: normal;
  }

  .title {
    font-size: 15px;
    font-weight: bold;
    margin-bottom: 5px;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 1;
    overflow: hidden;
  }

  .desc {
    font-size: 13px;
    white-space: pre-wrap;
    color: gray;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
  }

}
.notification-item{
  transition: .3s;
  &:hover{
    cursor: pointer;
    scale: 1.01;
  }
  margin: 10px 0;
}
 .theme-toggle {
   font-size: 22px;
   line-height: 14px;
   text-align: center;
   transition: all .3s;
   &:hover {
     cursor: pointer;
     color: var(--el-color-primary);
     transform: scale(1.1);
   }
 }
 
 .notification{
   font-size: 22px;
   line-height: 14px;
   text-align: center;
   transition: color .3s ;
   &:hover{
     cursor: pointer;
     color: grey;
   }
 }
 .main-content{
   height: 100vh;
   width: 100vw;
 }
 .main-content-header{
   border-bottom: solid 1px var(--el-border-color);
   height: 55px;
   display: flex;
   align-content: center;
   box-sizing:border-box;
 }
 .logo{
   margin-left: -15px;
   width: 240px;
 }
 .user-info{
   display: flex;
   justify-content: flex-end;
   align-items:center;
   .el-avatar:hover{
       cursor:pointer;
   }
 }
 .profile{
   text-align: right;
   margin-right: 20px;
   :first-child{
      font-size: 18px;
      font-weight: bold;
      line-height: 20px;
   }
   :last-child{
      font-size: 10px;
     color: gray;

   }
 }
 .main-content-page{
   padding: 0;
   background-color: #f7f8fa;
 }
 .dark .main-content-page{
   background-color: #212225;
 }
</style>
