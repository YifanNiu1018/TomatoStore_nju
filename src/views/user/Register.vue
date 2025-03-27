<script setup lang="ts">
import { ref, reactive, watch } from "vue";
import { userRegister } from "@/api/user.ts";
import { ElMessage, ElForm } from "element-plus";
import router from "@/router";

const formRef = ref<InstanceType<typeof ElForm> | null>(null); // 获取 el-form 实例
const isFormValid = ref(false); // 是否启用注册按钮

const form = reactive({
  username: "",
  password: "",
  name: "",
  avatar: "",
  role: "user", // 默认普通用户
  telephone: "",
  email: "",
  location: "",
});

// 表单验证规则
const rules = {
  username: [{ required: true, message: "用户名不能为空", trigger: "blur" }],
  password: [{ required: true, message: "密码不能为空", trigger: "blur" }],
  name: [{ required: true, message: "真实姓名不能为空", trigger: "blur" }],
  email: [
    { type: "email", message: "请输入正确的邮箱地址", trigger: "blur" },
  ],
  telephone: [
    {
      pattern: /^1(3[0-9]|4[579]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[189])\d{8}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
};

// 监听表单变化，实时校验表单是否合法
watch(form, () => {
  if (formRef.value) {
    formRef.value.validate((valid) => {
      isFormValid.value = valid;
    });
  }
});

// 注册用户
const handleRegister = () => {
  if (!formRef.value) return;

  formRef.value.validate((valid) => {
    if (valid) {
      userRegister(form)
        .then((res) => {
          if (res.data.code === "200") {
            ElMessage({
              message: "注册成功！请登录账号",
              type: "success",
              center: true,
            });
            router.push({ path: "/login" });
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
            message: "注册失败，请稍后重试",
            type: "error",
            center: true,
          });
        });
    }
  });
};
</script>

<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2 class="title">用户注册</h2>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            placeholder="请输入密码"
          />
        </el-form-item>

        <el-form-item label="真实姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入真实姓名" />
        </el-form-item>

        <el-form-item label="身份">
          <el-select v-model="form.role">
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>

        <el-form-item label="手机号" prop="telephone">
          <el-input v-model="form.telephone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="位置">
          <el-input v-model="form.location" placeholder="请输入所在位置" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleRegister" :disabled="!isFormValid" class="register-btn">
            注册
          </el-button>
          <el-button type="text" @click="router.push('/login')" class="login-btn">
            去登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: url("@/assets/bookShelve.jpg") no-repeat center center/cover;
}

.register-card {
  width: 400px;
  padding: 30px 40px 30px 10px;
  background: rgba(0, 0, 0, 0.65);
  border-radius: 5px;
  color: #ffffff;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(250, 245, 245, 0.5);
}

.title {
  text-align: center;
  color: white;
  margin-bottom: 20px;
}

.register-btn {
  width: 100%;
  border-radius: 10px;
  font-size: 16px;
  font-weight: bold;
  transition: all 0.3s ease-in-out;
}

.register-btn:disabled {
  cursor: not-allowed;
}

.register-btn:hover {
  background: #409eff;
  transform: translateY(-2px);
  box-shadow: 0px 5px 15px rgba(64, 158, 255, 0.4);
}
.login-btn {
  padding-top: 20px;
}
</style>
