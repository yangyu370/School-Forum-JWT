<script setup lang="ts">
import {ChatSquare, Delete, Document, View, Loading, Plus, Lock, Hide, Unlock, Search} from "@element-plus/icons-vue";
import {reactive, watchEffect,ref} from "vue";
import {apiAdminTopicList,apiForumTopic,apiAdminSetTop,
  apiAdminDeleteTopic,apiForumComments,apiAdminDeleteComment,
  apiForumTopicLocked,apiForumTopicInvisible,apiForumProhibitedList,apiForumSaveProhibitedList} from "@/net/api/forum"
import {useStore} from "@/store";
import ColorDot from "@/components/ColorDot.vue";
import {QuillDeltaToHtmlConverter} from "quill-delta-to-html";
import {ElMessage, ElMessageBox} from "element-plus";
import AnnouncementEditor from "@/components/AnnouncementEditor.vue";
const listTable=reactive({
  page: 1,
  size: 10,
  total: 0,
  data:[]
})
const prohibitedWords=ref('')
const keyword=ref('')
const searchText=ref('')
const refreshList=()=>{
  apiAdminTopicList(listTable.page,listTable.size,keyword.value,data=>{
    listTable.data=data.list;
    listTable.total=data.total;
  })
}
const store=useStore()
store.loadForumTypes()
watchEffect(()=>apiAdminTopicList(listTable.page,listTable.size,keyword.value,data => {
  listTable.total=data.total;
  listTable.data=data.list;
}))
const topic=reactive({
  id:0,
  display:false,
  temp:{},
  top:false,
  loading:false,
  role:''
})
function openEditor(id,top,role){
  topic.id=id;
  topic.display=true;
  topic.loading=true;
  topic.top=top;
  topic.role=role;
  apiForumTopic(id,data => {
    topic.temp=data;
    topic.loading=false;
  })
}
function ConvertToHtml(content) {
  if (!content) return '<p style="color: grey;">暂无内容</p>'
  try {
    const ops = JSON.parse(content).ops
    if (!ops || ops.length === 0) return '<p style="color: grey;">内容为空</p>'
    const converter = new QuillDeltaToHtmlConverter(ops, { inlineStyles: true })
    return converter.convert()
  } catch (e) {
    console.error('内容转换失败:', e, content)
    return '<p style="color: red;">内容格式错误</p>'
  }
}
function toggleTop() {
  const currentStatus = topic.top
  const newStatus = !currentStatus
  const action = newStatus ? '置顶' : '取消置顶'
  ElMessageBox.confirm(
      `确定要${action}这篇帖子吗？`,
      `${action}确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(() => {
    apiAdminSetTop(topic.id, newStatus, () => {
      // 更新表格中的数据
      const item = listTable.data.find(t => t.id === topic.id)
      if (item) {
        item.top = newStatus ? 1 : 0
      }
      topic.top = newStatus
      ElMessage.success(`${action}成功`)
      topic.display = false
    })
  }).catch(() => {

  })
}
function DeleteTopic(){
  ElMessageBox.confirm(
      `确认要删除该帖子吗?`,
      `删除确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(() => {
    apiAdminDeleteTopic(topic.id, () => {
      ElMessage.success('删除成功')
      topic.display = false
      const index = listTable.data.findIndex(t => t.id === topic.id)
      if (index !== -1) {
        listTable.data.splice(index, 1)
      }
      listTable.total--
      if (listTable.data.length === 0 && listTable.page > 1) {
        listTable.page--
      }
    })
  }).catch(() => {

  })
}
const lockTopic=(id,status) =>{
   const action = status ? '锁定' : '解锁'
   apiForumTopicLocked(id, status, ()=>{
     ElMessage.success(`帖子${action}成功`)
     // 更新列表中对应的项
     const item = listTable.data.find(t => t.id === id)
     if (item) {
       item.locked = status
     }
   })
}
const invisibleTopic=(id,status) =>{
  const action = status ? '屏蔽' : '取消'
  apiForumTopicInvisible(id, status, ()=>{
    ElMessage.success(`帖子${action}成功`)
    const item = listTable.data.find(t => t.id === id)
    if (item) {
      item.invisible = status
    }
  })
}
const comment=reactive({
  display:false,
  loading:false,
  loadingMore:false,
  id:0,
  page:0,
  temp:[],
  hasMore:true
})
const saveProhibitedWords=()=>{
    const list=prohibitedWords.value.split(',')
    apiForumSaveProhibitedList(list,()=>{
      ElMessage.success("违禁词列表更新成功")
    })
}
function openComment(id){
  comment.display=true;
  comment.loading=true;
  comment.id=id;
  comment.page=0;  // 重置页码为0（后端会+1变成第1页）
  comment.temp=[];  // 清空旧数据
  comment.hasMore=true;  // 重置hasMore状态
  apiForumComments(id,comment.page,data=>{
    comment.temp=data;
    comment.loading=false;
    // 如果返回数据少于10条，说明没有更多了
    comment.hasMore = data && data.length >= 10;
  })
}
// 加载更多评论
function loadMoreComments(){
  if(comment.loadingMore || !comment.hasMore) return;
  comment.loadingMore=true;
  comment.page++;
  apiForumComments(comment.id,comment.page,data=>{
    if(data && data.length > 0){
      comment.temp.push(...data);
      // 如果返回数据少于10条，说明没有更多了
      comment.hasMore = data.length >= 10;
    }else{
      comment.hasMore=false;
    }
    comment.loadingMore=false;
  })
}
// 评论区滚动事件处理
function handleCommentScroll(event){
  const target = event.target;
  const scrollHeight = target.scrollHeight;
  const scrollTop = target.scrollTop;
  const clientHeight = target.clientHeight;
  // 距离底部50px时触发加载
  if(scrollHeight - scrollTop - clientHeight < 50 && comment.hasMore && !comment.loadingMore){
    loadMoreComments();
  }
}
function deleteComment(id){
  ElMessageBox.confirm(
      `确定要删除该评论吗`,
      `删除确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(
      ()=>{
        apiAdminDeleteComment(id,()=>{
          ElMessage.success('删除成功')
          // 删除成功后重新加载所有评论
          comment.page=0;
          comment.temp=[];
          comment.hasMore=true;
          apiForumComments(comment.id, comment.page, data => {
            comment.temp = data;
            comment.hasMore = data && data.length >= 10;
          })
        })
      }
  ).catch(() => {
  })
}
const editor=ref(false);
apiForumProhibitedList(data=>prohibitedWords.value=data.join(','))
</script>
<template>
  <div class="forum-admin">
    <div style="display: flex;justify-content: space-between;align-items:end">
      <div>
        <div class="title">
          <el-icon><Document/></el-icon>
          论坛帖子列表
        </div>
        <div class="desc">
          管理论坛中的所有帖子，包括帖子的查看，删除以及设置置顶帖子
        </div>
      </div>
      <div class="search-container">
        <el-input 
          v-model="searchText"
          placeholder="搜索帖子标题..." 
          clearable
          class="search-input">
          <template #prefix>
            <el-icon><Search/></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="keyword=searchText">搜索</el-button>
      </div>
      <div style="margin-bottom: 20px">
        <el-button :icon="Plus" type="primary" @click="editor=true">发布论坛公告</el-button>
      </div>
    </div>

    <el-table :data="listTable.data" height="600">
       <el-table-column prop="id" label="帖子编号 " width="80"/>
       <el-table-column label="用户名" width="100">
         <template #default="{row}">
           <div class="table-username">
             <el-avatar :size="30" :src="store.avatarUserUrl(row.avatar)"/>
             <div>{{row.username}}</div>
           </div>
         </template>
       </el-table-column>
       <el-table-column label="角色" width="120" align="center">
         <template #default="{row}">
           <el-tag type="danger" v-if="row.role==='admin'">管理员</el-tag>
           <el-tag type="primary" v-else>普通用户</el-tag>
         </template>
       </el-table-column>
       <el-table-column label="标题" width="200" prop="title" show-overflow-tooltip/>
       <el-table-column label="创建时间" width="170" show-overflow-tooltip>
         <template #default="{row}">
           {{new Date(row.time).toLocaleString()}}
         </template>
       </el-table-column>
      <el-table-column label="置顶状态" align="center" width="100">
        <template #default="{row}">
          <el-tag type="success" v-if="row.top > 0">置顶</el-tag>
          <el-tag type="primary" v-else>未置顶</el-tag>
        </template>
      </el-table-column>
       <el-table-column label="板块" width="180" align="center">
            <template #default="{row}">
              <el-tag type="info" effect="light">
                <color-dot :color="store.findTypeById(row.type).color"/>
                {{store.findTypeById(row.type).name}}
              </el-tag>
            </template>
       </el-table-column>
       <el-table-column label="操作" width="250" align="center" fixed="right">
         <template #default="{row}">
           <el-button :icon="View" size="small" type="primary" @click="openEditor(row.id,row.top,row.role)">查看</el-button>
           <el-button :icon="Unlock" size="small" type="warning" @click="lockTopic(row.id,false)" v-if="row.locked">解锁</el-button>
           <el-button :icon="Lock" size="small" type="warning" @click="lockTopic(row.id,true)" plain v-else>锁定</el-button>
           <el-button :icon="View" size="small" type="info" @click="invisibleTopic(row.id,false)" v-if="row.invisible">取消</el-button>
           <el-button :icon="Hide" size="small" type="info" @click="invisibleTopic(row.id,true)" plain v-else>屏蔽</el-button>
         </template>
       </el-table-column>
    </el-table>
    <el-pagination class="pagination" layout="total,sizes,prev,pager,next,jumper"
                   :total="listTable.total"
                   v-model:current-page="listTable.page"
                   v-model:page-size="listTable.size"/>
    <div class="prohibited-input">
      <div class="title">
        违禁词管理
      </div>
      <div class="desc">
        所有存在违禁词的帖子和评论都将被限制，使用逗号隔开
      </div>
        <el-input type="textarea" :rows="8" v-model="prohibitedWords"/>
      <div style="text-align: right;margin-top:20px">
        <el-button type="primary" @click="saveProhibitedWords">保存违禁词列表</el-button>
      </div>
    </div>
    <el-drawer v-model="topic.display" size="30%" class="topic-drawer">
       <template #header>
         <div>
           <div style="font-weight:bold">
             <el-icon><Document/></el-icon> 查看帖子详情
           </div>
           <div style="font-size:13px;color: gray;margin-top: 5px">
             可删除帖子或评论以及对帖子进行置顶
           </div>
         </div>
       </template>
      <div>
        <div class="topic-meta" v-if="topic.temp.user">
          <div style="display: flex;align-items: center;gap: 10px">
            <el-avatar :size="40" :src="store.avatarUserUrl(topic.temp.user.avatar)"/>
            <div style="font-weight: bold">{{ topic.temp.user.username }}</div>
          </div>
          <div style="margin-left: 10px;gap: 5px;">
            <div style="font-size: 12px; color: grey">
              发布于 {{ new Date(topic.temp.time).toLocaleString() }}
            </div>
          </div>
        </div>
        <div class="topic-title">
          {{ topic.temp.title }}
        </div>
         <div class="topic-content" v-html="ConvertToHtml(topic.temp.content)"/>
        <img :src="topic.temp.image" v-if="topic.temp.image">
      </div>
      <template #footer>
        <div style="display: flex; justify-content: space-between;">
          <el-button @click="openComment(topic.id)" :icon="ChatSquare">
            评论区
          </el-button>
          <div>
            <el-button type="primary" @click="toggleTop">
              {{ topic.top ? '取消置顶' : '置顶' }}
            </el-button>
            <el-button type="danger" :disabled="topic.role=='admin'" @click="DeleteTopic">删除</el-button>
          </div>
        </div>
      </template>
    </el-drawer>
    <el-drawer v-model="comment.display" size="30%" class="comment-drawer">
      <template #header>
        <div>
          <div style="font-weight:bold">
            <el-icon><ChatSquare/></el-icon> 评论区详情
          </div>
          <div style="font-size:13px;color: gray;margin-top: 5px">
            查看或删除该帖子的评论（共{{comment.temp.length}}条）
          </div>
        </div>
      </template>
      <div class="comment-list-container" @scroll="handleCommentScroll">
        <el-skeleton :loading="comment.loading" animated :rows="5">
          <div v-if="comment.temp && comment.temp.length > 0">
            <div v-for="(item, index) in comment.temp"
                 :key="item.id"
                 class="comment-item">
              <div class="comment-header">
                <el-avatar :size="40" :src="store.avatarUserUrl(item.user.avatar)"/>
                <div class="comment-user-info">
                  <div class="comment-username">
                    {{ item.user.username }}
                    <el-tag type="danger" size="small" style="margin-left: 8px" v-if="item.user.role === 'admin'">
                      管理员
                    </el-tag>
                  </div>
                  <div class="comment-time">{{ new Date(item.time).toLocaleString() }}</div>
                </div>
                <div style="margin-left:auto">
                  <el-button type="danger" plain="plain" :icon="Delete" :disabled="item.user.role==='admin'" @click="deleteComment(item.id)">删除</el-button>
                </div>
              </div>
              <div v-if="item.quote" class="comment-quote">
                回复:{{item.quote}}
              </div>
              <div class="comment-content" v-html="ConvertToHtml(item.content)"/>
              <el-divider v-if="index < comment.temp.length - 1"/>
            </div>
            <!-- 加载更多指示器 -->
            <div v-if="comment.loadingMore" class="loading-more">
              <el-icon class="is-loading"><Loading /></el-icon>
              <span style="margin-left: 8px">加载中...</span>
            </div>
            <div v-else-if="!comment.hasMore && comment.temp.length > 0" class="no-more">
              没有更多评论了
            </div>
          </div>
          <el-empty v-else description="暂无评论" :image-size="100"/>
        </el-skeleton>
      </div>
    </el-drawer>
    <AnnouncementEditor :show="editor" @close="editor=false" @success="editor=false"/>
  </div>
</template>
<style scoped>
.forum-admin {
  .title{
    font-weight: bold;
  }
  .desc{
    color: #bababa;
    font-size: 13px;
    margin-bottom: 20px;
  }
  .table-username{
    height: 40px;
    display: flex;
    align-items: center;
    gap: 10px;
  }
  .pagination{
    margin-top: 20px;
    display: flex;
    justify-content: right;
  }
  .topic-title{
    font-size: 20px;
    font-weight: bold;
    margin-top: 20px;
    margin-bottom: 10px;
  }
  .comment-drawer :deep(.el-drawer) {
    background-color: #909399;
  }

  .comment-drawer :deep(.el-drawer__body) {
    background-color: #909399;
    padding: 0;
    overflow: hidden;
  }

  .comment-list-container {
    height: 100%;
    overflow-y: auto;
    padding: 20px;
  }

  .comment-item {
    padding: 15px 0;
  }

  .comment-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 10px;
  }

  .comment-user-info {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .comment-username {
    font-weight: bold;
    font-size: 14px;
    display: flex;
    align-items: center;
  }

  .comment-time {
    font-size: 12px;
    color: #909399;
  }

  .comment-content {
    margin-left: 52px;
    font-size: 14px;
    line-height: 1.6;
    color: #333;
  }
  .comment-quote {
    font-size: 13px;
    color: grey;
    background-color: rgba(94, 94, 94, 0.1);
    padding: 10px;
    margin-top: 10px;
    margin-left: 52px;
    border-radius: 5px;
  }

  .loading-more {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 20px;
    color: #909399;
    font-size: 14px;
  }

  .no-more {
    text-align: center;
    padding: 20px;
    color: #909399;
    font-size: 13px;
  }
  
  .search-container {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 20px;
  }
  
  .search-input {
    width: 300px;
  }
  
  .search-input :deep(.el-input__wrapper) {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
  }
  
  .search-input :deep(.el-input__wrapper:hover) {
    box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
  }
  
  .search-input :deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 2px 12px rgba(64, 158, 255, 0.3);
  }
}
.prohibited-input{
  margin-top: 50px;
}
/* 只针对 topic-drawer 和 comment-drawer 的样式，不影响 AnnouncementEditor */
:deep(.topic-drawer.el-drawer),
:deep(.comment-drawer.el-drawer) {
  margin: 10px;
  height: calc(100% - 20px);
  border-radius: 10px;
}

:deep(.topic-drawer .el-drawer__header),
:deep(.comment-drawer .el-drawer__header) {
  margin-bottom: 10px;
}
</style>