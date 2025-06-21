<!-- src/views/advertisement/AdvertisementManageView.vue -->
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElRow, ElCol, ElCard, ElButton, ElMessageBox } from 'element-plus'
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

const handleDelete = async (id: number, title: string) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除广告"${title}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    await deleteAd(id)
    ElMessage.success('广告删除成功')
    loadAdvertisements()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 查看广告详情
const handleViewDetail = (ad: AdvertisementVO) => {
  ElMessageBox.alert(
    `
    <div style="text-align: left; color: #333; line-height: 1.6;">
      <div style="margin-bottom: 20px;">
        <h3 style="color: #ffd700; margin-bottom: 15px; font-size: 1.3rem; border-bottom: 2px solid #ffd700; padding-bottom: 8px;">📢 广告详情</h3>
      </div>

      <div style="margin-bottom: 15px;">
        <p style="margin: 8px 0;"><strong style="color: #666; min-width: 80px; display: inline-block;">标题：</strong><span style="color: #333;">${ad.title}</span></p>
        <p style="margin: 8px 0;"><strong style="color: #666; min-width: 80px; display: inline-block;">内容：</strong><span style="color: #333;">${ad.content}</span></p>
        <p style="margin: 8px 0;"><strong style="color: #666; min-width: 80px; display: inline-block;">商品ID：</strong><span style="color: #ffd700; font-weight: bold;">#${ad.productId}</span></p>
      </div>

      <div style="margin-top: 20px;">
        <strong style="color: #666;">广告图片：</strong><br>
        <div style="margin-top: 10px; text-align: center; background: #f5f5f5; padding: 10px; border-radius: 8px;">
          <img src="${ad.imageUrl || '/src/assets/loadFailed.png'}"
               style="max-width: 100%; max-height: 250px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);"
               alt="广告图片">
        </div>
      </div>

      <div style="margin-top: 20px; padding: 10px; background: #f0f9ff; border-left: 4px solid #ffd700; border-radius: 4px;">
        <p style="margin: 0; color: #666; font-size: 0.9rem;">💡 提示：点击商品ID可以跳转到商品详情页面</p>
      </div>
    </div>
    `,
    '广告详情',
    {
      confirmButtonText: '关闭',
      dangerouslyUseHTMLString: true,
      customStyle: {
        width: '600px'
      }
    }
  )
}

// 跳转到关联商品
const goToProduct = (productId: number | string) => {
  console.log('=== 商品跳转调试信息 ===')
  console.log('点击商品ID，准备跳转到商品详情')
  console.log('productId:', productId, 'type:', typeof productId)
  console.log('router对象:', router)

  if (!productId) {
    console.error('商品ID为空或无效')
    ElMessage.error('商品ID无效')
    return
  }

  try {
    const targetPath = `/productlist/${productId}`
    console.log('目标跳转路径:', targetPath)

    // 先尝试简单的跳转
    router.push(targetPath).then(() => {
      console.log('路由跳转成功')
      ElMessage.success(`正在跳转到商品 #${productId} 的详情页面`)
    }).catch((error) => {
      console.error('路由跳转Promise失败:', error)
      ElMessage.error('跳转失败：' + error.message)
    })

  } catch (error) {
    console.error('路由跳转同步失败:', error)
    ElMessage.error('跳转失败，请稍后重试')
  }
}

// 测试方法
const testNavigation = () => {
  console.log('测试导航到商品列表')
  router.push('/productlist')
}

onMounted(() => {
  loadAdvertisements()
})
</script>

<template>
  <div class="ad-manage-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">广告管理</h1>
      <p class="page-subtitle">管理您的商品推广广告</p>
