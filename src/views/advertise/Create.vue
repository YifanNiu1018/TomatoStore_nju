<!-- src/views/advertisement/CreateAdvertisementView.vue -->
<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElForm, ElCard, ElFormItem, ElInput, ElButton, ElUpload, ElImage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { createAd } from '@/api/advertise.ts'
import { uploadImage } from '@/api/tools.ts'

// 权限检查
const checkAdminPermission = () => {
  const role = sessionStorage.getItem("role");
  console.log('CreateAd页面权限检查，当前role:', role);

  if (role !== 'admin') {
    ElMessage.error('您没有权限访问此页面，请联系管理员');
    router.push('/productlist');
    return false;
  }
  return true;
}

const router = useRouter()
const route = useRoute()

const adForm = reactive({
  title: '',
  content: '',
  imageUrl: '',
  productId: route.params.id as string
})

const imagePreview = ref('')
const selectedFile = ref<File | null>(null)
const formRef = ref<InstanceType<typeof ElForm> | null>(null)
const isUploading = ref(false)

const rules = {
  title: [{ required: true, message: '广告标题不能为空' }],
  content: [{ required: true, message: '广告内容不能为空' }],
}

const handleImageChange = (file: any) => {
  selectedFile.value = file.raw
  imagePreview.value = URL.createObjectURL(file.raw)
}

const uploadAdImage = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择广告图片')
    return
  }

  try {

    const res = await uploadImage(selectedFile.value)
    adForm.imageUrl = res.data.data

  } catch (error) {
    ElMessage.error('图片上传失败')
    throw error
  } finally {
    isUploading.value = false
  }
}

const submitForm = async () => {
  if (!formRef.value) return

  try {

    await uploadAdImage()


    formRef.value.validate(async (valid) => {
      if (valid) {
        await createAd(adForm)
        ElMessage.success('广告创建成功')
        router.push(`/productlist/${route.params.id}`)
      }
    })
  } catch (error) {
    ElMessage.error('广告创建失败：' + (error as Error).message)
  }
}

onMounted(() => {
  checkAdminPermission();
})
</script>

<template>
  <div class="ad-create-container">
    <ElCard class="form-card">
      <!-- 页面标题 -->
      <div class="header-section">
        <h1 class="page-title">创建商品广告</h1>
        <p class="page-subtitle">为您的商品创建精美的推广广告</p>
      </div>

      <ElForm ref="formRef" :model="adForm" :rules="rules" label-width="0" class="ad-form">
        <!-- 基本信息区域 -->
        <div class="form-section">
          <h2 class="section-title">基本信息</h2>
          <div class="section-content">
            <ElFormItem prop="title" class="form-item">
              <div class="input-wrapper">
                <span class="input-icon">📢</span>
                <ElInput
                  v-model="adForm.title"
                  placeholder="请输入广告标题"
                  class="form-input"
                  size="large"
                />
              </div>
            </ElFormItem>

            <ElFormItem prop="content" class="form-item">
              <div class="input-wrapper">
                <span class="input-icon textarea-icon">📝</span>
                <ElInput
                  v-model="adForm.content"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入广告详细内容，描述商品的特色和优势"
                  class="form-textarea"
                />
              </div>
            </ElFormItem>

            <ElFormItem class="form-item">
              <div class="input-wrapper">
                <span class="input-icon">🔗</span>
                <ElInput
                  v-model="adForm.productId"
                  disabled
                  placeholder="关联商品ID"
                  class="form-input"
                  size="large"
                />
              </div>
            </ElFormItem>
          </div>
        </div>

        <!-- 广告图片区域 -->
        <div class="form-section">
          <h2 class="section-title">广告图片</h2>
          <div class="section-content">
            <ElFormItem class="form-item">
              <div class="image-upload-container">
                <ElUpload
                  :auto-upload="false"
                  :on-change="handleImageChange"
                  :show-file-list="false"
                  class="image-uploader"
                >
                  <div class="upload-area">
                    <ElImage
                      v-if="imagePreview"
                      :src="imagePreview"
                      class="preview-image"
                      fit="cover"
                    />
                    <div v-else class="upload-placeholder">
                      <i class="upload-icon">🖼️</i>
                      <p>点击上传广告图片</p>
                      <p class="upload-tip">建议尺寸：1200x600px，支持 JPG、PNG 格式</p>
                    </div>
                  </div>
                </ElUpload>
              </div>
            </ElFormItem>
          </div>
        </div>

        <!-- 操作按钮区域 -->
        <div class="form-actions">
          <ElButton
            type="primary"
            @click="submitForm"
            :loading="isUploading"
            class="submit-btn"
            size="large"
          >
            <span v-if="isUploading">创建中...</span>
            <span v-else>立即创建</span>
          </ElButton>
          <ElButton
            @click="router.go(-1)"
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
.ad-create-container {
  background: url("@/assets/bookShelve.jpg") no-repeat fixed center center;
  background-size: cover;
  padding: 2rem;
  min-height: 100vh;
}

.form-card {
  max-width: 900px;
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

.ad-form {
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

    &.textarea-icon {
      top: 20px;
      transform: none;
    }
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

      &:disabled {
        background: rgba(100, 100, 100, 0.3) !important;
        color: #888 !important;
      }
    }
  }
}

// 图片上传样式
.image-upload-container {
  width: 100%;
}

.image-uploader {
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
  height: 250px;
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

.preview-image {
  width: 400px;
  height: 250px;
  object-fit: cover;
  border-radius: 8px;
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

.cancel-btn {
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
  .ad-create-container {
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
    height: 200px;
  }

  .preview-image {
    width: 100%;
    height: 200px;
  }

  .form-actions {
    flex-direction: column;
    align-items: center;
  }

  .submit-btn, .cancel-btn {
    width: 100%;
    max-width: 250px;
  }
}
</style>