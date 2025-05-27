<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import router from "@/router";

const paths = [
  'M20 100 Q 100 20 180 100 Q 100 180 20 100',
  'M50 50 L150 150 M50 150 L150 50',
  'M100 20 L180 100 L100 180 L20 100 Z'
]

const currentPathIndex = ref(0)
const currentPath = ref(paths[0])
const dots = ref(Array(20).fill(null).map(() => ({ x: 0, y: 0 })))
const isHover = ref(false)

onMounted(() => {
  setInterval(() => {
    currentPathIndex.value = (currentPathIndex.value + 1) % paths.length
    currentPath.value = paths[currentPathIndex.value]
  }, 3000)
})

const handleMouseMove = (e: MouseEvent) => {
  const rect = (e.target as SVGSVGElement).getBoundingClientRect()
  const mouseX = e.clientX - rect.left
  const mouseY = e.clientY - rect.top

  dots.value = dots.value.map((_, i) => {
    const angle = (i * 2 * Math.PI) / dots.value.length
    return {
      x: mouseX + Math.cos(angle) * 15,
      y: mouseY + Math.sin(angle) * 15
    }
  })
}

const dotStyle = (index: number) => ({
  transition: `all 0.3s ${index * 0.02}s`,
  opacity: '0.7'
})

const gotoweb = () => {
  window.location.href = 'https://www.bilibili.com'
}
</script>

<template>
  <div class="container">
    <div class="error-animation">
      <svg viewBox="0 0 200 200" @mousemove="handleMouseMove">
        <defs>
          <linearGradient id="grad" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" style="stop-color:#6366f1" />
            <stop offset="100%" style="stop-color:#a855f7" />
          </linearGradient>
        </defs>

        <path
            :class="{ 'animate-path': true }"
            fill="none"
            stroke="url(#grad)"
            stroke-width="4"
            :d="currentPath"
        />

        <circle
            v-for="(dot, index) in dots"
            :key="index"
            :cx="dot.x"
            :cy="dot.y"
            r="2"
            fill="#4F46E5"
            :style="dotStyle(index)"
        />
      </svg>
    </div>

    <h1 class="title">Oops, the page got lost! 🧭</h1>
    <p class="description">We are trying to find it, or you can:</p>
    <button
        class="home-btn"
        @mouseenter="isHover = true"
        @mouseleave="isHover = false"
        @click="gotoweb"
    >
      {{ isHover ? '🚀 (゜-゜)つロ 干杯~-' : '🏠 (゜-゜)つロ 干杯~-' }}
    </button>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1f2937, #111827);
  color: white;
  text-align: center;
  padding: 2rem;
}

.error-animation {
  width: 300px;
  height: 300px;
  margin-bottom: 2rem;
}

.animate-path {
  animation: pathMorph 3s infinite;
}

@keyframes pathMorph {
  0%, 100% { opacity: 0.8; }
  50% { opacity: 0.3; }
}

.title {
  font-size: 2rem;
  margin-bottom: 1rem;
  letter-spacing: 0.1em;
}

.description {
  color: #94a3b8;
  margin-bottom: 2rem;
}

.home-btn {
  background: #4F46E5;
  padding: 1rem 2rem;
  border: none;
  border-radius: 999px;
  color: white;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.3s;
  transform: scale(1);
}

.home-btn:hover {
  background: #6366f1;
  transform: scale(1.05);
  box-shadow: 0 0 20px rgba(79, 70, 229, 0.5);
}
</style>