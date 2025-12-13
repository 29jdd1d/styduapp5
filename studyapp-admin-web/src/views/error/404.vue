<template>
  <div class="not-found">
    <div class="content">
      <div class="code">404</div>
      <div class="title">页面不存在</div>
      <div class="description">抱歉，您访问的页面不存在或已被删除</div>
      <el-button type="primary" @click="goHome">
        <el-icon><HomeFilled /></el-icon>
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { HomeFilled } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style lang="scss" scoped>
.not-found {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  
  .content {
    text-align: center;
    color: #fff;
    
    .code {
      font-size: 120px;
      font-weight: bold;
      text-shadow: 4px 4px 0 rgba(0, 0, 0, 0.1);
      margin-bottom: 16px;
      animation: pulse 2s ease-in-out infinite;
    }
    
    .title {
      font-size: 32px;
      font-weight: 600;
      margin-bottom: 12px;
    }
    
    .description {
      font-size: 16px;
      opacity: 0.8;
      margin-bottom: 32px;
    }
    
    .el-button {
      padding: 12px 32px;
      font-size: 16px;
    }
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}
</style>
