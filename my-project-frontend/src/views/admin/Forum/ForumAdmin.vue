<script setup lang="ts">
import ForumTopicProhibitedAdmin from "@/views/admin/Forum/Components/ForumTopicProhibitedAdmin.vue";
import ForumTopicAdmin from "@/views/admin/Forum/Components/ForumTopicAdmin.vue";
import {apiForumTypes} from "@/net/api/forum";
import {ref} from "vue";
import ForumTopicTypeAdmin from "@/views/admin/Forum/Components/ForumTopicTypeAdmin.vue";
const type=ref([])
apiForumTypes(data=>type.value=data)
</script>
<template>
  <div class="forum-admin">
    <ForumTopicAdmin/>
    <ForumTopicTypeAdmin :types="type"/>
    <ForumTopicProhibitedAdmin/>

  </div>
</template>
<style scoped>
.forum-admin {
   display: flex;
   flex-direction: column;
   gap: 50px;
}
</style>