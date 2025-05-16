<script setup>
import {User} from '@element-plus/icons-vue'
import {Lock} from '@element-plus/icons-vue'
import {login} from "@/net/index.js";
import {reactive, ref} from "vue";
import router from "@/router/index.js";
const formRef=ref()
const form=reactive(
    {
      username:'',
      password:'',
      remember:false
    }
)
const rule={
  username:[
    {required:true,message:'请输入用户名'}
  ],
  password:[
    {required:true,message:'请输入密码'}
  ]
}
function userLogin(){
  formRef.value.validate((valid)=>{
    if(valid){
      login(form.username,form.password,form.remember,success=>{router.push('/index')})
    }
  })
}
</script>
<template>
   <div style="text-align:center;margin: 0 20px;">
     <div style="margin-top:150px">
         <div style="font-size:40px;font-weight:bold">登录</div>
         <div style="font-size: 14px;color:gray;margin-top: 40px">请输入用户名和密码进行登录</div>
     </div>
     <div style="margin-top:30px;margin-bottom:40px">
         <el-form :model="form" :rules="rule" ref="formRef">
           <el-form-item prop="username" >
             <el-input v-model="form.username" maxlength="20" type="text" placeholder="请输入用户名或邮箱">
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
             </el-input>
           </el-form-item>
           <el-form-item prop="password">
              <el-input v-model="form.password" maxlength="20" type="password" placeholder="请输入密码">
                   <template #prefix>
                     <el-icon><Lock /></el-icon>
                   </template>
              </el-input>
           </el-form-item>
           <el-row>
             <el-col :span="12" style="text-align:left">
               <el-form-item prop="remember">
                  <el-checkbox v-model="form.remember" label="记住我"/>
               </el-form-item>
             </el-col>
             <el-col :span="12" style="text-align:right">
                <el-link @click="router.push('/reset') ">忘记密码？</el-link>
             </el-col>
           </el-row>
         </el-form>
     </div>
     <div style="margin-top:30px">
       <el-button @click="userLogin" style="width: 270px" type="primary">登录</el-button>
     </div>
     <el-divider style="margin-top:30px;margin-bottom:40px">
       <span style="font-size:15px;color:gray">还没有账号?</span>
     </el-divider>
     <div>
       <el-button @click="router.push('/register')" type="warning" style="width: 270px" plain>立即注册</el-button>
     </div>
   </div>
</template>

<style scoped>

</style>