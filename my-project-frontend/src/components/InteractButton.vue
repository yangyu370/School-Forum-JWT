<script setup lang="ts">
const props=defineProps({
  name:String,
  checkName:String,
  color:String,
  checked:Boolean,
  disabled: Boolean
})
const emit = defineEmits(['check'])
const onClick= ()=>{
  if(!props.disabled){
    emit('check')
  }

}
</script>

<template>
  <div class="interact-button" :class="{'locked':disabled}">
     <span class="icon" :style="{'color': checked ? color:'unset'}" @click=onClick>
        <slot/>
     </span>
    <span class="name" :style="{'color': checked ? color: 'unset'}" @click=onClick>
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
  &.locked{
    opacity: 0.5;
    .icon:hover{
      font-size:unset;
      cursor:not-allowed;
    }
  }
}

</style>