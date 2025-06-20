<script setup>
import { ref, computed } from "vue";
import { ElDropdown, ElDropdownItem, ElDropdownMenu, ElAvatar, ElInput, ElButton, ElIcon } from "element-plus";
import { useRouter } from "vue-router";
import { User } from '@element-plus/icons-vue';

const router = useRouter();
const searchKeyword = ref('');

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
      <a href="/product-create">创建商品</a>
      <a href="/advertise/manage">管理广告</a>
    </nav>
    <div class="right">
      <ElInput
        v-model="searchKeyword"
        placeholder="搜索文章..."
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

<style scoped>
.header-space {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #111;
  margin: 20px 20px;
  color: white;
}
.header {
  position: fixed;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #111;
  padding: 10px 20px;
  color: white;
  width: 98.5%;
  z-index: 9;
}

.logo {
  font-size: 24px;
  font-weight: bold;
}

.nav {
  display: flex;
  gap: 20px;
}

.nav a {
  color: white;
  text-decoration: none;
  font-size: 14px;
}

.nav a:hover {
  color: #ff4d4f;
}

.right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.search {
  width: 300px;
  border-radius: 30px;
}

.user-section {
  .user-info {
    display: flex;
    align-items: center;
    gap: 10px;
    cursor: pointer;
    padding: 5px 10px;
    border-radius: 20px;
    transition: background-color 0.3s;

    &:hover {
      background-color: rgba(255, 255, 255, 0.1);
    }

    .avatar {
      cursor: pointer;
    }

    .username {
      color: white;
      font-size: 14px;
      font-weight: 500;
    }
  }
}

.login-section {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
