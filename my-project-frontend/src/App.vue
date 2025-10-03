<script setup>
import {useDark, useToggle} from "@vueuse/core";
import {onMounted, provide, ref} from "vue";
import {unauthorized} from "@/net/index.js";
import {apiUserInfo} from "@/net/api/user.js";
useDark({
  selector:'html',
  attribute:'class',
  valueDark:'dark',
  valueLight:'light'
})
useDark({
  onChanged(dark){useToggle(dark)}
})
const loading=ref(true)
provide('userLoading',loading)
onMounted(() => {
  if(!unauthorized()) {
    apiUserInfo(loading)
  }
})

</script>

<template>
 <div>
   <router-view></router-view>
 </div>
</template>

<style scoped>

</style>
