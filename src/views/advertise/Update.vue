<!-- src/views/advertisement/AdvertisementUpdateView.vue -->
<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import {ElMessage, ElForm, ElLoading} from 'element-plus'
import { getAdById, updateAd } from '@/api/advertise.ts'
import { uploadImage } from '@/api/tools.ts'
import router from '@/router'
import { useRoute } from 'vue-router'

const route = useRoute()
const adId = ref<number>(Number(route.params.id))

// 广告表单数据
const adForm = reactive({
  id: adId.value,
  title: '',
  content: '',
  imageUrl: '',
  productId: ''
})

// 图片相关状态
const imagePreview = ref('')
const selectedFile = ref<File | null>(null)
const formRef = ref<InstanceType<typeof ElForm> | null>(null)
const isUploading = ref(false)

const rules = {
  title: [{ required: true, message: '广告标题不能为空' }],
  content: [{ required: true, message: '广告内容不能为空' }],
  imageUrl: [{ required: true, message: '广告图片不能为空' }]
}

const fetchAdvertisement = async () => {
  try {
    const loading = ElLoading.service({
      lock: true,
      text: '正在加载广告数据...',
    });

    const res = await getAdById(adId.value.toString());
    if (!res || typeof res !== 'object') {
      throw new Error('无效的广告数据格式');
    }


    const normalizedData = {
      id: res.id,
      title: res.title,
      content: res.content,
      imageUrl: res.imageUrl,
      productId: res.productId
    };


    Object.assign(adForm, normalizedData);
    imagePreview.value = normalizedData.imageUrl;

    loading.close();
  } catch (error) {
    ElMessage.error(`广告加载失败: ${error.message}`);
    console.error('详细错误信息:', {
      error,
      adId: adId.value,
      routeParams: route.params
    });
    router.push('/advertise/manage');
  }
}

onMounted(() => {
  fetchAdvertisement()
})

const handleImageChange = (file: any) => {
  selectedFile.value = file.raw
  imagePreview.value = URL.createObjectURL(file.raw)
  formRef.value?.validateField('imageUrl')
}

const uploadAdImage = async () => {
  if (!selectedFile.value) return

  try {
    isUploading.value = true
    const res = await uploadImage(selectedFile.value)
    alert("pic upload suc")
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
    await updateAd(adForm)
    ElMessage.success('广告更新成功')
    router.push(`/advertise/manage`)
  } catch (error) {
    ElMessage.error('广告更新失败')
  }
}
</script>

<template>
  <div class="ad-update-container">
    <ElCard class="form-card">
      <h2 class="title">编辑广告</h2>
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

        <ElFormItem label="关联商品ID">
          <ElInput
              v-model="adId"
              disabled
              placeholder="自动获取商品ID"
          />
        </ElFormItem>

        <ElFormItem label="广告图片" prop="imageUrl" required>
          <el-upload
              :auto-upload="false"
              :on-change="handleImageChange"
              :show-file-list="false"
              class="image-uploader"
          >
            <template v-if="imagePreview">
              <el-image
                  :src="imagePreview"
                  class="preview-image"
                  fit="cover"
              />
              <div class="update-tip">点击更换图片</div>
            </template>
            <el-button v-else type="primary">上传图片</el-button>
          </el-upload>
        </ElFormItem>

        <ElFormItem>
          <ElButton
              type="primary"
              @click="submitForm"
              :loading="isUploading"
          >保存修改</ElButton>
          <ElButton @click="router.go(-1)">返回</ElButton>
        </ElFormItem>
      </ElForm>
    </ElCard>
  </div>
</template>

<style scoped>
.ad-update-container {
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
  position: relative;
}

.update-tip {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  color: #fff;
  background: rgba(0,0,0,0.5);
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
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