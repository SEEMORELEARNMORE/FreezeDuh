<template>
  <div class="play-bar" :class="{ show: !toggle }">
    <div class="fold" :class="{ turn: toggle }">
      <yin-icon :icon="iconList.ZHEDIE" @click="toggle = !toggle"></yin-icon>
    </div>
    <!--播放进度-->
    <el-slider class="progress" v-model="nowTime" @change="changeTime" size="small" :color="{ color: '#3A18AE' }"></el-slider>
    <div class="control-box">
      <div class="info-box">
        <!--歌曲图片-->
        <el-image class="song-bar-img" fit="contain" :src="attachImageUrl(songPic)" @click="goPlayerPage" />
        <!--播放开始结束时间-->
        <div v-if="songId">
          <div class="song-info">{{ songTitle }} - {{ singerName }}</div>
          <div class="time-info">{{ startTime }} / {{ endTime }}</div>
        </div>
      </div>
      <div class="song-ctr">
        <yin-icon class="yin-play-show" :icon="playStateList[playStateIndex]" @click="changePlayState"></yin-icon>
        <!--上一首-->
        <yin-icon class="yin-play-show" :icon="iconList.SHANGYISHOU" @click="prev"></yin-icon>
        <!--播放-->
        <yin-icon :icon="playBtnIcon" @click="togglePlay"></yin-icon>
        <!--下一首-->
        <yin-icon class="yin-play-show" :icon="iconList.XIAYISHOU" @click="next"></yin-icon>
        <!--音量-->
        <el-dropdown class="yin-play-show" trigger="click">
          <yin-icon v-if="volume !== 0" :icon="iconList.YINLIANG"></yin-icon>
          <yin-icon v-else :icon="iconList.JINGYIN"></yin-icon>
          <template #dropdown>
            <el-dropdown-menu>
              <el-slider class="yin-slider" style="height: 150px; margin: 10px 0" v-model="volume" :vertical="true"></el-slider>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <div class="song-ctr song-edit">
        <!--收藏-->
        <yin-icon class="yin-play-show" :class="{ active: isCollection }" :icon="isCollection ? iconList.like : iconList.dislike" @click="changeCollection"></yin-icon>
        <!--下载-->
        <yin-icon
          class="yin-play-show"
          :icon="iconList.download"
          @click="
            downloadMusic({
              songUrl,
              songName: singerName + '-' + songTitle,
            })
          "
        ></yin-icon>
        <!--歌曲列表-->
        <yin-icon :icon="iconList.LIEBIAO" @click="changeAside"></yin-icon>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, getCurrentInstance, onMounted, reactive, ref, watch } from 'vue'
