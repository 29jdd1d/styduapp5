<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="昵称/手机号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="专业">
          <el-select v-model="searchForm.majorId" placeholder="全部" clearable style="width: 180px">
            <el-option v-for="major in majorList" :key="major.id" :label="major.name" :value="major.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <el-table :data="userList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="用户信息" width="200">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="40" :src="row.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="user-detail">
                <div class="nickname">{{ row.nickname || '未设置' }}</div>
                <div class="phone">{{ row.phone || '-' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="性别" width="80">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '未知' }}
          </template>
        </el-table-column>
        <el-table-column prop="majorName" label="专业" width="150">
          <template #default="{ row }">
            {{ row.majorName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="examYear" label="考研年份" width="100">
          <template #default="{ row }">
            {{ row.examYear || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="studyDays" label="学习天数" width="100" />
        <el-table-column prop="totalQuestions" label="做题数" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button 
              :type="row.status === 1 ? 'danger' : 'success'" 
              link 
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 用户详情弹窗 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="500px">
      <el-descriptions :column="2" border v-if="currentUser">
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ currentUser.gender === 1 ? '男' : currentUser.gender === 2 ? '女' : '未知' }}
        </el-descriptions-item>
        <el-descriptions-item label="专业">{{ currentUser.majorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="考研年份">{{ currentUser.examYear || '-' }}</el-descriptions-item>
        <el-descriptions-item label="学习天数">{{ currentUser.studyDays }}</el-descriptions-item>
        <el-descriptions-item label="做题数">{{ currentUser.totalQuestions }}</el-descriptions-item>
        <el-descriptions-item label="状态" :span="2">
          <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
            {{ currentUser.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">{{ currentUser.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh, User } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, getUserDetail, updateUserStatus } from '@/api/user'
import { getMajorList } from '@/api/major'

const loading = ref(false)
const userList = ref([])
const majorList = ref([])
const detailVisible = ref(false)
const currentUser = ref(null)

const searchForm = reactive({
  keyword: '',
  status: null,
  majorId: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    })
    if (res.code === 200) {
      userList.value = res.data.list || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取专业列表
const fetchMajorList = async () => {
  try {
    const res = await getMajorList()
    if (res.code === 200) {
      majorList.value = res.data || []
    }
  } catch (error) {
    console.error('获取专业列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchUserList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = null
  searchForm.majorId = null
  pagination.page = 1
  fetchUserList()
}

// 查看详情
const handleView = async (row) => {
  try {
    const res = await getUserDetail(row.id)
    if (res.code === 200) {
      currentUser.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取用户详情失败')
  }
}

// 切换状态
const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 0 ? '禁用' : '启用'
  
  try {
    await ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', {
      type: 'warning'
    })
    
    const res = await updateUserStatus(row.id, newStatus)
    if (res.code === 200) {
      ElMessage.success(`${action}成功`)
      fetchUserList()
    } else {
      ElMessage.error(res.message || `${action}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

// 分页
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchUserList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  fetchUserList()
}

onMounted(() => {
  fetchUserList()
  fetchMajorList()
})
</script>

<style lang="scss" scoped>
.page-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
  
  .search-form {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
}

.table-card {
  .user-info {
    display: flex;
    align-items: center;
    gap: 10px;
    
    .user-detail {
      .nickname {
        font-weight: 500;
      }
      .phone {
        font-size: 12px;
        color: #909399;
      }
    }
  }
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
