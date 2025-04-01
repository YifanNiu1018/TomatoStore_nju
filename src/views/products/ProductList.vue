<!-- src/views/product/ProductsView.vue -->
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElRow, ElCol, ElCard, ElRate } from 'element-plus'
import type { ProductVO } from '@/api/product'
import { getAllProducts } from '@/api/product'
import router from "@/router";

const products = ref<ProductVO[]>([])

const loadProducts = async () => {
  try {
    const res = await getAllProducts()
    products.value = res.data.data
  } catch (error) {
    ElMessage.error('商品加载失败')
  }
}

onMounted(() => {
  loadProducts()
})
// products.value = [
//   {
//     id: 1,
//     title: 'aaa',
//     price: 100,
//     rate: 0,
//     description: 'none',
//     cover: 'data:image/jpeg;base64,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',
//   },
// ]


</script>

<template>
  <div class="product-container">
    <ElRow :gutter="20" class="product-grid">
      <ElCol
        v-for="product in products"
        :key="product.id"
        :xs="24" :sm="12" :md="8" :lg="6"
        class="product-col"
      >
        <ElCard class="product-card" shadow="hover" @click="router.push(`/productlist/${product.id}`)">
          <!--          product img-->
          <div class="product-cover">
            <img
              :src="product.cover || '/src/assets/loadFailed.png'"
              :alt="product.title"
            >
          </div>

          <!--          info about this product-->
          <div class="product-info">
            <h3 class="product-title">{{ product.title }}</h3>

            <div class="product-meta">
              <ElRate
                v-model="product.rate"
                disabled
                :colors="['#ffd700', '#ffd700', '#ffd700']"
                class="product-rate"
              />
              <span class="product-price">¥{{ product.price }}</span>
            </div>

            <p class="product-desc">{{ product.description }}</p>
          </div>
        </ElCard>
      </ElCol>
    </ElRow>
  </div>
</template>

<style scoped lang="scss">
.product-container {
  background: url("@/assets/bookShelve.jpg") no-repeat fixed center center;
  padding: 2rem;
  min-height: 100vh;
}

.product-grid {
  min-width: 100px;
  min-height: 100px;
  max-width: 1400px;
  margin: 0 auto;
}

.product-col {
  margin-bottom: 20px;
}

.product-card {
  background: #2d2d2d;
  border: none;
  border-radius: 8px;
  transition: transform 0.3s ease;

  &:hover {
    transform: translateY(-5px);
  }
}

.product-cover {
  height: 250px;
  overflow: hidden;
  border-radius: 6px 6px 0 0;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;

    &:hover {
      transform: scale(1.05);
    }
  }
}

.product-info {
  padding: 1.2rem;
}

.product-title {
  color: #fff;
  font-size: 1.1rem;
  margin: 0 0 1rem;
  font-weight: 500;
  line-height: 1.4;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.product-price {
  color: #ffd700;
  font-size: 1.2rem;
  font-weight: 600;
}

.product-desc {
  color: #888;
  font-size: 0.9rem;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

:deep(.el-card__body) {
  padding: 0;
}
</style>