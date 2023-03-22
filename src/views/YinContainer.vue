<template>
  <el-container>
    <el-header>
      <yin-header></yin-header>
    </el-header>
    <el-main>
      <router-view v-slot="{ Component }">
    <keep-alive>
      <component :is="Component"
                 v-if="$route.meta.keepAlive"
                 :key="$route.path" />
    </keep-alive>
    <component :is="Component"
               v-if="!$route.meta.keepAlive" />
</router-view>
      <yin-current-play></yin-current-play>
      <yin-play-bar></yin-play-bar>
      <yin-scroll-top></yin-scroll-top>
      <yin-audio></yin-audio>
    </el-main>
    <el-footer>
      <yin-footer></yin-footer>
    </el-footer>
  </el-container>
</template>

<script lang="ts" setup>
import { getCurrentInstance } from "vue";
import YinHeader from "@/components/layouts/YinHeader.vue";
import YinCurrentPlay from "@/components/layouts/YinCurrentPlay.vue";
import YinPlayBar from "@/components/layouts/YinPlayBar.vue";
import YinScrollTop from "@/components/layouts/YinScrollTop.vue";
import YinFooter from "@/components/layouts/YinFooter.vue";
import YinAudio from "@/components/layouts/YinAudio.vue";
import useStore from "@/store";
import { Icon } from "@/enums";

const { proxy } = getCurrentInstance();
const store = useStore();

if (sessionStorage.getItem("dataStore")) {
  const storeObj = JSON.parse(sessionStorage.getItem("dataStore"));
  store.user.$patch(storeObj.user)
  store.configure.$patch(storeObj.configure)
  store.song.$patch(storeObj.song)
}
window.onerror = function (errorMessage, scriptURI, lineNumber, columnNumber, error) {
      console.log('错误', errorMessage);
    };

window.addEventListener("beforeunload", () => {
  // store.song.setPlayBtnIcon(Icon.BOFANG)
  // store.song.setIsPlay(false)
  const storeObj = {
    user:store.user.$state,
    configure:store.configure.$state,
    song:store.song.$state,
  }
  sessionStorage.setItem("dataStore", JSON.stringify(storeObj));
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

.el-container {
  min-height: calc(100% - 60px);
}
.el-header {
  padding: 0;
}
.el-main {
  padding-left: 0;
  padding-right: 0;
}
</style>
