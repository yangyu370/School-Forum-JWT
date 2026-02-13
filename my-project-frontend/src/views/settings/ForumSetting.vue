<script setup>
import {apiForumTypes, apiForumUserTopic, apiForumTopicDelete, apiAdminTopicList} from "@/net/api/forum.js";
import {ref} from "vue";
import Card from "@/components/Card.vue";
import {useStore} from "@/store";
import TopicTag from "@/components/TopicTag.vue";
import {Clock, Delete, Lock} from "@element-plus/icons-vue";
import {ElMessage, ElMessageBox} from "element-plus";
const store=useStore()
const list=ref([])
const refreshList = () => apiForumUserTopic(data => list.value = data)
apiForumUserTopic(data=>list.value=data)
apiForumTypes(data=>store.forum.types=data)
const deleteTopic=(id)=>{
  ElMessageBox.confirm("删除帖子将永远无法找回,您确定这么做吗？","删除",{
    callback:value=>{
      if(value === 'confirm')
        apiForumTopicDelete(id,()=>{
          ElMessage.success('帖子删除成功')
          refreshList()
        })
    }
  })
}
refreshList()
</script>

<template>
  <div style="margin:auto;max-width: 700px">
    <div class="topic-list">
      <card v-for="topic in list">
          <div class="title">
            <el-tag size="small" effect="dark" type="warning" disable-transitions
                    style="margin-right: 10px" v-if="topic.locked">
              <el-icon>
                <Lock/>
              </el-icon>
              已锁定
            </el-tag>
            <el-tag size="small" effect="dark" type="info" disable-transitions
                    style="margin-right: 10px" v-if="topic.invisible">
              <el-icon>
                <Lock/>
              </el-icon>
              屏蔽
            </el-tag>
            <topic-tag style="margin-right:10px" :type="topic.type"/>
            <el-link :href="`/index/topic-detail/${topic.id}`"  style="font-weight: bold">{{topic.title}}</el-link>
          </div>
          <div class="content" >
            <div style="color: grey;font-size: 12px">
              <el-icon>
                <Clock/>
              </el-icon>
              <div style="margin-left:2px;display: inline-block;">
                {{new Date(topic.time).toLocaleString()}}
              </div>
            </div>
              <el-link type="danger" @click="deleteTopic(topic.id)">
                <el-icon><Delete/></el-icon>
                <span>删除帖子</span>
              </el-link>
          </div>
      </card>
    </div>
  </div>
</template>

<style lang="less" scoped>
.topic-list{
  gap:10px;
  display:flex;
  flex-direction: column;
  margin: 20px 0;
  .content{
    display: flex;
    margin-top: 10px;
    justify-content: space-between;
  }
}
</style>