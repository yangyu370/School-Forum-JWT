<script setup>
import {ref} from "vue";
import {apiAnnouncementList} from "@/net/api/forum.js";
import {convertToHtml} from "@/tools/index.ts";
import axios from "axios";
import LightCard from "@/components/LightCard.vue";

defineProps({
  show: Boolean
})

const emit = defineEmits(['close'])
const list = ref([])
const loading = ref(false)

// 初始化时获取公告列表
function init() {
  loading.value = true
  apiAnnouncementList(data => {
    list.value = data || []
    loading.value = false
  })
}

function getAvatarUrl(avatar) {
  if (avatar) {
    return `${axios.defaults.baseURL}/images${avatar}`
  } else {
    return 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
  }
}
</script>

<template>
  <div>
    <el-drawer :model-value="show" @close="emit('close')" @open="init" title="论坛公告" size="40%">
      <div class="announcement-list" v-loading="loading">
        <!-- 公告列表 -->
        <div v-if="list.length > 0">
          <light-card v-for="item in list" :key="item.id" class="announcement-card" style="margin:10px 0">
            <!-- 公告头部 -->
            <div class="announcement-header">
              <div class="user-info">
                <el-avatar :size="40" :src="getAvatarUrl(item.user?.avatar)"/>
                <div class="user-detail">
                  <div class="username">{{ item.user?.username || '未知用户' }}</div>
                  <div class="time">{{ new Date(item.time).toLocaleString() }}</div>
                </div>
              </div>
              <el-tag type="danger" effect="dark">公告</el-tag>
            </div>
            <!-- 公告标题 -->
            <div class="announcement-title">
              {{ item.title }}
            </div>
            <!-- 公告内容 -->
            <div class="announcement-content" v-html="convertToHtml(item.content)"/>
            <el-divider/>
          </light-card>
        </div>
        <!-- 空状态 -->
        <el-empty v-else description="暂无公告" :image-size="100"/>
      </div>
    </el-drawer>
  </div>
</template>

<style scoped>
.announcement-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 10px;
}

/* 浅色模式 - 卡片样式 */
.announcement-card {
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all .3s ease;
  position: relative;
  overflow: hidden;
}

/* 暗黑模式 - 卡片样式 */
.dark .announcement-card {
  background: linear-gradient(135deg, #2c2c2c 0%, #1f1f1f 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.announcement-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
}

.announcement-card:hover {
  transform: translateY(-4px);
}

.dark .announcement-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.5);
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.user-info .el-avatar {
  border: 2px solid #e4e7ed;
  transition: all .3s;
}

.dark .user-info .el-avatar {
  border: 2px solid #4c4d4f;
}

.announcement-card:hover .el-avatar {
  border-color: #409EFF;
  transform: scale(1.05);
}

.user-detail {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.username {
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.dark .username {
  color: #e5eaf3;
}

.time {
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.dark .time {
  color: #a8abb2;
}

.time::before {
  content: '📅';
  font-size: 12px;
}

.announcement-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 16px;
  color: #303133;
  line-height: 1.4;
  display: flex;
  align-items: center;
  gap: 8px;
}

.dark .announcement-title {
  color: #e5eaf3;
}

.announcement-title::before {
  content: '📢';
  font-size: 18px;
}

.announcement-content {
  font-size: 15px;
  line-height: 1.9;
  color: #606266;
  background-color: rgba(255, 255, 255, 0.6);
  padding: 16px;
  border-radius: 6px;
  border: 1px solid rgba(228, 231, 237, 0.6);
}

.dark .announcement-content {
  color: #c0c4cc;
  background-color: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(76, 77, 79, 0.6);
}

.announcement-content :deep(p) {
  margin: 10px 0;
}

.announcement-content :deep(img) {
  max-width: 100%;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin: 12px 0;
}

.dark .announcement-content :deep(img) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
}

.announcement-content :deep(pre) {
  background-color: #f5f7fa;
  padding: 12px;
  border-radius: 6px;
  overflow-x: auto;
  border: 1px solid #e4e7ed;
  font-size: 13px;
}

.dark .announcement-content :deep(pre) {
  background-color: #1a1a1a;
  border: 1px solid #4c4d4f;
  color: #e5eaf3;
}

.announcement-content :deep(code) {
  background-color: #f5f7fa;
  padding: 2px 6px;
  border-radius: 3px;
  color: #e74c3c;
  font-size: 13px;
}

.dark .announcement-content :deep(code) {
  background-color: #1a1a1a;
  color: #ff6b6b;
}

.announcement-content :deep(blockquote) {
  border-left: 4px solid #409EFF;
  padding-left: 12px;
  margin: 12px 0;
  color: #606266;
  background-color: rgba(64, 158, 255, 0.05);
  padding: 10px 12px;
  border-radius: 4px;
}

.dark .announcement-content :deep(blockquote) {
  color: #c0c4cc;
  background-color: rgba(64, 158, 255, 0.15);
}

.announcement-content :deep(h1),
.announcement-content :deep(h2),
.announcement-content :deep(h3) {
  color: #303133;
  margin: 16px 0 8px;
  font-weight: 600;
}

.dark .announcement-content :deep(h1),
.dark .announcement-content :deep(h2),
.dark .announcement-content :deep(h3) {
  color: #e5eaf3;
}

.announcement-content :deep(a) {
  color: #409EFF;
  text-decoration: none;
  transition: color .3s;
}

.announcement-content :deep(a:hover) {
  color: #66b1ff;
  text-decoration: underline;
}

:deep(.el-drawer__header) {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #e4e7ed;
  font-weight: 700;
  font-size: 18px;
  color: #303133;
}

.dark :deep(.el-drawer__header) {
  border-bottom: 2px solid #4c4d4f;
  color: #e5eaf3;
}

:deep(.el-drawer) {
  margin: 10px;
  height: calc(100% - 20px);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.dark :deep(.el-drawer) {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
}

:deep(.el-divider) {
  margin: 20px 0 0;
  border-top: 1px dashed #e4e7ed;
}

.dark :deep(.el-divider) {
  border-top: 1px dashed #4c4d4f;
}

:deep(.el-tag) {
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
}

:deep(.el-empty) {
  padding: 60px 0;
}
</style>
