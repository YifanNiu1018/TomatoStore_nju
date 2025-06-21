<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElButton, ElCard, ElPagination, ElTag, ElEmpty } from 'element-plus'
import { getAllArticles, likeArticle, incrementReadCount, type ArticleVO, type ArticlePage } from '@/api/article'

const router = useRouter()

// 文章数据
const articles = ref<ArticleVO[]>([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 用户信息
const currentUser = computed(() => ({
  userId: sessionStorage.getItem('username') || 'guest',
  nickName: sessionStorage.getItem('username') || '游客'
}))

// 获取文章列表
const fetchArticles = async () => {
  loading.value = true
  try {
    const response = await getAllArticles(currentPage.value - 1, pageSize.value)
    console.log('文章列表完整响应:', response)

    const data: ArticlePage = response.data
    console.log('文章列表数据:', data)

    articles.value = data.content
    total.value = data.totalElements

    // 调试：检查文章ID
    console.log('文章列表:', articles.value)
    articles.value.forEach((article, index) => {
      console.log(`文章${index}: articleId=${article.articleId}, type=${typeof article.articleId}`)
    })
  } catch (error) {
    console.error('获取文章列表失败:', error)
    ElMessage.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

// 页码变化
const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchArticles()
}

// 增加文章阅读数
const incrementArticleReadCount = async (article: ArticleVO) => {
  try {
    await incrementReadCount(article.articleId!)
    // 更新本地阅读数
    article.readCount = (article.readCount || 0) + 1
  } catch (error) {
    console.error('增加阅读数失败:', error)
  }
}

// 点赞文章
const handleLike = async (article: ArticleVO) => {
  if (!currentUser.value.userId || currentUser.value.userId === 'guest') {
    ElMessage.warning('请先登录后再点赞')
    return
  }

  try {
    await likeArticle(article.articleId!)
    ElMessage.success('点赞成功')
    // 更新本地点赞数
    article.goodCount = (article.goodCount || 0) + 1
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('点赞失败')
  }
}

// 跳转到创建文章页面
const goToCreateArticle = () => {
  if (!currentUser.value.userId || currentUser.value.userId === 'guest') {
    ElMessage.warning('请先登录后再发表文章')
    return
  }
  router.push('/forum/create')
}

// 格式化时间
const formatTime = (time: Date | string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return date.toLocaleDateString()
}

// 获取编辑器类型标签
const getEditorTypeTag = (editorType: number) => {
  return editorType === 1 ? 'Markdown' : '富文本'
}

onMounted(() => {
  fetchArticles()
})
</script>

<template>
  <div class="forum-container">
    <!-- 论坛头部 -->
    <div class="forum-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="forum-title">番茄论坛</h1>
          <p class="forum-subtitle">分享知识，交流思想</p>
        </div>
        <div class="action-section">
          <ElButton 
            type="primary" 
            @click="goToCreateArticle"
            class="create-btn"
            size="large"
          >
            📝 发表文章
          </ElButton>
        </div>
      </div>
    </div>

    <!-- 文章列表 -->
    <div class="articles-section">
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner">📚</div>
        <p>正在加载文章...</p>
      </div>

      <div v-else-if="articles.length === 0" class="empty-state">
        <ElEmpty description="暂无文章">
          <template #image>
            <div class="empty-icon">📖</div>
          </template>
          <ElButton type="primary" @click="goToCreateArticle">
            发表第一篇文章
          </ElButton>
        </ElEmpty>
      </div>

      <div v-else class="articles-list">
        <ElCard
          v-for="article in articles"
          :key="article.articleId"
          class="article-card"
          shadow="hover"
        >
          <div class="article-content">
            <!-- 文章封面 -->
            <div v-if="article.cover" class="article-cover">
              <img :src="article.cover" :alt="article.title" />
            </div>
            
            <!-- 文章信息 -->
            <div class="article-info">
              <div class="article-header">
                <h3 class="article-title">{{ article.title }}</h3>
                <div class="article-tags">
                  <ElTag 
                    :type="article.editorType === 1 ? 'success' : 'info'" 
                    size="small"
                  >
                    {{ getEditorTypeTag(article.editorType || 0) }}
                  </ElTag>
                </div>
              </div>
              
              <p class="article-summary">{{ article.summary || '暂无摘要' }}</p>
              
              <div class="article-meta">
                <div class="author-info">
                  <span class="author-name">{{ article.nickName }}</span>
                  <span class="post-time">{{ formatTime(article.postTime!) }}</span>
                </div>
                
                <div class="article-stats">
                  <span class="stat-item">
                    👁️ {{ article.readCount || 0 }}
                  </span>
                  <span class="stat-item">
                    👍 {{ article.goodCount || 0 }}
                  </span>
                  <span class="stat-item">
                    💬 {{ article.commentCount || 0 }}
                  </span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 操作按钮 -->
          <div class="article-actions">
            <ElButton
              text
              @click.stop="handleLike(article)"
              class="like-btn"
            >
              👍 点赞
            </ElButton>
          </div>
        </ElCard>
      </div>

      <!-- 分页 -->
      <div v-if="total > pageSize" class="pagination-wrapper">
        <ElPagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next, jumper, total"
          @current-change="handlePageChange"
          class="pagination"
        />
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.forum-container {
  background: url("@/assets/bookShelve.jpg") no-repeat fixed center center;
  background-size: cover;
  min-height: 100vh;
  padding: 2rem;
}

.forum-header {
  margin-bottom: 2rem;
  
  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: rgba(30, 30, 30, 0.8);
    padding: 2rem;
    border-radius: 16px;
    border: 1px solid rgba(255, 215, 0, 0.3);
  }
  
  .title-section {
    .forum-title {
      font-size: 3rem;
      color: #ffd700;
      margin-bottom: 0.5rem;
      text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
      font-weight: bold;
    }
    
    .forum-subtitle {
      color: #aaa;
      font-size: 1.2rem;
      margin: 0;
    }
  }
  
  .create-btn {
    background: linear-gradient(135deg, rgba(255, 215, 0, 0.8), rgba(255, 215, 0, 0.6));
    border: none;
    color: #333;
    font-weight: bold;
    padding: 0 2rem;
    height: 50px;
    border-radius: 25px;
    
    &:hover {
      background: linear-gradient(135deg, rgba(255, 215, 0, 0.6), rgba(255, 215, 0, 0.4));
      transform: translateY(-2px);
      box-shadow: 0 8px 20px rgba(255, 215, 0, 0.3);
    }
  }
}

.articles-section {
  max-width: 1200px;
  margin: 0 auto;
}

.loading-state {
  text-align: center;
  padding: 4rem 2rem;
  color: #aaa;
  
  .loading-spinner {
    font-size: 3rem;
    margin-bottom: 1rem;
    animation: bounce 2s infinite;
  }
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-10px); }
  60% { transform: translateY(-5px); }
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  
  .empty-icon {
    font-size: 4rem;
    margin-bottom: 1rem;
  }
  
  :deep(.el-empty) {
    .el-empty__description {
      color: #aaa;
    }
  }
}

