<script setup>

import Card from "@/components/Card.vue";
import {Edit, Lock, Refresh, Setting, Unlock} from "@element-plus/icons-vue";
import {reactive,ref} from "vue";
import {post,get} from "@/net/index.js";
import {ElMessage} from "element-plus";

const form=reactive({
   password:'',
   new_password:'',
   new_password_repeat: ''

})
const validatePassword=(rules,value,callback)=>{
  if(value===' '){
    callback(new Error("请再次输入密码"))
  }else if(value!==form.new_password){
    callback(new Error('两次输入的密码不一致'))
  }else
    callback()
}
const rules={
   password:[
     {required:true,message:'请输入密码!',trigger:'blur'}
   ],
  new_password:[
    { required:true,message:'密码不能为空!',trigger:'blur'},
    {min:6,max:20,message:'密码的长度必须在2-20个字符之间', trigger:['blur','change']}
  ],
  new_password_repeat:[
    { required:true,message:'请重新输入密码!',trigger:'blur'},
    {validator: validatePassword,trigger:['blur','change']}
  ]
}
const formRef=ref()
const valid=ref(false)
const onValidate=(prop,isValid)=>valid.value=isValid
function resetPassword(){
   formRef.value.validate(valid=>{
     if(valid){
         post("api/user/change-password",form,()=>{
            ElMessage.success("修改密码成功!")
            formRef.value.resetFields()
         })
     }
   })
}
const saving=ref(false)
const privacy=reactive({
  phone:false,
  wx:false,
  qq:false,
  email:false,
  gender:false,
})
get("/api/user/privacy",data=>{
  privacy.email=data.email
  privacy.wx=data.wx
  privacy.qq=data.qq
  privacy.phone=data.phone
  privacy.gender=data.gender
})
function savePrivacy(type,status){
   saving.value=true;
   post("api/user/save-privacy",{
     type:type,
     status:status
   },()=>{
     ElMessage.success("隐私设置修改成功")
     saving.value=false;
   })
}

</script>

<template>
<div style="margin:auto;max-width: 900px">
  <div style="margin-top: 20px">
   <card :icon="Setting" title="隐私设置" desc="在这里设置个人隐私相关的信息，请各位小伙伴注意保护自己的隐私" v-loading="saving">
      <div class="checkbox-list">
        <el-checkbox @change="savePrivacy('phone',privacy.phone)" v-model="privacy.phone">公开展示手机号</el-checkbox>
        <el-checkbox @change="savePrivacy('email',privacy.email)" v-model="privacy.email">公开展示电子邮件地址</el-checkbox>
        <el-checkbox @change="savePrivacy('wx',privacy.wx)" v-model="privacy.wx">公开展示微信号</el-checkbox>
        <el-checkbox @change="savePrivacy('qq',privacy.qq)" v-model="privacy.qq" >公开展示QQ号</el-checkbox>
        <el-checkbox @change="savePrivacy('gender',privacy.gender)" v-model="privacy.gender">公开展示我的性别</el-checkbox>
      </div>
   </card>
    <card :icon="Edit" title="修改密码" desc="修改密码，请在下方进行操作，请务必牢记您的密码" style="margin: 20px 0">
          <el-form :model="form" label-width="90" ref="formRef" :rules="rules" @validate="onValidate" style="margin: 20px">
             <el-form-item label="当前密码" prop="password">
                <el-input type="password" :prefix-icon="Unlock" placeholder="当前密码" maxlength="20" v-model="form.password"/>
             </el-form-item>
            <el-form-item label="输入新密码" prop="new_password">
              <el-input type="password" :prefix-icon="Lock" placeholder="输入新密码" maxlength="20" v-model="form.new_password"/>
            </el-form-item>
            <el-form-item label="重复新密码" prop="new_password_repeat">
              <el-input type="password" :prefix-icon="Lock" v-model="form.new_password_repeat" placeholder="请重新输入密码" maxlength="20"/>
            </el-form-item>
          </el-form>
          <div style="text-align: right;margin-right: 20px">
              <el-button type="primary" :icon="Refresh" @click="resetPassword">重置密码</el-button>
          </div>
    </card>
  </div>
</div>
</template>
<style scoped>
 .checkbox-list{
    margin: 20px 0 0 10px;
    display: flex;
    flex-direction: column;
 }
</style>