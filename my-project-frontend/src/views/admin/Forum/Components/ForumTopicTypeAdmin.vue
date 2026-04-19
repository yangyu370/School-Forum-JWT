<script setup>
import ColorDot from "@/components/ColorDot.vue";
defineProps({
  types: {
    type: Array,
    default: () => []
  }
})
</script>

<template>
<div>
   <div>
     <div class="title">论坛帖子类型</div>
     <div class="desc">在这里管理所有帖子类型</div>
     <el-table :data="types" style="height: 400px">
       <el-table-column prop="id" label="类型ID" width="80" align="center"/>
       <el-table-column prop="name" label="类型名称" width="160" align="center">
          <template #default="{row}">
            <el-tag type="info" effect="light">
              <color-dot :color="row.color || '#999999'"/>
              {{ row.name || '未知类型' }}
            </el-tag>
          </template>
       </el-table-column>
       <el-table-column prop="desc" label="类型介绍" min-width="400" show-overflow-tooltip/>
       <el-table-column  label="操作" width="100" align="center">
         <template #default="{row}">
           <el-button type="primary" size="small">编辑</el-button>
         </template>
       </el-table-column>
     </el-table>
   </div>
</div>
</template>

<style scoped>
.title{
  font-weight: bold;
}
.desc{
  color: #bababa;
  font-size: 13px;
  margin-bottom: 20px;
}
</style>