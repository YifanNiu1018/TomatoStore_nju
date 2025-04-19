<script setup lang="ts">
import {ref, onMounted, computed} from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElCard, ElTag, ElRate, ElButton, ElInputNumber } from 'element-plus'
import { deleteProduct, type ProductVO, type StockpileVO, addToCart } from '@/api/product'
import { getProductById, getStockpile } from '@/api/product'
import router from "@/router";

const route = useRoute()
const productId = ref<number>(Number(route.params.id))
const product = ref<ProductVO>({} as ProductVO)
const stockpile = ref<StockpileVO>({} as StockpileVO)
const loading = ref(true)
const role = 'admin'
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
    const res = await deleteProduct(productId.value)
    router.push('/productlist')
  } catch (error) {
    ElMessage.error('DELETE ERROR');
    console.error('Delete ERROR: ', error);
  }
}
</script>

<template>
  <div class="product-detail-container">
    <ElCard class="detail-card">
      <div class="product-header">
        <div class="cover-container">
          <img
            :src="product.cover || '/src/assets/loadFailed.png'"
            :alt="product.title"
            class="detail-cover"
          >
        </div>

        <div class="basic-info">
          <h1 class="product-title">{{ product.title }}</h1>
          <div class="price-rate">
            <span class="price">¥{{ product.price?.toFixed(2) }}</span>
            <ElRate
              v-model="product.rate"
              disabled
              :colors="['#ffd700', '#ffd700', '#ffd700']"
              class="rate"
            />
          </div>

          <!-- 库存信息 -->
          <div class="stock-info">
            <div class="stock-item">
              <span class="label">总库存：</span>
              <span class="value">{{ stockpile.amount || 0 }}</span>
            </div>
            <div class="stock-item">
              <span class="label">可售数量：</span>
              <span class="value">{{ (stockpile.amount || 0) - (stockpile.frozen || 0) }}</span>
            </div>
          </div>

          <!-- 购物车操作区-->
          <div class="cart-operations">
            <ElInputNumber
                v-model="quantity"
                :min="1"
                :max="maxQuantity"
                class="quantity-input"
            />
            <ElButton
                type="warning"
                @click="handleAddToCart"
                class="add-to-cart-btn"
            >
              加入购物车
            </ElButton>
          </div>
        </div>
        <div v-if="role === 'admin'" class="admin-btns">
          <div class="admin-btn">
            <button class="delete-btn" @click=handleDelete()>DELETE</button>
          </div>
          <div class="admin-btn">
            <button class="delete-btn" @click="router.push(`/productlist/${productId}/update`)">UPDATE</button>
          </div>
        </div>
      </div>

      <div class="detail-sections">
        <section class="detail-section">
          <h2 class="section-title">商品描述</h2>
          <p class="description">{{ product.description }}</p>
        </section>

        <section class="detail-section" v-if="product.specifications?.length">
          <h2 class="section-title">商品详情</h2>
          <div class="spec-grid">
            <div
              v-for="spec in product.specifications"
              :key="spec.item"
              class="spec-item"
            >
              <span class="spec-label">{{ spec.item }}：</span>
              <ElTag type="info" class="spec-value">
                {{ spec.value }}
              </ElTag>
            </div>
          </div>
        </section>

        <section class="detail-section" v-if="product.detail">
          <h2 class="section-title">商品细节</h2>
          <div class="product-detail" v-html="product.detail"></div>
        </section>
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
}

.product-header {
  display: flex;
  gap: 2rem;
  margin-bottom: 2rem;
}

.cover-container {
  flex: 0 0 400px;
  .detail-cover {
    width: 100%;
    height: 500px;
    object-fit: cover;
    border-radius: 8px;
  }
}

.basic-info {
  flex: 1;
  .product-title {
    font-size: 2rem;
    margin-bottom: 1.5rem;
    color: #fff;
  }

  .price-rate {
    display: flex;
    align-items: center;
    gap: 2rem;
    margin-bottom: 2rem;

    .price {
      font-size: 2.5rem;
      color: #ffd700;
      font-weight: bold;
    }

    :deep(.el-rate) {
      .el-rate__icon {
        font-size: 28px;
      }
    }
  }
}

.stock-info {
  display: flex;
  gap: 2rem;
  margin-bottom: 2rem;

  .stock-item {
    .label {
      color: #888;
      margin-right: 8px;
    }
    .value {
      color: #fff;
      font-weight: 500;
    }
  }
}

.detail-sections {
  border-top: 1px solid #444;
  padding-top: 2rem;
}

.detail-section {
  margin-bottom: 3rem;
  .section-title {
    font-size: 1.5rem;
    color: #fff;
    border-left: 4px solid #ffd700;
    padding-left: 1rem;
    margin-bottom: 1.5rem;
  }

  .description {
    color: #aaa;
    line-height: 1.8;
    font-size: 1.1rem;
  }
}

.spec-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;

  .spec-item {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 1rem;
    background: rgba(150, 150, 150, 0.7);
    border-radius: 6px;

    .spec-label {
      color: rgb(20,20,20);
      min-width: 100px;
    }

    :deep(.el-tag) {
      background: #444;
      border-color: #555;
      color: #fff;
      font-size: 1rem;
    }
  }
}

.product-detail {
  color: #ccc;
  line-height: 1.8;

  :deep(img) {
    max-width: 100%;
    margin: 1rem 0;
    border-radius: 8px;
  }
}

.admin-btns {
  position: relative;
  right:-100px;
  top:10px;
  height: 60%;
  width: 300px;
}

.admin-btn {
  display: inline-block;
  height: 60px;
  width: 30%;
  margin-left: 7px;
}

.delete-btn {
  position: relative;
  left:10px;
  cursor: pointer;
  height: 100%;
  width: 100%;
  background: rgba(230, 100, 0, 0.75);
  border-radius: 15px;
  color: #444444;
}
.delete-btn:hover {
  background: rgba(220, 50, 0, 0.5);
}
.cart-operations {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;

  .quantity-input {
    width: 120px;

    :deep(.el-input__inner) {
      background: rgba(255, 255, 255, 0.1);
      color: #333; /* 修改字体颜色为深色 */
      border-color: #555;
    }

    /* 调整增减按钮样式 */
    :deep(.el-input-number__decrease),
    :deep(.el-input-number__increase) {
      background: rgba(255, 215, 0, 0.3);
      color: #333;
      border-color: #555;

      &:hover {
        color: #000;
      }
    }

    /* 调整禁用状态样式 */
    :deep(.is-disabled) {
      opacity: 0.7;
    }
  }

  .add-to-cart-btn {
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
}
</style>