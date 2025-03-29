import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import './style.css'
import axios from 'axios'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

//设置后端地址（本地或服务器），会将请求转发到后端端口
axios.defaults.baseURL = ("http://localhost:8080")
axios.defaults.timeout = 30000;

createApp(App).use(ElementPlus).use(router).mount('#app')