<!--      &lt;!&ndash; 调试按钮 &ndash;&gt;-->
<!--      <div class="debug-buttons" style="margin-top: 1rem;">-->
<!--        <ElButton type="primary" size="small" @click="testNavigation">-->
<!--          测试导航-->
<!--        </ElButton>-->
<!--        <ElButton type="success" size="small" @click="goToProduct('1')">-->
<!--          测试跳转商品1-->
<!--        </ElButton>-->
<!--      </div>-->
    </div>

    <!-- 广告网格 -->
    <div class="ads-section">
      <div v-if="advertisements.length === 0" class="empty-state">
        <div class="empty-icon">📢</div>
        <h3 class="empty-title">暂无广告</h3>
        <p class="empty-text">还没有创建任何广告，去商品详情页创建第一个广告吧！</p>
      </div>

      <ElRow v-else :gutter="24" class="ad-grid">
        <ElCol
            v-for="ad in advertisements"
            :key="ad.id"
            :xs="24" :sm="12" :md="8" :lg="6"
            class="ad-col"
        >
          <ElCard class="ad-card" shadow="hover" @click="handleViewDetail(ad)">
            <!-- 操作按钮 -->
            <div class="action-buttons">
              <ElButton
                  type="primary"
                  size="small"
                  @click.stop="router.push(`/advertise/${ad.id}/update`)"
                  class="edit-btn"
              >
                <span class="btn-icon">✏️</span>
                编辑
              </ElButton>
              <ElButton
                  type="danger"
                  size="small"
                  @click.stop="handleDelete(ad.id!, ad.title)"
                  class="delete-btn"
              >
                <span class="btn-icon">🗑️</span>
                删除
              </ElButton>
            </div>

            <!-- 广告图片 -->
            <div class="ad-image">
              <img
                  :src="ad.imageUrl || '/src/assets/loadFailed.png'"
                  :alt="ad.title"
              >
              <div class="image-overlay">
                <span class="view-text">查看详情</span>
              </div>
            </div>

            <!-- 广告信息 -->
            <div class="ad-info">
              <h3 class="ad-title">{{ ad.title }}</h3>
              <p class="ad-content">{{ ad.content }}</p>
              <div class="ad-meta">
                <div class="meta-item">
                  <span class="meta-label">关联商品:</span>
                  <span
                    class="product-id clickable"
                    @click.stop="goToProduct(ad.productId)"
                    title="点击查看商品详情"
                  >
                    #{{ ad.productId }}
                  </span>
                  <ElButton
                    type="text"
                    size="small"
                    @click.stop="goToProduct(ad.productId)"
                    class="view-product-btn"
                  >
                    查看商品
                  </ElButton>
                </div>
                <div class="meta-item">
                  <span class="status-badge active">活跃中</span>
                </div>
              </div>
            </div>
          </ElCard>
        </ElCol>
      </ElRow>
    </div>
  </div>
</template>

<style scoped lang="scss">
.ad-manage-container {
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

.ads-section {
  max-width: 1400px;
  margin: 0 auto;
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  background: rgba(30, 30, 30, 0.7);
  border-radius: 16px;
  border: 1px solid rgba(255, 215, 0, 0.3);

  .empty-icon {
    font-size: 4rem;
    margin-bottom: 1rem;
  }

  .empty-title {
    font-size: 1.5rem;
    color: #ffd700;
    margin-bottom: 1rem;
  }

  .empty-text {
    color: #aaa;
    font-size: 1rem;
    margin: 0;
  }
}

.ad-grid {
  .ad-col {
    margin-bottom: 24px;
  }
}

.ad-card {
  position: relative;
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

    .image-overlay {
      opacity: 1;
    }

    .ad-image img {
      transform: scale(1.05);
    }
  }
}

.action-buttons {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 3;
  display: flex;
  gap: 8px;

  .edit-btn, .delete-btn {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 6px 12px;
    border-radius: 20px;
    font-size: 0.85rem;
    font-weight: 600;
    backdrop-filter: blur(4px);

    .btn-icon {
      font-size: 0.9rem;
    }
  }

  .edit-btn {
    background: rgba(0, 110, 255, 0.8);
    border: none;
    color: #fff;

    &:hover {
      background: rgba(0, 110, 255, 0.6);
      transform: translateY(-2px);
    }
  }

  .delete-btn {
    background: rgba(220, 50, 0, 0.8);
    border: none;
    color: #fff;

    &:hover {
      background: rgba(220, 50, 0, 0.6);
      transform: translateY(-2px);
    }
  }
}

.ad-image {
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

  .image-overlay {
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

    .view-text {
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

.ad-info {
  padding: 1.5rem;
}

.ad-title {
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

.ad-content {
  color: #aaa;
  font-size: 0.95rem;
  line-height: 1.5;
  height: 66px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  margin-bottom: 1rem;
}

.ad-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .meta-item {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    flex-wrap: wrap;

    .meta-label {
      color: #666;
      font-size: 0.85rem;
    }

    .product-id {
      color: #ffd700;
      font-size: 0.9rem;
      font-weight: bold;

      &.clickable {
        cursor: pointer;
        transition: all 0.3s ease;
        padding: 0.2rem 0.5rem;
        border-radius: 4px;

        &:hover {
          background: rgba(255, 215, 0, 0.2);
          color: #fff;
          transform: translateY(-1px);
        }
      }
    }

    .view-product-btn {
      color: #ffd700;
      font-size: 0.8rem;
      padding: 0.2rem 0.5rem;

      &:hover {
        background: rgba(255, 215, 0, 0.1);
        color: #fff;
      }
    }
  }

  .status-badge {
    padding: 0.3rem 0.8rem;
    border-radius: 15px;
    font-size: 0.8rem;
    font-weight: 600;

    &.active {
      background: rgba(0, 200, 0, 0.2);
      color: #00c800;
      border: 1px solid rgba(0, 200, 0, 0.5);
    }
  }
}

:deep(.el-card__body) {
  padding: 0;
}

// 响应式设计
@media (max-width: 768px) {
  .ad-manage-container {
    padding: 1rem;
  }

  .page-header {
    .page-title {
      font-size: 2.5rem;
    }
  }

  .action-buttons {
    flex-direction: column;
    gap: 4px;

    .edit-btn, .delete-btn {
      padding: 4px 8px;
      font-size: 0.8rem;
    }
  }

  .ad-image {
    height: 180px;
  }

  .ad-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
}
</style>