.articles-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.article-card {
  background: rgba(30, 30, 30, 0.8);
  border: 1px solid rgba(255, 215, 0, 0.2);
  border-radius: 16px;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.3);
    border-color: rgba(255, 215, 0, 0.5);
  }
  
  :deep(.el-card__body) {
    padding: 0;
  }
}

.article-content {
  display: flex;
  gap: 1.5rem;
  padding: 1.5rem;
}

.article-cover {
  flex-shrink: 0;
  width: 200px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.article-info {
  flex: 1;
  
  .article-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 0.8rem;
    
    .article-title {
      color: #fff;
      font-size: 1.3rem;
      font-weight: 600;
      margin: 0;
      line-height: 1.4;
      flex: 1;
      
      &:hover {
        color: #ffd700;
      }
    }
    
    .article-tags {
      margin-left: 1rem;
    }
  }
  
  .article-summary {
    color: #aaa;
    line-height: 1.6;
    margin-bottom: 1rem;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  
  .article-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .author-info {
      display: flex;
      align-items: center;
      gap: 1rem;
      
      .author-name {
        color: #ffd700;
        font-weight: 600;
      }
      
      .post-time {
        color: #888;
        font-size: 0.9rem;
      }
    }
    
    .article-stats {
      display: flex;
      gap: 1rem;
      
      .stat-item {
        color: #aaa;
        font-size: 0.9rem;
        display: flex;
        align-items: center;
        gap: 0.3rem;
      }
    }
  }
}

.article-actions {
  display: flex;
  gap: 1rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid rgba(255, 215, 0, 0.2);
  
  .like-btn {
    color: #aaa;

    &:hover {
      color: #ffd700;
      background: rgba(255, 215, 0, 0.1);
    }
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
  
  .pagination {
    :deep(.el-pager li) {
      background: rgba(30, 30, 30, 0.8);
      color: #fff;
      border: 1px solid rgba(255, 215, 0, 0.3);
      
      &:hover {
        color: #ffd700;
        border-color: #ffd700;
      }
      
      &.is-active {
        background: rgba(255, 215, 0, 0.7);
        color: #333;
        border-color: #ffd700;
      }
    }
    
    :deep(.btn-prev), :deep(.btn-next) {
      background: rgba(30, 30, 30, 0.8);
      color: #fff;
      border: 1px solid rgba(255, 215, 0, 0.3);
      
      &:hover {
        color: #ffd700;
        border-color: #ffd700;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .forum-container {
    padding: 1rem;
  }
  
  .forum-header .header-content {
    flex-direction: column;
    gap: 1.5rem;
    text-align: center;
  }
  
  .article-content {
    flex-direction: column;
  }
  
  .article-cover {
    width: 100%;
    height: 200px;
  }
  
  .article-header {
    flex-direction: column;
    align-items: flex-start !important;
    gap: 0.5rem;
  }
  
  .article-meta {
    flex-direction: column;
    align-items: flex-start !important;
    gap: 0.8rem;
  }
}
</style>
