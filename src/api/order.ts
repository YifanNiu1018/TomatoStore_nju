// src/api/order.ts
import { axios } from '@/utils/request'

export interface PaymentResultVO {
    paymentForm: string
    orderId: string
    totalAmount: number
    paymentMethod: string
}
export interface OrderItemVO {
    productId: number
    title: string
    price: number
    quantity: number
    cover: string
}

export interface OrderVO {
    orderId: string
    userId: number
    totalAmount: number
    status: 'PENDING' | 'COMPLETED' | 'CANCELLED'
    paymentMethod: string
    createTime: string
    items: OrderItemVO[]
}
/**
 * 发起支付
 * @param orderId 订单ID
 */
export const requestPayment = (orderId: string) => {
    return axios.post<PaymentResultVO>(`/api/orders/${orderId}/pay`)
        .then(res => res.data.data)
}
/**
 * 获取订单详情
 * @param orderId 订单ID
 */
export const getOrderDetails = (orderId: string) => {
    return axios.get<OrderVO>(`/api/orders/${orderId}`)
        .then(res => res.data.data)
}
/**
 * 检查支付状态
 * @param orderId 订单ID
 */
export const checkPaymentStatus = (orderId: string) => {
    return axios.get<{ paid: boolean }>(`/api/orders/${orderId}/payment-status`)
        .then(res => res.data.data)
}