<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon user-icon">
            <el-icon size="32"><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
            <div class="stat-label">用户总数</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon course-icon">
            <el-icon size="32"><VideoCamera /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalCourses || 0 }}</div>
            <div class="stat-label">课程总数</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon question-icon">
            <el-icon size="32"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalQuestions || 0 }}</div>
            <div class="stat-label">题目总数</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon post-icon">
            <el-icon size="32"><ChatDotSquare /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalPosts || 0 }}</div>
            <div class="stat-label">帖子总数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span>数据概览</span>
          </template>
          <div ref="barChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span>数据分布</span>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 更多统计 -->
    <el-row :gutter="20" class="more-stats">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <span>今日数据</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="新增用户">
              <el-tag type="success">{{ stats.todayNewUsers || 0 }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="活跃用户">
              <el-tag type="primary">{{ stats.activeUsers || 0 }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="今日签到">
              <el-tag type="warning">{{ stats.todayCheckins || 0 }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="今日练习">
              <el-tag type="info">{{ stats.todayPracticeCount || 0 }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <span>系统信息</span>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="系统版本">v1.0.0</el-descriptions-item>
            <el-descriptions-item label="后端服务">Spring Cloud Alibaba</el-descriptions-item>
            <el-descriptions-item label="注册中心">Nacos 2.4.3</el-descriptions-item>
            <el-descriptions-item label="数据库">MySQL 8.0</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { User, VideoCamera, Document, ChatDotSquare } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getDashboardStats } from '@/api/dashboard'
import { ElMessage } from 'element-plus'

const stats = ref({})
const barChartRef = ref(null)
const pieChartRef = ref(null)
let barChart = null
let pieChart = null

// 获取统计数据
const fetchStats = async () => {
  try {
    const res = await getDashboardStats()
    if (res.code === 200) {
      stats.value = res.data || {}
      updateCharts()
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  }
}

// 初始化图表
const initCharts = () => {
  // 数据概览柱状图
  if (barChartRef.value) {
    barChart = echarts.init(barChartRef.value)
  }
  
  // 数据分布饼图
  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value)
  }
}

// 更新图表
const updateCharts = () => {
  // 更新柱状图
  if (barChart) {
    barChart.setOption({
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      xAxis: {
        type: 'category',
        data: ['用户', '课程', '题目', '帖子']
      },
      yAxis: {
        type: 'value'
      },
      series: [{
        name: '数量',
        type: 'bar',
        barWidth: '50%',
        itemStyle: {
          color: function(params) {
            const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C']
            return colors[params.dataIndex]
          },
          borderRadius: [4, 4, 0, 0]
        },
        data: [
          stats.value.totalUsers || 0,
          stats.value.totalCourses || 0,
          stats.value.totalQuestions || 0,
          stats.value.totalPosts || 0
        ]
      }]
    })
  }
  
  // 更新饼图
  if (pieChart) {
    pieChart.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [{
        name: '数据分布',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: stats.value.totalUsers || 0, name: '用户', itemStyle: { color: '#409EFF' } },
          { value: stats.value.totalCourses || 0, name: '课程', itemStyle: { color: '#67C23A' } },
          { value: stats.value.totalQuestions || 0, name: '题目', itemStyle: { color: '#E6A23C' } },
          { value: stats.value.totalPosts || 0, name: '帖子', itemStyle: { color: '#F56C6C' } }
        ]
      }]
    })
  }
}

// 处理窗口大小变化
const handleResize = () => {
  barChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  fetchStats()
  initCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  barChart?.dispose()
  pieChart?.dispose()
})
</script>
</script>

<style lang="scss" scoped>
.dashboard {
  .stat-cards {
    margin-bottom: 20px;
    
    .stat-card {
      :deep(.el-card__body) {
        display: flex;
        align-items: center;
        padding: 20px;
      }
      
      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        color: #fff;
        
        &.user-icon {
          background: linear-gradient(135deg, #409EFF, #66b1ff);
        }
        
        &.course-icon {
          background: linear-gradient(135deg, #67C23A, #85ce61);
        }
        
        &.question-icon {
          background: linear-gradient(135deg, #E6A23C, #ebb563);
        }
        
        &.post-icon {
          background: linear-gradient(135deg, #F56C6C, #f78989);
        }
      }
      
      .stat-info {
        .stat-value {
          font-size: 28px;
          font-weight: bold;
          color: #303133;
          line-height: 1;
          margin-bottom: 8px;
        }
        
        .stat-label {
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }
  
  .chart-row {
    margin-bottom: 20px;
    
    .chart-card {
      height: 400px;
      
      .chart-container {
        height: 320px;
      }
    }
  }
  
  .more-stats {
    .el-col {
      margin-bottom: 20px;
    }
  }
}

@media (max-width: 768px) {
  .dashboard {
    .stat-cards {
      .el-col {
        margin-bottom: 12px;
      }
    }
  }
}
</style>
