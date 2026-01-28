<script setup>

import {User} from "@element-plus/icons-vue";
</script>

<template>
  <div class="email-admin">
    <div class="title">
        <el-icon><User/></el-icon>
        论坛邮件列表
    </div>
    <div class="desc">
       在这里管理论坛发送的所有邮件，并操作重发
    </div>
  </div>
</template>

<style scoped>
.email-admin{
  .title{
    font-weight: bold;
  }
  .desc{
    color: #bababa;
    font-size: 13px;
    margin-bottom: 20px;
  }
}
</style>