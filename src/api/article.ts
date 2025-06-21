import { axios } from '../utils/request'

export interface ArticleVO {
  articleId?: string
  userId: string
  nickName: string
  title: string
  cover?: string
  content: string
  markdownContent?: string
  editorType?: number // 0:富文本编辑器 1:markdown编辑器
  summary?: string
  postTime?: Date | string
  readCount?: number
  goodCount?: number
  commentCount?: number
}

export interface ArticlePage {
  content: ArticleVO[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

/**
 * 创建文章
 * @param article 文章信息
 */
export const createArticle = (article: ArticleVO) => {
  console.log('创建文章:', article)
  return axios.post('/api/articles', article)
    .then(res => {
      console.log('创建文章响应:', res)
      return res.data
    })
    .catch(error => {
      console.error('创建文章失败:', error)
      throw error
    })
}

/**
 * 分页获取所有文章
 * @param page 页码（从0开始）
 * @param size 每页大小
 * @param sortBy 排序字段
 * @param sortDir 排序方向
 */
export const getAllArticles = (
  page: number = 0,
  size: number = 10,
  sortBy: string = 'postTime',
  sortDir: string = 'desc'
) => {
  console.log('获取文章列表:', { page, size, sortBy, sortDir })
  return axios.get('/api/articles', {
    params: { page, size, sortBy, sortDir }
  })
    .then(res => {
      console.log('文章列表响应:', res)
      return res.data
    })
    .catch(error => {
      console.error('获取文章列表失败:', error)
      throw error
    })
}



/**
 * 增加文章阅读数
 * @param articleId 文章ID
 */
export const incrementReadCount = (articleId: string) => {
  return axios.post(`/api/articles/${articleId}/read`)
    .then(res => res.data)
    .catch(error => {
      console.error('增加阅读数失败:', error)
      throw error
    })
}

/**
 * 点赞文章
 * @param articleId 文章ID
 */
export const likeArticle = (articleId: string) => {
  return axios.post(`/api/articles/${articleId}/like`)
    .then(res => res.data)
    .catch(error => {
      console.error('点赞文章失败:', error)
      throw error
    })
}

/**
 * 取消点赞文章
 * @param articleId 文章ID
 */
export const unlikeArticle = (articleId: string) => {
  return axios.delete(`/api/articles/${articleId}/like`)
    .then(res => res.data)
    .catch(error => {
      console.error('取消点赞失败:', error)
      throw error
    })
}

/**
 * 更新文章评论数
 * @param articleId 文章ID
 * @param commentCount 评论数
 */
export const updateCommentCount = (articleId: string, commentCount: number) => {
  return axios.patch(`/api/articles/${articleId}/comment-count`, null, {
    params: { commentCount }
  })
    .then(res => res.data)
    .catch(error => {
      console.error('更新评论数失败:', error)
      throw error
    })
}
