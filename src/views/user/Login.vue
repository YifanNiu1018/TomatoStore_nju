<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import {userInfo, userLogin} from "@/api/user.ts";
import { ElMessage, ElForm, ElCard, ElFormItem, ElInput, ElButton } from "element-plus";
import router from "@/router";

const formRef = ref<InstanceType<typeof ElForm> | null>(null);
const isFormValid = ref(false); // 登录按钮是否启用

const form = reactive({
  username: "",
  password: "",
});
// 表单验证规则
const rules = {
  username: [{ required: true, message: ""}],
  password: [{ required: true, message: ""}],
};
// 监听表单变化，实时校验表单是否合法
watch(form, () => {
  if (formRef.value) {
    formRef.value.validate((valid) => {
      isFormValid.value = valid;
    });
  }
});
// 登录用户
const handleLogin = () => {
  if (!formRef.value) return;

  formRef.value.validate((valid) => {
    if (valid) {
      userLogin(form)
        .then((res) => {
          if (res.data.code === "200") {
            ElMessage({
              message: "登录成功！",
              type: "success",
              center: true,
            });
            sessionStorage.setItem("username", form.username);
            sessionStorage.setItem("token", res.data.data);
            router.push({ path: "/productlist" }); // 登录成功
          } else {
            ElMessage({
              message: res.data.msg,
              type: "error",
              center: true,
            });
          }
        })
        .catch(() => {
          ElMessage({
            message: "登录失败，请检查用户名或密码",
            type: "error",
            center: true,
          });
        });
    }
  });
};
</script>

<template>
  <div class="login-container">
    <div class="login-wrapper">
      <ElCard class="login-card">
        <!-- 标题区域 -->
        <div class="header-section">
          <h1 class="title">番茄书店</h1>
          <h2 class="subtitle">用户登录</h2>
          <p class="welcome-text">欢迎回来，请登录您的账户</p>
        </div>

        <!-- 表单区域 -->
        <ElForm ref="formRef" :model="form" :rules="rules" label-width="0" class="login-form">
          <ElFormItem prop="username" class="form-item">
            <div class="input-wrapper">
              <span class="input-icon">👤</span>
              <ElInput
                v-model="form.username"
                placeholder="请输入用户名"
                class="form-input"
                size="large"
              />
            </div>
          </ElFormItem>

          <ElFormItem prop="password" class="form-item">
            <div class="input-wrapper">
              <span class="input-icon">🔒</span>
              <ElInput
                v-model="form.password"
                type="password"
                show-password
                placeholder="请输入密码"
                class="form-input"
                size="large"
                @keyup.enter="handleLogin"
              />
            </div>
          </ElFormItem>

          <ElFormItem class="form-item">
            <ElButton
              type="primary"
              @click="handleLogin"
              :disabled="!isFormValid"
              class="login-btn"
              size="large"
            >
              立即登录
            </ElButton>
          </ElFormItem>

          <div class="form-footer">
            <span class="register-text">还没有账户？</span>
            <ElButton type="text" @click="router.push('/register')" class="register-btn">
              立即注册
            </ElButton>
          </div>
        </ElForm>
      </ElCard>
    </div>
  </div>
</template>

<style scoped lang="scss">
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: url("@/assets/bookShelve.jpg") no-repeat center center/cover;
  padding: 2rem;
}

.login-wrapper {
  width: 100%;
  max-width: 450px;
}

.login-card {
  background: rgba(20, 20, 20, 0.8);
  border: 1px solid rgba(255, 215, 0, 0.3);
  border-radius: 16px;
  padding: 3rem 2.5rem;
  backdrop-filter: blur(15px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
  color: #fff;
}

.header-section {
  text-align: center;
  margin-bottom: 2.5rem;

  .title {
    font-size: 2.5rem;
    color: #ffd700;
    margin-bottom: 0.5rem;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
    font-weight: bold;
  }

  .subtitle {
    font-size: 1.5rem;
    color: #fff;
    margin-bottom: 0.8rem;
    font-weight: 600;
  }

  .welcome-text {
    color: #aaa;
    font-size: 1rem;
    margin: 0;
    line-height: 1.5;
  }
}

.login-form {
  .form-item {
    margin-bottom: 1.5rem;

    &:last-child {
      margin-bottom: 0;
    }
  }
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;

  .input-icon {
    position: absolute;
    left: 15px;
    font-size: 1.2rem;
    z-index: 2;
    opacity: 0.7;
  }

  .form-input {
    width: 100%;

    :deep(.el-input__wrapper) {
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 215, 0, 0.3);
      border-radius: 12px;
      padding-left: 45px;
      box-shadow: none;
      transition: all 0.3s ease;

      &:hover {
        border-color: rgba(255, 215, 0, 0.5);
        background: rgba(255, 255, 255, 0.15);
      }

      &.is-focus {
        border-color: #ffd700;
        box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
        background: rgba(255, 255, 255, 0.15);
      }
    }

    :deep(.el-input__inner) {
      color: #fff;
      font-size: 1rem;

      &::placeholder {
        color: #aaa;
      }
    }

    :deep(.el-input__password) {
      color: #aaa;

      &:hover {
        color: #fff;
      }
    }
  }
}

.login-btn {
  width: 100%;
  height: 50px;
  background: linear-gradient(135deg, rgba(255, 215, 0, 0.8), rgba(255, 215, 0, 0.6));
  border: none;
  border-radius: 12px;
  font-size: 1.1rem;
  font-weight: bold;
  color: #333;
  transition: all 0.3s ease;
  margin-top: 1rem;

  &:hover:not(:disabled) {
    background: linear-gradient(135deg, rgba(255, 215, 0, 0.6), rgba(255, 215, 0, 0.4));
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(255, 215, 0, 0.3);
  }

  &:disabled {
    background: rgba(100, 100, 100, 0.5);
    color: #666;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
  }
}

.form-footer {
  text-align: center;
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid rgba(255, 215, 0, 0.2);

  .register-text {
    color: #aaa;
    font-size: 0.95rem;
    margin-right: 0.5rem;
  }

  .register-btn {
    color: #ffd700;
    font-weight: 600;
    font-size: 0.95rem;
    padding: 0;

    &:hover {
      color: #fff;
      text-decoration: underline;
    }
  }
}

// 全局表单样式覆盖
:deep(.el-form-item__error) {
  color: #ff6b6b;
  font-size: 0.85rem;
  margin-top: 0.5rem;
}

// 响应式设计
@media (max-width: 768px) {
  .login-container {
    padding: 1rem;
  }

  .login-card {
    padding: 2rem 1.5rem;
  }

  .header-section {
    .title {
      font-size: 2rem;
    }

    .subtitle {
      font-size: 1.3rem;
    }
  }
}
</style>
