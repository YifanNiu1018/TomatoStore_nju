<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElButton, ElInput, ElCard, ElAvatar, ElDivider, ElMessageBox } from 'element-plus'
import {
  getProductComments,
  getCommentReplies,
  addComment,
  replyComment,
  likeComment,
  deleteComment,
  type CommentVO,
  type CommentWithReplies
} from '@/api/comment'

interface Props {
  productId: number
  commentType?: 'product' | 'article' // 评论类型：商品评论或文章评论
}

const props = withDefaults(defineProps<Props>(), {
  commentType: 'product'
})

// 评论数据
const comments = ref<CommentWithReplies[]>([])
const loading = ref(false)

// 新评论表单
const newComment = ref('')
const isSubmitting = ref(false)

// 回复表单
const replyingTo = ref<number | null>(null)
const replyContent = ref('')
const isReplying = ref(false)

// 用户信息（从sessionStorage获取）
const currentUser = computed(() => ({
  userId: sessionStorage.getItem('username') || 'guest',
  nickName: sessionStorage.getItem('username') || '游客'
}))

// 获取评论
const fetchComments = async () => {
  loading.value = true
  try {
    console.log('获取评论，productId:', props.productId, 'commentType:', props.commentType)

    // 验证 productId
    if (!props.productId || isNaN(props.productId)) {
      console.error('无效的 productId:', props.productId)
      ElMessage.error('无效的ID参数')
      comments.value = []
      return
    }

    const response = await getProductComments(props.productId, props.commentType)
    console.log('获取到的评论响应:', response)

    // 检查响应数据格式
    let commentsData = []
    if (Array.isArray(response)) {
      commentsData = response
    } else if (response && Array.isArray(response.data)) {
      commentsData = response.data
    } else if (response && response.content && Array.isArray(response.content)) {
      commentsData = response.content
    } else {
      console.warn('评论数据格式异常，设置为空数组:', response)
      commentsData = []
    }

    console.log('处理后的评论数据:', commentsData)

    // 转换为组件需要的格式
    comments.value = commentsData.map((comment: CommentVO) => ({
      ...comment,
      replies: [],
      showReplies: false,
      replyCount: 0  // 初始设为0，点击展开时再获取
    }))

    console.log('最终的评论数据:', comments.value)
  } catch (error) {
    console.error('获取评论失败:', error)
    ElMessage.error('获取评论失败')
    // 设置空数组，避免页面崩溃
    comments.value = []
  } finally {
    loading.value = false
  }
}

// 获取评论回复
const fetchReplies = async (commentId: number) => {
  try {
    const response = await getCommentReplies(commentId)
    const commentIndex = comments.value.findIndex(c => c.commentId === commentId)
    if (commentIndex !== -1) {
      comments.value[commentIndex].replies = response
      comments.value[commentIndex].replyCount = response.length
    }
  } catch (error) {
    console.error('获取回复失败:', error)
    ElMessage.error('获取回复失败')
  }
}

// 切换回复显示
const toggleReplies = async (comment: CommentWithReplies) => {
  if (!comment.showReplies && (!comment.replies || comment.replies.length === 0)) {
    await fetchReplies(comment.commentId!)
  }
  comment.showReplies = !comment.showReplies
}

// 提交新评论
const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  if (!currentUser.value.userId || currentUser.value.userId === 'guest') {
    ElMessage.warning('请先登录后再评论')
    return
  }

  isSubmitting.value = true
  try {
    const commentData: CommentVO = {
      productId: props.productId,
      content: newComment.value.trim(),
      userId: currentUser.value.userId,
      nickName: currentUser.value.nickName,
      PCommentId: null  // 一级评论，父评论ID为null
    }

    console.log('准备发送评论数据:', commentData)

    const result = await addComment(commentData)
    console.log('评论成功，结果:', result)

    ElMessage.success('评论发表成功')
    newComment.value = ''
    await fetchComments()
  } catch (error) {
    console.error('发表评论失败:', error)

    let errorMessage = '发表评论失败'
    if (error?.response?.data?.message) {
      errorMessage = error.response.data.message
    } else if (error?.message) {
      errorMessage = error.message
    }

    ElMessage.error(errorMessage)
  } finally {
    isSubmitting.value = false
  }
}

