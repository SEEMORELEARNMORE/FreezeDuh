<template>
  <transition name="aside-fade">
    <div class="yin-current-play" v-if="showAside" ref="target">
      <h2 class="title">当前播放</h2>
      <span class="control">共 {{ (currentPlayList && currentPlayList.length) || 0 }} 首</span>
      <div style="float: right">
        <div class="collect-all" @click="collectAll"><i class="iconfont icon-shoucang"></i> <span>收藏全部</span></div>
        <span class="clear-list" @click="clearList">清空列表</span>
      </div>
      <el-divider />
      <div class="noPlayList" v-show="!currentPlayList.length">
        <div style="margin-bottom: 20px">你还没有添加任何歌曲!</div>
        <div>去首页<span @click="goHome">发现音乐</span></div>
      </div>

      <ul class="menus" v-show="currentPlayList.length">
        <li
          v-for="(item, index) in currentPlayList"
          :class="{ 'is-play': songId === item.id }"
          :key="index"
          @click="
            playMusic({
              id: item.id,
              url: item.url,
              pic: item.pic,
              index: index,
              name: item.name,
              lyric: item.lyric,
              collectioncount: item.collectioncount,
              clickcount: item.clickcount,
              currentSongList: currentPlayList,
            })
          "
        >
          <i :class="{ iconfont: songId === item.id, 'icon-24gl-play': songId === item.id }" style="width: 20px; display: inline-block"></i>
          {{ getSongTitle(item.name) }}
        </li>
      </ul>
    </div>
  </transition>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, computed, ref } from 'vue'
import mixin from '@/mixins/mixin'
import { RouterName } from '@/enums'
import { HttpManager } from '@/api'
import { onClickOutside } from '@vueuse/core'
import useStore from '@/store'
export default defineComponent({
  setup() {
    const { proxy } = getCurrentInstance()
    const store = useStore()
    const { getSongTitle, playMusic, checkStatus, routerManager } = mixin()
    const songId = computed(() => store.song.songId) // 音乐 ID
    const currentPlayList = computed(() => store.song.currentPlayList) // 当前播放
    let showAside = computed(() => store.configure.showAside) || false // 是否显示侧边栏
    const target = ref(null)
    onClickOutside(target, () => {
      store.configure.setShowAside(false)
      console.log(showAside)
    })
    async function collectAll() {
      if (!checkStatus()) return
      let collectList = []
      currentPlayList.value.forEach((item) => {
        const obj = {}
        obj['userId'] = store.user.userId
        obj['type'] = '0'
        obj['songId'] = item.id
        collectList.push(obj)
      })

      const result = await HttpManager.collectionAll(collectList)
      console.log(result)
      if (result['type'] === 'success') {
        (proxy as any).$message({
          message: '收藏成功',
          type: 'success',
        })
      }
    }
    function clearList() {
      console.log('清空列表')
      store.song.setCurrentPlayList([])
    }
    function goHome() {
      store.configure.setShowAside(false)
      routerManager(RouterName.Home, { path: RouterName.Home })
    }
    return {
      target,
      songId,
      currentPlayList,
      showAside,
      getSongTitle,
      playMusic,
      collectAll,
      clearList,
      goHome,
    }
  },
})
</script>

<style lang="scss" scoped>
@import '@/assets/css/yin-current-play.scss';
</style>

function clearList() { throw new Error("Function not implemented."); }
