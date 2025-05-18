<script setup>

import Card from "@/components/Card.vue";
import {Message, Refresh, Select, User} from "@element-plus/icons-vue";
import {useStore} from "@/store/index.js";
import {computed,reactive,ref} from "vue";
import {post,get} from "@/net/index.js";
import {ElMessage} from "element-plus";
const store=useStore()
const registerTime=computed(()=>new Date(store.user.registerTime).toLocaleString())
const baseForm= reactive({
         username: '',
         gender: '1',
         phone:  '',
         qq:    '',
         wx:   '',
         desc: ''
})
const desc=ref('')
const baseFormRef = ref()
const emailFormRef = ref()
const emailForm = reactive(({
      email:'',
      code:''
}))
const validateUsername= (rule ,value,callback) =>{
  if(value=== ''){
    callback(new Error("请输入用户名"))
  }else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error("用户名不能存在特殊字符"))
  }else
    callback()
}

const rules={
    username:[
      { validator : validateUsername,trigger:['blur','change']},
      { min:2,max:8,message:'用户名长度必须在2-8个字符之间',trigger:['blur','change']}
    ],
    email:[
      {type:'email', message:'请输入正确的邮箱地址',trigger:['blur','change']},
      {required:true,message:'邮箱不能为空',trigger:'blur'}
    ],
    code: [
      {required:true,message:'请输入验证码！',trigger:'blur'}
    ]
}
const loading=reactive({
  form: true,
  base: false
})
function saveDetails(){
   baseFormRef.value.validate(isValid=>{

        if(isValid){
           loading.base=true;
           post('api/user/save-details',baseForm,()=>{
                  ElMessage.success("用户信息更改成功!")
                  store.user.username=baseForm.username
                  desc.value=baseForm.desc
                  loading.base=false
           },(message)=>{
              ElMessage.warning(message)
              loading.base=false
           })
        }
   })
}
get('/api/user/details',data=>{
  baseForm.username=store.user.username
  baseForm.gender=data.gender
  baseForm.wx=data.wx
  baseForm.phone=data.phone
  baseForm.qq=data.qq
  baseForm.desc=desc.value=data.desc
  loading.form=false
  emailForm.email=store.user.email
})
const coldTime=ref(0)
const isEmailValidate=computed(()=>{
  return /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(emailForm.email)
})
function askCode(){
  if(isEmailValidate){
    coldTime.value=60
    get(`/api/auth/ask-code?email=${emailForm.email}&type=modify`,()=>{
      ElMessage.success("验证码已发送到您的邮箱,请注意查收!")
      setInterval(()=>coldTime.value--,1000)
    },(message)=>{
      ElMessage.warning(message)
      coldTime.value=0})
  }
  else ElMessage.error("请输入正确的电子邮件格式！")
}
function modifyEmail(){
  emailFormRef.value.validate(isValid=>{
       post('api/user/modify-email',emailForm,()=>{
          ElMessage.success("邮件修改成功！")
          store.user.email=emailForm.email
          emailForm.code=''
       })
  })
}
</script>

<template>
   <div style="display: flex">
         <div class="setting-left">
             <card :icon="User" title="账号信息设置" desc="在这里更改您的个人的信息，你可以在隐私设置中选择是否展示这些信息" v-loading="loading.form">
                <el-form :model="baseForm" :rules="rules" ref="baseFormRef" label-position="top" style="margin: 0 10px 10px 10px">
                   <el-form-item label="用户名" prop="username">
                     <el-input v-model="baseForm.username" maxlength="10"/>
                   </el-form-item>
                  <el-form-item label="性别" prop="gender">
                    <el-radio-group v-model="baseForm.gender">
                       <el-radio :label="0">男</el-radio>
                      <el-radio :label="1">女</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item label="手机号" prop="phone">
                    <el-input v-model="baseForm.phone" maxlength="11"/>
                  </el-form-item>
                  <el-form-item label="QQ号" prop="qq">
                    <el-input v-model="baseForm.qq" maxlength="13"/>
                  </el-form-item>
                  <el-form-item label="微信号" prop="wx">
                    <el-input v-model="baseForm.wx" maxlength="20"/>
                  </el-form-item>
                  <el-form-item label="个人简介" prop="desc">
                    <el-input type="textarea" :rows="7" v-model="baseForm.desc" maxlength="200"/>
                  </el-form-item>
                </el-form>
               <div style="text-align: right;margin-right:10px">
                  <el-button :icon="Select" @click="saveDetails" type="primary" :loading="loading.base">保存</el-button>
               </div>
             </card>
           <card style="margin-top: 10px" :icon="Message" title="电子邮件设置" desc="在这里修改绑定的电子邮件地址">
             <el-form :model="emailForm" :rules="rules"  ref="emailFormRef" label-position="top" style="margin: 0 10px 10px 10px">
               <el-form-item label="电子邮件" style="margin-right: 5px" prop="email">
                 <el-input v-model="emailForm.email"/>
               </el-form-item>
               <el-form-item style="width:100%;margin-right: 0" prop="code">
                  <el-row style="width: 100%;" :gutter="10">
                    <el-col :span="18">
                      <el-input placeholder="请获取验证码" v-model="emailForm.code"></el-input>
                    </el-col>
                    <el-col :span="6" style="width: 100%;padding-left: 1px;padding-right: 1px">
                      <el-button type="primary" style="width: 100%;margin-right: 0;padding-right: 0"  @click="askCode"
                                 :disabled="!isEmailValidate || coldTime > 0"> {{coldTime > 0 ? '请稍后 ' + coldTime + ' 秒' : '获取验证码'}}
                      </el-button>
                    </el-col>
                  </el-row>
               </el-form-item>
             </el-form>
             <div style="text-align: right;margin-right:15px">
                <el-button :icon="Refresh" type="primary" @click="modifyEmail">更新</el-button>
             </div>
           </card>
         </div>
       <div class="setting-right" >
         <div style="position: static;top: 20px">
           <card>
              <div style="text-align: center;padding: 5px 15px 0 15px">
                   <el-avatar :size="70" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
                   <div style=" margin-top: 10px;font-size: 18px;font-weight: bolder">你好,{{store.user.username}}</div>
                   <el-divider style="margin: 10px 10px"/>
                   <div style="font-size: 14px;color: gray;padding: 10px">
                      {{desc || "这个用户很懒，没有填写个人简介~"}}
                   </div>
              </div>
           </card>
           <card style="margin-top: 10px;font-size: 14px">
              <div style="text-align: center" >账号注册时间:{{registerTime}}</div>
              <div style="font-size: 14px;color: gray;text-align: center">欢迎加入我们的学习论坛!</div>
           </card>
         </div>
       </div>
   </div>
</template>

<style scoped>
 .setting-left{
   flex: 1;
   margin: 20px;
 }
 .setting-right{
    width: 300px;
    margin:20px 30px 20px 0;
 }
</style>