// 提交回复
const submitReply = async (parentCommentId: number) => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  if (!currentUser.value.userId || currentUser.value.userId === 'guest') {
    ElMessage.warning('请先登录后再回复')
    return
  }

  isReplying.value = true
  try {
    const replyData: CommentVO = {
      productId: props.productId,
      PCommentId: parentCommentId,  // 确保字段名正确
      content: replyContent.value.trim(),
      userId: currentUser.value.userId,
      nickName: currentUser.value.nickName
    }

    console.log('准备发送回复数据:', replyData)
    console.log('父评论ID:', parentCommentId)

    const result = await replyComment(replyData)
    console.log('回复成功，结果:', result)

    ElMessage.success('回复发表成功')
    replyContent.value = ''
    replyingTo.value = null

    // 重新获取该评论的回复
    await fetchReplies(parentCommentId)

    // 确保回复区域是展开的
    const commentIndex = comments.value.findIndex(c => c.commentId === parentCommentId)
    if (commentIndex !== -1) {
      comments.value[commentIndex].showReplies = true
    }
  } catch (error) {
    console.error('发表回复失败:', error)
    console.error('错误对象:', error)

    let errorMessage = '发表回复失败'
    if (error?.response?.data?.message) {
      errorMessage = error.response.data.message
    } else if (error?.message) {
      errorMessage = error.message
    }

    ElMessage.error(errorMessage)
  } finally {
    isReplying.value = false
  }
}

// 点赞评论
const handleLike = async (commentId: number) => {
  if (!currentUser.value.userId || currentUser.value.userId === 'guest') {
    ElMessage.warning('请先登录后再点赞')
    return
  }

  try {
    await likeComment(commentId)
    ElMessage.success('点赞成功')

    // 更新本地点赞数
    const updateLikeCount = (commentsList: CommentWithReplies[]) => {
      commentsList.forEach(comment => {
        if (comment.commentId === commentId) {
          comment.goodCount = (comment.goodCount || 0) + 1
        }
        if (comment.replies) {
          updateLikeCount(comment.replies)
        }
      })
    }

    updateLikeCount(comments.value)
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('点赞失败')
  }
}

// 开始回复
const startReply = (commentId: number) => {
  replyingTo.value = commentId
  replyContent.value = ''
}

// 取消回复
const cancelReply = () => {
  replyingTo.value = null
  replyContent.value = ''
}

// 删除评论
const handleDeleteComment = async (comment: CommentVO) => {
  if (!currentUser.value.userId || currentUser.value.userId === 'guest') {
    ElMessage.warning('请先登录')
    return
  }

  // 检查是否是评论作者
  if (comment.userId !== currentUser.value.userId) {
    ElMessage.warning('只能删除自己的评论')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除这条评论吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    await deleteComment(comment.commentId!)
    ElMessage.success('评论删除成功')

    // 重新获取评论列表
    await fetchComments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
      ElMessage.error('删除评论失败')
    }
  }
}

