<!-- src/views/cart/CartView.vue -->
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElCard, ElButton, ElInputNumber } from 'element-plus'
import {
  getCartItems,
  deleteCartItem,
  updateCart,
  type CartVO,
  type CartListVO
} from '@/api/cart'
import router from '@/router'

const cartData = ref<CartListVO>({
  items: [],
  total: 0,
  totalAmount: 0
})
const loading = ref(true)

// 获取购物车列表
const fetchCartItems = async () => {
  try {
    const data = await getCartItems()
    cartData.value = data
  } catch (error) {
    ElMessage.error('获取购物车失败')
    console.error('Error fetching cart:', error)
  } finally {
    loading.value = false
  }
}

// 更新商品数量
const handleUpdateQuantity = async (item: CartVO, newQuantity: number | undefined) => {
  console.log(item);
  if (!newQuantity || newQuantity < 1) return

  try {
    await updateCart(item.cartitemid, newQuantity)
    // 更新后重新获取最新数据
    await fetchCartItems()
    ElMessage.success('数量更新成功')
  } catch (error) {
    ElMessage.error('更新数量失败')
    console.error('Error updating quantity:', error)
  }
}

// 删除商品
const handleRemoveItem = async (cartItemId: number) => {
  try {
    await deleteCartItem(cartItemId)
    // 删除后重新获取最新数据
    await fetchCartItems()
    ElMessage.success('商品已移除')
  } catch (error) {
    ElMessage.error('移除商品失败')
    console.error('Error removing item:', error)
  }
}

// 跳转到商品详情
const goToProductDetail = (productId: number) => {
  router.push(`/product/${productId}`)
}

onMounted(() => {
  fetchCartItems()
})
</script>

<template>
  <div class="cart-container">
    <ElCard class="cart-card">
      <h1 class="cart-title">我的购物车 <span class="item-count">(共 {{ cartData.total }} 件商品)</span></h1>

      <div v-if="loading && cartData.total" class="loading-text">加载中...</div>

      <div v-else>
        <div v-if="cartData.items.length === 0" class="empty-cart">
          <p>购物车是空的，快去挑选商品吧~</p>
          <ElButton type="warning" @click="router.push('/productlist')">去逛逛</ElButton>
        </div>

        <div v-else class="cart-items">
          <div v-for="item in cartData.items" :key="item.cartitemid" class="cart-item">
            <div class="item-cover" @click="goToProductDetail(item.productid)">
              <img :src="item.cover || '/src/assets/loadFailed.png'" :alt="item.title" />
            </div>

            <div class="item-info">
              <h2 class="item-title" @click="goToProductDetail(item.productid)">{{ item.title }}</h2>
              <p class="item-desc">{{ item.description }}</p>

              <div class="item-price">
                <span class="price">¥{{ item.price.toFixed(2) }}</span>
              </div>
            </div>

            <div class="item-actions">
              <div class="quantity-control">
                <ElInputNumber
                    v-model="item.quantity"
                    :min="1"
                    @change="(val) => handleUpdateQuantity(item, val)"
                    class="quantity-input"
                />
              </div>

              <ElButton
                  type="danger"
                  @click="handleRemoveItem(item.cartitemid)"
                  class="remove-btn"
              >
                删除
              </ElButton>
            </div>
          </div>

          <div class="cart-summary">
            <div class="total-info">
              <div class="total-amount">
                总金额: <span class="amount">¥{{ cartData.totalAmount.toFixed(2) }}</span>
              </div>
              <div class="total-items">
                共 {{ cartData.total }} 件商品
              </div>
            </div>
            <ElButton type="warning" class="checkout-btn">去结算</ElButton>
          </div>
        </div>
      </div>
    </ElCard>
  </div>
</template>

<style scoped lang="scss">
.cart-container {
  background: url("@/assets/bookShelve.jpg") no-repeat fixed center center;
  background-size: cover;
  padding: 2rem;
  min-height: 100vh;
}

.cart-card {
  max-width: 1200px;
  margin: 0 auto;
  background: rgba(20, 20, 20, 0.7);
  border: none;
  color: #fff;
  padding: 2rem;
}

.cart-title {
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

.empty-cart {
  text-align: center;
  padding: 3rem;
  color: #aaa;

  p {
    margin-bottom: 1.5rem;
    font-size: 1.2rem;
  }

  .el-button {
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

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.cart-item {
  display: flex;
  gap: 2rem;
  padding: 1.5rem;
  background: rgba(30, 30, 30, 0.5);
  border-radius: 8px;
  align-items: center;

  .item-cover {
    flex: 0 0 150px;
    cursor: pointer;

    img {
      width: 100%;
      height: 150px;
      object-fit: cover;
      border-radius: 8px;
    }
  }

  .item-info {
    flex: 1;

    .item-title {
      font-size: 1.3rem;
      margin-bottom: 0.5rem;
      color: #fff;
      cursor: pointer;

      &:hover {
        color: #ffd700;
      }
    }

    .item-desc {
      color: #aaa;
      margin-bottom: 1rem;
      line-height: 1.5;
    }

    .item-price {
      .price {
        font-size: 1.5rem;
        color: #ffd700;
        font-weight: bold;
      }
    }
  }

  .item-actions {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1rem;

    .quantity-input {
      width: 120px;

      /* 调整输入框数字颜色为深色 */
      :deep(.el-input__inner) {
        background: rgba(255, 255, 255, 0.1);
        color: #333; /* 改为深色字体 */
        border-color: #555;
        font-weight: bold; /* 加粗更清晰 */
      }

      /* 调整增减按钮样式 */
      :deep(.el-input-number__decrease),
      :deep(.el-input-number__increase) {
        background: rgba(255, 215, 0, 0.3);
        color: #333; /* 按钮文字颜色 */
        border-color: #555;

        &:hover {
          color: #000;
          background: rgba(255, 215, 0, 0.5);
        }
      }

      /* 调整禁用状态样式 */
      :deep(.is-disabled) {
        opacity: 0.7;
        color: #666 !important;
      }

      /* 调整输入框聚焦状态 */
      :deep(.el-input__inner:focus) {
        border-color: #ffd700;
        box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
      }
    }

    .remove-btn {
      background: rgba(220, 50, 0, 0.7);
      border: none;
      color: #fff;

      &:hover {
        background: rgba(220, 50, 0, 0.5);
      }
    }
  }
}

.cart-summary {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 2rem;
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid #444;

  .total-price {
    font-size: 1.5rem;
    color: #ffd700;
  }

  .checkout-btn {
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
.cart-title {
  .item-count {
    font-size: 1rem;
    color: #aaa;
    margin-left: 1rem;
    font-weight: normal;
  }
}

.cart-summary {
  .total-info {
    text-align: right;
    margin-right: 2rem;

    .total-amount {
      font-size: 1.5rem;
      margin-bottom: 0.5rem;

      .amount {
        color: #ffd700;
        font-weight: bold;
      }
    }

    .total-items {
      color: #aaa;
    }
  }
}
</style>