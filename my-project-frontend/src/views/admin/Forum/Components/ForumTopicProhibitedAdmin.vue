<script setup>
import {ElMessage} from "element-plus";
import {apiForumProhibitedList, apiForumSaveProhibitedList} from "@/net/api/forum.js";
import {ref} from "vue";
const prohibitedWords=ref('')
const saveProhibitedWords=()=>{
  const list=prohibitedWords.value.split(',')
  apiForumSaveProhibitedList(list,()=>{
    ElMessage.success("违禁词列表更新成功")
  })
}
apiForumProhibitedList(data=>prohibitedWords.value=data.join(','))
</script>

<template>
  <div class="prohibited-input">
    <div class="title">
      违禁词管理
    </div>
    <div class="desc">
      所有存在违禁词的帖子和评论都将被限制，使用逗号隔开
    </div>
    <el-input type="textarea" :rows="8" v-model="prohibitedWords"/>
    <div style="text-align: right;margin-top:20px">
      <el-button type="primary" @click="saveProhibitedWords">保存违禁词列表</el-button>
    </div>
  </div>
</template>
<style scoped>
.title{
  font-weight: bold;
}
.desc{
  color: #bababa;
  font-size: 13px;
  margin-bottom: 20px;
}
.prohibited-input{
  margin-top: 50px;
}
</style>