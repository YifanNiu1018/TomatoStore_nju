<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElCard, ElForm, ElFormItem, ElInput, ElButton, ElRadioGroup, ElRadio, ElUpload } from 'element-plus'
import { createArticle, type ArticleVO } from '@/api/article'

const router = useRouter()

// 表单数据
const articleForm = ref<ArticleVO>({
  userId: '',
  nickName: '',
  title: '',
  cover: '',
  content: '',
  markdownContent: '',
  editorType: 0, // 0:富文本编辑器 1:markdown编辑器
  summary: ''
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 1, max: 100, message: '标题长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' },
    { min: 10, message: '内容至少需要 10 个字符', trigger: 'blur' }
  ],
  summary: [
    { max: 200, message: '摘要不能超过 200 个字符', trigger: 'blur' }
  ]
}

// 用户信息
const currentUser = computed(() => ({
  userId: sessionStorage.getItem('username') || '',
  nickName: sessionStorage.getItem('username') || ''
}))

// 表单引用
const formRef = ref<InstanceType<typeof ElForm>>()
const isSubmitting = ref(false)

// 封面图片预览
const coverPreview = ref('')

// 处理封面图片上传
const handleCoverChange = (file: any) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    coverPreview.value = e.target?.result as string
    articleForm.value.cover = coverPreview.value
  }
  reader.readAsDataURL(file.raw)
}

