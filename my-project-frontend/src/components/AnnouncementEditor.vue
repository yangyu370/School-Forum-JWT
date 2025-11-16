<script setup>
import {Delta, Quill, QuillEditor} from "@vueup/vue-quill";
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import {Document, Position} from "@element-plus/icons-vue";
import {computed, reactive} from "vue";
import {ImageExtend, QuillWatch} from "quill-image-super-solution-module";
import ImageResize from "quill-image-resize-vue";
import axios from "axios";
import {accessHeader, post} from "@/net/index.js";
import {ElMessage} from "element-plus";
import {ref} from "vue";
import {apiAdminCreateAnnouncement} from "@/net/api/forum"
const props=defineProps({
  show:Boolean,
  defaultTitle:{
    type:String,
    default:''
  },
  defaultText:{
    type:String,
    default:''
  },
  submitButton:{
    default:'立即发表',
    type:String,
  },
  submit: {
    default: (editor, success) => {
      apiAdminCreateAnnouncement({
        title: editor.title,
        content: editor.text
      }, () => {
        ElMessage.success("公告发表成功!")
        success();
      })
    },
    type: Function
  }
})
const emit=defineEmits(['close','success'])
const editor=reactive({
  title:'',
  text:'',
  loading:false,
  uploading:false,
})
function initEditor(){
  if(props.defaultText)
    editor.text=new Delta(JSON.parse(props.defaultText));
  else
    refEditor.value.setContents(' ','user')
  editor.title=props.defaultTitle
}
Quill. register( 'modules/imageResize', ImageResize)
Quill. register( 'modules/ImageExtend', ImageExtend)
function deltaToText(delta){
  if(!delta.ops) return " "
  let str=""
  for (let op of delta.ops)
    str+=op.insert
  return str.replace(/\s/g," ")
}
const contentLength=computed(()=>deltaToText(editor.text).length)
const editorOption={
  modules:{
    toolbar:{
      container:[
        "bold", "italic", "underline", "strike", "clean",
        {color: []}, {'background': []},
        {size: ["small", false, "large", "huge"]},
        { header: [1, 2, 3, 4, 5, 6, false] },
        {list: "ordered"}, {list: "bullet"}, {align: []},
        "blockquote", "code-block", "link", "image",
        {indent: '-1' }, { indent: '+1' }
      ],handlers:{
        'image':function(){
          QuillWatch.emit(this.quill.id)
        }
      }
    },imageResize:{
      modules:['Resize','DisplaySize']
    },ImageExtend: {
      action:  axios.defaults.baseURL + '/api/image/cache',
      name: 'file',
      size: 10,
      loading: true,
      accept: 'image/png, image/jpeg',
      response: (resp) => {
        if(resp.data) {
          return axios.defaults.baseURL + '/images' + resp.data
        } else {
          return null
        }
      },
      methods: 'POST',
      headers: xhr => {
        xhr.setRequestHeader('Authorization', accessHeader().Authorization);
      },
      start: () => editor.uploading = true,
      success: () => {
        ElMessage.success('图片上传成功!')
        editor.uploading = false
      },
      error: () => {
        ElMessage.warning('图片上传失败，请联系管理员!')
        editor.uploading = false
      }
    }

  }
}
const refEditor=ref()
function submitText(){
  const text=deltaToText(editor.text)
  if(text.length>20000){
    ElMessage.warning("字数超出限制，无法发布")
    return
  }
  if(!editor.title){
    ElMessage.warning("标题不能为空！")
    return
  }
  props.submit(editor,()=>emit('success'))
}
</script>

<template>
  <div>
    <el-drawer :model-value="show" direction="btt" :size="650"
               :close-on-click-modal ="false" @open="initEditor" @close="emit('close')">
      <template #header>
        <div>
          <div style="font-weight: bolder">发布公告</div>
          <div style="font-size: 13px">发表内容之前，请遵守相关法律法规，不要出现骂人等爆粗口的不文明行为</div>
        </div>
      </template>
      <div style="display: flex;gap: 10px">
        <div style="flex:1">
          <el-input v-model="editor.title" placeholder="请输入公告标题..." :prefix-icon="Document"
                    style="height: 100%" maxlength="30"/>
        </div>
      </div>
      <div style="margin-top: 10px;height: 460px;overflow: hidden;border-radius: 5px" v-loading="editor.uploading" element-loading-text="正在上传图片，请稍后...">
        <quill-editor v-model:content="editor.text" style="height: calc(100% - 44px)"
                      content-type="delta"
                      :options="editorOption"
                      ref="refEditor"
                      placeholder="发布公告内容"/>
      </div>
      <div style="margin-top: 5px;display: flex;justify-content: space-between">
        <div style="color: grey;font-size: 13px">
          当前字数 {{ contentLength }} （最大支持2000字）
        </div>
        <div>
          <el-button type="primary" :icon="Position" @click="submitText">
            {{submitButton}}
          </el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<style scoped>
:deep(.el-drawer){
  width: 800px;
  margin:auto;
  border-radius:10px 10px
}
:deep(.el-drawer__header){
  margin: 0;
}
</style>