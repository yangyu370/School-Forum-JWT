<script setup>
import {logout} from "@/net/index.js";
import router from "@/router/index.js";
import {get} from "@/net/index.js"
import {useStore} from "@/store/index.js";
import { ref,reactive,onMounted} from 'vue';
import {
  Back,
  Bell,
  ChatDotSquare, Collection, DataLine,
  Document, Files,
  Location, Lock, Message, Monitor,
  Notification, Operation, Position, Moon,
  School, Search,
  Umbrella, User, Sunny
} from '@element-plus/icons-vue'
function userLogout(){
   logout(()=>router.push('/'))
}
function goUserSetting(){
   router.push('/index/user-setting')
}
const store=useStore()
const loading=ref(true)
const searchInput=reactive({
  type:"1",
  text: ''
})
get("api/user/info",(data)=>{
  store.user=data
  loading.value=false;
})
const isDark = ref(false)
onMounted(() => {
  isDark.value = localStorage.getItem('dark-mode') === 'true'
  updateDarkClass()
})

const toggleDarkMode = () => {
  localStorage.setItem('dark-mode', isDark.value)
  updateDarkClass()
}

const updateDarkClass = () => {
  const html = document.documentElement
  if (isDark.value) {
    html.classList.add('dark')
  } else {
    html.classList.remove('dark')
  }
}

</script>

<template>
    <div  class="main-content" v-loading="loading" element-loading-text="正在进入,请稍后..">
      <el-container style="height: 100%" v-if="!loading">
        <el-header class="main-content-header">
          <el-image class="logo" src="/images/icon.png"></el-image>
          <div style="flex: 1;padding: 0 20px;margin-top: 10px;text-align: center">
             <el-input v-model="searchInput.text" style="width: 100%;max-width: 500px" placeholder="搜索论坛相关内容...">
                <template #prefix>
                   <el-icon><Search /></el-icon>
                </template>
               <template #append>
                 <el-select v-model="searchInput.type" style="width: 120px">
                  <el-option value="1" label="帖子广场"/>
                 <el-option value="2" label="校园活动"/>
                 <el-option value="3" label="表白墙"/>
                 <el-option value="4" label="教务通知"/>
                 </el-select>
               </template>
             </el-input>
          </div>
          <div class="user-info">
              <div class="flex items-center p-4">
                <el-switch
                    v-model="isDark"
                    :active-icon="Moon"
                    :inactive-icon="Sunny"
                    :style="switchStyle"
                    @change="toggleDarkMode"
                    style="margin-right: 20px"
                />
              </div>
            <div class="profile">
                 <div>{{store.user.username}}</div>
                 <div>{{store.user.email}}</div>
            </div>
            <el-dropdown>
              <el-avatar  :src="store.avatarUrl"/>
              <template #dropdown>
                <el-dropdown-item @click="goUserSetting">
                  <el-icon><operation/></el-icon>
                  个人设置
                </el-dropdown-item>
                <el-dropdown-item divided>
                  <el-icon><message/></el-icon>
                  消息列表
                </el-dropdown-item>
                <el-dropdown-item divided @click="userLogout">
                  <el-icon><Back/></el-icon>
                   退出登录
                </el-dropdown-item>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-container>
          <el-aside width="230px">
            <el-scrollbar style="height: calc(100vh - 55px)">
              <el-menu router
                       :default-active="$route.path"
                       style="min-height: calc(100vh - 55px)">
                <el-sub-menu index="1">
                  <template #title>
                    <el-icon><Location/></el-icon>
                    <span><b>校园论坛</b></span>
                  </template>
                  <el-menu-item index="/index">
                    <template #title>
                      <el-icon><chat-dot-square/></el-icon>
                      帖子广场
                    </template>
                  </el-menu-item>
                  <el-menu-item index="1-2">
                    <template #title>
                      <el-icon><Bell/></el-icon>
                      失物招领
                    </template>
                  </el-menu-item>
                  <el-menu-item index="1-3">
                    <template #title>
                      <el-icon><Notification/></el-icon>
                      校园活动
                    </template>
                  </el-menu-item>
                  <el-menu-item index="1-4">
                    <template #title>
                      <el-icon><Umbrella/></el-icon>
                      表白墙
                    </template>
                  </el-menu-item>
                  <el-menu-item index="1-5">
                    <template #title>
                      <el-icon><School/></el-icon>
                      海文考研
                      <el-tag style="margin-left: 10px;font-size: 10px">合作机构</el-tag>
                    </template>
                  </el-menu-item>
                </el-sub-menu>
                <el-sub-menu index="2">
                  <template #title>
                    <el-icon><Position/></el-icon>
                    <span><b>探索与发现</b></span>
                  </template>
                  <el-menu-item index="2-1">
                    <template #title>
                      <el-icon><Document/></el-icon>
                      成绩查询
                    </template>
                  </el-menu-item>
                  <el-menu-item index="2-2">
                    <template #title>
                      <el-icon><Files/></el-icon>
                      班级课程表
                    </template>
                  </el-menu-item>
                  <el-menu-item index="2-3">
                    <template #title>
                      <el-icon><Monitor/></el-icon>
                      教务通知
                    </template>
                  </el-menu-item>
                  <el-menu-item index="2-4">
                    <template #title>
                      <el-icon><Collection/></el-icon>
                      在线图书馆
                    </template>
                  </el-menu-item>
                  <el-menu-item index="2-5">
                    <template #title>
                      <el-icon><DataLine/></el-icon>
                      预约教室
                    </template>
                  </el-menu-item>
                </el-sub-menu>
                <el-sub-menu index="3">
                   <template #title>
                       <el-icon><Operation/></el-icon>
                       <span><b>个人设置</b></span>
                   </template>
                  <el-menu-item index="/index/user-setting">
                    <template #title>
                      <el-icon><User/></el-icon>
                      个人信息设置
                    </template>
                  </el-menu-item>
                  <el-menu-item index="/index/privacy-setting">
                    <template #title>
                      <el-icon><Lock/></el-icon>
                      账号安全设置
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