<script setup lang="ts">
defineProps({
  name:String,
  checkName:String,
  color:String,
  checked:Boolean,
})
const emit = defineEmits(['check'])
</script>

<template>
  <div class="interact-button">
     <span class="icon" :style="{'color': checked ? color:'unset'}" @click="emit('check')">
        <slot/>
     </span>
    <span class="name" :style="{'color': checked ? color: 'unset'}" @click="emit('check')">
         {{checked? checkName : name}}
    </span>
  </div>
</template>

<style scoped>
.interact-button {
  display: inline-block;
  height: 20px;
  .name{
    opacity: 0.7;
    margin-left: 5px;
    font-size: 13px;
  }
  .icon{
    vertical-align: middle;
    transition: .3s;
    &:hover{
      cursor: pointer;
      font-size: 20px;
    }
  }

}

</style>