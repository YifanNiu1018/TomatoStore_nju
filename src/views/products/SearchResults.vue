<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElRow, ElCol, ElCard, ElRate, ElEmpty, ElInput, ElButton } from 'element-plus'
import { searchProducts, type ProductVO } from '@/api/product'

const route = useRoute()
const router = useRouter()

const searchKeyword = ref('')
const searchResults = ref<ProductVO[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 执行搜索
const performSearch = async (keyword: string) => {
  if (!keyword.trim()) {
    searchResults.value = []
    total.value = 0
    return
  }

  loading.value = true
  try {
    // 这里需要根据你的API调整搜索逻辑
    // 假设有一个搜索API，如果没有，我们可以获取所有商品然后过滤
    const response = await searchProducts(keyword, currentPage.value, pageSize.value)
    searchResults.value = response.data.data || []
    total.value = response.data.total || 0
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败，请稍后重试')
    searchResults.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 跳转到商品详情
const goToProductDetail = (productId: number) => {
  router.push(`/productlist/${productId}`)
}

// 新搜索
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/search',
      query: { keyword: searchKeyword.value }
    })
  }
}

// 监听路由变化
watch(() => route.query.keyword, (newKeyword) => {
  if (newKeyword && typeof newKeyword === 'string') {
    searchKeyword.value = newKeyword
    performSearch(newKeyword)
  }
}, { immediate: true })

onMounted(() => {
  const keyword = route.query.keyword as string
  if (keyword) {
    searchKeyword.value = keyword
    performSearch(keyword)
  }
})
</script>

<template>
  <div class="search-container">
    <!-- 搜索头部 -->
    <div class="search-header">
      <h1 class="page-title">商品搜索</h1>
      <div class="search-box">
        <ElInput
          v-model="searchKeyword"
          placeholder="搜索商品..."
          class="search-input"
          size="large"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <ElButton @click="handleSearch" type="primary">搜索</ElButton>
          </template>
        </ElInput>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div class="search-results">
      <!-- 结果统计 -->
      <div v-if="!loading && searchKeyword" class="result-stats">
        <p class="stats-text">
          搜索"<span class="keyword">{{ searchKeyword }}</span>"，找到 
          <span class="count">{{ total }}</span> 个结果
        </p>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner">🔍</div>
        <p>正在搜索中...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="!searchKeyword" class="empty-state">
        <ElEmpty description="请输入关键词开始搜索" />
      </div>

      <!-- 无结果 -->
      <div v-else-if="searchResults.length === 0" class="no-results">
        <ElEmpty description="没有找到相关商品">
          <template #image>
            <div class="empty-icon">📚</div>
          </template>
          <div class="empty-actions">
            <p>尝试以下操作：</p>
            <ul>
              <li>检查关键词拼写</li>
              <li>使用更通用的关键词</li>
              <li>减少关键词数量</li>
            </ul>
            <ElButton type="primary" @click="router.push('/productlist')">
              浏览所有商品
            </ElButton>
          </div>
        </ElEmpty>
      </div>

      <!-- 搜索结果列表 -->
      <ElRow v-else :gutter="24" class="product-grid">
        <ElCol
          v-for="product in searchResults"
          :key="product.id"
          :xs="24" :sm="12" :md="8" :lg="6"
          class="product-col"
        >
          <ElCard class="product-card" shadow="hover" @click="goToProductDetail(product.id!)">
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
.search-container {
  background: url("@/assets/bookShelve.jpg") no-repeat fixed center center;
  background-size: cover;
  padding: 2rem;
  min-height: 100vh;
}

.search-header {
  text-align: center;
  margin-bottom: 3rem;
  
  .page-title {
    font-size: 3rem;
    color: #ffd700;
    margin-bottom: 1.5rem;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
    font-weight: bold;
  }
  
  .search-box {
    max-width: 600px;
    margin: 0 auto;
    
    .search-input {
      :deep(.el-input__wrapper) {
        background: rgba(255, 255, 255, 0.1);
        border: 1px solid rgba(255, 215, 0, 0.3);
        border-radius: 25px;
        box-shadow: none;
        
        &:hover {
          border-color: rgba(255, 215, 0, 0.5);
        }
        
        &.is-focus {
          border-color: #ffd700;
          box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
        }
      }
      
      :deep(.el-input__inner) {
        color: #fff;
        
        &::placeholder {
          color: #aaa;
        }
      }
      
      :deep(.el-input-group__append) {
        background: rgba(255, 215, 0, 0.7);
        border: none;
        border-radius: 0 25px 25px 0;
        
        .el-button {
          background: transparent;
          border: none;
          color: #333;
          font-weight: bold;
          
          &:hover {
            background: rgba(255, 215, 0, 0.2);
          }
        }
      }
    }
  }
}

.search-results {
  max-width: 1400px;
  margin: 0 auto;
}

.result-stats {
  margin-bottom: 2rem;
  text-align: center;
  
  .stats-text {
    color: #aaa;
    font-size: 1.1rem;
    margin: 0;
    
    .keyword {
      color: #ffd700;
      font-weight: bold;
    }
    
    .count {
      color: #ffd700;
      font-weight: bold;
    }
  }
}

.loading-state {
  text-align: center;
  padding: 4rem 2rem;
  color: #aaa;
  
  .loading-spinner {
    font-size: 3rem;
    margin-bottom: 1rem;
    animation: spin 2s linear infinite;
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.empty-state, .no-results {
  text-align: center;
  padding: 4rem 2rem;
  
  :deep(.el-empty) {
    .el-empty__description {
      color: #aaa;
    }
  }
  
  .empty-icon {
    font-size: 4rem;
    margin-bottom: 1rem;
  }
  
  .empty-actions {
    margin-top: 2rem;
    
    p {
      color: #aaa;
      margin-bottom: 1rem;
    }
    
    ul {
      color: #666;
      text-align: left;
      display: inline-block;
      margin-bottom: 2rem;
      
      li {
        margin-bottom: 0.5rem;
      }
    }
  }
}

// 商品卡片样式（复用ProductList的样式）
.product-grid {
  .product-col {
    margin-bottom: 24px;
  }
}

.product-card {
  background: rgba(30, 30, 30, 0.8);
  border: 1px solid rgba(255, 215, 0, 0.2);
  border-radius: 16px;
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
  height: 220px;
  overflow: hidden;
  border-radius: 16px 16px 0 0;
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
  margin: 0 0 0.8rem;
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

// 响应式设计
@media (max-width: 768px) {
  .search-container {
    padding: 1rem;
  }
  
  .search-header {
    .page-title {
      font-size: 2.5rem;
    }
  }
  
  .search-box {
    .search-input {
      :deep(.el-input-group__append) {
        .el-button {
          padding: 0 12px;
        }
      }
    }
  }
}
</style>
