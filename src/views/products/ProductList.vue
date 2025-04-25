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
    <div v-if="advertisements.length > 0" class="ad-carousel-container">
      <div class="ad-badge">广告</div>

      <div
          class="ad-carousel"
          @click="goToProduct(advertisements[currentAdIndex].productId)"
      >
        <img
            :src="advertisements[currentAdIndex].imageUrl || '/src/assets/loadFailed.png'"
            :alt="advertisements[currentAdIndex].title"
        />
        <div class="ad-info">
          <h3 style="color: #ff4d4f">{{ advertisements[currentAdIndex].title }}</h3>
          <p>{{ advertisements[currentAdIndex].content }}</p>
        </div>

        <div class="carousel-control prev" @click.stop="switchAd('prev')">
          <el-icon :size="30"><ArrowLeft /></el-icon>
        </div>
        <div class="carousel-control next" @click.stop="switchAd('next')">
          <el-icon :size="30"><ArrowRight /></el-icon>
        </div>
      </div>
    </div>

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
  background-size: cover;
  padding: 2rem;
  min-height: 100vh;
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
    top: 10px;
    left: 10px;
    background: rgba(255, 0, 0, 0.8);
    color: white;
    padding: 4px 12px;
    border-radius: 4px;
    font-size: 12px;
    z-index: 2;
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

      h3 {
        font-size: 1.5rem;
        margin-bottom: 0.5rem;
      }

      p {
        font-size: 1rem;
        opacity: 0.9;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
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