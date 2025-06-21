<script setup>
import { ref, computed } from "vue";
import { ElDropdown, ElDropdownItem, ElDropdownMenu, ElAvatar, ElInput, ElButton, ElIcon } from "element-plus";
import { useRouter } from "vue-router";
import { User } from '@element-plus/icons-vue';

const router = useRouter();
const searchKeyword = ref('');

// 响应式的role获取
const role = computed(() => {
  const userRole = sessionStorage.getItem("role");
  console.log('当前用户role:', userRole); // 添加调试日志
  return userRole;
});

// 用户信息（可以从 store 或 API 获取）
const userInfo = ref({
  username: sessionStorage.getItem("username") || "测试用户",
  avatar: sessionStorage.getItem("avatar") || "", // 用户头像URL，如果为空则显示默认图标
});

// 是否已登录 - 强制显示头像用于测试
const isLoggedIn = computed(() => {
  return true; // 临时设为 true 以便测试头像显示
});

const handleCommand = (command) => {
  if (command === "settings") {
    router.push("/userInfo"); // 跳转到个人设置页面
  } else if (command === "logout") {
    console.log("执行退出逻辑");
    sessionStorage.removeItem("token");
    sessionStorage.removeItem("username");
    sessionStorage.removeItem("role"); // 清除role信息
    router.push("/login");
    // 这里可以添加退出登录的逻辑，比如清除token等
  }
};

// 处理头像加载错误
const handleAvatarError = () => {
  console.log('头像加载失败，使用默认图标');
};

// 处理搜索
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/search',
      query: { keyword: searchKeyword.value }
    });
  }
};
</script>

<template>
  <div class="header-space">

  </div>
  <div class="header">
    <div class="logo">番茄书店</div>
    <nav class="nav">
      <a href="/productlist">番茄商城</a>
      <a href="/cart">我的购物车</a>
      <a href="/forum">番茄论坛</a>
      <a v-if="role === 'admin'" href="/product-create">创建商品</a>
      <a v-if="role === 'admin'" href="/advertise/manage">管理广告</a>
    </nav>
    <div class="right">
      <ElInput
        v-model="searchKeyword"
        placeholder="搜索商品..."
        class="search"
        size="small"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">搜索</el-button>
        </template>
      </ElInput>

      <!-- 已登录状态显示用户头像 -->
      <div v-if="isLoggedIn" class="user-section">
        <ElDropdown @command="handleCommand">
          <div class="user-info">
            <ElAvatar
              :size="40"
              :src="userInfo.avatar"
              class="avatar"
              @error="handleAvatarError"
            >
              <el-icon><User /></el-icon>
            </ElAvatar>
            <span class="username">{{ userInfo.username }}</span>
          </div>
          <template #dropdown>
            <ElDropdownMenu>
              <ElDropdownItem command="settings">个人信息</ElDropdownItem>
              <ElDropdownItem command="logout">退出</ElDropdownItem>
            </ElDropdownMenu>
          </template>
        </ElDropdown>
      </div>

      <!-- 未登录状态显示登录按钮 -->
      <div v-else class="login-section">
        <el-button type="primary" size="small" @click="router.push('/login')">
          登录
        </el-button>
        <el-button size="small" @click="router.push('/register')">
          注册
        </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.header-space {
  height: 70px;
  background: transparent;
}

.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(20, 20, 20, 0.95);
  backdrop-filter: blur(10px);
  padding: 15px 30px;
  color: white;
  z-index: 1000;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.3);
  border-bottom: 1px solid rgba(255, 215, 0, 0.2);
}

.logo {
  font-size: 28px;
  font-weight: bold;
  color: #ffd700;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: scale(1.05);
    text-shadow: 2px 2px 8px rgba(255, 215, 0, 0.5);
  }
}

.nav {
  display: flex;
  gap: 30px;

  a, .router-link {
    color: #fff;
    text-decoration: none;
    font-size: 16px;
    font-weight: 500;
    padding: 8px 16px;
    border-radius: 25px;
    transition: all 0.3s ease;
    position: relative;

    &:hover {
      color: #ffd700;
      background: rgba(255, 215, 0, 0.1);
      transform: translateY(-2px);
    }

    &.router-link-active {
      color: #ffd700;
      background: rgba(255, 215, 0, 0.1);
    }

    &:before {
      content: '';
      position: absolute;
      bottom: -2px;
      left: 50%;
      width: 0;
      height: 2px;
      background: #ffd700;
      transition: all 0.3s ease;
      transform: translateX(-50%);
    }

    &:hover:before, &.router-link-active:before {
      width: 80%;
    }
  }
}

.right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.search {
  width: 320px;

  :deep(.el-input__wrapper) {
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 215, 0, 0.3);
    border-radius: 25px;
    box-shadow: none;

    &:hover {
      border-color: rgba(255, 215, 0, 0.5);
    }

    &.is-focus {
      border-color: #ffd700;
      box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
    }
  }

  :deep(.el-input__inner) {
    color: #fff;

    &::placeholder {
      color: #aaa;
    }
  }

  :deep(.el-input-group__append) {
    background: rgba(255, 215, 0, 0.7);
    border: none;
    border-radius: 0 25px 25px 0;

    .el-button {
      background: transparent;
      border: none;
      color: #333;
      font-weight: bold;

      &:hover {
        background: rgba(255, 215, 0, 0.2);
      }
    }
  }
}

.user-section {
  .user-info {
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
    padding: 8px 16px;
    border-radius: 25px;
    transition: all 0.3s ease;
    border: 1px solid rgba(255, 215, 0, 0.3);

    &:hover {
      background: rgba(255, 215, 0, 0.1);
      border-color: rgba(255, 215, 0, 0.5);
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
    }

    .avatar {
      cursor: pointer;
      border: 2px solid rgba(255, 215, 0, 0.5);
      transition: all 0.3s ease;

      &:hover {
        border-color: #ffd700;
        transform: scale(1.1);
      }
    }

    .username {
      color: #fff;
      font-size: 15px;
      font-weight: 600;
    }
  }
}

.login-section {
  display: flex;
  align-items: center;
  gap: 12px;

  .el-button {
    border-radius: 20px;
    font-weight: 600;
    padding: 8px 20px;

    &.el-button--primary {
      background: rgba(255, 215, 0, 0.7);
      border: none;
      color: #333;

      &:hover {
        background: rgba(255, 215, 0, 0.5);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(255, 215, 0, 0.3);
      }
    }

    &:not(.el-button--primary) {
      background: transparent;
      border: 1px solid rgba(255, 215, 0, 0.5);
      color: #ffd700;

      &:hover {
        background: rgba(255, 215, 0, 0.1);
        border-color: #ffd700;
        transform: translateY(-2px);
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .header {
    padding: 10px 15px;
  }

  .logo {
    font-size: 24px;
  }

  .nav {
    display: none; // 在移动端隐藏导航，可以考虑添加汉堡菜单
  }

  .search {
    width: 200px;
  }

  .user-section .user-info .username {
    display: none; // 在移动端只显示头像
  }
}
</style>
