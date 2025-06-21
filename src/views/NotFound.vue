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

const goHome = () => {
  router.push('/productlist')
}

const goBack = () => {
  router.back()
}
</script>

<template>
  <div class="container">
    <div class="content-wrapper">
      <!-- 404动画 -->
      <div class="error-animation">
        <div class="error-number">404</div>
        <svg viewBox="0 0 200 200" @mousemove="handleMouseMove" class="floating-svg">
          <defs>
            <linearGradient id="grad" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" style="stop-color:#ffd700" />
              <stop offset="100%" style="stop-color:#ffed4e" />
            </linearGradient>
          </defs>

          <path
              :class="{ 'animate-path': true }"
              fill="none"
              stroke="url(#grad)"
              stroke-width="3"
              :d="currentPath"
          />

          <circle
              v-for="(dot, index) in dots"
              :key="index"
              :cx="dot.x"
              :cy="dot.y"
              r="2"
              fill="#ffd700"
              :style="dotStyle(index)"
          />
        </svg>
      </div>

      <!-- 错误信息 -->
      <div class="error-content">
        <h1 class="title">页面走丢了 📚</h1>
        <p class="description">
          抱歉，您访问的页面不存在或已被移动。<br>
          让我们帮您找到正确的路径吧！
        </p>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <button
              class="action-btn primary"
              @mouseenter="isHover = true"
              @mouseleave="isHover = false"
              @click="goHome"
          >
            <span class="btn-icon">🏠</span>
            <span class="btn-text">回到首页</span>
          </button>

          <button
              class="action-btn secondary"
              @click="goBack"
          >
            <span class="btn-icon">↩️</span>
            <span class="btn-text">返回上页</span>
          </button>
        </div>

        <!-- 建议链接 -->
        <div class="suggestions">
          <p class="suggestions-title">您可能想要访问：</p>
          <div class="suggestion-links">
            <a href="/productlist" class="suggestion-link">📖 商品列表</a>
            <a href="/cart" class="suggestion-link">🛒 购物车</a>
            <a href="/forum" class="suggestion-link">💬 论坛</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: url("@/assets/bookShelve.jpg") no-repeat center center/cover;
  padding: 2rem;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(20, 20, 20, 0.7);
    backdrop-filter: blur(5px);
  }
}

.content-wrapper {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 800px;
  width: 100%;
  text-align: center;
}

.error-animation {
  position: relative;
  margin-bottom: 3rem;

  .error-number {
    font-size: 8rem;
    font-weight: bold;
    color: #ffd700;
    text-shadow: 3px 3px 6px rgba(0, 0, 0, 0.5);
    margin-bottom: 1rem;
    animation: float 3s ease-in-out infinite;
  }

  .floating-svg {
    width: 200px;
    height: 200px;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    opacity: 0.7;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

.animate-path {
  animation: pathMorph 3s infinite;
}

@keyframes pathMorph {
  0%, 100% {
    opacity: 0.8;
    stroke-width: 3;
  }
  50% {
    opacity: 0.3;
    stroke-width: 5;
  }
}

.error-content {
  color: #fff;

  .title {
    font-size: 2.5rem;
    margin-bottom: 1.5rem;
    color: #ffd700;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
    font-weight: bold;
  }

  .description {
    color: #aaa;
    font-size: 1.1rem;
    line-height: 1.6;
    margin-bottom: 3rem;
  }
}

.action-buttons {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 3rem;
  justify-content: center;
  flex-wrap: wrap;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 2rem;
  border: none;
  border-radius: 25px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;

  .btn-icon {
    font-size: 1.2rem;
  }

  &.primary {
    background: linear-gradient(135deg, rgba(255, 215, 0, 0.8), rgba(255, 215, 0, 0.6));
    color: #333;

    &:hover {
      background: linear-gradient(135deg, rgba(255, 215, 0, 0.6), rgba(255, 215, 0, 0.4));
      transform: translateY(-3px);
      box-shadow: 0 10px 25px rgba(255, 215, 0, 0.3);
    }
  }

  &.secondary {
    background: rgba(255, 255, 255, 0.1);
    color: #fff;
    border: 1px solid rgba(255, 215, 0, 0.3);

    &:hover {
      background: rgba(255, 215, 0, 0.1);
      border-color: rgba(255, 215, 0, 0.5);
      transform: translateY(-3px);
      box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
    }
  }
}

.suggestions {
  .suggestions-title {
    color: #aaa;
    font-size: 1rem;
    margin-bottom: 1rem;
  }

  .suggestion-links {
    display: flex;
    gap: 1rem;
    justify-content: center;
    flex-wrap: wrap;
  }

  .suggestion-link {
    display: inline-flex;
    align-items: center;
    padding: 0.5rem 1rem;
    background: rgba(40, 40, 40, 0.7);
    color: #fff;
    text-decoration: none;
    border-radius: 20px;
    font-size: 0.9rem;
    transition: all 0.3s ease;
    border: 1px solid rgba(255, 215, 0, 0.2);

    &:hover {
      background: rgba(255, 215, 0, 0.1);
      border-color: rgba(255, 215, 0, 0.5);
      color: #ffd700;
      transform: translateY(-2px);
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .container {
    padding: 1rem;
  }

  .error-animation {
    .error-number {
      font-size: 6rem;
    }

    .floating-svg {
      width: 150px;
      height: 150px;
    }
  }

  .error-content {
    .title {
      font-size: 2rem;
    }

    .description {
      font-size: 1rem;
    }
  }

  .action-buttons {
    flex-direction: column;
    align-items: center;
  }

  .action-btn {
    width: 100%;
    max-width: 250px;
    justify-content: center;
  }

  .suggestion-links {
    flex-direction: column;
    align-items: center;
  }

  .suggestion-link {
    width: 100%;
    max-width: 200px;
    justify-content: center;
  }
}
</style>