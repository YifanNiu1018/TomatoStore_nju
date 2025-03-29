// router/request.ts
import { createRouter, createWebHistory } from 'vue-router'
import Start from '@/views/user/Start.vue'
import Register from '@/views/user/Register.vue'
import Login from '@/views/user/Login.vue'
import Home from '@/views/Home.vue'
import UserInfo from '@/views/user/UserInfo.vue'

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
      redirect: '/UserInfo',
      component: Home,
      children: [
        {
          path: '/userInfo',
          name: 'UserInfo',
          component: UserInfo,
          meta: {title: '个人信息'},
        },
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