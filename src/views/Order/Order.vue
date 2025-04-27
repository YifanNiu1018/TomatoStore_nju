<!-- src/views/order/OrderView.vue -->
<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { getOrderDetails } from '@/api/order'
import { useRoute } from 'vue-router'
import router from '@/router'

const route = useRoute()
const orderId = ref(route.params.id as string)
const orderData = ref<OrderVO | null>(null)
const loading = ref(true)
const paymentStatusTimer = ref<NodeJS.Timeout | null>(null)

// 获取订单详情（添加自动刷新）
const fetchOrderDetails = async (showLoading = true) => {
  if (showLoading) loading.value = true
  try {
    const data = await getOrderDetails(orderId.value)
    orderData.value = data

    // 如果订单已支付，停止轮询
    if (data.status === 'COMPLETED' && paymentStatusTimer.value) {
      clearInterval(paymentStatusTimer.value)
      ElMessage.success('支付成功！')
    }
  } finally {
    loading.value = false
  }
}

// 开始轮询订单状态
const startPaymentStatusPolling = () => {
  paymentStatusTimer.value = setInterval(() => {
    fetchOrderDetails(false)
  }, 3000) // 每3秒检查一次
}

// 跳转到支付页面
const handlePayment = () => {
  const paymentWindow = window.open('', '_blank')

  // 实际项目中这里应该调用API获取支付URL
  const paymentUrl = `/api/orders/${orderId.value}/pay`

  if (paymentWindow) {
    paymentWindow.location.href = paymentUrl
    startPaymentStatusPolling()
  } else {
    ElMessage.warning('请允许弹出窗口以完成支付')
  }
}

onMounted(() => {
  fetchOrderDetails()
})

