import { createApp } from "vue";
import ElementPlus, { Table } from "element-plus";
import App from "./App.vue";
import router from "./router";
import "element-plus/dist/index.css";
import "./assets/css/index.scss";
import "./assets/icons/index.js";
import "@/assets/font/iconfont.css"
import '@/router/permission'
import Particles from 'particles.vue3';
import '@/router/permission'
import {pinia} from '@/store'
declare module "@vue/runtime-core" {
  interface State {
    count: number;
  }
}
createApp(App).use(pinia).use(router).use(ElementPlus).use(Particles).mount("#app");
