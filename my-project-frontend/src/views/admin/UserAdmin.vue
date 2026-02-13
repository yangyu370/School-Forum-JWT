<script setup lang="ts">
import {EditPen, Unlock, User, Search} from "@element-plus/icons-vue";
import {apiUserList,apiUserModifyPassword} from "@/net/api/user"
import {reactive, watchEffect,ref} from "vue";
import {useStore} from "@/store"
import UserEditor from "@/components/UserEditor.vue";
import {ElMessage, ElMessageBox} from "element-plus";
const userTable=reactive({
   page: 1,
   size: 10,
   total: 0,
   data:[]
})
const store=useStore()
const editorRef=ref()
const keyword=ref('')
const searchText=ref('')


watchEffect(()=>apiUserList(userTable.page,userTable.size,keyword.value,data => {
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
function changePassword({ id, username}) {
  ElMessageBox.prompt(`您确定要修改用户 ${username} 的密码吗，修改后用户将不能使用原密码登录？`, '修改密码', {
    inputPattern: /^.{6,20}$/,
    inputErrorMessage: '密码长度必须在6-20个字符之间',
    callback: ({ action, value }) => {
      if(action === 'confirm') {
        apiUserModifyPassword({id, newPassword: value},
            () => ElMessage.success('密码修改成功'))
      }
    }
  })
}


</script>

<template>
 <div class="user-admin">
    <div style="display: flex;justify-content: space-between;align-items: end">
      <div>
        <div class="title">
          <el-icon><User/></el-icon>
          论坛用户列表
        </div>
        <div class="desc">
          管理论坛中所有的用户，包括账号信息，封禁和禁言处理。
        </div>
      </div>
      <div class="search-container">
        <el-input 
          v-model="searchText"
          placeholder="搜索用户名或邮箱..." 
          clearable
          class="search-input">
          <template #prefix>
            <el-icon><Search/></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="keyword=searchText">搜索</el-button>
      </div>
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
               <el-button type="warning" size="small" :icon="Unlock"
                          @click="changePassword(row)"
                          :disabled="row.role==='admin'">修改密码</el-button>
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
:deep(.el-drawer){
  margin:10px;
  height: calc(100% - 20px);
  border-radius: 10px;
}
</style>