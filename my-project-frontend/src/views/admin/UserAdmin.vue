<script setup lang="ts">
import {EditPen, User} from "@element-plus/icons-vue";
import {apiUserList} from "@/net/api/user"
import {reactive, watchEffect,ref} from "vue";
import {useStore} from "@/store"
import UserEditor from "@/components/UserEditor.vue";
const userTable=reactive({
   page: 1,
   size: 10,
   total: 0,
   data:[]
})
const store=useStore()
const  editorRef=ref()

watchEffect(()=>apiUserList(userTable.page,userTable.size,data => {
   userTable.total=data.total;
   userTable.data=data.list;
}))

function userStatus(user){
  if(user.mute&&user.banned)
    return '禁言中，封禁中'
  else if(user.mute)
    return '禁言中'
  else if(user.banned)
    return '封禁中'
  else
    return '正常'
}

function handleEditUser(row) {
  if (editorRef.value) {
    editorRef.value.openUserEditor(row)
  } else {
    console.error('editorRef is not available')
  }
}

</script>

<template>
 <div class="user-admin">
    <div class="title">
      <el-icon><User/></el-icon>
      论坛用户列表
    </div>
    <div class="desc">
      管理论坛中所有的用户，包括账号信息，封禁和禁言处理。
    </div>
    <el-table :data="userTable.data" height="320">
         <el-table-column prop="id" label="编号 " width="80"/>
         <el-table-column label="用户名" width="180">
            <template #default="{row}">
              <div class="table-username">
                <el-avatar :size="30" :src="store.avatarUserUrl(row.avatar)"/>
                <div>{{row.username}}</div>
              </div>
            </template>
         </el-table-column>
         <el-table-column label="角色" align="center" width="100">
            <template #default="{row}">
              <el-tag type="danger" v-if="row.role==='admin'">管理员</el-tag>
              <el-tag type="primary" v-else>普通用户</el-tag>
            </template>
         </el-table-column>
         <el-table-column label="邮箱" prop="email" width="200" show-overflow-tooltip/>
         <el-table-column label="注册时间" width="180"  show-overflow-tooltip>
           <template #default="{row}">
             {{new Date(row.registerTime).toLocaleString()}}
           </template>
         </el-table-column>
         <el-table-column label="状态" width="200" align="center">
            <template #default="{row}">
              {{userStatus(row)}}
            </template>
         </el-table-column>
         <el-table-column label="操作" align="center" fixed="right">
             <template #default="{row}">
               <el-button type="primary" size="small" :icon="EditPen"
                          @click="handleEditUser(row)"
                          :disabled="row.role==='admin'">编辑</el-button>
             </template>
         </el-table-column>
    </el-table>
    <el-pagination class="pagination" layout="total,sizes,prev,pager,next,jumper"
                   :total="userTable.total"
                   v-model:current-page="userTable.page"
                   v-model:page-size="userTable.size"/>
   <user-editor :user-table="userTable" ref="editorRef"/>
 </div>
</template>

<style lang="less" scoped>
.user-admin {
  .title{
    font-weight: bold;
  }
  .desc {
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
  :deep(.el-drawer__header){
    margin-bottom: 0;
  }
}
:deep(.el-drawer){
  margin:10px;
  height: calc(100% - 20px);
  border-radius: 10px;
}
</style>