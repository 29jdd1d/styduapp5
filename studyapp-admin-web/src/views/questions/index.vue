<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="题目内容" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="题型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable style="width: 120px">
            <el-option label="单选题" :value="1" />
            <el-option label="多选题" :value="2" />
            <el-option label="判断题" :value="3" />
            <el-option label="填空题" :value="4" />
            <el-option label="简答题" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="searchForm.difficulty" placeholder="全部" clearable style="width: 120px">
            <el-option label="简单" :value="1" />
            <el-option label="中等" :value="2" />
            <el-option label="困难" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="专业">
          <el-select v-model="searchForm.majorId" placeholder="全部" clearable style="width: 150px" @change="handleMajorChange">
            <el-option v-for="major in majorList" :key="major.id" :label="major.name" :value="major.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-cascader
            v-model="searchForm.categoryId"
            :options="categoryTree"
            :props="{ value: 'id', label: 'name', children: 'children', checkStrictly: true, emitPath: false }"
            placeholder="全部"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>添加题目
          </el-button>
          <el-button type="warning" @click="handleManageCategory">
            <el-icon><FolderOpened /></el-icon>分类管理
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <el-table :data="questionList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="content" label="题目内容" show-overflow-tooltip />
        <el-table-column label="题型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)" size="small">{{ getTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="难度" width="80">
          <template #default="{ row }">
            <el-tag :type="getDifficultyTag(row.difficulty)" size="small">{{ getDifficultyName(row.difficulty) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="source" label="来源" width="100" />
        <el-table-column prop="year" label="年份" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="info" link @click="handleView(row)">查看</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <!-- 添加/编辑题目弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="800px"
      @close="handleDialogClose"
    >
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属专业" prop="majorId">
              <el-select v-model="form.majorId" placeholder="请选择专业" style="width: 100%" @change="handleFormMajorChange">
                <el-option v-for="major in majorList" :key="major.id" :label="major.name" :value="major.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属分类" prop="categoryId">
              <el-cascader
                v-model="form.categoryId"
                :options="formCategoryTree"
                :props="{ value: 'id', label: 'name', children: 'children', checkStrictly: true, emitPath: false }"
                placeholder="请选择分类"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="题型" prop="type">
              <el-select v-model="form.type" placeholder="请选择题型" style="width: 100%">
                <el-option label="单选题" :value="1" />
                <el-option label="多选题" :value="2" />
                <el-option label="判断题" :value="3" />
                <el-option label="填空题" :value="4" />
                <el-option label="简答题" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="难度" prop="difficulty">
              <el-select v-model="form.difficulty" placeholder="请选择难度" style="width: 100%">
                <el-option label="简单" :value="1" />
                <el-option label="中等" :value="2" />
                <el-option label="困难" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="题目内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="请输入题目内容" />
        </el-form-item>
        <el-form-item label="选项" prop="options" v-if="[1, 2].includes(form.type)">
          <div class="options-container">
            <div v-for="(option, index) in optionsList" :key="index" class="option-item">
              <el-input v-model="optionsList[index]" :placeholder="`选项${String.fromCharCode(65 + index)}`">
                <template #prepend>{{ String.fromCharCode(65 + index) }}</template>
              </el-input>
              <el-button v-if="optionsList.length > 2" type="danger" :icon="Delete" circle @click="removeOption(index)" />
            </div>
            <el-button type="primary" link @click="addOption" v-if="optionsList.length < 6">+ 添加选项</el-button>
          </div>
        </el-form-item>
        <el-form-item label="答案" prop="answer">
          <el-input v-model="form.answer" :placeholder="getAnswerPlaceholder()" />
        </el-form-item>
        <el-form-item label="解析" prop="analysis">
          <el-input v-model="form.analysis" type="textarea" :rows="3" placeholder="请输入题目解析" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="来源" prop="source">
              <el-input v-model="form.source" placeholder="如: 2023年真题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年份" prop="year">
              <el-input-number v-model="form.year" :min="2000" :max="2030" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="form.sort" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看题目详情 -->
    <el-dialog v-model="viewDialogVisible" title="题目详情" width="700px">
      <div class="question-detail" v-if="currentQuestion">
        <div class="detail-item">
          <label>题目内容：</label>
          <div class="content">{{ currentQuestion.content }}</div>
        </div>
        <div class="detail-item" v-if="currentQuestion.options">
          <label>选项：</label>
          <div class="options">
            <div v-for="(opt, idx) in parseOptions(currentQuestion.options)" :key="idx">
              {{ String.fromCharCode(65 + idx) }}. {{ opt }}
            </div>
          </div>
        </div>
        <div class="detail-item">
          <label>答案：</label>
          <div class="answer">{{ currentQuestion.answer }}</div>
        </div>
        <div class="detail-item" v-if="currentQuestion.analysis">
          <label>解析：</label>
          <div class="analysis">{{ currentQuestion.analysis }}</div>
        </div>
      </div>
    </el-dialog>

    <!-- 分类管理弹窗 -->
    <el-dialog v-model="categoryDialogVisible" title="分类管理" width="700px">
      <div class="category-header">
        <el-select v-model="categoryMajorId" placeholder="选择专业" style="width: 200px" @change="fetchCategoryList">
          <el-option v-for="major in majorList" :key="major.id" :label="major.name" :value="major.id" />
        </el-select>
        <el-button type="primary" @click="handleAddCategory" :disabled="!categoryMajorId">添加分类</el-button>
      </div>
      
      <el-table :data="flatCategoryList" row-key="id" default-expand-all :tree-props="{ children: 'children' }">
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="questionCount" label="题目数" width="100" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleAddSubCategory(row)">添加子分类</el-button>
            <el-button type="primary" link size="small" @click="handleEditCategory(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDeleteCategory(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 分类表单弹窗 -->
    <el-dialog v-model="categoryFormVisible" :title="categoryFormTitle" width="500px">
      <el-form ref="categoryFormRef" :model="categoryForm" :rules="categoryRules" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="categoryForm.icon" placeholder="请输入图标名称或URL" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="categoryForm.description" type="textarea" :rows="2" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="categoryForm.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="categoryForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryFormVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCategorySubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Search, Refresh, Plus, FolderOpened, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getQuestionList, getQuestionDetail, addQuestion, updateQuestion, deleteQuestion,
  getCategoryList, addCategory, updateCategory, deleteCategory
} from '@/api/question'
import { getMajorList } from '@/api/major'

const loading = ref(false)
const submitLoading = ref(false)
const questionList = ref([])
const majorList = ref([])
const categoryTree = ref([])
const formCategoryTree = ref([])
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)
const currentQuestion = ref(null)
const formRef = ref(null)

// 分类管理
const categoryDialogVisible = ref(false)
const categoryMajorId = ref(null)
const flatCategoryList = ref([])
const categoryFormVisible = ref(false)
const categoryFormTitle = ref('添加分类')
const categoryFormRef = ref(null)
const isEditCategory = ref(false)
const currentCategoryId = ref(null)
const parentCategoryId = ref(0)
const categoryForm = reactive({
  name: '',
  icon: '',
  description: '',
  sort: 0,
  status: 1
})
const categoryRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

// 选项列表
const optionsList = ref(['', '', '', ''])

const dialogTitle = computed(() => isEdit.value ? '编辑题目' : '添加题目')

const searchForm = reactive({
  keyword: '',
  type: null,
  difficulty: null,
  majorId: null,
  categoryId: null
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = reactive({
  majorId: null,
  categoryId: null,
  type: 1,
  difficulty: 1,
  content: '',
  options: '',
  answer: '',
  analysis: '',
  source: '',
  year: null,
  sort: 0,
  status: 1
})

const rules = {
  majorId: [{ required: true, message: '请选择专业', trigger: 'change' }],
  type: [{ required: true, message: '请选择题型', trigger: 'change' }],
  content: [{ required: true, message: '请输入题目内容', trigger: 'blur' }],
  answer: [{ required: true, message: '请输入答案', trigger: 'blur' }]
}

// 获取题目列表
const fetchQuestionList = async () => {
  loading.value = true
  try {
    const res = await getQuestionList({
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    })
    if (res.code === 200) {
      questionList.value = res.data.list || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('获取题目列表失败:', error)
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

// 获取分类列表
const fetchCategoryList = async (majorId) => {
  if (!majorId) {
    flatCategoryList.value = []
    return
  }
  try {
    const res = await getCategoryList(majorId)
    if (res.code === 200) {
      flatCategoryList.value = res.data || []
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 获取搜索用的分类树
const fetchSearchCategoryTree = async (majorId) => {
  if (!majorId) {
    categoryTree.value = []
    return
  }
  try {
    const res = await getCategoryList(majorId)
    if (res.code === 200) {
      categoryTree.value = res.data || []
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 专业变更
const handleMajorChange = (majorId) => {
  searchForm.categoryId = null
  fetchSearchCategoryTree(majorId)
}

// 表单专业变更
const handleFormMajorChange = (majorId) => {
  form.categoryId = null
  if (majorId) {
    getCategoryList(majorId).then(res => {
      if (res.code === 200) {
        formCategoryTree.value = res.data || []
      }
    })
  } else {
    formCategoryTree.value = []
  }
}

// 题型标签
const getTypeTag = (type) => {
  const tags = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'info', 5: 'danger' }
  return tags[type] || 'info'
}

const getTypeName = (type) => {
  const names = { 1: '单选题', 2: '多选题', 3: '判断题', 4: '填空题', 5: '简答题' }
  return names[type] || '未知'
}

// 难度标签
const getDifficultyTag = (difficulty) => {
  const tags = { 1: 'success', 2: 'warning', 3: 'danger' }
  return tags[difficulty] || 'info'
}

const getDifficultyName = (difficulty) => {
  const names = { 1: '简单', 2: '中等', 3: '困难' }
  return names[difficulty] || '未知'
}

// 答案提示
const getAnswerPlaceholder = () => {
  switch (form.type) {
    case 1: return '请输入正确选项，如: A'
    case 2: return '请输入正确选项，如: A,B,C'
    case 3: return '请输入 对 或 错'
    case 4: return '请输入填空答案'
    case 5: return '请输入简答参考答案'
    default: return '请输入答案'
  }
}

// 解析选项
const parseOptions = (options) => {
  try {
    return JSON.parse(options) || []
  } catch {
    return []
  }
}

// 添加选项
const addOption = () => {
  if (optionsList.value.length < 6) {
    optionsList.value.push('')
  }
}

// 删除选项
const removeOption = (index) => {
  if (optionsList.value.length > 2) {
    optionsList.value.splice(index, 1)
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchQuestionList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.type = null
  searchForm.difficulty = null
  searchForm.majorId = null
  searchForm.categoryId = null
  categoryTree.value = []
  pagination.page = 1
  fetchQuestionList()
}

// 添加题目
const handleAdd = () => {
  isEdit.value = false
  currentId.value = null
  resetForm()
  dialogVisible.value = true
}

// 编辑题目
const handleEdit = async (row) => {
  isEdit.value = true
  currentId.value = row.id
  
  try {
    const res = await getQuestionDetail(row.id)
    if (res.code === 200) {
      const data = res.data
      Object.assign(form, {
        majorId: data.majorId,
        categoryId: data.categoryId,
        type: data.type,
        difficulty: data.difficulty,
        content: data.content,
        answer: data.answer,
        analysis: data.analysis,
        source: data.source,
        year: data.year,
        status: data.status
      })
      
      // 解析选项
      if (data.options) {
        try {
          optionsList.value = JSON.parse(data.options)
        } catch {
          optionsList.value = ['', '', '', '']
        }
      }
      
      // 加载分类
      if (data.majorId) {
        handleFormMajorChange(data.majorId)
      }
      
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取题目详情失败')
  }
}

// 查看题目
const handleView = async (row) => {
  try {
    const res = await getQuestionDetail(row.id)
    if (res.code === 200) {
      currentQuestion.value = res.data
      viewDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取题目详情失败')
  }
}

// 删除题目
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该题目吗？', '提示', { type: 'warning' })
    const res = await deleteQuestion(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchQuestionList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

// 提交题目
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    // 处理选项
    if ([1, 2].includes(form.type)) {
      form.options = JSON.stringify(optionsList.value.filter(o => o.trim()))
    } else {
      form.options = ''
    }
    
    const res = isEdit.value 
      ? await updateQuestion(currentId.value, form)
      : await addQuestion(form)
    
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      fetchQuestionList()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 分类管理
const handleManageCategory = () => {
  categoryDialogVisible.value = true
}

// 添加分类
const handleAddCategory = () => {
  isEditCategory.value = false
  currentCategoryId.value = null
  parentCategoryId.value = 0
  Object.assign(categoryForm, { name: '', icon: '', description: '', sort: 0, status: 1 })
  categoryFormTitle.value = '添加分类'
  categoryFormVisible.value = true
}

// 添加子分类
const handleAddSubCategory = (row) => {
  isEditCategory.value = false
  currentCategoryId.value = null
  parentCategoryId.value = row.id
  Object.assign(categoryForm, { name: '', icon: '', description: '', sort: 0, status: 1 })
  categoryFormTitle.value = `添加子分类 (父: ${row.name})`
  categoryFormVisible.value = true
}

// 编辑分类
const handleEditCategory = (row) => {
  isEditCategory.value = true
  currentCategoryId.value = row.id
  parentCategoryId.value = row.parentId
  Object.assign(categoryForm, {
    name: row.name,
    icon: row.icon,
    description: row.description,
    sort: row.sort,
    status: row.status
  })
  categoryFormTitle.value = '编辑分类'
  categoryFormVisible.value = true
}

// 提交分类
const handleCategorySubmit = async () => {
  try {
    await categoryFormRef.value.validate()
    const data = {
      majorId: categoryMajorId.value,
      parentId: parentCategoryId.value,
      ...categoryForm
    }
    
    const res = isEditCategory.value
      ? await updateCategory(currentCategoryId.value, data)
      : await addCategory(data)
    
    if (res.code === 200) {
      ElMessage.success(isEditCategory.value ? '编辑成功' : '添加成功')
      categoryFormVisible.value = false
      fetchCategoryList(categoryMajorId.value)
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 删除分类
const handleDeleteCategory = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？', '提示', { type: 'warning' })
    const res = await deleteCategory(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchCategoryList(categoryMajorId.value)
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    majorId: null,
    categoryId: null,
    type: 1,
    difficulty: 1,
    content: '',
    options: '',
    answer: '',
    analysis: '',
    source: '',
    year: null,
    sort: 0,
    status: 1
  })
  optionsList.value = ['', '', '', '']
  formCategoryTree.value = []
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchQuestionList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  fetchQuestionList()
}

onMounted(() => {
  fetchQuestionList()
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.options-container {
  width: 100%;
  
  .option-item {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 10px;
    
    .el-input {
      flex: 1;
    }
  }
}

.question-detail {
  .detail-item {
    margin-bottom: 15px;
    
    label {
      font-weight: bold;
      color: #606266;
    }
    
    .content, .options, .answer, .analysis {
      margin-top: 5px;
      padding: 10px;
      background: #f5f7fa;
      border-radius: 4px;
    }
    
    .answer {
      color: #67C23A;
      font-weight: bold;
    }
  }
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}
</style>
