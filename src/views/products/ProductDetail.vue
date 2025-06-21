<script setup lang="ts">
import {ref, onMounted, computed} from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElCard, ElTag, ElRate, ElButton, ElInputNumber, ElMessageBox } from 'element-plus'
import { deleteProduct, type ProductVO, type StockpileVO, addToCart } from '@/api/product'
import { getProductById, getStockpile } from '@/api/product'
import router from "@/router";

const route = useRoute()
const productId = ref<number>(Number(route.params.id))
const product = ref<ProductVO>({} as ProductVO)
const stockpile = ref<StockpileVO>({} as StockpileVO)
const loading = ref(true)
const role = sessionStorage.getItem('role')
const quantity = ref(1) // 购物车数量
const maxQuantity = computed(() => {
  const available = (stockpile.value.amount || 0) - (stockpile.value.frozen || 0)
  return Math.max(available, 1) // 确保至少为1
})

const fetchProduct = async () => {
  try {
    const res = await getProductById(productId.value)
    product.value = res.data.data
    const stockRes = await getStockpile(productId.value)
    stockpile.value = stockRes.data.data
  } catch (error) {
    ElMessage.error('获取商品信息失败')
  } finally {
    loading.value = false
  }
}

const handleAddToCart = async () => {
  console.log("productid: " + productId.value);
  try {
    const available = (stockpile.value.amount || 0) - (stockpile.value.frozen || 0)
    if (quantity.value > available) {
      ElMessage.warning(`库存不足，当前可售数量为 ${available}`)
      return
    }

    await addToCart(productId.value, quantity.value)
    ElMessage.success('商品已成功加入购物车')
  } catch (error) {
    ElMessage.error('加入购物车失败')
    console.error('Add to cart error:', error)
  }
}

onMounted(() => {
  fetchProduct();
})

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除商品"${product.value.title}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    const res = await deleteProduct(productId.value)
    ElMessage.success('商品删除成功')
    router.push('/productlist')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除商品失败');
      console.error('Delete ERROR: ', error);
    }
  }
}

// 跳转到更新商品页面
const handleUpdate = () => {
  console.log('跳转到更新页面，productId:', productId.value)
  router.push(`/productlist/${productId.value}/update`)
}

// 跳转到创建广告页面
const handleCreateAd = () => {
  console.log('跳转到创建广告页面，productId:', productId.value)
  router.push(`/advertise/${productId.value}/create`)
}
</script>

