<script setup>
import {ref} from "vue"
import {apiChatWithAI} from "@/net/api/ai.js"
import {CloseBold} from "@element-plus/icons-vue";
import MarkdownIt from "markdown-it";
const chatLoading=ref(false)
const isOpen=ref(false)
const inputText=ref('')
const messages=ref([])
const md=new MarkdownIt()
const renderMarkdown=(text)=>{
   return md.render(text)
}
const sendMessage=()=>{
  if(inputText.value.trim() && !chatLoading.value){
    const userMessage = inputText.value
    messages.value.push({
      type:'user',
      text: userMessage,
      timestamp:new Date()
    })
    inputText.value=''
    chatLoading.value=true

    messages.value.push({
      type:'assistant',
      text:"",
      timestamp:new Date()
    })

    const context = messages.value
      .slice(0, -1)
      .map(msg => ({
        type: msg.type,
        text: msg.text
      }))
    apiChatWithAI(context, text => {
      messages.value[messages.value.length - 1].text += text
    }, () => {
      messages.value[messages.value.length - 1].text = '生成失败，请重试'
      chatLoading.value = false
    }, () => {
      chatLoading.value = false
    })

  }
}
</script>

<template>
   <div class="ai-chat-window">
       <button v-if="!isOpen" class="chat-button" @click="isOpen=true">
         <span>💬</span>
       </button>
       <div v-else class="chat-window">
         <div class="chat-window-header">
           <h3>校园AI助手</h3>
           <button class="close-btn" @click="isOpen=false">
             <el-icon><CloseBold/></el-icon>
           </button>
         </div>
         <div class="chat-window__messages">
             <div v-for="(message,index) in messages" :key="index" :class="[`message-${message.type}`]">
               <div class="message-text" v-if="message.type==='user'">
                 {{message.text}}
               </div>
               <div class="message-text markdown-body" v-if="message.type==='assistant'"
                    v-html="renderMarkdown(message.text)">
               </div>
             </div>
         </div>
         <div class="chat-window-input">
            <input 
              v-model="inputText" 
              type="text" 
              placeholder="请输入要询问的问题..." 
              @keydown.enter="sendMessage"
              :disabled="chatLoading">
           <button 
             class="send-btn" 
             @click="sendMessage"
             :disabled="chatLoading">
             {{ chatLoading ? '发送中...' : '发送' }}
           </button>
         </div>
       </div>
   </div>
</template>

<style lang="less" scoped>
.ai-chat-window{
   position: fixed;
   bottom: 20px;
   right: 20px;
   z-index: 999;
  .chat-button{
     width: 42px;
     height: 42px;
    border-radius: 50%;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border:none;
    box-shadow: var(--el-box-shadow-light);
    color: white;
    cursor: pointer;
    font-size: 18px;
    transition: transform .3s ease;
    &:hover{
      transform: scale(1.1);
    }
  }
  .chat-window{
    width: 380px;
    height: 500px;
    border-radius: 12px;
    box-shadow: var(--el-box-shadow-light);
    background: white;
    display: flex;
    flex-direction: column;
    animation: slideUp .3s ease;
    overflow: hidden;
    .chat-window-header{
      color: white;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 16px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      h3{
        margin:0;
        font-size: 16px;
      }
      .close-btn{
        background: none;
        border: none;
        color: white;
        font-size: 20px;
        cursor: pointer;
        padding: 0;
        width: 30px;
        height: 30px;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
    .chat-window__messages {
      flex: 1;
      padding: 16px;
      overflow-y: auto;
      display: flex;
      flex-direction: column;
      gap: 12px;
      background: #f5f5f5;

      .dark & {
        color: #e4e4e4;
        background: #222;
      }

      .message-user, .message-assistant {
        width: fit-content;
        max-width: 80%;

        .message-text {
          background: white;
          font-size: 14px;
          padding: 8px 14px;
          min-height: 16px;
          border-radius: 18px;
          word-wrap: break-word;
          overflow: hidden;

          .dark & { background: #111; }
        }
      }

      .message-user {
        align-self: flex-end;
      }

      .message-assistant {
        align-self: flex-start;
      }
    }

    .chat-window-input{
      padding: 16px;
      background: white;
      border-top: 1px solid #e0e0e0;
      display: flex;
      gap: 8px;
      .dark & {
        color: #e4e4e4;
        background: #222;
      }
      input {
        flex: 1;
        border-radius: 20px;
        border: 1px solid #e0e0e0;
        padding: 10px 14px;
        font-size: 14px;
        outline: none;

        &:focus {
          border-color: #667eea;
        }
        
        &:disabled {
          background: #f5f5f5;
          cursor: not-allowed;
        }
      }
      
      .send-btn{
        border: none;
        color: white;
        cursor: pointer;
        padding: 10px 20px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 20px;
        font-size: 14px;
        transition: opacity .3s ease;
        white-space: nowrap;
        &:hover:not(:disabled){
          opacity: 0.9;
        }
        &:disabled{
          opacity: 0.6;
          cursor: not-allowed;
        }
      }
    }
  }
}
@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

</style>
<style lang="less">
 .markdown-body{
  p:first-child{margin-top: 0}
  p:last-child{margin-bottom: 0}
 }
</style>