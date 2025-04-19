// src/api/cart.ts
import { axios } from '@/utils/request'

export interface CartVO {
    cartitemid: number
    productid: number
    title: string
    price: number
    description: string
    cover: string
    quantity: number
    detail?: string
}

export interface CartListVO {
    items: CartVO[]
    total: number       // 商品种类数
    totalAmount: number // 总金额
}

/**
 * 获取购物车列表
 */
export const getCartItems = () => {
    return axios.get<CartListVO>('/api/cart/')
        .then(res => res.data.data)
}

/**
 * 删除购物车商品
 * @param cartItemId 购物车商品ID
 */
export const deleteCartItem = (cartItemId: number) => {
    return axios.delete(`/api/cart/${cartItemId}`)
}

/**
 * 更新购物车商品数量
 * @param cartItemId 购物车商品ID
 * @param quantity 新的数量
 */
export const updateCart = (cartItemId: number, quantity: number) => {
    return axios.patch(`/api/cart/item/${cartItemId}`, {quantity: quantity })
}