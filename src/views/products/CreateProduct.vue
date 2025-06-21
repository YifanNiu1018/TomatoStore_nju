<!-- src/views/product/CreateProductView.vue -->
<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElForm, ElCard, ElFormItem, ElInput, ElSelect, ElOption, ElDatePicker, ElButton, ElTag, ElUpload, ElImage } from 'element-plus'
import type { ProductVO, SpecificationVO, StockpileVO } from '@/api/product'
import { createProduct } from '@/api/product'
import { uploadImage } from '@/api/tools.ts'
import router from '@/router'

// 权限检查
const checkAdminPermission = () => {
  const role = sessionStorage.getItem("role");
  console.log('CreateProduct页面权限检查，当前role:', role);

  if (role !== 'admin') {
    ElMessage.error('您没有权限访问此页面，请联系管理员');
    router.push('/productlist');
    return false;
  }
  return true;
}

onMounted(() => {
  checkAdminPermission();
})

const specItems = [
  '作者',
  '副标题',
  'ISBN',
  '装帧',
  '页数',
  '出版社',
  '出版日期'
]

const productForm = reactive<ProductVO>({
  title: '',
  price: 0,
  rate: 0,
  description: '',
  cover: '',
  detail: '',
  specifications: [],
  stockpile: { amount: 0, frozen: 0 }
})

const specifications = ref<SpecificationVO[]>([])
const newSpec = reactive({ item: '', value: '' })
const showDatePicker = ref(false)
const imagePreview = ref('')
const selectedFile = ref(null)

const handleImageChange = (file: any) => {
  selectedFile.value = file.raw // 直接赋值File类型
  imagePreview.value = URL.createObjectURL(file.raw)
}

const uploadPic = async () => {
  if (!selectedFile.value) return;

  try {
    const res = await uploadImage(selectedFile.value);
    productForm.cover = res.data.data;
  } catch (error) {
    ElMessage.error('Create Failed');
    console.error(error);
  }
};

const rules = {
  title: [{ required: true, message: '商品标题不能为空' }],
  price: [
    { required: true, message: '价格不能为空' },
    { pattern: /^\d+(\.\d{1,2})?$/, message: '最多保留两位小数' }
  ],
  'stockpile.amount': [
    { pattern: /^[1-9]\d*$/, message: '请输入正整数' }
  ],
  'stockpile.frozen': [
    { pattern: /^[0-9]\d*$/, message: '请输入非负整数' }
  ]
}

const formRef = ref<InstanceType<typeof ElForm> | null>(null)

const addSpecification = () => {
  if (!newSpec.item || !newSpec.value) {
    ElMessage.warning('请填写完整的规格参数')
    return
  }

  if (newSpec.item === '页数' && !/^\d+$/.test(newSpec.value)) {
    ElMessage.warning('页数必须为整数')
    return
  }

  if (newSpec.item === '出版日期' && !/^\d{4}\/\d{2}\/\d{2}$/.test(newSpec.value)) {
    ElMessage.warning('日期格式应为YYYY/MM/DD')
    return
  }

  specifications.value.push({
    item: newSpec.item,
    value: newSpec.value,
    productId: null
  })
  newSpec.item = ''
  newSpec.value = ''
}

const handleDateChange = (date: Date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  newSpec.value = `${year}/${month}/${day}`
  showDatePicker.value = false
}

const submitForm = async () => {
  if (!formRef.value) return

  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await uploadPic();
        productForm.specifications = specifications.value
        await createProduct(productForm).then(
          res => {
            ElMessage.success('商品创建成功')
            router.push('/productlist');
          }
        )
        // router.push(`/products/${res.data.id}`);
      } catch (error) {
        ElMessage.error('商品创建失败')
      }
    }
  })
}

const resetForm = () => {
  formRef.value?.resetFields()
  specifications.value = []
  productForm.stockpile = { amount: 0, frozen: 0 }
}
</script>

