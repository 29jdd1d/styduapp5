<template>
  <div class="page-container">
    <!-- 操作栏 -->
    <el-card shadow="never" class="action-card">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加专业
      </el-button>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <el-table :data="majorList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="专业名称" width="180" />
        <el-table-column prop="code" label="专业代码" width="120" />
        <el-table-column prop="category" label="学科门类" width="120" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button 
              :type="row.status === 1 ? 'warning' : 'success'" 
              link 
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="500px"
      @close="handleDialogClose"
    >
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-width="100px"
      >
        <el-form-item label="专业名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入专业名称" />
        </el-form-item>
        <el-form-item label="专业代码" prop="code">
          <el-input v-model="form.code" placeholder="请输入专业代码" />
        </el-form-item>
        <el-form-item label="学科门类" prop="category">
          <el-select v-model="form.category" placeholder="请选择学科门类" style="width: 100%">
            <el-option label="工学" value="工学" />
            <el-option label="理学" value="理学" />
            <el-option label="经济学" value="经济学" />
            <el-option label="管理学" value="管理学" />
            <el-option label="法学" value="法学" />
            <el-option label="文学" value="文学" />
            <el-option label="教育学" value="教育学" />
            <el-option label="医学" value="医学" />
            <el-option label="农学" value="农学" />
            <el-option label="艺术学" value="艺术学" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入专业描述" 
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMajorList, addMajor, updateMajor, deleteMajor, updateMajorStatus } from '@/api/major'

const loading = ref(false)
const submitLoading = ref(false)
const majorList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)
const formRef = ref(null)

const dialogTitle = computed(() => isEdit.value ? '编辑专业' : '添加专业')

const form = reactive({
  name: '',
  code: '',
  category: '',
  description: '',
  sort: 0,
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入专业名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择学科门类', trigger: 'change' }]
}

// 获取专业列表
const fetchMajorList = async () => {
  loading.value = true
  try {
    const res = await getMajorList()
    if (res.code === 200) {
      majorList.value = res.data || []
    }
  } catch (error) {
    console.error('获取专业列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 添加
const handleAdd = () => {
  isEdit.value = false
  currentId.value = null
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  currentId.value = row.id
  Object.assign(form, {
    name: row.name,
    code: row.code,
    category: row.category,
    description: row.description,
    sort: row.sort,
    status: row.status
  })
  dialogVisible.value = true
}

// 切换状态
const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 0 ? '禁用' : '启用'
  
  try {
    await ElMessageBox.confirm(`确定要${action}该专业吗？`, '提示', {
      type: 'warning'
    })
    
    const res = await updateMajorStatus(row.id, newStatus)
    if (res.code === 200) {
      ElMessage.success(`${action}成功`)
      fetchMajorList()
    } else {
      ElMessage.error(res.message || `${action}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该专业吗？', '提示', {
      type: 'warning'
    })
    
    const res = await deleteMajor(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchMajorList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    const res = isEdit.value 
      ? await updateMajor(currentId.value, form)
      : await addMajor(form)
    
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      fetchMajorList()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    name: '',
    code: '',
    category: '',
    description: '',
    sort: 0,
    status: 1
  })
}

// 弹窗关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

onMounted(() => {
  fetchMajorList()
})
</script>

<style lang="scss" scoped>
.page-container {
  padding: 20px;
}

.action-card {
  margin-bottom: 20px;
}

.table-card {
  // 表格样式
}
</style>
