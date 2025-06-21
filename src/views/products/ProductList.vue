<!-- src/views/product/ProductsView.vue -->
<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElRow, ElCol, ElCard, ElRate } from 'element-plus'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue';
import type { ProductVO } from '@/api/product'
import { getAllProducts  } from '@/api/product'
import type { AdvertisementVO } from '@/api/advertise.ts'
import {getAllAdvertisements} from '@/api/advertise.ts'
import router from "@/router";

const products = ref<ProductVO[]>([])
const advertisements = ref<AdvertisementVO[]>([])
const currentAdIndex = ref(0)
let autoPlayTimer: NodeJS.Timeout | null = null


const loadProducts = async () => {
  try {
    const res = await getAllProducts()
    products.value = res.data.data
  } catch (error) {
    ElMessage.error('商品加载失败')
  }
}


const loadAdvertisements = async () => {
  try {
    const res = await getAllAdvertisements()
    advertisements.value = res.data.data
    startAutoPlay()
  } catch (error) {
    ElMessage.error('广告加载失败')
  }
}


const startAutoPlay = () => {
  if (advertisements.value.length > 1) {
    autoPlayTimer = setInterval(() => {
      currentAdIndex.value = (currentAdIndex.value + 1) % advertisements.value.length
    }, 5000)
  }
}


const switchAd = (direction: 'prev' | 'next') => {
  if (autoPlayTimer) clearInterval(autoPlayTimer)

  const total = advertisements.value.length
  if (direction === 'prev') {
    currentAdIndex.value = (currentAdIndex.value - 1 + total) % total
  } else {
    currentAdIndex.value = (currentAdIndex.value + 1) % total
  }

  startAutoPlay()
}


const goToProduct = (productId: string) => {
  router.push(`/productlist/${productId}`)
}

onMounted(() => {
  loadProducts()
  loadAdvertisements()
})

onUnmounted(() => {
  if (autoPlayTimer) clearInterval(autoPlayTimer)
})
</script>

<template>
  <div class="product-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">番茄书店</h1>
      <p class="page-subtitle">发现好书，享受阅读</p>
    </div>

    <!-- 广告轮播 -->
    <div v-if="advertisements.length > 0" class="ad-carousel-container">
      <div class="ad-badge">推荐</div>

      <div
          class="ad-carousel"
          @click="goToProduct(advertisements[currentAdIndex].productId)"
      >
        <img
            :src="advertisements[currentAdIndex].imageUrl || '/src/assets/loadFailed.png'"
            :alt="advertisements[currentAdIndex].title"
        />
        <div class="ad-info">
          <h3 class="ad-title">{{ advertisements[currentAdIndex].title }}</h3>
          <p class="ad-content">{{ advertisements[currentAdIndex].content }}</p>
        </div>

        <div class="carousel-control prev" @click.stop="switchAd('prev')">
          <el-icon :size="30"><ArrowLeft /></el-icon>
        </div>
        <div class="carousel-control next" @click.stop="switchAd('next')">
          <el-icon :size="30"><ArrowRight /></el-icon>
        </div>

        <!-- 指示器 -->
        <div class="carousel-indicators">
          <span
            v-for="(ad, index) in advertisements"
            :key="index"
            :class="['indicator', { active: index === currentAdIndex }]"
            @click.stop="currentAdIndex = index"
          ></span>
        </div>
      </div>
    </div>

    <!-- 商品网格 -->
    <div class="products-section">
      <h2 class="section-title">精选商品</h2>
      <ElRow :gutter="20" class="product-grid">
        <ElCol
            v-for="product in products"
            :key="product.id"
            :xs="24" :sm="12" :md="8" :lg="6"
            class="product-col"
        >
          <ElCard class="product-card" shadow="hover" @click="router.push(`/productlist/${product.id}`)">
            <div class="product-cover">
              <img
                  :src="product.cover || '/src/assets/loadFailed.png'"
                  :alt="product.title"
              >
              <div class="product-overlay">
                <span class="view-detail">查看详情</span>
              </div>
            </div>

            <div class="product-info">
              <h3 class="product-title">{{ product.title }}</h3>
              <div class="product-meta">
                <ElRate
                    v-model="product.rate"
                    disabled
                    :colors="['#ffd700', '#ffd700', '#ffd700']"
                    class="product-rate"
                />
                <span class="product-price">¥{{ product.price?.toFixed(2) }}</span>
              </div>
              <p class="product-desc">{{ product.description }}</p>
            </div>
          </ElCard>
        </ElCol>
      </ElRow>
    </div>
  </div>
</template>

<style scoped lang="scss">
.product-container {
  background: url("@/assets/bookShelve.jpg") no-repeat fixed center center;
  background-size: cover;
  padding: 2rem;
  min-height: 100vh;
}

