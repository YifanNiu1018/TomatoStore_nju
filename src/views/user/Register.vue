<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { userRegister } from '@/api/user.ts'
import { ElMessage, ElForm, ElCard, ElFormItem, ElInput, ElButton, ElSelect, ElOption } from 'element-plus'
import router from '@/router'

const formRef = ref<InstanceType<typeof ElForm> | null>(null) // 获取 el-form 实例
const isFormValid = ref(false) // 是否启用注册按钮

const form = reactive({
  username: '',
  password: '',
  name: '',
  avatar: '',
  role: '',
  telephone: '',
  email: '',
  location: ''
})

// 表单验证规则
const rules = {
  username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
  name: [{ required: true, message: '真实姓名不能为空', trigger: 'blur' }],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  telephone: [
    {
      pattern: /^1(3[0-9]|4[579]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[189])\d{8}$/,
      message: '请输入正确的手机号',
      trigger: 'blur'
    }
  ]
}

// 监听表单变化，实时校验表单是否合法
watch(form, () => {
  if (formRef.value) {
    formRef.value.validate((valid) => {
      isFormValid.value = valid
    })
  }
})

// 注册用户
const handleRegister = () => {
  if (!formRef.value) return

  formRef.value.validate((valid) => {
    if (valid) {
      userRegister(form)
        .then((res) => {
          if (res.data.code === '200') {
            ElMessage({
              message: '注册成功！请登录账号',
              type: 'success',
              center: true
            })
            router.push({ path: '/login' })
          } else {
            ElMessage({
              message: res.data.msg,
              type: 'error',
              center: true
            })
          }
        })
        .catch(() => {
          ElMessage({
            message: '注册失败，请稍后重试',
            type: 'error',
            center: true
          })
        })
    }
  })
}
</script>

<template>
  <div class="register-container">
    <div class="register-wrapper">
      <ElCard class="register-card">
        <!-- 标题区域 -->
        <div class="header-section">
          <h1 class="title">番茄书店</h1>
          <h2 class="subtitle">用户注册</h2>
          <p class="welcome-text">创建您的账户，开启阅读之旅</p>
        </div>

        <!-- 表单区域 -->
        <ElForm ref="formRef" :model="form" :rules="rules" label-width="0" class="register-form">
          <!-- 基本信息 -->
          <div class="form-section">
            <h3 class="section-title">基本信息</h3>

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
                />
              </div>
            </ElFormItem>

            <ElFormItem prop="name" class="form-item">
              <div class="input-wrapper">
                <span class="input-icon">📝</span>
                <ElInput
                  v-model="form.name"
                  placeholder="请输入真实姓名"
                  class="form-input"
                  size="large"
                />
              </div>
            </ElFormItem>

            <ElFormItem class="form-item">
              <div class="input-wrapper">
                <span class="input-icon">👥</span>
                <ElSelect
                  v-model="form.role"
                  placeholder="请选择身份"
                  class="form-select"
                  size="large"
                >
                  <ElOption label="普通用户" value="customer" />
                  <ElOption label="管理员" value="admin" />
                </ElSelect>
              </div>
            </ElFormItem>
          </div>

          <!-- 联系信息 -->
          <div class="form-section">
            <h3 class="section-title">联系信息</h3>

            <ElFormItem prop="telephone" class="form-item">
              <div class="input-wrapper">
                <span class="input-icon">📱</span>
                <ElInput
                  v-model="form.telephone"
                  placeholder="请输入手机号"
                  class="form-input"
                  size="large"
                />
              </div>
            </ElFormItem>

            <ElFormItem prop="email" class="form-item">
              <div class="input-wrapper">
                <span class="input-icon">📧</span>
                <ElInput
                  v-model="form.email"
                  placeholder="请输入邮箱"
                  class="form-input"
                  size="large"
                />
              </div>
            </ElFormItem>

            <ElFormItem class="form-item">
              <div class="input-wrapper">
                <span class="input-icon">📍</span>
                <ElInput
                  v-model="form.location"
                  placeholder="请输入所在位置"
                  class="form-input"
                  size="large"
                />
              </div>
            </ElFormItem>
          </div>

          <!-- 操作按钮 -->
          <ElFormItem class="form-item">
            <ElButton
              type="primary"
              @click="handleRegister"
              :disabled="!isFormValid"
              class="register-btn"
              size="large"
            >
              立即注册
            </ElButton>
          </ElFormItem>

          <div class="form-footer">
            <span class="login-text">已有账户？</span>
            <ElButton type="text" @click="router.push('/login')" class="login-btn">
              立即登录
            </ElButton>
          </div>
        </ElForm>
      </ElCard>
    </div>
  </div>
</template>

<style scoped lang="scss">
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: url("@/assets/bookShelve.jpg") no-repeat center center/cover;
  padding: 2rem;
}

.register-wrapper {
  width: 100%;
  max-width: 550px;
}

.register-card {
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

.register-form {
  .form-item {
    margin-bottom: 1.5rem;

    &:last-child {
      margin-bottom: 0;
    }
  }
}

.form-section {
  margin-bottom: 2rem;

  .section-title {
    font-size: 1.2rem;
    color: #ffd700;
    margin-bottom: 1rem;
    padding-bottom: 0.5rem;
    border-bottom: 1px solid rgba(255, 215, 0, 0.3);
    font-weight: 600;
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

  .form-input, .form-select {
    width: 100%;

    :deep(.el-input__wrapper), :deep(.el-select__wrapper) {
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

    :deep(.el-input__inner), :deep(.el-select__placeholder) {
      color: #fff;
      font-size: 1rem;

      &::placeholder {
        color: #aaa;
      }
    }

    :deep(.el-select__placeholder) {
      color: #aaa;
    }

    :deep(.el-select__selected-item) {
      color: #fff;
    }

    :deep(.el-input__password) {
      color: #aaa;

      &:hover {
        color: #fff;
      }
    }
  }
}

.register-btn {
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

  .login-text {
    color: #aaa;
    font-size: 0.95rem;
    margin-right: 0.5rem;
  }

  .login-btn {
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

:deep(.el-select-dropdown) {
  background: rgba(40, 40, 40, 0.95);
  border-color: #555;
}

:deep(.el-select-dropdown__item) {
  color: #fff;

  &:hover {
    background: rgba(255, 215, 0, 0.2);
  }

  &.selected {
    background: rgba(255, 215, 0, 0.3);
    color: #333;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .register-container {
    padding: 1rem;
  }

  .register-card {
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

  .form-section {
    margin-bottom: 1.5rem;
  }
}
</style>
