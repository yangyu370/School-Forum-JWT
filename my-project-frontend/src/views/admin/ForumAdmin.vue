<script setup lang="ts">
import {Document, View} from "@element-plus/icons-vue";
import {reactive, watchEffect} from "vue";
import {apiAdminTopicList,apiForumTopic,apiAdminSetTop,apiAdminDeleteTopic} from "@/net/api/forum"
import {useStore} from "@/store";
import ColorDot from "@/components/ColorDot.vue";
import {QuillDeltaToHtmlConverter} from "quill-delta-to-html";
import {ElMessage, ElMessageBox} from "element-plus";
const listTable=reactive({
  page: 1,
  size: 10,
  total: 0,
  data:[]
})
const store=useStore()
store.loadForumTypes()
watchEffect(()=>apiAdminTopicList(listTable.page,listTable.size,data => {
  listTable.total=data.total;
  listTable.data=data.list;
}))
const editor=reactive({
  id:0,
  display:false,
  temp:{},
  top:false,
  loading:false,
  role:''
})
function openEditor(id,top,role){
  editor.id=id;
  editor.display=true;
  editor.loading=true;
  editor.top=top;
  editor.role=role;
  apiForumTopic(id,data => {
    editor.temp=data;
    editor.loading=false;
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
  const currentStatus = editor.top
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
    apiAdminSetTop(editor.id, newStatus, () => {
      // 更新表格中的数据
      const item = listTable.data.find(t => t.id === editor.id)
      if (item) {
        item.top = newStatus ? 1 : 0
      }
      editor.top = newStatus
      ElMessage.success(`${action}成功`)
      editor.display = false
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
    apiAdminDeleteTopic(editor.id, () => {
      ElMessage.success('删除成功')
      editor.display = false
      const index = listTable.data.findIndex(t => t.id === editor.id)
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
</script>
<template>
  <div class="forum-admin">
    <div class="title">
      <el-icon><Document/></el-icon>
      论坛帖子列表
    </div>
    <div class="desc">
      管理论坛中的所有帖子，包括帖子的查看，删除以及设置置顶帖子
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
       <el-table-column label="标题" width="350" prop="title"/>
       <el-table-column label="创建时间" width="180">
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
       <el-table-column label="操作" width="100" align="center">
         <template #default="{row}">
           <el-button :icon="View" size="small" type="primary" @click="openEditor(row.id,row.top,row.role)">查看</el-button>
         </template>
       </el-table-column>
    </el-table>
    <el-pagination class="pagination" layout="total,sizes,prev,pager,next,jumper"
                   :total="listTable.total"
                   v-model:current-page="listTable.page"
                   v-model:page-size="listTable.size"/>
    <el-drawer v-model="editor.display">
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
        <div class="topic-meta" v-if="editor.temp.user">
          <div style="display: flex;align-items: center;gap: 10px">
            <el-avatar :size="40" :src="store.avatarUserUrl(editor.temp.user.avatar)"/>
            <div style="font-weight: bold">{{ editor.temp.user.username }}</div>
          </div>
          <div style="margin-left: 10px;gap: 5px;">
            <div style="font-size: 12px; color: grey">
              发布于 {{ new Date(editor.temp.time).toLocaleString() }}
            </div>
          </div>
        </div>
        <div class="topic-title">
          {{editor.temp.title}}
        </div>
         <div class="topic-content" v-html="ConvertToHtml(editor.temp.content)"/>
        <img :src="editor.temp.image" v-if="editor.temp.image">
      </div>
      <template #footer>
        <el-button type="primary" @click="toggleTop">
          {{ editor.top ? '取消置顶' : '置顶' }}
        </el-button>
        <el-button type="danger" :disabled="editor.role=='admin'" @click="DeleteTopic">删除</el-button>
      </template>
    </el-drawer>
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
}
</style>