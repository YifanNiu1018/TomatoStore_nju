<script setup lang="ts">
import { ref } from "vue";
import router from "@/router";
</script>

<template>
  <div class="start-container">
    <h1 class="title">番茄书店</h1>
    <div class="buttons">
      <button class="btn" @click="router.push({ path: '/login' });">登录</button>
      <button class="btn" @click="router.push({ path: '/register' });">注册</button>
    </div>
  </div>
</template>

<style scoped>
.start-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  overflow: hidden;
  background: url("@/assets/bookShelve.jpg") no-repeat center center/cover;
  text-align: center;
  position: relative;
}

.title {
  font-size: 6rem;
  color: white;
  margin-bottom: 200px;
  animation: fadeIn 1.5s ease-in forwards;
  position: absolute;
  transition: transform 0.8s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.buttons {
  display: flex;
  margin-bottom: -200px;
  position: absolute;
  gap: 200px;
}

.btn {
  background: black;
  color: white;
  border: 2px solid white;
  padding: 10px 50px;
  font-size: 1.2rem;
  border-radius: 40px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn:hover {
  background: white;
  color: black;
}
</style>
