
import { createApp } from 'vue'
import App from './App.vue'
import router from "@/router";
import axios from "axios";
import 'element-plus/dist/index.css'  // 添加这行：导入完整样式
import 'element-plus/theme-chalk/dark/css-vars.css'
import {createPinia} from "pinia";
import '@/assets/quill.css'
axios.defaults.baseURL = 'http://localhost:8080'
const app=createApp(App)
app.use(router)
const pinia = createPinia()
app.use(pinia)
app.mount('#app')