onUnmounted(() => {
  if (paymentStatusTimer.value) {
    clearInterval(paymentStatusTimer.value)
  }
})
</script>
<template>
  <div class="order-container">
    <ElCard class="order-card">
      <h1 class="order-title">订单详情 <span class="order-id">订单号: {{ orderId }}</span></h1>

      <div v-if="loading" class="loading-text">加载中...</div>

      <div v-else-if="orderData" class="order-content">
        <!-- 订单基本信息 -->
        <div class="order-info">
          <div class="info-row">
            <span class="info-label">订单状态:</span>
            <ElTag :type="orderData.status === 'COMPLETED' ? 'success' : 'warning'">
              {{ orderData.status === 'COMPLETED' ? '已完成' : '待支付' }}
            </ElTag>
          </div>
          <div class="info-row">
            <span class="info-label">创建时间:</span>
            <span class="info-value">{{ orderData.createTime }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">支付方式:</span>
            <span class="info-value">{{ orderData.paymentMethod === 'ALIPAY' ? '支付宝' : orderData.paymentMethod }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">订单金额:</span>
            <span class="info-price">¥{{ orderData.totalAmount.toFixed(2) }}</span>
          </div>
        </div>

        <!-- 商品列表 -->
        <div class="order-items">
          <h2 class="section-title">商品清单</h2>
          <ElTable :data="orderData.items" style="width: 100%" class="items-table">
            <ElTableColumn prop="cover" label="商品图片" width="120">
              <template #default="{ row }">
                <img :src="row.cover || '/src/assets/loadFailed.png'" class="item-image" />
              </template>
            </ElTableColumn>
            <ElTableColumn prop="title" label="商品名称" />
            <ElTableColumn prop="price" label="单价" width="120">
              <template #default="{ row }">¥{{ row.price.toFixed(2) }}</template>
            </ElTableColumn>
            <ElTableColumn prop="quantity" label="数量" width="100" />
            <ElTableColumn label="小计" width="120">
              <template #default="{ row }">¥{{ (row.price * row.quantity).toFixed(2) }}</template>
            </ElTableColumn>
          </ElTable>
        </div>

        <!-- 订单总结 -->
        <div class="order-summary">
          <div class="summary-row">
            <span class="summary-label">商品总数:</span>
            <span class="summary-value">{{ orderData.items.length }} 件</span>
          </div>
          <div class="summary-row">
            <span class="summary-label">商品总价:</span>
            <span class="summary-value">¥{{ orderData.totalAmount.toFixed(2) }}</span>
          </div>
          <div class="summary-row total">
            <span class="summary-label">实付款:</span>
            <span class="summary-price">¥{{ orderData.totalAmount.toFixed(2) }}</span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="order-actions">
          <ElButton type="warning" v-if="orderData.status === 'PENDING'" @click="router.push('/cart')">继续购物</ElButton>
          <ElButton type="primary" v-if="orderData.status === 'PENDING'">立即支付</ElButton>
          <ElButton @click="backToCart">返回购物车</ElButton>
        </div>
      </div>
    </ElCard>
    <div v-if="orderData?.status === 'PENDING'" class="payment-status">
      <el-alert title="等待支付完成" type="warning" show-icon>
        <template #default>
          <p>如果已完成支付，请稍候，系统正在同步支付状态...</p>
          <el-button type="text" @click="fetchOrderDetails(false)">
            手动刷新
          </el-button>
        </template>
      </el-alert>
    </div>
  </div>
</template>

<style scoped lang="scss">
.order-container {
  background: url("@/assets/bookShelve.jpg") no-repeat fixed center center;
  background-size: cover;
  padding: 2rem;
  min-height: 100vh;
}

.order-card {
  max-width: 1200px;
  margin: 0 auto;
  background: rgba(20, 20, 20, 0.7);
  border: none;
  color: #fff;
  padding: 2rem;
}

.order-title {
  font-size: 2rem;
  margin-bottom: 2rem;
  color: #ffd700;
  border-bottom: 1px solid #444;
  padding-bottom: 1rem;

  .order-id {
    font-size: 1rem;
    color: #aaa;
    margin-left: 1rem;
    font-weight: normal;
  }
}

.loading-text {
  color: #aaa;
  text-align: center;
  padding: 2rem;
}

.order-content {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.order-info {
  background: rgba(30, 30, 30, 0.5);
  padding: 1.5rem;
  border-radius: 8px;

  .info-row {
    display: flex;
    align-items: center;
    margin-bottom: 1rem;

    &:last-child {
      margin-bottom: 0;
    }

    .info-label {
      width: 100px;
      color: #aaa;
    }

    .info-value {
      color: #fff;
    }

    .info-price {
      color: #ffd700;
      font-weight: bold;
      font-size: 1.2rem;
    }
  }
}

.section-title {
  font-size: 1.5rem;
  color: #ffd700;
  margin-bottom: 1rem;
  border-bottom: 1px solid #444;
  padding-bottom: 0.5rem;
}

.items-table {
  background: transparent;

  :deep(.el-table__header) {
    th {
      background-color: rgba(40, 40, 40, 0.7);
      color: #ffd700;
    }
  }

  :deep(.el-table__body) {
    tr {
      background-color: rgba(30, 30, 30, 0.5);

      &:hover {
        background-color: rgba(50, 50, 50, 0.5);
      }
    }

    td {
      border-bottom: 1px solid #444;
      color: #fff;
    }
  }

  .item-image {
    width: 80px;
    height: 80px;
    object-fit: cover;
    border-radius: 4px;
  }
}

.order-summary {
  background: rgba(30, 30, 30, 0.5);
  padding: 1.5rem;
  border-radius: 8px;
  margin-left: auto;
  width: 300px;

  .summary-row {
    display: flex;
    justify-content: space-between;
    margin-bottom: 0.8rem;

    &.total {
      border-top: 1px solid #444;
      padding-top: 0.8rem;
      margin-top: 0.8rem;
    }

    .summary-label {
      color: #aaa;
    }

    .summary-value {
      color: #fff;
    }

    .summary-price {
      color: #ffd700;
      font-weight: bold;
      font-size: 1.2rem;
    }
  }
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;

  .el-button {
    padding: 0 2rem;
    height: 40px;

    &.el-button--warning {
      background: rgba(255, 215, 0, 0.7);
      border: none;
      color: #333;
      font-weight: bold;

      &:hover {
        background: rgba(255, 215, 0, 0.5);
      }
    }

    &.el-button--primary {
      background: rgba(0, 110, 255, 0.7);
      border: none;

      &:hover {
        background: rgba(0, 110, 255, 0.5);
      }
    }
  }
}
</style>