<template>
  <div class="product-create-container">
    <ElCard class="form-card">
      <!-- 页面标题 -->
      <h1 class="page-title">创建新商品</h1>

      <ElForm ref="formRef" :model="productForm" :rules="rules" label-width="120px" class="product-form">
        <!-- 基本信息区域 -->
        <div class="form-section">
          <h2 class="section-title">基本信息</h2>
          <div class="section-content">
            <ElFormItem label="商品标题" prop="title" class="form-item">
              <ElInput
                v-model="productForm.title"
                placeholder="请输入商品标题"
                class="form-input"
              />
            </ElFormItem>

            <ElFormItem label="商品价格" prop="price" class="form-item">
              <ElInput
                v-model.number="productForm.price"
                placeholder="请输入价格"
                class="form-input"
              >
                <template #prepend>¥</template>
              </ElInput>
            </ElFormItem>

            <ElFormItem label="商品描述" class="form-item">
              <ElInput
                v-model="productForm.description"
                type="textarea"
                :rows="4"
                placeholder="请输入商品描述"
                class="form-textarea"
              />
            </ElFormItem>
          </div>
        </div>

        <!-- 商品图片区域 -->
        <div class="form-section">
          <h2 class="section-title">商品图片</h2>
          <div class="section-content">
            <ElFormItem label="商品图片" required class="form-item">
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
                      <i class="upload-icon">📷</i>
                      <p>点击上传商品图片</p>
                      <p class="upload-tip">支持 JPG、PNG 格式</p>
                    </div>
                  </div>
                </ElUpload>
              </div>
            </ElFormItem>
          </div>
        </div>

        <!-- 规格参数区域 -->
        <div class="form-section">
          <h2 class="section-title">规格参数</h2>
          <div class="section-content">
            <ElFormItem label="添加规格" class="form-item">
              <div class="spec-form">
                <ElSelect
                  v-model="newSpec.item"
                  placeholder="选择参数项"
                  class="spec-select"
                  clearable
                >
                  <ElOption
                    v-for="item in specItems"
                    :key="item"
                    :label="item"
                    :value="item"
                  />
                </ElSelect>

                <template v-if="newSpec.item === '出版日期'">
                  <ElDatePicker
                    v-model="newSpec.value"
                    type="date"
                    placeholder="选择日期"
                    value-format="YYYY/MM/DD"
                    class="spec-input"
                  />
                </template>

                <template v-else>
                  <ElInput
                    v-model="newSpec.value"
                    :placeholder="newSpec.item === '页数' ? '请输入整数' : '请输入参数值'"
                    class="spec-input"
                    :type="newSpec.item === '页数' ? 'number' : 'text'"
                    :min="newSpec.item === '页数' ? 1 : null"
                  />
                </template>

                <ElButton type="primary" @click="addSpecification" class="add-spec-btn">
                  添加规格
                </ElButton>
              </div>

              <div class="spec-list" v-if="specifications.length > 0">
                <h3 class="spec-list-title">已添加的规格：</h3>
                <div class="spec-tags">
                  <ElTag
                    v-for="(spec, index) in specifications"
                    :key="index"
                    type="info"
                    closable
                    @close="specifications.splice(index, 1)"
                    class="spec-tag"
                  >
                    {{ spec.item }}: {{ spec.value }}
                  </ElTag>
                </div>
              </div>
            </ElFormItem>
          </div>
        </div>

        <!-- 库存信息区域 -->
        <div class="form-section">
          <h2 class="section-title">库存信息</h2>
          <div class="section-content">
            <div class="stock-row">
              <ElFormItem label="库存总量" prop="stockpile.amount" class="form-item stock-item">
                <ElInput
                  v-model.number="productForm.stockpile.amount"
                  type="number"
                  placeholder="请输入正整数"
                  :min="1"
                  class="form-input"
                >
                  <template #append>件</template>
                </ElInput>
              </ElFormItem>

              <ElFormItem label="冻结数量" prop="stockpile.frozen" class="form-item stock-item">
                <ElInput
                  v-model.number="productForm.stockpile.frozen"
                  type="number"
                  placeholder="请输入非负整数"
                  :min="0"
                  class="form-input"
                >
                  <template #append>件</template>
                </ElInput>
              </ElFormItem>
            </div>

            <div class="stock-info">
              <p class="stock-tip">
                <span class="tip-icon">💡</span>
                可售数量 = 库存总量 - 冻结数量 = {{ Math.max(0, (productForm.stockpile?.amount || 0) - (productForm.stockpile?.frozen || 0)) }} 件
              </p>
            </div>
          </div>
        </div>

        <!-- 操作按钮区域 -->
        <div class="form-actions">
          <ElButton type="primary" @click="submitForm" class="submit-btn">
            立即创建
          </ElButton>
          <ElButton @click="resetForm" class="reset-btn">
            重置表单
          </ElButton>
        </div>
      </ElForm>
    </ElCard>
  </div>