// 检查是否可以删除评论（是否是评论作者）
const canDeleteComment = (comment: CommentVO) => {
  return currentUser.value.userId &&
         currentUser.value.userId !== 'guest' &&
         comment.userId === currentUser.value.userId
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

onMounted(() => {
  fetchComments()
})
</script>

<template>
  <div class="product-comments">
    <!-- 评论标题 -->
    <div class="comments-header">
      <h3 class="comments-title">
        {{ props.commentType === 'article' ? '文章评论' : '商品评论' }}
      </h3>
      <span class="comments-count">{{ comments.length }} 条评论</span>
    </div>

    <!-- 发表评论 -->
    <div class="comment-form">
      <div class="form-header">
        <ElAvatar :size="40" class="user-avatar">
          {{ currentUser.nickName.charAt(0) }}
        </ElAvatar>
        <span class="user-name">{{ currentUser.nickName }}</span>
      </div>
      
      <ElInput
        v-model="newComment"
        type="textarea"
        :rows="3"
        :placeholder="props.commentType === 'article' ? '写下你对这篇文章的看法...' : '写下你的评论...'"
        class="comment-input"
        maxlength="500"
        show-word-limit
      />
      
      <div class="form-actions">
        <ElButton 
          type="primary" 
          @click="submitComment"
          :loading="isSubmitting"
          class="submit-btn"
        >
          发表评论
        </ElButton>
      </div>
    </div>

    <ElDivider />

    <!-- 评论列表 -->
    <div class="comments-list" v-loading="loading">
      <div v-if="comments.length === 0 && !loading" class="empty-comments">
        <div class="empty-icon">💬</div>
        <p>暂无评论，快来发表第一条评论吧！</p>
      </div>

      <div v-for="comment in comments" :key="comment.commentId" class="comment-item">
        <!-- 一级评论 -->
        <div class="comment-main">
          <ElAvatar :size="40" class="comment-avatar">
            {{ comment.nickName.charAt(0) }}
          </ElAvatar>
          
          <div class="comment-content">
            <div class="comment-header">
              <span class="comment-author">{{ comment.nickName }}</span>
              <span class="comment-time">{{ formatTime(comment.postTime!) }}</span>
            </div>
            
            <div class="comment-text">{{ comment.content }}</div>
            
            <div class="comment-actions">
              <ElButton
                text
                @click="handleLike(comment.commentId!)"
                class="action-btn like-btn"
              >
                👍 {{ comment.goodCount || 0 }}
              </ElButton>

              <ElButton
                text
                @click="startReply(comment.commentId!)"
                class="action-btn reply-btn"
              >
                💬 回复
              </ElButton>

              <ElButton
                v-if="comment.replyCount && comment.replyCount > 0"
                text
                @click="toggleReplies(comment)"
                class="action-btn toggle-btn"
              >
                {{ comment.showReplies ? '收起' : '展开' }} {{ comment.replyCount }} 条回复
              </ElButton>

              <!-- 删除按钮 - 只有评论作者可以看到 -->
              <ElButton
                v-if="canDeleteComment(comment)"
                text
                @click="handleDeleteComment(comment)"
                class="action-btn delete-btn"
              >
                🗑️ 删除
              </ElButton>
            </div>
          </div>
        </div>

        <!-- 回复表单 -->
        <div v-if="replyingTo === comment.commentId" class="reply-form">
          <ElInput
            v-model="replyContent"
            type="textarea"
            :rows="2"
            placeholder="写下你的回复..."
            class="reply-input"
            maxlength="300"
            show-word-limit
          />
          <div class="reply-actions">
            <ElButton size="small" @click="cancelReply">取消</ElButton>
            <ElButton 
              type="primary" 
              size="small" 
              @click="submitReply(comment.commentId!)"
              :loading="isReplying"
            >
              发表回复
            </ElButton>
          </div>
        </div>

        <!-- 二级评论（回复） -->
        <div v-if="comment.showReplies && comment.replies" class="replies-list">
          <div 
            v-for="reply in comment.replies" 
            :key="reply.commentId" 
            class="reply-item"
          >
            <ElAvatar :size="32" class="reply-avatar">
              {{ reply.nickName.charAt(0) }}
            </ElAvatar>
            
            <div class="reply-content">
              <div class="reply-header">
                <span class="reply-author">{{ reply.nickName }}</span>
                <span class="reply-time">{{ formatTime(reply.postTime!) }}</span>
              </div>
              
              <div class="reply-text">{{ reply.content }}</div>
              
              <div class="reply-actions">
                <ElButton
                  text
                  @click="handleLike(reply.commentId!)"
                  class="action-btn like-btn"
                >
                  👍 {{ reply.goodCount || 0 }}
                </ElButton>

                <!-- 删除回复按钮 - 只有回复作者可以看到 -->
                <ElButton
                  v-if="canDeleteComment(reply)"
                  text
                  @click="handleDeleteComment(reply)"
                  class="action-btn delete-btn"
                >
                  🗑️ 删除
                </ElButton>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.product-comments {
  margin-top: 2rem;
  padding: 1.5rem;
  background: rgba(30, 30, 30, 0.5);
  border-radius: 12px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  
  .comments-title {
    font-size: 1.5rem;
    color: #ffd700;
    margin: 0;
  }
  
  .comments-count {
    color: #aaa;
    font-size: 0.9rem;
  }
}

.comment-form {
  margin-bottom: 1.5rem;
  
  .form-header {
    display: flex;
    align-items: center;
    gap: 0.8rem;
    margin-bottom: 1rem;
    
    .user-avatar {
      background: rgba(255, 215, 0, 0.7);
      color: #333;
      font-weight: bold;
    }
    
    .user-name {
      color: #fff;
      font-weight: 500;
    }
  }
  
  .comment-input {
    margin-bottom: 1rem;
    
    :deep(.el-textarea__inner) {
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 215, 0, 0.3);
      color: #fff;
      border-radius: 8px;
      
      &:focus {
        border-color: #ffd700;
        box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
      }
      
      &::placeholder {
        color: #aaa;
      }
    }
  }
  
  .form-actions {
    text-align: right;
    
    .submit-btn {
      background: rgba(255, 215, 0, 0.7);
      border: none;
      color: #333;
      font-weight: bold;
      
      &:hover {
        background: rgba(255, 215, 0, 0.5);
      }
    }
  }
}

