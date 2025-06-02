<script setup lang="ts">
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import {Delta, QuillEditor} from "@vueup/vue-quill";
import {ref} from "vue";
import {post} from "@/net"
import {ElMessage} from "element-plus";
const props = defineProps({
   show:Boolean,
   tid:String,
   quote:Number,
})
const emit=defineEmits(['close','comment'])
const init = ( )=>content.value=new Delta()
const content=ref()
function  submitComment(){
  post( '/api/forum/add-comment',{
    tid:props.tid,
    quote:props.quote,
    content:JSON.stringify(content.value)
  },()=>{
    ElMessage.success("发表评论成功")
    emit('comment')
  })
}
</script>

<template>
<div>
    <el-drawer :model-value="show" @close="emit('close')"
                title="发表评论"
                @open="init"
                :direction="'btt'" :size="270"
               :close-on-click-modal ="false">
    <div>
        <div>
            <quill-editor style="height:120px" v-model:content="content" placeholder="评论千万条，等你发一条（请友善发言哦～）"/>
        </div>
        <div style="margin-top: 10px;text-align: right">
          <el-button type="primary" @click="submitComment">发表评论</el-button>
        </div>
    </div>
    </el-drawer>
</div>
</template>

<style lang="less" scoped>
:deep(.el-drawer){
  width: 1000px;
  margin: 20px auto;
  border-radius:10px
}
:deep(.el-drawer__header){
  margin: 0;
}
:deep(.el-drawer__body){
  padding: 10px;
}
</style>