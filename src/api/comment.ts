import { axios } from '../utils/request'

export interface CommentVO {
  commentId?: number
  PCommentId?: number | null  // 注意：后端使用的是大写P
  productId: number
  content: string
  imgPath?: string
  userId: string
  nickName: string
  postTime?: Date | string
  goodCount?: number
}

export interface CommentWithReplies extends CommentVO {
  replies?: CommentVO[]
  showReplies?: boolean
  replyCount?: number
}

/**
 * 添加评论（一级评论）
 * @param comment 评论信息
 */
export const addComment = (comment: CommentVO) => {
  console.log('发送评论数据:', comment)
  return axios.post('/api/comments', comment)
    .then(res => {
      console.log('评论响应:', res)
      return res.data
    })
    .catch(error => {
      console.error('评论请求失败:', error)
      throw error
    })
}

/**
 * 回复评论（二级评论）
 * @param comment 回复信息
 */
export const replyComment = (comment: CommentVO) => {
  console.log('发送回复数据:', comment)

  // 尝试使用普通的评论接口，但设置PCommentId
  // 如果后端的reply接口有问题，可以尝试这种方式
  return axios.post('/api/comments', comment)
    .then(res => {
      console.log('回复响应:', res)
      return res.data
    })
    .catch(error => {
      console.error('回复请求失败:', error)
      console.error('错误详情:', error.response?.data)
      console.error('错误状态:', error.response?.status)
      console.error('错误头:', error.response?.headers)
      throw error
    })
}

/**
 * 获取产品的所有一级评论
 * @param productId 商品ID或文章ID
 * @param commentType 评论类型
 */
export const getProductComments = (productId: number, commentType: string = 'product') => {
  console.log('获取评论，productId:', productId, 'commentType:', commentType)

  // 根据评论类型选择不同的API路径
  const apiPath = commentType === 'article'
    ? `/api/comments/article/${productId}`
    : `/api/comments/product/${productId}`

  console.log('评论API路径:', apiPath)

  return axios.get(apiPath)
    .then(res => {
      console.log('评论API响应:', res)
      return res.data
    })
    .catch(error => {
      console.error('获取评论API失败:', error)

      // 如果专门的文章评论接口不存在，尝试使用商品评论接口
      if (commentType === 'article' && error.response?.status === 404) {
        console.log('文章评论接口不存在，尝试使用商品评论接口')
        return axios.get(`/api/comments/product/${productId}`)
          .then(res => {
            console.log('备用评论API响应:', res)
            return res.data
          })
      }

      throw error
    })
}

/**
 * 获取评论的所有回复
 * @param commentId 评论ID
 */
export const getCommentReplies = (commentId: number) => {
  return axios.get(`/api/comments/${commentId}/replies`)
    .then(res => res.data)
}

/**
 * 获取产品的所有评论（包括一级和二级）
 * @param productId 商品ID
 */
export const getAllProductComments = (productId: number) => {
  return axios.get(`/api/comments/product/${productId}/all`)
    .then(res => res.data)
}

/**
 * 给评论点赞
 * @param commentId 评论ID
 */
export const likeComment = (commentId: number) => {
  return axios.post(`/api/comments/${commentId}/like`)
    .then(res => res.data)
}

/**
 * 删除评论
 * @param commentId 评论ID
 */
export const deleteComment = (commentId: number) => {
  console.log('删除评论，commentId:', commentId)
  return axios.delete(`/api/comments/${commentId}`)
    .then(res => {
      console.log('删除评论响应:', res)
      return res.data
    })
    .catch(error => {
      console.error('删除评论失败:', error)
      throw error
    })
}
