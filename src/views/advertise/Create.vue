<!-- src/views/advertisement/CreateAdvertisementView.vue -->
<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElForm } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { createAd } from '@/api/advertise.ts'
import { uploadImage } from '@/api/tools.ts'

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
</script>

<template>
  <div class="ad-create-container">
    <ElCard class="form-card">
      <h2 class="title">创建商品广告</h2>
      <ElForm ref="formRef" :model="adForm" :rules="rules" label-width="120px">

        <ElFormItem label="广告标题" prop="title">
          <ElInput v-model="adForm.title" placeholder="请输入广告标题" />
        </ElFormItem>

        <ElFormItem label="广告内容" prop="content">
          <ElInput
              v-model="adForm.content"
              type="textarea"
              :rows="4"
              placeholder="请输入广告详细内容"
          />
        </ElFormItem>

        <ElFormItem label="关联商品ID" prop="productId">
          <ElInput
              v-model="adForm.productId"
              disabled
          />
        </ElFormItem>

        <ElFormItem label="广告图片" prop="imageUrl">
          <el-upload
              :auto-upload="false"
              :on-change="handleImageChange"
              :show-file-list="false"
          >
            <el-image v-if="imagePreview" :src="imagePreview" class="preview-image"/>
            <el-button v-else type="primary">上传图片</el-button>
          </el-upload>
        </ElFormItem>

        <ElFormItem>
          <ElButton
              type="primary"
              @click="submitForm"
              :loading="isUploading"
          >立即创建</ElButton>
          <ElButton @click="router.go(-1)">返回</ElButton>
        </ElFormItem>
      </ElForm>
    </ElCard>
  </div>
</template>

<style scoped>
.ad-create-container {
  background: url("@/assets/bookShelve.jpg") no-repeat fixed center center;
  background-size: cover;
  padding: 2rem;
  min-height: 100vh;
}

.form-card {
  max-width: 800px;
  margin: 0 auto;
  background: rgba(20,20,20,0.8);
  border: none;
  color: #fff;
}

.title {
  text-align: center;
  color: #fff;
  margin-bottom: 2rem;
}

.preview-image {
  width: 300px;
  height: 200px;
  border-radius: 4px;
  object-fit: cover;
}

:deep(.el-form-item__label) {
  color: #fff !important;
}

:deep(.el-input__inner),
:deep(.el-textarea__inner) {
  background: #404040 !important;
  border-color: #555 !important;
  color: #fff !important;
}

:deep(.image-uploader .el-upload) {
  border: 1px dashed #666;
  border-radius: 4px;
  cursor: pointer;
  transition: border-color 0.3s;
}

:deep(.image-uploader .el-upload:hover) {
  border-color: #409eff;
}
</style>