// router/request.ts
import { createRouter, createWebHistory } from 'vue-router'
import Start from '@/views/user/Start.vue'
import Register from '@/views/user/Register.vue'
import Login from '@/views/user/Login.vue'
import Home from '@/views/Home.vue'
import UserInfo from '@/views/user/UserInfo.vue'
import Cart from "@/views/cart/Cart.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Start',
      component: Start,
      meta: { title: '番茄书店' }
    },
    {
      path: '/register',
      name: 'Register',
      component: Register,
      meta: { title: '用户注册' }
    },
    {
      path: '/login',
      name: 'Login',
      component: Login,
      meta: {title: '用户登录'}
    },
    {
      path: '/home',
      redirect: '/productlist',
      component: Home,
      children: [
        {
          path: '/userInfo',
          name: 'UserInfo',
          component: UserInfo,
          meta: {title: '个人信息'},
        },
        {
          path: '/productlist',
          name: 'productlist',
          component: () => import('@/views/products/ProductList.vue'),
          meta: {title: '番茄商城喵~'}
        },
        {
          path: '/product-create',
          name: 'CreateProduct',
          component: () => import('@/views/products/CreateProduct.vue'),
          meta: { title: '创建商品ing...' }
        },
        {
          path: '/productlist/:id',
          name: 'ProductDetail',
          component: () => import('@/views/products/ProductDetail.vue'),
          meta: {title: '商品详情'}
        },
        {
          path: '/productlist/:id/update',
          name: 'ProductUpdate',
          component: () => import('@/views/products/ProductUpdate.vue'),
          meta: { title: '更新商品信息ing...' }
        },
        {
          path: '/cart',
          name: 'cart',
          component: Cart,
          meta: {title: '我的购物车'}
        },
        {
          path: '/advertise/:id/create',
          name: 'AdvertiseCreate',
          component: () => import('@/views/advertise/Create.vue'),
          meta: {title: '创建广告喵~'}
        },
        {
          path: '/advertise/manage',
          name: 'AdvertiseManage',
          component: () => import('@/views/advertise/Manage.vue'),
          meta: {title: '管理广告喵~'}
        },
        {
          path: '/advertise/:id/update',
          name: 'AdvertiseUpdate',
          component: () => import('@/views/advertise/Update.vue'),
          meta: {title: '更新广告喵~'}
        },
        {
          path: '/forum',
          name: 'Forum',
          component: () => import('@/views/forum/Forum.vue'),
          meta: {title: '番茄论坛喵~'}
        }
      ]
    }
    // 可以继续添加其他路由...
  ]
})

// 设置页面标题
router.beforeEach((to, from, next) => {
  const token: string | null = sessionStorage.getItem('token');
  const role: string | null = sessionStorage.getItem('role')
  const username: string | null = sessionStorage.getItem('username')
  const title = to.meta.title as string || '默认标题'
  document.title = title
  next()
})

export default router