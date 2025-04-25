<!-- src/views/advertisement/AdvertisementManageView.vue -->
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElRow, ElCol, ElCard, ElButton } from 'element-plus'
import type { AdvertisementVO } from '@/api/advertise.ts'
import { getAllAdvertisements, deleteAd } from '@/api/advertise.ts'
import router from "@/router"

const advertisements = ref<AdvertisementVO[]>([])

const loadAdvertisements = async () => {
  try {
    const res = await getAllAdvertisements()
    advertisements.value = res.data.data
  } catch (error) {
    ElMessage.error('广告加载失败')
  }
}

const handleDelete = async (id: number) => {
  try {
    await deleteAd(id)
    ElMessage.success('删除成功')
    loadAdvertisements()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadAdvertisements()
})
</script>

<template>
  <div class="ad-manage-container">
    <ElRow :gutter="20" class="ad-grid">
      <ElCol
          v-for="ad in advertisements"
          :key="ad.id"
          :xs="24" :sm="12" :md="8" :lg="6"
          class="ad-col"
      >
        <ElCard class="ad-card" shadow="hover">
          <div class="action-buttons">
            <ElButton
                type="primary"
                size="small"
                @click.stop="router.push(`/advertise/${ad.id}/update`)"
            >编辑</ElButton>
            <ElButton
                type="danger"
                size="small"
                @click.stop="handleDelete(ad.id!)"
            >删除</ElButton>
          </div>

          <div class="ad-image">
            <img
                :src="ad.imageUrl || '/src/assets/loadFailed.png'"
                :alt="ad.title"
            >
          </div>

          <div class="ad-info">
            <h3 class="ad-title">{{ ad.title }}</h3>
            <p class="ad-content">{{ ad.content }}</p>
            <div class="ad-meta">
              <span class="product-id">关联商品ID: {{ ad.productId }}</span>
            </div>
          </div>
        </ElCard>
      </ElCol>
    </ElRow>
  </div>
</template>

<style scoped lang="scss">
.ad-manage-container {
  background: url("@/assets/bookShelve.jpg") no-repeat fixed center center;
  background-size: cover;
  padding: 2rem;
  min-height: 100vh;
}

.ad-grid {
  max-width: 1400px;
  margin: 0 auto;
}

.ad-col {
  margin-bottom: 20px;
}

.ad-card {
  position: relative;
  background: #2d2d2d;
  border: none;
  border-radius: 8px;
  transition: transform 0.3s ease;

  &:hover {
    transform: translateY(-5px);
  }
}

.action-buttons {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 2;
  display: flex;
  gap: 5px;
}

.ad-image {
  height: 200px;
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

.ad-info {
  padding: 1.2rem;
}

.ad-title {
  color: #fff;
  font-size: 1.1rem;
  margin: 0 0 0.5rem;
  font-weight: 500;
}

.ad-content {
  color: #888;
  font-size: 0.9rem;
  line-height: 1.4;
  height: 60px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.ad-meta {
  margin-top: 1rem;
  .product-id {
    color: #ffd700;
    font-size: 0.8rem;
  }
}

:deep(.el-card__body) {
  padding: 0;
}
</style>