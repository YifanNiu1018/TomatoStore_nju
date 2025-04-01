<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { userLogin } from "@/api/user.ts";
import { ElMessage, ElForm } from "element-plus";
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
    <el-card class="login-card">
      <h2 class="title">用户登录</h2>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleLogin" :disabled="!isFormValid" class="login-btn">
            登录
          </el-button>
          <el-button type="text" @click="router.push('/register')" class="register-btn">
            去注册
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: url("@/assets/bookShelve.jpg") no-repeat center center/cover;
}

.login-card {
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

.login-btn {
  width: 100%;
  border-radius: 10px;
  font-size: 16px;
  font-weight: bold;
  transition: all 0.3s ease-in-out;
}

.login-btn:disabled {
  cursor: not-allowed;
}

.login-btn:hover {
  background: #409eff;
  transform: translateY(-2px);
  box-shadow: 0px 5px 15px rgba(64, 158, 255, 0.4);
}

.register-btn {
  margin-top: 20px;
}

</style>