</template>

<style scoped lang="scss">
.product-create-container {
  background: url("@/assets/bookShelve.jpg") no-repeat fixed center center;
  background-size: cover;
  padding: 2rem;
  min-height: 100vh;
}

.form-card {
  max-width: 900px;
  margin: 0 auto;
  background: rgba(20, 20, 20, 0.7);
  border: none;
  color: #fff;
  padding: 2rem;
}

.page-title {
  font-size: 2rem;
  margin-bottom: 2rem;
  color: #ffd700;
  border-bottom: 1px solid #444;
  padding-bottom: 1rem;
  text-align: center;
}

.product-form {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.form-section {
  background: rgba(30, 30, 30, 0.5);
  border-radius: 8px;
  overflow: hidden;

  .section-title {
    font-size: 1.3rem;
    color: #ffd700;
    margin: 0;
    padding: 1rem 1.5rem;
    background: rgba(40, 40, 40, 0.7);
    border-bottom: 1px solid #444;
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

.form-input, .form-textarea {
  :deep(.el-input__inner) {
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
    border-color: #555;

    &:focus {
      border-color: #ffd700;
      box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
    }
  }

  :deep(.el-input-group__prepend) {
    background: rgba(255, 215, 0, 0.3);
    color: #333;
    border-color: #555;
  }

  :deep(.el-input-group__append) {
    background: rgba(255, 215, 0, 0.3);
    color: #333;
    border-color: #555;
  }
}

.form-textarea {
  :deep(.el-textarea__inner) {
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
    border-color: #555;

    &:focus {
      border-color: #ffd700;
      box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
    }
  }
}

// 图片上传样式
.image-upload-container {
  width: 100%;
}

.image-uploader {
  :deep(.el-upload) {
    border: 2px dashed #555;
    border-radius: 8px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: border-color 0.3s;

    &:hover {
      border-color: #ffd700;
    }
  }
}

.upload-area {
  width: 300px;
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

    &.upload-tip {
      font-size: 0.9rem;
      color: #666;
    }
  }
}

.preview-image {
  width: 300px;
  height: 200px;
  object-fit: cover;
  border-radius: 6px;
}

// 规格参数样式
.spec-form {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.spec-select, .spec-input {
  min-width: 150px;

  :deep(.el-input__inner) {
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
    border-color: #555;

    &:focus {
      border-color: #ffd700;
      box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
    }
  }
}

.add-spec-btn {
  background: rgba(255, 215, 0, 0.7);
  border: none;
  color: #333;
  font-weight: bold;

  &:hover {
    background: rgba(255, 215, 0, 0.5);
  }
}

.spec-list {
  margin-top: 1rem;

  .spec-list-title {
    color: #aaa;
    font-size: 1rem;
    margin-bottom: 0.8rem;
  }

  .spec-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 0.5rem;
  }

  .spec-tag {
    :deep(.el-tag) {
      background: rgba(40, 40, 40, 0.7);
      border-color: #555;
      color: #fff;

      .el-tag__close {
        color: #aaa;

        &:hover {
          color: #fff;
          background: rgba(220, 50, 0, 0.7);
        }
      }
    }
  }
}

// 库存信息样式
.stock-row {
  display: flex;
  gap: 2rem;
  margin-bottom: 1rem;

  .stock-item {
    flex: 1;
  }
}

.stock-info {
  padding: 1rem;
  background: rgba(40, 40, 40, 0.5);
  border-radius: 6px;
  border-left: 4px solid #ffd700;

  .stock-tip {
    margin: 0;
    color: #aaa;
    display: flex;
    align-items: center;
    gap: 0.5rem;

    .tip-icon {
      font-size: 1.2rem;
    }
  }
}

// 操作按钮样式
.form-actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
  padding: 2rem 0;
  border-top: 1px solid #444;
  margin-top: 1rem;
}

.submit-btn {
  background: rgba(255, 215, 0, 0.7);
  border: none;
  color: #333;
  font-weight: bold;
  padding: 0 2rem;
  height: 40px;

  &:hover {
    background: rgba(255, 215, 0, 0.5);
  }
}

.reset-btn {
  background: rgba(100, 100, 100, 0.7);
  border: none;
  color: #fff;
  padding: 0 2rem;
  height: 40px;

  &:hover {
    background: rgba(100, 100, 100, 0.5);
  }
}

// 全局表单样式覆盖
:deep(.el-form-item__label) {
  color: #aaa !important;
  font-weight: 500;
}

// 修复所有输入框样式
:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid #555 !important;
  box-shadow: none !important;

  &:hover {
    border-color: #ffd700 !important;
  }

  &.is-focus {
    border-color: #ffd700 !important;
    box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2) !important;
  }
}

