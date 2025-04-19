<script setup>
import { ref } from "vue";
import { ElDropdown, ElDropdownItem, ElDropdownMenu, ElAvatar, ElInput } from "element-plus";
import { useRouter } from "vue-router";

const router = useRouter();
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
</script>

<template>
  <div class="header-space">

  </div>
  <div class="header">
    <div class="logo">番茄书店</div>
    <nav class="nav">
      <a href="#">发现好书</a>
      <a href="#">我的电子书</a>
      <a href="/productlist">番茄商城</a>
      <a href="/cart">我的购物车</a>
      <a href="#">待定2</a>
      <a href="#">待定3</a>
      <a href="/product-create">创建商品</a>
    </nav>
    <div class="right">
      <ElInput placeholder="书名/作者" class="search" size="small" />
      <ElDropdown @command="handleCommand">
        <ElAvatar
          size=""
          src=""
          class="avatar"
        />
        <template #dropdown>
          <ElDropdownMenu>
            <ElDropdownItem command="settings">个人信息</ElDropdownItem>
            <ElDropdownItem command="logout">退出</ElDropdownItem>
          </ElDropdownMenu>
        </template>
      </ElDropdown>
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

.avatar {
  cursor: pointer;
}
</style>
