<script setup lang="ts">
import {Document} from "@element-plus/icons-vue";
import {reactive, watchEffect} from "vue";
import {apiAdminTopicList} from "@/net/api/forum"
import {useStore} from "@/store";
const listTable=reactive({
  page: 1,
  size: 10,
  total: 0,
  data:[]
})
const store=useStore()
watchEffect(()=>apiAdminTopicList(listTable.page,listTable.size,data => {
  listTable.total=data.total;
  listTable.data=data.list;
}))
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
       <el-table-column label="查看" width="180" align="center">

       </el-table-column>
       <el-table-column label="删除" width="80" align="center">

       </el-table-column>
    </el-table>
    <el-pagination class="pagination" layout="total,sizes,prev,pager,next,jumper"
                   :total="listTable.total"
                   v-model:current-page="listTable.page"
                   v-model:page-size="listTable.size"/>
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
}
</style>