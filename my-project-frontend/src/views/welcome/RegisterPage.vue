<script setup>
import {reactive} from "vue";
import {User} from '@element-plus/icons-vue'
import {Lock,Message,EditPen} from '@element-plus/icons-vue'
import router from "@/router/index.js";
import { ElMessage } from 'element-plus';
import {get} from "@/net/index.js"
import {ref} from "vue";
import {computed} from "vue";
import {post} from "@/net/index.js";
import apis from "@vueup/vue-quill/dist/vue-quill.global.js";
import {apiAuthAskCode, apiAuthRegister} from "@/net/api/user.js";
const coldTime=ref(0)
const formRef=ref()
const  form=reactive(
    {
      username:'',
      password:'',
      password_repeat:'',
      email:'',
      code:''
    }
)
const validateUsername= (rule ,value,callback) =>{
   if(value=== ''){
     callback(new Error("请输入用户名"))
   }else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
     callback(new Error("用户名不能存在特殊字符"))
   }else
     callback()
}
const validatePassword=(rule,value,callback)=>{
   if(value===' '){
     callback(new Error("请再次输入密码"))
   }else if(value!==form.password){
      callback(new Error('两次输入的密码不一致'))
   }else
     callback()
}
const rule={
    username:[
      { validator : validateUsername,trigger:['blur','change']},
      { min:2,max:8,message:'用户名长度必须在2-8个字符之间',trigger:['blur','change']}
    ],
    password:[
      { required:true,message:'密码不能为空!',trigger:'blur'},
      {min:6,max:20,message:'密码的长度必须在2-20个字符之间', trigger:['blur','change']}
    ],
   password_repeat:[
     { required:true,message:'请再次输入密码!',trigger:'blur'},
     {validator: validatePassword,trigger:['blur','change']}
   ],
   email: [
     {type:'email', message:'请输入正确的邮箱地址',trigger:['blur','change']},
     {required:true,message:'邮箱不能为空',trigger:'blur'}
   ],
   code:[
     {required:true,message:'请输入验证码！',trigger:'blur'}
   ]
}
const validateEmail = () => apiAuthAskCode(form.email, coldTime)
const isEmailValidate=computed(()=>{
 return /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(form.email)
})
const register = () => {
  formRef.value.validate((isValid) => {
    if(isValid) {
      apiAuthRegister({
        username: form.username,
        password: form.password,
        email: form.email,
        code: form.code
      })
    } else {
      ElMessage.warning('请完整填写注册表单内容！')
    }
  })
}

</script>

<template>
  <div style="text-align:center;margin: 0 20px;">
    <div>
       <div style="margin-top:100px;font-weight:bold;font-size:40px">注册新用户</div>
       <div style="color: gray;font-size:14px;margin-top: 50px">欢迎注册我们的交流平台，请在下方填写注册信息</div>
    </div>
    <div style="margin-top:50px">
      <el-form :model="form" :rules="rule" ref="formRef">
          <el-form-item prop="username">
             <el-input v-model="form.username" maxlength="10" type="text" placeholder="请输入用户名">
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
        <el-form-item prop="password_repeat">
          <el-input v-model="form.password_repeat" maxlength="20" type="password" placeholder="请重新输入密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" maxlength="20" type="text" placeholder="请输入邮箱地址">
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-row :gutter="10" style="width: 100%">
            <el-col :span="17">
               <el-input v-model="form.code" maxlength="6" minlength="6" type="text" placeholder="请输入验证码">
                   <template #prefix>
                     <el-icon><EditPen /></el-icon>
                   </template>
               </el-input>
            </el-col>
            <el-col :span="5">
              <el-button type="primary" @click="validateEmail"
                         :disabled="!isEmailValidate || coldTime > 0">
                {{coldTime > 0 ? '请稍后 ' + coldTime + ' 秒' : '获取验证码'}}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </div>
    <div style="margin-top: 80px">
      <el-button @click="register" style="width:270px" type="primary">
          立即注册
      </el-button>
    </div>
    <el-divider style="margin-top: 20px">
       <div style="text-align: center;color: gray;font-size: 14px">
          已有账户?&nbsp; <el-link @click="router.push('/')" style="color: gray">立即登录</el-link>
       </div>

    </el-divider>
</div>
</template>

<style scoped>

</style>