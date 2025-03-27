// router/request.ts
import { createRouter, createWebHistory } from 'vue-router'
import Start from '@/views/user/Start.vue'
import Register from '@/views/user/Register.vue'
import Login from '@/views/user/Login.vue'

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
    }
    // 可以继续添加其他路由...
  ]
})

// 设置页面标题
router.beforeEach((to, from, next) => {
  const title = to.meta.title as string || '默认标题'
  document.title = title
  next()
})

export default router