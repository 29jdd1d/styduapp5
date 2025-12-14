<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="课程名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
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
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>添加课程
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <el-table :data="courseList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="课程信息" width="300">
          <template #default="{ row }">
            <div class="course-info">
              <el-image :src="row.cover" fit="cover" class="cover">
                <template #error>
                  <div class="image-placeholder">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="course-detail">
                <div class="title">{{ row.title }}</div>
                <div class="teacher">讲师: {{ row.teacherName || '-' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="免费" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isFree === 1 ? 'success' : 'warning'" size="small">
              {{ row.isFree === 1 ? '免费' : '付费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="chapterCount" label="章节数" width="80" />
        <el-table-column prop="videoCount" label="视频数" width="80" />
        <el-table-column prop="viewCount" label="观看数" width="100" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="info" link @click="handleManageChapter(row)">章节</el-button>
            <el-button 
              :type="row.status === 1 ? 'warning' : 'success'" 
              link 
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
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

    <!-- 添加/编辑课程弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="600px"
      @close="handleDialogClose"
    >
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-width="100px"
      >
        <el-form-item label="课程名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="所属专业" prop="majorId">
          <el-select v-model="form.majorId" placeholder="请选择专业" style="width: 100%">
            <el-option v-for="major in majorList" :key="major.id" :label="major.name" :value="major.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="封面图" prop="cover">
          <div class="cover-upload">
            <el-upload
              class="cover-uploader"
              :action="imageUploadAction"
              :headers="uploadHeaders"
              :show-file-list="false"
              :before-upload="beforeCoverUpload"
              :on-success="handleCoverUploadSuccess"
              :on-error="handleCoverUploadError"
              accept="image/*"
            >
              <img v-if="form.cover" :src="form.cover" class="cover-preview" />
              <div v-else class="cover-placeholder">
                <el-icon><Plus /></el-icon>
                <span>上传封面</span>
              </div>
            </el-upload>
            <el-input v-model="form.cover" placeholder="或直接输入封面图URL" style="margin-top: 10px" />
          </div>
        </el-form-item>
        <el-form-item label="讲师名称" prop="teacherName">
          <el-input v-model="form.teacherName" placeholder="请输入讲师名称" />
        </el-form-item>
        <el-form-item label="课程简介" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入课程简介" 
          />
        </el-form-item>
        <el-form-item label="是否免费" prop="isFree">
          <el-radio-group v-model="form.isFree">
            <el-radio :value="1">免费</el-radio>
            <el-radio :value="0">付费</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 章节管理弹窗 -->
    <el-dialog 
      v-model="chapterDialogVisible" 
      :title="`章节管理 - ${currentCourse?.title || ''}`" 
      width="800px"
    >
      <div class="chapter-header">
        <el-button type="primary" size="small" @click="handleAddChapter">
          <el-icon><Plus /></el-icon>添加章节
        </el-button>
      </div>
      
      <el-collapse v-model="activeChapter" accordion>
        <el-collapse-item 
          v-for="chapter in chapterList" 
          :key="chapter.id" 
          :name="chapter.id"
        >
          <template #title>
            <div class="chapter-title">
              <span>{{ chapter.title }}</span>
              <div class="chapter-actions" @click.stop>
                <el-button type="primary" link size="small" @click="handleEditChapter(chapter)">编辑</el-button>
                <el-button type="success" link size="small" @click="handleAddVideo(chapter)">添加视频</el-button>
                <el-button type="danger" link size="small" @click="handleDeleteChapter(chapter)">删除</el-button>
              </div>
            </div>
          </template>
          
          <el-table :data="chapter.videos" size="small" stripe>
            <el-table-column prop="title" label="视频标题" />
            <el-table-column prop="duration" label="时长(秒)" width="100" />
            <el-table-column label="试看" width="80">
              <template #default="{ row }">
                <el-tag :type="row.isFree === 1 ? 'success' : 'info'" size="small">
                  {{ row.isFree === 1 ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="handleEditVideo(row)">编辑</el-button>
                <el-button type="danger" link size="small" @click="handleDeleteVideo(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
      
      <el-empty v-if="chapterList.length === 0" description="暂无章节" />
    </el-dialog>

    <!-- 章节表单弹窗 -->
    <el-dialog v-model="chapterFormVisible" :title="chapterFormTitle" width="500px">
      <el-form ref="chapterFormRef" :model="chapterForm" :rules="chapterRules" label-width="80px">
        <el-form-item label="章节标题" prop="title">
          <el-input v-model="chapterForm.title" placeholder="请输入章节标题" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="chapterForm.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="chapterForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="chapterFormVisible = false">取消</el-button>
        <el-button type="primary" @click="handleChapterSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 视频表单弹窗 -->
    <el-dialog v-model="videoFormVisible" :title="videoFormTitle" width="600px">
      <el-form ref="videoFormRef" :model="videoForm" :rules="videoRules" label-width="100px">
        <el-form-item label="视频标题" prop="title">
          <el-input v-model="videoForm.title" placeholder="请输入视频标题" />
        </el-form-item>
        <el-form-item label="上传视频">
          <el-upload
            class="video-uploader"
            :action="uploadAction"
            :headers="uploadHeaders"
            :show-file-list="false"
            :before-upload="beforeVideoUpload"
            :on-success="handleVideoUploadSuccess"
            :on-error="handleVideoUploadError"
            :on-progress="handleVideoUploadProgress"
            accept="video/*"
          >
            <el-button type="primary" :loading="videoUploading">
              <el-icon><Upload /></el-icon>
              {{ videoUploading ? `上传中 ${uploadProgress}%` : '点击上传视频' }}
            </el-button>
            <template #tip>
              <div class="el-upload__tip">支持 mp4、avi、mov 等格式，最大500MB</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="视频地址" prop="videoUrl">
          <el-input v-model="videoForm.videoUrl" placeholder="上传后自动填充或手动输入URL" />
        </el-form-item>
        <el-form-item label="视频Key" prop="videoKey">
          <el-input v-model="videoForm.videoKey" placeholder="上传后自动填充(用于签名URL)" />
        </el-form-item>
        <el-form-item label="时长(秒)" prop="duration">
          <el-input-number v-model="videoForm.duration" :min="0" />
        </el-form-item>
        <el-form-item label="可试看" prop="isFree">
          <el-radio-group v-model="videoForm.isFree">
            <el-radio :value="1">是</el-radio>
            <el-radio :value="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="videoForm.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="videoForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="videoFormVisible = false">取消</el-button>
        <el-button type="primary" @click="handleVideoSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Search, Refresh, Plus, Picture, Upload } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getCourseList, getCourseDetail, addCourse, updateCourse, deleteCourse, updateCourseStatus,
  addChapter, updateChapter, deleteChapter,
  addVideo, updateVideo, deleteVideo
} from '@/api/course'
import { getMajorList } from '@/api/major'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const loading = ref(false)
const submitLoading = ref(false)
const courseList = ref([])
const majorList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)
const formRef = ref(null)

// 视频上传相关
const videoUploading = ref(false)
const uploadProgress = ref(0)
const uploadAction = '/api/admin/upload/video'
const imageUploadAction = '/api/admin/upload/image'
const uploadHeaders = computed(() => ({
  'Authorization': `Bearer ${userStore.token}`
}))

// 章节管理
const chapterDialogVisible = ref(false)
const currentCourse = ref(null)
const chapterList = ref([])
const activeChapter = ref(null)

// 章节表单
const chapterFormVisible = ref(false)
const chapterFormTitle = ref('添加章节')
const chapterFormRef = ref(null)
const isEditChapter = ref(false)
const currentChapterId = ref(null)
const chapterForm = reactive({
  title: '',
  sort: 0,
  status: 1
})
const chapterRules = {
  title: [{ required: true, message: '请输入章节标题', trigger: 'blur' }]
}

// 视频表单
const videoFormVisible = ref(false)
const videoFormTitle = ref('添加视频')
const videoFormRef = ref(null)
const isEditVideo = ref(false)
const currentVideoId = ref(null)
const currentChapterIdForVideo = ref(null)
const videoForm = reactive({
  title: '',
  videoUrl: '',
  videoKey: '',
  duration: 0,
  isFree: 0,
  sort: 0,
  status: 1
})
const videoRules = {
  title: [{ required: true, message: '请输入视频标题', trigger: 'blur' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑课程' : '添加课程')

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

const form = reactive({
  title: '',
  majorId: null,
  cover: '',
  teacherName: '',
  description: '',
  isFree: 1,
  sort: 0,
  status: 1
})

const rules = {
  title: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  majorId: [{ required: true, message: '请选择专业', trigger: 'change' }]
}

// 获取课程列表
const fetchCourseList = async () => {
  loading.value = true
  try {
    const res = await getCourseList({
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    })
    if (res.code === 200) {
      courseList.value = res.data.list || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
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
  fetchCourseList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = null
  searchForm.majorId = null
  pagination.page = 1
  fetchCourseList()
}

// 添加课程
const handleAdd = () => {
  isEdit.value = false
  currentId.value = null
  resetForm()
  dialogVisible.value = true
}

// 编辑课程
const handleEdit = (row) => {
  isEdit.value = true
  currentId.value = row.id
  Object.assign(form, {
    title: row.title,
    majorId: row.majorId,
    cover: row.cover,
    teacherName: row.teacherName,
    description: row.description,
    isFree: row.isFree,
    sort: row.sort,
    status: row.status
  })
  dialogVisible.value = true
}

// 切换状态
const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 0 ? '下架' : '上架'
  
  try {
    await ElMessageBox.confirm(`确定要${action}该课程吗？`, '提示', { type: 'warning' })
    const res = await updateCourseStatus(row.id, newStatus)
    if (res.code === 200) {
      ElMessage.success(`${action}成功`)
      fetchCourseList()
    } else {
      ElMessage.error(res.message || `${action}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(`${action}失败`)
  }
}

// 删除课程
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该课程吗？删除后将同时删除所有章节和视频！', '提示', { type: 'warning' })
    const res = await deleteCourse(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchCourseList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

// 提交课程表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true
    
    const res = isEdit.value 
      ? await updateCourse(currentId.value, form)
      : await addCourse(form)
    
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      fetchCourseList()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 管理章节
const handleManageChapter = async (row) => {
  currentCourse.value = row
  try {
    const res = await getCourseDetail(row.id)
    if (res.code === 200) {
      chapterList.value = res.data.chapters || []
      chapterDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取章节列表失败')
  }
}

// 添加章节
const handleAddChapter = () => {
  isEditChapter.value = false
  currentChapterId.value = null
  chapterForm.title = ''
  chapterForm.sort = 0
  chapterForm.status = 1
  chapterFormTitle.value = '添加章节'
  chapterFormVisible.value = true
}

// 编辑章节
const handleEditChapter = (chapter) => {
  isEditChapter.value = true
  currentChapterId.value = chapter.id
  chapterForm.title = chapter.title
  chapterForm.sort = chapter.sort
  chapterForm.status = chapter.status
  chapterFormTitle.value = '编辑章节'
  chapterFormVisible.value = true
}

// 提交章节
const handleChapterSubmit = async () => {
  try {
    await chapterFormRef.value.validate()
    const data = {
      courseId: currentCourse.value.id,
      title: chapterForm.title,
      sort: chapterForm.sort,
      status: chapterForm.status
    }
    
    const res = isEditChapter.value
      ? await updateChapter(currentChapterId.value, data)
      : await addChapter(data)
    
    if (res.code === 200) {
      ElMessage.success(isEditChapter.value ? '编辑成功' : '添加成功')
      chapterFormVisible.value = false
      handleManageChapter(currentCourse.value)
      fetchCourseList()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 删除章节
const handleDeleteChapter = async (chapter) => {
  try {
    await ElMessageBox.confirm('确定要删除该章节吗？同时会删除章节下所有视频！', '提示', { type: 'warning' })
    const res = await deleteChapter(chapter.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      handleManageChapter(currentCourse.value)
      fetchCourseList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

// 添加视频
const handleAddVideo = (chapter) => {
  isEditVideo.value = false
  currentVideoId.value = null
  currentChapterIdForVideo.value = chapter.id
  Object.assign(videoForm, { title: '', videoUrl: '', videoKey: '', duration: 0, isFree: 0, sort: 0, status: 1 })
  videoFormTitle.value = '添加视频'
  videoFormVisible.value = true
}

// 编辑视频
const handleEditVideo = (video) => {
  isEditVideo.value = true
  currentVideoId.value = video.id
  Object.assign(videoForm, {
    title: video.title,
    videoUrl: video.videoUrl,
    videoKey: video.videoKey,
    duration: video.duration,
    isFree: video.isFree,
    sort: video.sort,
    status: video.status
  })
  videoFormTitle.value = '编辑视频'
  videoFormVisible.value = true
}

// 提交视频
const handleVideoSubmit = async () => {
  try {
    await videoFormRef.value.validate()
    const data = {
      chapterId: currentChapterIdForVideo.value,
      ...videoForm
    }
    
    const res = isEditVideo.value
      ? await updateVideo(currentVideoId.value, data)
      : await addVideo(data)
    
    if (res.code === 200) {
      ElMessage.success(isEditVideo.value ? '编辑成功' : '添加成功')
      videoFormVisible.value = false
      handleManageChapter(currentCourse.value)
      fetchCourseList()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 删除视频
const handleDeleteVideo = async (video) => {
  try {
    await ElMessageBox.confirm('确定要删除该视频吗？', '提示', { type: 'warning' })
    const res = await deleteVideo(video.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      handleManageChapter(currentCourse.value)
      fetchCourseList()
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
    title: '',
    majorId: null,
    cover: '',
    teacherName: '',
    description: '',
    isFree: 1,
    sort: 0,
    status: 1
  })
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchCourseList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  fetchCourseList()
}

// 视频上传前校验
const beforeVideoUpload = (file) => {
  const isVideo = file.type.startsWith('video/')
  const isLt500M = file.size / 1024 / 1024 < 500

  if (!isVideo) {
    ElMessage.error('只能上传视频文件!')
    return false
  }
  if (!isLt500M) {
    ElMessage.error('视频大小不能超过500MB!')
    return false
  }
  
  videoUploading.value = true
  uploadProgress.value = 0
  return true
}

// 视频上传成功
const handleVideoUploadSuccess = (response) => {
  videoUploading.value = false
  uploadProgress.value = 100
  
  if (response.code === 200) {
    ElMessage.success('视频上传成功')
    videoForm.videoUrl = response.data.url
    videoForm.videoKey = response.data.videoKey
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 视频上传失败
const handleVideoUploadError = (error) => {
  videoUploading.value = false
  uploadProgress.value = 0
  ElMessage.error('视频上传失败，请重试')
  console.error('上传错误:', error)
}

// 视频上传进度
const handleVideoUploadProgress = (event) => {
  uploadProgress.value = Math.round(event.percent || 0)
}

// 封面上传前校验
const beforeCoverUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB!')
    return false
  }
  return true
}

// 封面上传成功
const handleCoverUploadSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success('封面上传成功')
    form.cover = response.data.url
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 封面上传失败
const handleCoverUploadError = (error) => {
  ElMessage.error('封面上传失败，请重试')
  console.error('上传错误:', error)
}

onMounted(() => {
  fetchCourseList()
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
  .course-info {
    display: flex;
    align-items: center;
    gap: 10px;
    
    .cover {
      width: 80px;
      height: 45px;
      border-radius: 4px;
      
      .image-placeholder {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #f5f7fa;
        color: #909399;
      }
    }
    
    .course-detail {
      .title {
        font-weight: 500;
        margin-bottom: 4px;
      }
      .teacher {
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

.chapter-header {
  margin-bottom: 15px;
}

.chapter-title {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-right: 20px;
}

.cover-upload {
  width: 100%;
  
  .cover-uploader {
    :deep(.el-upload) {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: border-color 0.3s;
      
      &:hover {
        border-color: #409eff;
      }
    }
  }
  
  .cover-preview {
    width: 200px;
    height: 120px;
    object-fit: cover;
    display: block;
  }
  
  .cover-placeholder {
    width: 200px;
    height: 120px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #8c939d;
    font-size: 14px;
    
    .el-icon {
      font-size: 28px;
      margin-bottom: 8px;
    }
  }
}
</style>
