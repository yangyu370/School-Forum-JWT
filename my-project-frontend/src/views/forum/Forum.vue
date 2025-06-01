<script setup lang="ts">
import {get} from '@/net'
import {useStore} from "@/store/index.js";
const store = useStore()
get('/api/forum/types',data=>{
  const array=[]
  array.push({name: '全部',id:0,color:'#CE1986F0'})
  data.forEach(d=>{array.push(d)})
  store.forum.types=data
  store.forum.types=array
})
</script>

<template>
  <router-view v-slot="{Component}">
    <transition name="el-fade-in-linear" mode="out-in">
      <keep-alive include="TopicList">
        <component :is="Component" />
      </keep-alive>

    </transition>
  </router-view>
</template>

<style scoped>

</style>