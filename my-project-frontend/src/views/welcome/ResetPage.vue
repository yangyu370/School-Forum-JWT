<script setup>
import {ref, reactive, computed} from "vue";
import {EditPen, Lock, Message} from "@element-plus/icons-vue";
import {apiAuthAskCode, apiAuthResetPassword, apiAuthRestConfirm} from "@/net/api/user.js";
const active=ref(0)
const coldTime=ref(0)
const formRef=ref()
const form=reactive({
  email:'',
  code:'',
  password:'',
  password_repeat:''
})
const validateEmail = () => apiAuthAskCode(form.email, coldTime, 'reset')
const confirmReset = () => {
  formRef.value.validate((isValid) => {
    if(isValid) {
      apiAuthRestConfirm({
        email: form.email,
        code: form.code
      }, active)
    }
  })
}
const doReset = () => {
  formRef.value.validate((isValid) => {
    if(isValid) {
      apiAuthResetPassword({
        email: form.email,
        code: form.code,
        password: form.password
      })
    }
  })
}
const validatePassword=(rule,value,callback)=>{
  if(value===' '){
    callback(new Error("请再次输入密码"))
  }else if(value!==form.password){
    callback(new Error('两次输入的密码不一致'))
  }else
    callback()
}
const rules={
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

const isEmailValidate=computed(()=>{
  return /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(form.email)
})
</script>

<template>
<div style="text-align: center">
  <div style="margin-top: 50px">
    <el-steps :active="active" finish-status="success" align-center>
         <el-step title="验证电子邮件"/>
         <el-step title="重新设置密码"/>
    </el-steps>
  </div>
  <div style="margin: 0 20px;" v-if="active===0">
     <div style="margin-top: 80px">
         <div style="font-size:25px;font-weight: bold">重置密码</div>
         <div style="font-size: 14px;color: gray;margin-top: 15px">请输入需要重置密码的电子邮件地址</div>
         <div style="margin-top: 50px">
            <el-form :model="form" :rules="rules" ref="formRef">
               <el-form-item prop="email">
                  <el-input v-model="form.email" type="email" placeholder="请输入电子邮件地址">
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
         <el-button @click="confirmReset" style="width:270px" type="primary">
            开始重置
         </el-button>
       </div>
     </div>
  </div>
  <div style="margin: 0 20px;" v-if="active===1">
    <div style="margin-top: 80px">
      <div style="font-size:25px;font-weight: bold">重置密码</div>
      <div style="font-size: 14px;color: gray;margin-top: 15px">请输入您的新密码,务必牢记,防止丢失</div>
      <div style="margin-top: 50px">
         <el-form :model="form" :rules="rules" ref="formRef">
           <el-form-item prop="password">
             <el-input v-model="form.password" maxlength="20" type="password" placeholder="请输入密码">
               <template #prefix>
                 <el-icon><Lock /></el-icon>
               </template>
             </el-input>
           </el-form-item>
           <el-form-item style="margin-top: 10px"prop="password_repeat">
             <el-input v-model="form.password_repeat" maxlength="20" type="password" placeholder="请重新输入密码">
               <template #prefix>
                 <el-icon><Lock /></el-icon>
               </template>
             </el-input>
           </el-form-item>
         </el-form>
      </div>
      <div style="margin-top: 50px">
        <el-button @click="doReset" style="width:270px" type="primary">
           立即重置密码
        </el-button>
    </div>
  </div>
</div>
</div>
</template>

<style scoped>

</style>