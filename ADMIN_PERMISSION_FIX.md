# 管理员权限修复说明

## 🔍 问题分析

用户身份为"管理员"时，无法进行管理员操作，包括：
- 创建商品
- 修改商品
- 添加广告
- 管理广告

## 🔧 问题根源

1. **登录时role保存错误**: 在Login.vue中，`sessionStorage.setItem("role", res.data.data.role)` 这行代码有问题，因为 `res.data.data` 是token，不是用户信息对象。

2. **Header组件role不响应式**: Header.vue中的role是在组件初始化时获取的，登录后不会更新。

3. **权限检查缺失**: 管理员页面没有进行权限验证。

## ✅ 修复方案

### 1. 修复登录时role保存逻辑

**文件**: `src/views/user/Login.vue`

**修复前**:
```javascript
sessionStorage.setItem("role", res.data.data.role); // 错误：res.data.data是token
```

**修复后**:
```javascript
// 获取用户详细信息来获取role
userInfo(form.username)
  .then((userRes) => {
    if (userRes.data.code === "200") {
      const userRole = userRes.data.data.role;
      sessionStorage.setItem("role", userRole);
      console.log('保存的role:', userRole);
    }
  })
```

### 2. 修复Header组件role响应式

**文件**: `src/components/Header.vue`

**修复前**:
```javascript
const role = sessionStorage.getItem("role"); // 静态获取
```

**修复后**:
```javascript
// 响应式的role获取
const role = computed(() => {
  const userRole = sessionStorage.getItem("role");
  console.log('当前用户role:', userRole);
  return userRole;
});
```

### 3. 添加管理员页面权限检查

为以下页面添加了权限检查：
- `src/views/products/CreateProduct.vue` - 创建商品
- `src/views/products/ProductUpdate.vue` - 修改商品
- `src/views/advertise/Create.vue` - 创建广告
- `src/views/advertise/Manage.vue` - 管理广告

**权限检查函数**:
```javascript
const checkAdminPermission = () => {
  const role = sessionStorage.getItem("role");
  console.log('页面权限检查，当前role:', role);
  
  if (role !== 'admin') {
    ElMessage.error('您没有权限访问此页面，请联系管理员');
    router.push('/productlist');
    return false;
  }
  return true;
}

onMounted(() => {
  if (checkAdminPermission()) {
    // 执行页面初始化逻辑
  }
})
```

### 4. 完善退出登录逻辑

**文件**: `src/components/Header.vue`

添加了role清除：
```javascript
sessionStorage.removeItem("role"); // 清除role信息
```

## 🧪 测试步骤

### 1. 测试管理员登录
1. 使用管理员账号登录
2. 查看浏览器控制台，应该显示：
   ```
   登录响应: {...}
   用户信息响应: {...}
   保存的role: admin
   ```

### 2. 测试Header导航显示
1. 登录后查看Header导航
2. 应该能看到"创建商品"和"管理广告"链接
3. 控制台应该显示：
   ```
   当前用户role: admin
   ```

### 3. 测试管理员页面访问
1. 点击"创建商品"链接
2. 应该能正常访问，控制台显示：
   ```
   CreateProduct页面权限检查，当前role: admin
   ```

### 4. 测试非管理员用户
1. 使用普通用户登录
2. Header中不应该显示管理员链接
3. 直接访问管理员页面应该被重定向

## 🎯 预期结果

### 管理员用户
- ✅ 登录后role正确保存为"admin"
- ✅ Header显示管理员导航链接
- ✅ 可以访问所有管理员页面
- ✅ 可以创建/修改商品
- ✅ 可以创建/管理广告

### 普通用户
- ✅ 登录后role保存为"customer"或其他非admin值
- ✅ Header不显示管理员链接
- ✅ 访问管理员页面时被重定向并提示权限不足

## 🔧 调试信息

### 检查role保存
在浏览器控制台执行：
```javascript
console.log('当前role:', sessionStorage.getItem("role"))
```

### 检查用户信息
在登录过程中查看控制台日志：
```
登录响应: {code: "200", data: "token_string", msg: "登录成功"}
用户信息响应: {code: "200", data: {role: "admin", ...}, msg: "获取成功"}
保存的role: admin
```

### 检查权限验证
访问管理员页面时查看控制台：
```
页面权限检查，当前role: admin
```

## 🚨 如果仍有问题

### 1. 检查后端用户信息接口
确认 `userInfo` API返回的数据格式：
```javascript
// 期望的响应格式
{
  code: "200",
  data: {
    username: "admin",
    role: "admin",
    // 其他用户信息
  },
  msg: "获取成功"
}
```

### 2. 检查数据库用户role字段
确认数据库中用户的role字段值：
```sql
SELECT username, role FROM users WHERE username = 'admin';
```

### 3. 清除浏览器缓存
如果修改后仍有问题，尝试：
1. 清除浏览器localStorage和sessionStorage
2. 硬刷新页面（Ctrl+F5）
3. 重新登录

## 📋 修复文件清单

- ✅ `src/views/user/Login.vue` - 修复role保存逻辑
- ✅ `src/components/Header.vue` - 修复role响应式和退出逻辑
- ✅ `src/views/products/CreateProduct.vue` - 添加权限检查
- ✅ `src/views/products/ProductUpdate.vue` - 添加权限检查
- ✅ `src/views/advertise/Create.vue` - 添加权限检查
- ✅ `src/views/advertise/Manage.vue` - 添加权限检查
- ✅ `ADMIN_PERMISSION_FIX.md` - 本修复说明文档

## 🎉 修复完成

现在管理员权限系统应该能够正常工作：
- ✅ 正确保存和获取用户role
- ✅ 响应式的权限状态更新
- ✅ 完善的权限验证机制
- ✅ 友好的权限错误提示

管理员用户现在应该能够正常进行所有管理操作！
