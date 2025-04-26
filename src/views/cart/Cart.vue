<!-- src/views/cart/CartView.vue -->
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElCard, ElButton, ElInputNumber, ElDialog, ElForm, ElFormItem, ElSelect, ElOption } from 'element-plus'
import {
  getCartItems,
  deleteCartItem,
  updateCart,
  checkoutCart,
  type CartVO,
  type CartListVO,
  type OrderResultVO
} from '@/api/cart'
import { requestPayment, type PaymentResultVO } from '@/api/order'
import router from '@/router'

const cartData = ref<CartListVO>({
  items: [],
  total: 0,
  totalAmount: 0
})
const loading = ref(true)
const showCheckoutDialog = ref(false)
const selectedItems = ref<number[]>([])
const paymentMethod = ref('ALIPAY')
const orderResult = ref<OrderResultVO | null>(null)
const showOrderResultDialog = ref(false)
const currentOrderId = ref('')

// 获取购物车列表
const fetchCartItems = async () => {
  try {
    const data = await getCartItems()
    cartData.value = data
    // 默认全选
    selectedItems.value = data.items.map(item => item.cartitemid)
  } catch (error) {
    ElMessage.error('获取购物车失败')
    console.error('Error fetching cart:', error)
  } finally {
    loading.value = false
  }
}

// 更新商品数量
const handleUpdateQuantity = async (item: CartVO, newQuantity: number | undefined) => {
  if (!newQuantity || newQuantity < 1) return

  try {
    await updateCart(item.cartitemid, newQuantity)
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
    await fetchCartItems()
    ElMessage.success('商品已移除')
    // 从选中列表中移除
    selectedItems.value = selectedItems.value.filter(id => id !== cartItemId)
  } catch (error) {
    ElMessage.error('移除商品失败')
    console.error('Error removing item:', error)
  }
}

// 跳转到商品详情
const goToProductDetail = (productId: number) => {
  router.push(`/productList/${productId}`)
}

// 打开结算对话框
const openCheckoutDialog = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请至少选择一件商品')
    return
  }
  showCheckoutDialog.value = true
}

// 提交订单
const handleCheckout = async () => {
  try {
    const result = await checkoutCart(selectedItems.value, paymentMethod.value)
    orderResult.value = result
    showCheckoutDialog.value = false
    showOrderResultDialog.value = true
    currentOrderId.value = result.orderId // 保存当前订单ID
    await fetchCartItems()
  } catch (error) {
    ElMessage.error('提交订单失败')
  }
}

// 跳转到订单详情
const goToOrderDetail = () => {
  if (orderResult.value) {
    router.push(`/order/${orderResult.value.orderId}`)
  }
}
// 新增支付方法
const handlePayment = async () => {
  try {
    const result = await requestPayment(currentOrderId.value)
    console.log(result)

    // 在新窗口打开支付表单
    const paymentWindow = window.open('', '_blank')
    if (paymentWindow) {
      paymentWindow.document.write(result.paymentForm)
      paymentWindow.document.close() // 确保文档完全写入

      // 关闭订单结果对话框
      showOrderResultDialog.value = false

      // 监听支付窗口关闭（可选）
      const timer = setInterval(() => {
        if (paymentWindow.closed) {
          clearInterval(timer)
          handlePaymentSuccess()
        }
      }, 500)
    } else {
      ElMessage.warning('请允许弹出窗口以完成支付')
    }
  } catch (error) {
    ElMessage.error('发起支付失败')
  }
}


// 支付成功后跳转
const handlePaymentSuccess = () => {
  showPaymentDialog.value = false
  router.push(`/order/${currentOrderId.value}`)
}
onMounted(() => {
  fetchCartItems()
})
</script>