.empty-comments {
  text-align: center;
  padding: 3rem 1rem;
  color: #aaa;
  
  .empty-icon {
    font-size: 3rem;
    margin-bottom: 1rem;
  }
}

.comments-list {
  .comment-item {
    margin-bottom: 1.5rem;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
}

.comment-main {
  display: flex;
  gap: 1rem;
  
  .comment-avatar {
    background: rgba(255, 215, 0, 0.7);
    color: #333;
    font-weight: bold;
    flex-shrink: 0;
  }
  
  .comment-content {
    flex: 1;
    
    .comment-header {
      display: flex;
      align-items: center;
      gap: 1rem;
      margin-bottom: 0.5rem;
      
      .comment-author {
        color: #ffd700;
        font-weight: 600;
      }
      
      .comment-time {
        color: #888;
        font-size: 0.85rem;
      }
    }
    
    .comment-text {
      color: #fff;
      line-height: 1.6;
      margin-bottom: 0.8rem;
    }
    
    .comment-actions {
      display: flex;
      gap: 1rem;
      
      .action-btn {
        color: #aaa;
        font-size: 0.85rem;
        padding: 0.2rem 0.5rem;
        
        &:hover {
          color: #ffd700;
          background: rgba(255, 215, 0, 0.1);
        }
        
        &.like-btn:hover {
          color: #ff6b6b;
        }

        &.delete-btn {
          color: #999;

          &:hover {
            color: #ff4757;
            background: rgba(255, 71, 87, 0.1);
          }
        }
      }
    }
  }
}

.reply-form {
  margin: 1rem 0 1rem 3rem;
  padding: 1rem;
  background: rgba(40, 40, 40, 0.5);
  border-radius: 8px;
  
  .reply-input {
    margin-bottom: 0.8rem;
    
    :deep(.el-textarea__inner) {
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 215, 0, 0.3);
      color: #fff;
      
      &:focus {
        border-color: #ffd700;
      }
      
      &::placeholder {
        color: #aaa;
      }
    }
  }
  
  .reply-actions {
    text-align: right;
    
    .el-button {
      margin-left: 0.5rem;
    }
  }
}

.replies-list {
  margin-left: 3rem;
  margin-top: 1rem;
  padding-left: 1rem;
  border-left: 2px solid rgba(255, 215, 0, 0.3);
  
  .reply-item {
    display: flex;
    gap: 0.8rem;
    margin-bottom: 1rem;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .reply-avatar {
      background: rgba(255, 215, 0, 0.5);
      color: #333;
      font-weight: bold;
      flex-shrink: 0;
    }
    
    .reply-content {
      flex: 1;
      
      .reply-header {
        display: flex;
        align-items: center;
        gap: 0.8rem;
        margin-bottom: 0.3rem;
        
        .reply-author {
          color: #ffd700;
          font-weight: 500;
          font-size: 0.9rem;
        }
        
        .reply-time {
          color: #888;
          font-size: 0.8rem;
        }
      }
      
      .reply-text {
        color: #fff;
        line-height: 1.5;
        margin-bottom: 0.5rem;
        font-size: 0.9rem;
      }
      
      .reply-actions {
        .action-btn {
          color: #aaa;
          font-size: 0.8rem;
          padding: 0.1rem 0.3rem;

          &:hover {
            color: #ff6b6b;
            background: rgba(255, 107, 107, 0.1);
          }

          &.delete-btn {
            color: #999;

            &:hover {
              color: #ff4757;
              background: rgba(255, 71, 87, 0.1);
            }
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .product-comments {
    padding: 1rem;
  }
  
  .comment-main {
    gap: 0.8rem;
  }
  
  .replies-list {
    margin-left: 2rem;
  }
  
  .reply-form {
    margin-left: 2rem;
  }
  
  .comment-actions {
    flex-wrap: wrap;
  }
}
</style>
