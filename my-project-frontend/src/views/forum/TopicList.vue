<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import LightCard from "@/components/LightCard.vue";
import { Calendar, CollectionTag, EditPen, Link } from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import { get } from "@/net/index.js";
import { ElMessage } from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";

const today = computed(() => {
  const date = new Date()
  return `${date.getFullYear()} 年 ${date.getMonth() + 1} 月 ${date.getDate()} 日`
})

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
const getWeather = (longitude, latitude) => {
  get(`/api/forum/weather?longitude=${longitude}&latitude=${latitude}`, 
    data => {
      Object.assign(weather, data)
      weather.success = true
    },
    error => {
      console.error('获取天气失败:', error)
      ElMessage.warning("位置信息获取失败，使用默认位置")
      // 使用默认位置（北京）
      getWeather(116.41, 39.92)
    }
  )
}

// 获取地理位置
onMounted(() => {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { longitude, latitude } = position.coords
        getWeather(longitude, latitude)
      },
      (error) => {
        switch (error.code) {
            case error.PERMISSION_DENIED:
                console.error("用户拒绝了地理位置请求。");
                // 在这里添加用户拒绝地理位置访问时的处理逻辑
                break;
            case error.POSITION_UNAVAILABLE:
                console.error("地理位置信息不可用。");
                break;
            case error.TIMEOUT:
                console.error("请求地理位置超时。");
                break;
            default:
                console.error("获取地理位置时发生未知错误。");
                break;
        }
      },
      {
        timeout: 30000,
        enableHighAccuracy: true
      }
    )
  } else {
    ElMessage.warning("您的浏览器不支持地理位置功能，使用默认位置")
    // 使用默认位置（北京）
    getWeather(116.41, 39.92)
  }
})
const editor=ref(false);
</script>

<template>
  <div style="display: flex;margin:20px auto;gap: 20px;max-width: 1100px">
    <div style="flex: 1">
         <light-card>
            <div class="create-topic" @click="editor=true">
              <el-icon><EditPen/></el-icon>
               点击发表主题...
            </div>
         </light-card>
         <light-card style="margin-top: 10px;height: 30px">
         </light-card>
         <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
            <light-card style="height: 150px" v-for="item in 10" :key="item">
            </light-card>
         </div>
    </div>
     <div style="width: 280px">
       <div style="position: sticky;top: 20px">
         <light-card>
             <div style="font-weight: bold;margin-top: 10px">
               <el-icon><CollectionTag/></el-icon>
                 论坛公告
             </div>
             <el-divider style="margin:10px 0"/>
             <div style="font-size: 14px;margin:10px;color:grey">
                卢本伟没有开挂，卢本伟表演了十七张牌被秒杀上央视的精彩表演
             </div>
         </light-card>
         <light-card style="margin-top: 10px">
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
        <topic-editor :show="editor" @close="editor=false" @success="editor=false"/>
     </div>
  </div>
</template>

<style lang="less" scoped>
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
.dark .create-topic{
  background-color: #232323;
}
</style>