<template>
  <div class="product-detail-container">
    <ElCard class="detail-card">
      <!-- 页面标题 -->
      <h1 class="page-title">商品详情</h1>

      <div v-if="loading" class="loading-text">加载中...</div>

      <div v-else class="product-content">
        <!-- 商品主要信息 -->
        <div class="product-main">
          <div class="cover-container">
            <img
              :src="product.cover || '/src/assets/loadFailed.png'"
              :alt="product.title"
              class="detail-cover"
            >
          </div>

          <div class="product-info">
            <h2 class="product-title">{{ product.title }}</h2>

            <div class="price-section">
              <span class="price-label">价格：</span>
              <span class="price">¥{{ product.price?.toFixed(2) }}</span>
            </div>

            <div class="rating-section" v-if="product.rate">
              <span class="rating-label">评分：</span>
              <ElRate
                v-model="product.rate"
                disabled
                :colors="['#ffd700', '#ffd700', '#ffd700']"
                class="rating"
              />
              <span class="rating-text">{{ product.rate?.toFixed(1) }} 分</span>
            </div>

            <!-- 库存信息 -->
            <div class="stock-section">
              <div class="stock-item">
                <span class="stock-label">总库存：</span>
                <span class="stock-value">{{ stockpile.amount || 0 }} 件</span>
              </div>
              <div class="stock-item">
                <span class="stock-label">可售数量：</span>
                <span class="stock-value available">{{ (stockpile.amount || 0) - (stockpile.frozen || 0) }} 件</span>
              </div>
            </div>

            <!-- 购物车操作区 -->
            <div class="cart-operations">
              <div class="quantity-section">
                <span class="quantity-label">数量：</span>
                <ElInputNumber
                  v-model="quantity"
                  :min="1"
                  :max="maxQuantity"
                  class="quantity-input"
                />
              </div>
              <ElButton
                type="warning"
                @click="handleAddToCart"
                class="add-to-cart-btn"
                :disabled="(stockpile.amount || 0) - (stockpile.frozen || 0) <= 0"
              >
                <span v-if="(stockpile.amount || 0) - (stockpile.frozen || 0) <= 0">库存不足</span>
                <span v-else>加入购物车</span>
              </ElButton>
            </div>

            <!-- 管理员操作按钮 -->
            <div v-if="role === 'admin'" class="admin-operations">
              <h3 class="admin-title">管理员操作</h3>
              <div class="admin-buttons">
                <ElButton type="danger" @click="handleDelete" class="admin-btn">
                  删除商品
                </ElButton>
                <ElButton type="primary" @click="handleUpdate" class="admin-btn">
                  更新商品
                </ElButton>
                <ElButton type="success" @click="handleCreateAd" class="admin-btn">
                  创建广告
                </ElButton>
              </div>
            </div>
          </div>
        </div>

        <!-- 商品详细信息 -->
        <div class="product-details">
          <!-- 商品描述 -->
          <div class="detail-section">
            <h2 class="section-title">商品描述</h2>
            <div class="section-content">
              <p class="description">{{ product.description || '暂无描述' }}</p>
            </div>
          </div>

          <!-- 商品规格 -->
          <div class="detail-section" v-if="product.specifications?.length">
            <h2 class="section-title">商品规格</h2>
            <div class="section-content">
              <div class="spec-grid">
                <div
                  v-for="spec in product.specifications"
                  :key="spec.item"
                  class="spec-item"
                >
                  <span class="spec-label">{{ spec.item }}</span>
                  <span class="spec-value">{{ spec.value }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 商品详情 -->
          <div class="detail-section" v-if="product.detail">
            <h2 class="section-title">详细信息</h2>
            <div class="section-content">
              <div class="product-detail-content" v-html="product.detail"></div>
            </div>
          </div>
        </div>
      </div>
    </ElCard>
  </div>
</template>

<style scoped lang="scss">
.product-detail-container {
  background: url("@/assets/bookShelve.jpg") no-repeat fixed center center;
  background-size: cover;
  padding: 2rem;
  min-height: 100vh;
}

.detail-card {
  max-width: 1200px;
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
}

.loading-text {
  color: #aaa;
  text-align: center;
  padding: 2rem;
}

.product-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.product-main {
  display: flex;
  gap: 2rem;
  padding: 1.5rem;
  background: rgba(30, 30, 30, 0.5);
  border-radius: 8px;
}

.cover-container {
  flex: 0 0 350px;

  .detail-cover {
    width: 100%;
    height: 450px;
    object-fit: cover;
    border-radius: 8px;
  }
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.product-title {
  font-size: 1.8rem;
  color: #fff;
  margin: 0;

  &:hover {
    color: #ffd700;
  }
}

.price-section {
  display: flex;
  align-items: center;
  gap: 1rem;

  .price-label {
    color: #aaa;
    font-size: 1.1rem;
  }

  .price {
    font-size: 2rem;
    color: #ffd700;
    font-weight: bold;
  }
}

.rating-section {
  display: flex;
  align-items: center;
  gap: 1rem;

  .rating-label {
    color: #aaa;
    font-size: 1.1rem;
  }

  .rating {
    :deep(.el-rate__icon) {
      font-size: 20px;
    }
  }

  .rating-text {
    color: #ffd700;
    font-weight: bold;
  }
}

.stock-section {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 1rem;
  background: rgba(40, 40, 40, 0.5);
  border-radius: 6px;

  .stock-item {
    display: flex;
    justify-content: space-between;

    .stock-label {
      color: #aaa;
    }

    .stock-value {
      color: #fff;
      font-weight: bold;

      &.available {
        color: #ffd700;
      }
    }
  }
}

.cart-operations {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1rem;
  background: rgba(40, 40, 40, 0.5);
  border-radius: 6px;

  .quantity-section {
    display: flex;
    align-items: center;
    gap: 1rem;

    .quantity-label {
      color: #aaa;
      min-width: 60px;
    }

    .quantity-input {
      width: 120px;

      :deep(.el-input__inner) {
        background: rgba(255, 255, 255, 0.1);
        color: #333;
        border-color: #555;
        font-weight: bold;
      }

      :deep(.el-input-number__decrease),
      :deep(.el-input-number__increase) {
        background: rgba(255, 215, 0, 0.3);
        color: #333;
        border-color: #555;

        &:hover {
          color: #000;
          background: rgba(255, 215, 0, 0.5);
        }
      }

      :deep(.is-disabled) {
        opacity: 0.7;
        color: #666 !important;
      }

      :deep(.el-input__inner:focus) {
        border-color: #ffd700;
        box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
      }
    }
  }

  .add-to-cart-btn {
    background: rgba(255, 215, 0, 0.7);
    border: none;
    color: #333;
    font-weight: bold;
    padding: 0 2rem;
    height: 40px;

    &:hover:not(:disabled) {
      background: rgba(255, 215, 0, 0.5);
    }

    &:disabled {
      background: rgba(100, 100, 100, 0.5);
      color: #666;
      cursor: not-allowed;
    }
  }
}

.admin-operations {
  margin-top: 1rem;
  padding: 1rem;
  background: rgba(40, 40, 40, 0.5);
  border-radius: 6px;
  border: 1px solid rgba(255, 193, 7, 0.3);

  .admin-title {
    color: #ffc107;
    font-size: 1.1rem;
    margin-bottom: 1rem;
  }

  .admin-buttons {
    display: flex;
    gap: 0.5rem;
    flex-wrap: wrap;

    .admin-btn {
      flex: 1;
      min-width: 100px;
      height: 35px;
      font-size: 0.9rem;

      &.el-button--danger {
        background: rgba(220, 50, 0, 0.7);
        border: none;
        color: #fff;

        &:hover {
          background: rgba(220, 50, 0, 0.5);
        }
      }

      &.el-button--primary {
        background: rgba(0, 110, 255, 0.7);
        border: none;
        color: #fff;

        &:hover {
          background: rgba(0, 110, 255, 0.5);
        }
      }

      &.el-button--success {
        background: rgba(0, 150, 0, 0.7);
        border: none;
        color: #fff;

        &:hover {
          background: rgba(0, 150, 0, 0.5);
        }
      }
    }
  }
}

.product-details {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.detail-section {
  background: rgba(30, 30, 30, 0.5);
  border-radius: 8px;
  overflow: hidden;

  .section-title {
    font-size: 1.5rem;
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

.description {
  color: #aaa;
  line-height: 1.8;
  font-size: 1.1rem;
  margin: 0;
}

.spec-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1rem;
}

.spec-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.8rem 1rem;
  background: rgba(40, 40, 40, 0.5);
  border-radius: 6px;
  border: 1px solid #555;

  .spec-label {
    color: #aaa;
    font-weight: 500;
  }

  .spec-value {
    color: #fff;
    font-weight: bold;
  }
}

.product-detail-content {
  color: #ccc;
  line-height: 1.8;

  :deep(img) {
    max-width: 100%;
    margin: 1rem 0;
    border-radius: 8px;
  }

  :deep(p) {
    margin-bottom: 1rem;
  }

  :deep(h1), :deep(h2), :deep(h3), :deep(h4), :deep(h5), :deep(h6) {
    color: #ffd700;
    margin: 1.5rem 0 1rem 0;
  }

  :deep(ul), :deep(ol) {
    padding-left: 2rem;
    margin-bottom: 1rem;
  }

  :deep(li) {
    margin-bottom: 0.5rem;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .product-main {
    flex-direction: column;

    .cover-container {
      flex: none;

      .detail-cover {
        height: 300px;
      }
    }
  }

  .admin-buttons {
    flex-direction: column;

    .admin-btn {
      flex: none;
    }
  }

  .spec-grid {
    grid-template-columns: 1fr;
  }

  .cart-operations {
    .quantity-section {
      flex-direction: column;
      align-items: flex-start;
      gap: 0.5rem;
    }
  }
}
</style>