.page-header {
  text-align: center;
  margin-bottom: 3rem;

  .page-title {
    font-size: 3rem;
    color: #ffd700;
    margin-bottom: 0.5rem;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
    font-weight: bold;
  }

  .page-subtitle {
    font-size: 1.2rem;
    color: #aaa;
    margin: 0;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
  }
}

.ad-carousel-container {
  position: relative;
  max-width: 1400px;
  margin: 0 auto 2rem;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);

  .ad-badge {
    position: absolute;
    top: 15px;
    left: 15px;
    background: rgba(255, 215, 0, 0.9);
    color: #333;
    padding: 6px 16px;
    border-radius: 20px;
    font-size: 14px;
    font-weight: bold;
    z-index: 2;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  }

  .ad-carousel {
    position: relative;
    height: 400px;
    cursor: pointer;
    transition: transform 0.3s ease;

    &:hover {
      transform: scale(1.01);
    }

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .ad-info {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background: linear-gradient(transparent, rgba(0,0,0,0.8));
      padding: 2rem;
      color: white;

      .ad-title {
        font-size: 1.8rem;
        margin-bottom: 0.8rem;
        color: #ffd700;
        font-weight: bold;
        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.7);
      }

      .ad-content {
        font-size: 1.1rem;
        opacity: 0.9;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        line-height: 1.5;
        margin: 0;
      }
    }

    .carousel-indicators {
      position: absolute;
      bottom: 20px;
      left: 50%;
      transform: translateX(-50%);
      display: flex;
      gap: 8px;
      z-index: 2;

      .indicator {
        width: 12px;
        height: 12px;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.5);
        cursor: pointer;
        transition: all 0.3s ease;

        &.active {
          background: #ffd700;
          transform: scale(1.2);
        }

        &:hover {
          background: rgba(255, 255, 255, 0.8);
        }
      }
    }
  }

  .carousel-control {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(0,0,0,0.2);
    border: 2px solid rgba(255,255,255,0.5);
    border-radius: 50%;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    backdrop-filter: blur(2px);

    .el-icon {
      color: rgba(255,255,255,0.9);
      font-size: 24px;
      filter: drop-shadow(0 2px 4px rgba(0,0,0,0.1));
    }

    &:hover {
      background: rgba(0,0,0,0.3);
      border-color: rgba(255,255,255,0.8);
      transform: translateY(-50%) scale(1.1);
      box-shadow: 0 4px 12px rgba(0,0,0,0.15);

      .el-icon {
        color: white;
      }
    }

    &.prev {
      left: 20px;
      .el-icon {
        margin-right: 2px;
      }
    }

    &.next {
      right: 20px;
      .el-icon {
        margin-left: 2px;
      }
    }
  }
}


.products-section {
  max-width: 1400px;
  margin: 0 auto;

  .section-title {
    font-size: 2rem;
    color: #ffd700;
    text-align: center;
    margin-bottom: 2rem;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
    font-weight: bold;
  }
}

.product-grid {
  min-width: 100px;
  min-height: 100px;
}

.product-col {
  margin-bottom: 20px;
}

.product-card {
  background: rgba(30, 30, 30, 0.8);
  border: 1px solid rgba(255, 215, 0, 0.2);
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
  overflow: hidden;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.3);
    border-color: rgba(255, 215, 0, 0.5);

    .product-overlay {
      opacity: 1;
    }

    .product-cover img {
      transform: scale(1.05);
    }
  }
}

.product-cover {
  height: 250px;
  overflow: hidden;
  border-radius: 12px 12px 0 0;
  position: relative;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }

  .product-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.6);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s ease;

    .view-detail {
      color: #ffd700;
      font-size: 1.1rem;
      font-weight: bold;
      padding: 0.5rem 1rem;
      border: 2px solid #ffd700;
      border-radius: 25px;
      background: rgba(255, 215, 0, 0.1);
      backdrop-filter: blur(4px);
    }
  }
}

.product-info {
  padding: 1.5rem;
}

.product-title {
  color: #fff;
  font-size: 1.2rem;
  margin: 0 0 1rem;
  font-weight: 600;
  line-height: 1.4;
  transition: color 0.3s ease;

  &:hover {
    color: #ffd700;
  }
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;

  .product-rate {
    :deep(.el-rate__icon) {
      font-size: 16px;
    }
  }
}

.product-price {
  color: #ffd700;
  font-size: 1.3rem;
  font-weight: bold;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
}

.product-desc {
  color: #aaa;
  font-size: 0.95rem;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin: 0;
}

:deep(.el-card__body) {
  padding: 0;
}
</style>