import mixin from '@/mixins/mixin'
import YinIcon from './YinIcon.vue'
import { HttpManager } from '@/api'
import { formatSeconds } from '@/utils'
import { Icon, RouterName } from '@/enums'
import useStore from '@/store'
export default defineComponent({
  components: {
    YinIcon,
  },
  setup() {
    const { proxy } = getCurrentInstance()
    const store = useStore()
    const { routerManager, playMusic, checkStatus, downloadMusic } = mixin()

    const isCollection = ref(false) // 是否收藏

    const userIdVO = computed(() => store.user.userId)
    const songIdVO = computed(() => store.song.songId)
    const token = computed(() => store.configure.token)
    const userId = computed(() => store.user.userId)
    const isPlay = computed(() => store.song.isPlay)
    const playBtnIcon = computed(() => store.song.playBtnIcon)
    const songId = computed(() => store.song.songId)
    const songUrl = computed(() => store.song.songUrl)
    const songTitle = computed(() => store.song.songTitle)
    const singerName = computed(() => store.song.singerName)
    const songPic = computed(() => store.song.songPic)
    const curTime = computed(() => store.song.curTime)
    const duration = computed(() => store.song.duration)
    const currentPlayList = computed(() => store.song.currentPlayList)
    const currentPlayIndex = computed(() => store.song.currentPlayIndex)
    const showAside = computed(() => store.configure.showAside)
    const autoNext = computed(() => store.song.autoNext)

    let startTime = ref('00:00'),
      endTime = ref('00:00'),
      nowTime = ref(0), // 进度条的位置
      toggle = ref(true),
      volume = ref(50),
      playState = ref(Icon.XUNHUAN),
      playStateList = ref([Icon.XUNHUAN, Icon.LUANXU]),
      playStateIndex = ref(0)
    const iconList = reactive({
      download: Icon.XIAZAI,
      ZHEDIE: Icon.ZHEDIE,
      SHANGYISHOU: Icon.SHANGYISHOU,
      XIAYISHOU: Icon.XIAYISHOU,
      YINLIANG: Icon.YINLIANG1,
      JINGYIN: Icon.JINGYIN,
      LIEBIAO: Icon.LIEBIAO,
      dislike: Icon.Dislike,
      like: Icon.Like,
    })
    watch(songIdVO, () => {
      initCollection()
    })
    watch(token, (value) => {
      if (!value) isCollection.value = false
    })
    watch(isPlay, (value) => {
      store.song.setPlayBtnIcon(value ? Icon.ZANTING : Icon.BOFANG)
    })
    watch(volume, () => {
      store.song.setVolume(volume.value / 100)
    })
    watch(curTime, () => {
      startTime.value = formatSeconds(curTime.value)
      endTime.value = formatSeconds(duration.value)
      // 移动进度条
      nowTime.value = (curTime.value / duration.value) * 100
    })
    watch(autoNext, () => {
      next()
    })
    async function initCollection() {
      if (!checkStatus(false)) return

      const userId = userIdVO.value
      const type = '0'
      const songId = songIdVO.value
      isCollection.value = ((await HttpManager.isCollection({ userId, type, songId })) as ResponseBody).data
    }

    async function changeCollection() {
      if (!checkStatus()) return

      const userId = userIdVO.value
      const type = '0' //这里要看看 不能直接写死
      const songId = songIdVO.value

      const result = isCollection.value ? ((await HttpManager.deleteCollection(userIdVO.value, songIdVO.value)) as ResponseBody) : ((await HttpManager.setCollection({ userId, type, songId })) as ResponseBody)
      ;(proxy as any).$message({
        message: result.message,
        type: result.type,
      })

      if (result.data == true || result.data == false) isCollection.value = result.data
    }

    onMounted(() => {
      if (songIdVO.value) initCollection()
    })

    function changeAside() {
      store.configure.setShowAside(!showAside.value)
    }
    // 控制音乐播放 / 暂停
    function togglePlay() {
      store.song.setIsPlay(isPlay.value ? false : true)
    }

    function changeTime() {
      store.song.setChangeTime(duration.value * (nowTime.value * 0.01))
    }

    function changePlayState() {
      playStateIndex.value = playStateIndex.value >= playStateList.value.length - 1 ? 0 : ++playStateIndex.value
      playState.value = playStateList.value[playStateIndex.value]
    }
    // 上一首
    function prev() {
      if (playState.value === Icon.LUANXU) {
        let playIndex = Math.floor(Math.random() * currentPlayList.value.length)
        playIndex = playIndex === currentPlayIndex.value ? playIndex + 1 : playIndex
        store.song.setCurrentPlayIndex(playIndex)
        toPlay(currentPlayList.value[playIndex].url)
      } else if (currentPlayIndex.value !== -1 && currentPlayList.value.length > 1) {
        if (currentPlayIndex.value > 0) {
          store.song.setCurrentPlayIndex(currentPlayIndex.value - 1)
          toPlay(currentPlayList.value[currentPlayIndex.value].url)
        } else {
          store.song.setCurrentPlayIndex(currentPlayList.value.length - 1)
          toPlay(currentPlayList.value[currentPlayIndex.value].url)
        }
      }
    }
    // 下一首
    function next() {
      if (playState.value === Icon.LUANXU) {
        let playIndex = Math.floor(Math.random() * currentPlayList.value.length)
        playIndex = playIndex === currentPlayIndex.value ? playIndex + 1 : playIndex
        store.song.setCurrentPlayIndex(playIndex)
        toPlay(currentPlayList.value[playIndex].url)
      } else if (currentPlayIndex.value !== -1 && currentPlayList.value.length > 1) {
        if (currentPlayIndex.value < currentPlayList.value.length - 1) {
          store.song.setCurrentPlayIndex(currentPlayIndex.value + 1)
          toPlay(currentPlayList.value[currentPlayIndex.value].url)
        } else {
          store.song.setCurrentPlayIndex(0)
          toPlay(currentPlayList.value[0].url)
        }
      }
    }
    // 选中播放
    function toPlay(url) {
      if (url && url !== songUrl.value) {
        const song = currentPlayList.value[currentPlayIndex.value]
        playMusic({
          id: song.id,
          url,
          pic: song.pic,
          index: currentPlayIndex.value,
          name: song.name,
          lyric: song.lyric,
          collectioncount: song.collectioncount,
          clickcount: song.clickcount,
          currentSongList: currentPlayList.value,
        })
      }
    }
    function goPlayerPage() {
      routerManager(RouterName.Lyric, { path: `${RouterName.Lyric}/${songId.value}` })
    }
    return {
      isCollection,
      userId,
      isPlay,
      playBtnIcon,
      songId,
      songUrl,
      songTitle,
      singerName,
      songPic,
      curTime,
      duration,
      currentPlayList,
      currentPlayIndex,
      showAside,
      autoNext,
      startTime,
      endTime,
      nowTime, // 进度条的位置
      toggle,
      volume,
      playState,
      playStateList,
      playStateIndex,
      iconList,
      playMusic,
      routerManager,
      checkStatus,
      attachImageUrl: HttpManager.attachImageUrl,
      changeCollection,
      downloadMusic,
      togglePlay,
      changeAside,
      changeTime,
      changePlayState,
      prev,
      next,
      toPlay,
      goPlayerPage,
    }
  },
})
</script>

<style lang="scss" scoped>
@import '@/assets/css/yin-play-bar.scss';
</style>