// 自动生成摘要
const generateSummary = () => {
  const content = articleForm.value.editorType === 1 
    ? articleForm.value.markdownContent 
    : articleForm.value.content
  
  if (content) {
    // 移除HTML标签和Markdown语法，取前150个字符作为摘要
    const plainText = content
      .replace(/<[^>]*>/g, '') // 移除HTML标签
      .replace(/[#*`_~\[\]()]/g, '') // 移除Markdown语法
      .replace(/\n+/g, ' ') // 替换换行为空格
      .trim()
    
    articleForm.value.summary = plainText.substring(0, 150)
    if (plainText.length > 150) {
      articleForm.value.summary += '...'
    }
  }
}

// 提交文章
const submitArticle = async () => {
  if (!currentUser.value.userId) {
    ElMessage.warning('请先登录')
    return
  }

  try {
    await formRef.value?.validate()
    
    isSubmitting.value = true
    
    // 设置用户信息
    articleForm.value.userId = currentUser.value.userId
    articleForm.value.nickName = currentUser.value.nickName
    
    // 如果没有手动设置摘要，自动生成
    if (!articleForm.value.summary) {
      generateSummary()
    }
    
    // 根据编辑器类型设置内容
    if (articleForm.value.editorType === 1) {
      // Markdown编辑器
      if (!articleForm.value.markdownContent) {
        articleForm.value.markdownContent = articleForm.value.content
      }
    }
    
    await createArticle(articleForm.value)
    ElMessage.success('文章发表成功')
    router.push('/forum')
  } catch (error) {
    console.error('发表文章失败:', error)
    ElMessage.error('发表文章失败')
  } finally {
    isSubmitting.value = false
  }
}

// 返回论坛
const goBack = () => {
  router.push('/forum')
}

// 预览文章
const previewArticle = () => {
  // 这里可以实现预览功能
  ElMessage.info('预览功能待实现')
}

// 自动生成摘要按钮
const handleGenerateSummary = () => {
  generateSummary()
  if (articleForm.value.summary) {
    ElMessage.success('摘要已自动生成')
  } else {
    ElMessage.warning('请先输入文章内容')
  }
}
</script>

<template>
  <div class="create-article-container">
    <ElCard class="form-card">
      <!-- 页面标题 -->
      <div class="header-section">
        <h1 class="page-title">发表文章</h1>
        <p class="page-subtitle">分享您的知识和见解</p>
      </div>
      
      <ElForm ref="formRef" :model="articleForm" :rules="rules" label-width="0" class="article-form">
        <!-- 基本信息 -->
        <div class="form-section">
          <h2 class="section-title">基本信息</h2>
          <div class="section-content">
            <ElFormItem prop="title" class="form-item">
              <div class="input-wrapper">
                <span class="input-icon">📝</span>
                <ElInput 
                  v-model="articleForm.title" 
                  placeholder="请输入文章标题"
                  class="form-input"
                  size="large"
                  maxlength="100"
                  show-word-limit
                />
              </div>
            </ElFormItem>

            <ElFormItem class="form-item">
              <div class="input-wrapper">
                <span class="input-icon">📋</span>
                <ElInput 
                  v-model="articleForm.summary" 
                  type="textarea"
                  :rows="3"
                  placeholder="请输入文章摘要（可选，留空将自动生成）"
                  class="form-textarea"
                  maxlength="200"
                  show-word-limit
                />
              </div>
            </ElFormItem>

            <!-- 编辑器类型选择 -->
            <ElFormItem class="form-item">
              <div class="input-wrapper">
                <span class="input-icon">⚙️</span>
                <div class="editor-type-wrapper">
                  <label class="editor-label">编辑器类型：</label>
                  <ElRadioGroup v-model="articleForm.editorType" class="editor-radio">
                    <ElRadio :label="0">富文本编辑器</ElRadio>
                    <ElRadio :label="1">Markdown编辑器</ElRadio>
                  </ElRadioGroup>
                </div>
              </div>
            </ElFormItem>
          </div>
        </div>

        <!-- 文章封面 -->
        <div class="form-section">
          <h2 class="section-title">文章封面</h2>
          <div class="section-content">
            <ElFormItem class="form-item">
              <div class="cover-upload-container">
                <ElUpload
                  :auto-upload="false"
                  :on-change="handleCoverChange"
                  :show-file-list="false"
                  accept="image/*"
                  class="cover-uploader"
                >
                  <div class="upload-area">
                    <img v-if="coverPreview" :src="coverPreview" class="cover-preview" />
                    <div v-else class="upload-placeholder">
                      <i class="upload-icon">🖼️</i>
                      <p>点击上传封面图片</p>
                      <p class="upload-tip">建议尺寸：800x400px，支持 JPG、PNG 格式</p>
                    </div>
                  </div>
                </ElUpload>
              </div>
            </ElFormItem>
          </div>
        </div>

        <!-- 文章内容 -->
        <div class="form-section">
          <h2 class="section-title">文章内容</h2>
          <div class="section-content">
            <ElFormItem prop="content" class="form-item">
              <div class="content-editor">
                <ElInput
                  v-if="articleForm.editorType === 0"
                  v-model="articleForm.content"
                  type="textarea"
                  :rows="15"
                  placeholder="请输入文章内容（富文本模式）"
                  class="content-textarea"
                />
                <ElInput
                  v-else
                  v-model="articleForm.content"
                  type="textarea"
                  :rows="15"
                  placeholder="请输入文章内容（Markdown模式）"
                  class="content-textarea markdown-editor"
                />
              </div>
            </ElFormItem>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <ElButton 
            type="primary" 
            @click="submitArticle" 
            :loading="isSubmitting" 
            class="submit-btn"
            size="large"
          >
            <span v-if="isSubmitting">发表中...</span>
            <span v-else>发表文章</span>
          </ElButton>
          
          <ElButton 
            @click="previewArticle" 
            class="preview-btn"
            size="large"
          >
            预览
          </ElButton>
          
          <ElButton 
            @click="goBack" 
            class="cancel-btn"
            size="large"
          >
            返回
          </ElButton>
        </div>
      </ElForm>
    </ElCard>
  </div>
</template>

<style scoped lang="scss">
.create-article-container {
  background: url("@/assets/bookShelve.jpg") no-repeat fixed center center;
  background-size: cover;
  padding: 2rem;
  min-height: 100vh;
}

.form-card {
  max-width: 1000px;
  margin: 0 auto;
  background: rgba(20, 20, 20, 0.7);
  border: 1px solid rgba(255, 215, 0, 0.3);
  border-radius: 16px;
  padding: 3rem 2.5rem;
  color: #fff;
}

.header-section {
  text-align: center;
  margin-bottom: 3rem;
  
  .page-title {
    font-size: 2.5rem;
    color: #ffd700;
    margin-bottom: 0.5rem;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
    font-weight: bold;
  }
  
  .page-subtitle {
    color: #aaa;
    font-size: 1.1rem;
    margin: 0;
    line-height: 1.5;
  }
}

.article-form {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.form-section {
  background: rgba(30, 30, 30, 0.5);
  border-radius: 12px;
  overflow: hidden;
  
  .section-title {
    font-size: 1.3rem;
    color: #ffd700;
    margin: 0;
    padding: 1rem 1.5rem;
    background: rgba(40, 40, 40, 0.7);
    border-bottom: 1px solid #444;
    font-weight: 600;
  }
  
  .section-content {
    padding: 1.5rem;
  }
}

.form-item {
  margin-bottom: 1.5rem;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: flex-start;
  
  .input-icon {
    position: absolute;
    left: 15px;
    top: 50%;
    transform: translateY(-50%);
    font-size: 1.2rem;
    z-index: 2;
    opacity: 0.7;
  }
  
  .form-input, .form-textarea {
    width: 100%;
    
    :deep(.el-input__wrapper), :deep(.el-textarea__inner) {
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 215, 0, 0.3);
      border-radius: 12px;
      padding-left: 45px;
      box-shadow: none;
      transition: all 0.3s ease;
      
      &:hover {
        border-color: rgba(255, 215, 0, 0.5);
        background: rgba(255, 255, 255, 0.15);
      }
      
      &.is-focus {
        border-color: #ffd700;
        box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
        background: rgba(255, 255, 255, 0.15);
      }
    }
    
    :deep(.el-input__inner), :deep(.el-textarea__inner) {
      color: #fff;
      font-size: 1rem;
      
      &::placeholder {
        color: #aaa;
      }
    }
  }
  
  .editor-type-wrapper {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding-left: 45px;
    
    .editor-label {
      color: #fff;
      font-weight: 500;
    }
    
    .editor-radio {
      :deep(.el-radio__label) {
        color: #fff;
      }
      
      :deep(.el-radio__input.is-checked .el-radio__inner) {
        background-color: #ffd700;
        border-color: #ffd700;
      }
    }
  }
}

// 封面上传样式
.cover-upload-container {
  width: 100%;
}

.cover-uploader {
  :deep(.el-upload) {
    border: 2px dashed rgba(255, 215, 0, 0.3);
    border-radius: 12px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: all 0.3s ease;
    
    &:hover {
      border-color: #ffd700;
      background: rgba(255, 215, 0, 0.05);
    }
  }
}

.upload-area {
  width: 400px;
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(40, 40, 40, 0.5);
}

.upload-placeholder {
  text-align: center;
  color: #aaa;
  
  .upload-icon {
    font-size: 3rem;
    margin-bottom: 1rem;
    display: block;
  }
  
  p {
    margin: 0.5rem 0;
    
    &:first-of-type {
      font-size: 1.1rem;
      color: #fff;
    }
    
    &.upload-tip {
      font-size: 0.9rem;
      color: #666;
    }
  }
}

.cover-preview {
  width: 400px;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
}

// 内容编辑器样式
.content-editor {
  .content-textarea {
    :deep(.el-textarea__inner) {
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 215, 0, 0.3);
      border-radius: 12px;
      color: #fff;
      font-size: 1rem;
      line-height: 1.6;
      
      &:focus {
        border-color: #ffd700;
        box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
      }
      
      &::placeholder {
        color: #aaa;
      }
    }
    
    &.markdown-editor {
      :deep(.el-textarea__inner) {
        font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
      }
    }
  }
}

// 操作按钮样式
.form-actions {
  display: flex;
  justify-content: center;
  gap: 1.5rem;
  padding: 2rem 0;
  border-top: 1px solid rgba(255, 215, 0, 0.2);
  margin-top: 1rem;
}

.submit-btn {
  background: linear-gradient(135deg, rgba(255, 215, 0, 0.8), rgba(255, 215, 0, 0.6));
  border: none;
  color: #333;
  font-weight: bold;
  padding: 0 2.5rem;
  height: 45px;
  border-radius: 25px;
  
  &:hover:not(:disabled) {
    background: linear-gradient(135deg, rgba(255, 215, 0, 0.6), rgba(255, 215, 0, 0.4));
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(255, 215, 0, 0.3);
  }
  
  &:disabled {
    background: rgba(100, 100, 100, 0.5);
    color: #666;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
  }
}

.preview-btn, .cancel-btn {
  background: rgba(100, 100, 100, 0.7);
  border: 1px solid rgba(255, 215, 0, 0.3);
  color: #fff;
  padding: 0 2.5rem;
  height: 45px;
  border-radius: 25px;
  
  &:hover {
    background: rgba(255, 215, 0, 0.1);
    border-color: rgba(255, 215, 0, 0.5);
    transform: translateY(-2px);
  }
}

// 全局表单样式覆盖
:deep(.el-form-item__error) {
  color: #ff6b6b;
  font-size: 0.85rem;
  margin-top: 0.5rem;
}

// 响应式设计
@media (max-width: 768px) {
  .create-article-container {
    padding: 1rem;
  }
  
  .form-card {
    padding: 2rem 1.5rem;
  }
  
  .header-section {
    .page-title {
      font-size: 2rem;
    }
  }
  
  .upload-area {
    width: 100%;
    height: 150px;
  }
  
  .cover-preview {
    width: 100%;
    height: 150px;
  }
  
  .form-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .submit-btn, .preview-btn, .cancel-btn {
    width: 100%;
    max-width: 250px;
  }
}
</style>
