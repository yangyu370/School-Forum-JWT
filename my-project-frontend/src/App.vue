<script setup>
import {useDark, useToggle} from "@vueuse/core";
import {onMounted, provide, ref} from "vue";
import {unauthorized} from "@/net/index.js";
import {apiUserInfo} from "@/net/api/user.js";
import zhCn from 'element-plus/es/locale/lang/zh-cn'

useDark({
  selector:'html',
  attribute:'class',
  valueDark:'dark',
  valueLight:'light'
})
useDark({
  onChanged(dark){useToggle(dark)}
})

const loading = ref(true)
provide('userLoading', loading)

onMounted(() => {
  if(!unauthorized()) {
    apiUserInfo(loading)
  } else {
    // 未登录时也要将 loading 设置为 false
    loading.value = false
  }
})

</script>

<template>
  <el-config-provider :locale="zhCn" >
    <div class="wrapper">
      <router-view></router-view>
    </div>
  </el-config-provider>
</template>

<style scoped>
 .wrapper{
   line-height: 1.5;
 }
</style>
