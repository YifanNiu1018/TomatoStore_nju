<!-- src/views/product/ProductUpdate.vue -->
<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElForm } from 'element-plus'
import {
  getProductById,
  getStockpile,
  type ProductVO,
  type SpecificationVO,
  type StockpileVO,
  updateProduct, updateStockpile
} from '@/api/product'
import { uploadImage } from '@/api/tools.ts'
import router from '@/router'
import { useRoute } from 'vue-router'

const route = useRoute()
const productId = ref<number>(Number(route.params.id))

// 规格参数预设选项
const specItems = [
  '作者',
  '副标题',
  'ISBN',
  '装帧',
  '页数',
  '出版社',
  '出版日期'
]

// 商品表单数据
const productForm = reactive<ProductVO>({
  id: productId.value,
  title: '',
  price: 0,
  rate: 0,
  description: '',
  cover: '',
  detail: '',
  specifications: [],
})

const stockpile = ref<StockpileVO>({
    amount: 0, frozen: 0,
})

// 规格参数管理
const specifications = ref<SpecificationVO[]>([])
const newSpec = reactive({ item: '', value: '' })
const showDatePicker = ref(false)
const imagePreview = ref('')
const selectedFile = ref(null)

// 获取商品详情
const fetchProduct = async () => {
  try {
    const res = await getProductById(productId.value)
    const productData = res.data.data
    const stockRes = await getStockpile(productId.value)
    stockpile.value = stockRes.data.data
    // 填充表单数据
    Object.assign(productForm, productData)
    specifications.value = productData.specifications || []
    imagePreview.value = productData.cover
  } catch (error) {
    ElMessage.error('获取商品信息失败')
    router.push('/productlist')
  }
}

onMounted(() => {
  fetchProduct()
})

const handleImageChange = (file: any) => {
  selectedFile.value = file.raw
  imagePreview.value = URL.createObjectURL(file.raw)
}

const uploadPic = async () => {
  if (selectedFile.value) {
    try {
      const res = await uploadImage(selectedFile.value)
      productForm.cover = res.data.data
    } catch (error) {
      ElMessage.error('图片上传失败')
      throw error
    }
  }
}

// 表单验证规则
const rules = {
  title: [{ required: true, message: '商品标题不能为空' }],
  price: [
    { required: true, message: '价格不能为空' },
    { pattern: /^(0|([1-9][0-9]*))(\.[\d]{1,2})?$/, message: '最多保留两位小数' }
  ],
  'stockpile.amount': [
    { pattern: /^[1-9]\d*$/, message: '请输入正整数' }
  ],
  'stockpile.frozen': [
    { pattern: /^[0-9]\d*$/, message: '请输入非负整数' }
  ]
}

const formRef = ref<InstanceType<typeof ElForm> | null>(null)

// 添加规格参数
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
    productId: productId.value
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
        await uploadPic()
        productForm.specifications = specifications.value

        await updateProduct(productForm)
        await updateStockpile(productId.value, stockpile.value)
        ElMessage.success('商品更新成功')
        router.push(`/productlist/${productId.value}`)
      } catch (error) {
        ElMessage.error('商品更新失败')
      }
    }
  })
}

const resetForm = () => {
  fetchProduct()
}
</script>

<template>
  <div class="product-create-container">
    <ElCard class="form-card">
      <h2 class="title">编辑商品</h2>
      <ElForm ref="formRef" :model="productForm" :rules="rules" label-width="120px">
        <ElFormItem label="商品标题" prop="title">
          <ElInput v-model="productForm.title" placeholder="请输入商品标题" />
        </ElFormItem>

        <ElFormItem label="价格" prop="price">
          <ElInput
            v-model.number="productForm.price"
            placeholder="请输入价格"
            :formatter="value => `¥ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
            :parser="value => value.replace(/¥\s?|(,*)/g, '')"
          />
        </ElFormItem>

        <ElFormItem label="商品描述">
          <ElInput v-model="productForm.description" type="textarea" :rows="3" />
        </ElFormItem>

        <el-form-item label="商品图片" required>
          <el-upload
            :auto-upload="false"
            :on-change="handleImageChange"
            :show-file-list="false"
          >
            <el-image v-if="imagePreview" :src="imagePreview" class="preview-image"/>
            <el-button type="primary">更换图片</el-button>
          </el-upload>
        </el-form-item>

        <ElFormItem label="规格参数">
          <div class="spec-form">
            <ElSelect
              v-model="newSpec.item"
              placeholder="选择参数项"
              style="width: 150px"
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
                style="margin-left: 10px; width: 200px"
              />
            </template>

            <template v-else>
              <ElInput
                v-model="newSpec.value"
                :placeholder="newSpec.item === '页数' ? '请输入整数' : '请输入参数值'"
                style="width: 200px; margin: 0 10px"
                :type="newSpec.item === '页数' ? 'number' : 'text'"
                :min="newSpec.item === '页数' ? 1 : null"
              />
            </template>

            <ElButton type="primary" @click="addSpecification">添加规格</ElButton>
          </div>
          <div class="spec-list">
            <ElTag
              v-for="(spec, index) in specifications"
              :key="index"
              type="info"
              closable
              @close="specifications.splice(index, 1)"
            >
              {{ spec.item }}: {{ spec.value }}
            </ElTag>
          </div>
        </ElFormItem>

        <ElFormItem label="库存总量" prop="stockpile.amount">
          <ElInput
            v-model.number="stockpile.amount"
            type="number"
            placeholder="请输入正整数"
            :min="1"
          />
        </ElFormItem>

        <ElFormItem label="冻结数量" prop="stockpile.frozen">
          <ElInput
            v-model.number="stockpile.frozen"
            type="number"
            placeholder="请输入非负整数"
            :min="0"
          />
        </ElFormItem>

        <ElFormItem>
          <ElButton type="primary" @click="submitForm">保存修改</ElButton>
          <ElButton @click="resetForm">重置修改</ElButton>
        </ElFormItem>
      </ElForm>
    </ElCard>
  </div>
</template>

<style scoped>
.product-create-container {
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

.spec-form {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
  gap: 10px;
}

.spec-list {
  display: flex;
  flex-wrap: wrap;
  width: 100%;
  gap: 8px;
}

:deep(.el-form-item__label) {
  color: #fff !important;
}

:deep(.el-input__inner),
:deep(.el-select .el-input__inner),
:deep(.el-textarea__inner) {
  background: #404040 !important;
  border-color: #555 !important;
  color: #fff !important;
}

:deep(.el-input-number__decrease),
:deep(.el-input-number__increase) {
  background: #505050;
  color: #fff;
  border-color: #555;
}
</style>