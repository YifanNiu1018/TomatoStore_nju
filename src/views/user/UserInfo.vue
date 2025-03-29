<script setup>
import { reactive, onMounted, ref } from 'vue'
import { ElMessage } from "element-plus";
import { userInfo, userInfoUpdate } from '@/api/user.js';
import { uploadImage } from '@/api/tools.js'

const currentUsername = sessionStorage.getItem('username') || '';
const selectedFile = ref(null); // 新增：存储用户选择的文件对象

const user = reactive({
  avatar: "",
  username: "",
  password: "",
  realName: "",
  identity: "",
  phone: "",
  email: "",
  location: "",
});

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const res = await userInfo(currentUsername);
    Object.assign(user, res.data);
  } catch (error) {
    ElMessage.error('获取用户信息失败');
    console.error('Error fetching user info:', error);
  }
};

// 头像上传前的校验
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/');
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) ElMessage.error('只能上传图片文件!');
  if (!isLt2M) ElMessage.error('图片大小不能超过2MB!');

  return isImage && isLt2M;
};

// 本地预览处理
const handleAvatarChange = (file) => {
  selectedFile.value = file.raw; // 存储原始文件对象

  const reader = new FileReader();
  reader.onload = (e) => {
    user.avatar = e.target.result; // 直接更新user.avatar用于预览
  };
  reader.readAsDataURL(file.raw);
};

// 实际上传头像到服务器
const uploadAvatar = async () => {
  if (!selectedFile.value) return; // 没有新文件时跳过

  try {
    const res = await uploadImage(selectedFile.value);
    user.avatar = res.data.result; // 更新为服务器返回的URL
    selectedFile.value = null; // 清空已上传的文件

    ElMessage.success('头像上传成功');
  } catch (error) {
    ElMessage.error('头像上传失败');
    console.error('Upload error:', error);
  }
};

// 更新用户信息（包含头像上传）
const updateInfo = async () => {
  try {
    // 1. 先上传新头像（如果有）
    await uploadAvatar();

    // 2. 更新用户信息
    const res = await userInfoUpdate(user);

    if (res.data.code === '200') {
      ElMessage.success('信息更新成功');
      fetchUserInfo(); // 刷新数据
    } else {
      ElMessage.error(res.data.msg || '更新失败');
    }
  } catch (error) {
    ElMessage.error('更新过程中发生错误');
    console.error('Error updating:', error);
  }
};

onMounted(fetchUserInfo);
</script>

<template>
  <div class="user-info">
    <div class="overlay">
      <div class="content-wrapper">
        <h2 class="title">个人信息</h2>

        <!-- 头像上传 -->
        <div class="avatar-container">
          <ElUpload
            class="avatar-uploader"
            :show-file-list="false"
            :auto-upload="false"
            :on-change="handleAvatarChange"
            accept="image/*"
            :before-upload="beforeAvatarUpload"
          >
            <div class="avatar-wrapper">
              <img v-if="user.avatar" :src="user.avatar" class="avatar" />
              <div v-else class="avatar-placeholder">
                <i class="icon-camera"></i>
                <span>上传头像</span>
              </div>
            </div>
          </ElUpload>
        </div>

        <!-- 用户信息 -->
        <div class="form">
          <div class="form-item">
            <label>用户名</label>
            <ElInput v-model="user.username" class="custom-input" />
          </div>

          <div class="form-item">
            <label>密码</label>
            <ElInput v-model="user.password" type="password" show-password class="custom-input" />
          </div>

          <div class="form-item">
            <label>真实姓名</label>
            <ElInput v-model="user.realName" class="custom-input" />
          </div>

          <div class="form-item">
            <label>身份</label>
            <ElInput v-model="user.identity" class="custom-input" disabled />
          </div>

          <div class="form-item">
            <label>手机号</label>
            <ElInput v-model="user.phone" class="custom-input" />
          </div>

          <div class="form-item">
            <label>邮箱</label>
            <ElInput v-model="user.email" class="custom-input" />
          </div>

          <div class="form-item">
            <label>位置</label>
            <ElInput v-model="user.location" class="custom-input" />
          </div>
        </div>

        <!-- 修改信息按钮 -->
        <div class="btn-container">
          <ElButton type="primary" class="submit-btn" @click="updateInfo">保存修改</ElButton>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.user-info {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: url("@/assets/bookShelve.jpg") no-repeat fixed center center;
  position: relative;
  padding: 20px;
}

.overlay {
  position: relative;
  width: 100%;
  max-width: 800px;
  height: auto;
  min-height: 90vh;
  background: rgba(55, 46, 46, 0.8);
  backdrop-filter: blur(5px);
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.title {
  color: #fff;
  text-align: center;
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: 600;
  letter-spacing: 1px;
}

.avatar-container {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.avatar-wrapper {
  cursor: pointer;
  transition: all 0.3s ease;
}

.avatar-wrapper:hover {
  transform: scale(1.05);
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.avatar-placeholder {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #fff;
  border: 2px dashed rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.avatar-placeholder:hover {
  background: rgba(255, 255, 255, 0.15);
}

.avatar-placeholder i {
  font-size: 24px;
  margin-bottom: 8px;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
  max-width: 500px;
  margin: 0 auto;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form label {
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
  font-weight: 500;
}

.custom-input {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: #fff;
}

.custom-input :deep(.el-input__wrapper) {
  background: transparent;
  box-shadow: none;
}

.custom-input :deep(.el-input__inner) {
  color: #fff;
}

.custom-input :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

.btn-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.submit-btn {
  background: linear-gradient(135deg, #409EFF, #3375b9);
  border: none;
  border-radius: 8px;
  padding: 12px 28px;
  font-weight: 500;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

@media (max-width: 768px) {
  .overlay {
    padding: 30px 20px;
  }

  .title {
    font-size: 24px;
  }

  .avatar {
    width: 100px;
    height: 100px;
  }

  .avatar-placeholder {
    width: 100px;
    height: 100px;
  }
}
</style>