:deep(.el-input__inner) {
  background: transparent !important;
  color: #fff !important;
  border: none !important;

  &::placeholder {
    color: #aaa !important;
  }
}

:deep(.el-textarea__inner) {
  background: rgba(255, 255, 255, 0.1) !important;
  color: #fff !important;
  border: 1px solid #555 !important;

  &:focus {
    border-color: #ffd700 !important;
    box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2) !important;
  }

  &::placeholder {
    color: #aaa !important;
  }
}

:deep(.el-select__wrapper) {
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid #555 !important;
  box-shadow: none !important;

  &:hover {
    border-color: #ffd700 !important;
  }

  &.is-focused {
    border-color: #ffd700 !important;
    box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2) !important;
  }
}

:deep(.el-select__selected-item) {
  color: #fff !important;
}

:deep(.el-select__placeholder) {
  color: #aaa !important;
}

:deep(.el-input-number__wrapper) {
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid #555 !important;
  box-shadow: none !important;

  &:hover {
    border-color: #ffd700 !important;
  }

  &.is-focus {
    border-color: #ffd700 !important;
    box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2) !important;
  }
}

:deep(.el-input-number__wrapper .el-input__inner) {
  background: transparent !important;
  color: #fff !important;
  border: none !important;
}

:deep(.el-select .el-input .el-select__caret) {
  color: #aaa;
}

:deep(.el-select-dropdown) {
  background: rgba(40, 40, 40, 0.95);
  border-color: #555;
}

:deep(.el-select-dropdown__item) {
  color: #fff;

  &:hover {
    background: rgba(255, 215, 0, 0.2);
  }

  &.selected {
    background: rgba(255, 215, 0, 0.3);
    color: #333;
  }
}

:deep(.el-date-picker) {
  .el-input__inner {
    background: rgba(255, 255, 255, 0.1) !important;
    color: #fff !important;
    border-color: #555 !important;
  }
}

// 修复日期选择器的输入框
:deep(.el-date-editor) {
  .el-input__wrapper {
    background: rgba(255, 255, 255, 0.1) !important;
    border: 1px solid #555 !important;

    &:hover {
      border-color: #ffd700 !important;
    }

    &.is-focus {
      border-color: #ffd700 !important;
      box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2) !important;
    }
  }

  .el-input__inner {
    background: transparent !important;
    color: #fff !important;
    border: none !important;

    &::placeholder {
      color: #aaa !important;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .form-card {
    margin: 1rem;
    padding: 1rem;
  }

  .spec-form {
    flex-direction: column;
    align-items: stretch;
  }

  .stock-row {
    flex-direction: column;
    gap: 1rem;
  }

  .form-actions {
    flex-direction: column;
  }

  .upload-area {
    width: 100%;
    height: 150px;
  }

  .preview-image {
    width: 100%;
    height: 150px;
  }
}
</style>