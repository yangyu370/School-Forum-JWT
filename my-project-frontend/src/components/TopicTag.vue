<script setup lang="ts">
import {useStore} from "@/store/index.js";
const store=useStore()
defineProps({
  type:Number,

})
</script>

<template>
  <div class="topic-type" :style="{color: store.findTypeById(type)?.color+'EE',
               'border-color':store.findTypeById(type)?.color+'77',
               'background':store.findTypeById(type)?.color +'33'}" style="padding-top: 2px">
    {{store.findTypeById(type).name}}
</div>
</template>

<style scoped>
.topic-type{
  display: inline-block;
  border-radius: 5px;
  font-size: 12px;
  padding:0 12px;
  border: 0.5px grey solid;
  height: 18px;
  margin-right: 5px;
}
</style>