<template>
  <div class="cart-container">
    <ElCard class="cart-card">
      <h1 class="cart-title">我的购物车 <span class="item-count">(共 {{ cartData.total==null ? 0 : cartData.total}} 件商品)</span></h1>

      <div v-if="loading && cartData.total" class="loading-text">加载中...</div>

      <div v-else>
        <div v-if="cartData.items.length === 0" class="empty-cart">
          <p>购物车是空的，快去挑选商品吧~</p>
          <ElButton type="warning" @click="router.push('/productlist')">去逛逛</ElButton>
        </div>

        <div v-else class="cart-items">
          <div v-for="item in cartData.items" :key="item.cartitemid" class="cart-item">
            <div class="item-checkbox">
              <input
                  type="checkbox"
                  v-model="selectedItems"
                  :value="item.cartitemid"
                  class="checkbox-input"
              />
            </div>

            <div class="item-cover" @click="goToProductDetail(item.productid)">
              <img :src="item.cover || '/src/assets/loadFailed.png'" :alt="item.title" />
            </div>

            <div class="item-info">
              <h2 class="item-title" @click="goToProductDetail(item.productid)">{{ item.title }}</h2>
              <p class="item-desc">{{ item.description }}</p>

              <div class="item-price">
                <span class="price">¥{{ item.price.toFixed(2) }}</span>
                <span class="quantity">x{{ item.quantity }}</span>
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
            <div class="selection-info">
              <span>已选择 {{ selectedItems.length }} 件商品</span>
            </div>
            <div class="total-info">
              <div class="total-amount">
                总金额: <span class="amount">¥{{ cartData.totalAmount.toFixed(2) }}</span>
              </div>
              <div class="total-items">
                共 {{ cartData.total }} 件商品
              </div>
            </div>
            <ElButton type="warning" class="checkout-btn" @click="openCheckoutDialog">去结算</ElButton>
          </div>
        </div>
      </div>
    </ElCard>

    <!-- 结算对话框 -->
    <ElDialog
        v-model="showCheckoutDialog"
        title="确认订单信息"
        width="50%"
        :close-on-click-modal="false"
    >
      <ElForm label-width="120px">
        <ElFormItem label="支付方式">
          <ElSelect v-model="paymentMethod" placeholder="请选择支付方式">
            <ElOption label="支付宝" value="ALIPAY" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="商品总价">
          <span class="dialog-price">¥{{ cartData.totalAmount.toFixed(2) }}</span>
        </ElFormItem>
      </ElForm>

      <template #footer>
        <ElButton @click="showCheckoutDialog = false">取消</ElButton>
        <ElButton type="warning" @click="handleCheckout">确认支付</ElButton>
      </template>
    </ElDialog>

    <!-- 订单结果对话框 -->
    <ElDialog
        v-model="showOrderResultDialog"
        title="订单创建成功"
        width="40%"
        :close-on-click-modal="false"
    >
      <div v-if="orderResult" class="order-result">
        <p>订单号: {{ orderResult.orderId }}</p>
        <p>支付方式: {{ orderResult.paymentMethod === 'ALIPAY' ? '支付宝' : orderResult.paymentMethod }}</p>
        <p>订单金额: ¥{{ orderResult.totalAmount.toFixed(2) }}</p>
        <p>订单状态: {{ orderResult.status === 'PENDING' ? '待支付' : orderResult.status }}</p>
      </div>

      <template #footer>
        <ElButton type="warning" @click="handlePayment">立即支付</ElButton>
        <ElButton @click="goToOrderDetail">稍后支付</ElButton>
      </template>
    </ElDialog>
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
  gap: 1rem;
  padding: 1.5rem;
  background: rgba(30, 30, 30, 0.5);
  border-radius: 8px;
  align-items: center;

  .item-checkbox {
    flex: 0 0 30px;

    .checkbox-input {
      width: 18px;
      height: 18px;
      cursor: pointer;
    }
  }

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
      display: flex;
      align-items: center;
      gap: 1rem;

      .price {
        font-size: 1.5rem;
        color: #ffd700;
        font-weight: bold;
      }

      .quantity {
        color: #aaa;
        font-size: 1rem;
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
  justify-content: space-between;
  align-items: center;
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid #444;

  .selection-info {
    color: #aaa;
    font-size: 1rem;
  }

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

.dialog-price {
  font-size: 1.2rem;
  color: #ffd700;
  font-weight: bold;
}

.order-result {
  color: #fff;
  line-height: 2;

  p {
    margin: 0.5rem 0;
  }
}
.payment-container {
  width: 100%;
  height: 500px;
  border: none;
  background: white;
}

.order-result {
  p {
    margin: 0.5rem 0;
    color: #333;
    font-size: 1rem;

    &:first-child {
      font-weight: bold;
    }
  }
}
</style>