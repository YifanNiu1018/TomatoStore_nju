import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import './style.css'
import axios from 'axios'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

createApp(App).use(ElementPlus).use(router).mount('#app')