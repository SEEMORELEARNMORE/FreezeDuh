<template>
  <audio :src="attachImageUrl(songUrl)" controls="controls" :ref="player" preload="true" @canplay="canplay" @timeupdate="timeupdate" @ended="ended" >
    <!--（1）属性：controls，preload（2）事件：canplay，timeupdate，ended（3）方法：play()，pause() -->
    <!--controls：向用户显示音频控件（播放/暂停/进度条/音量）-->
    <!--preload：属性规定是否在页面加载后载入音频-->
    <!--canplay：当音频/视频处于加载过程中时，会发生的事件-->
    <!--timeupdate：当目前的播放位置已更改时-->
    <!--ended：当目前的播放列表已结束时-->
  </audio>
  <!-- <el-dialog
    v-model="dialogVisible"
    title="Tips"
    width="30%"
  >
    <span>由于您的浏览器设置，音乐无法自动播放，请手动点击播放。</span>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="dialogVisible = false">
          Confirm
        </el-button>
      </span>
    </template>
  </el-dialog> -->
</template>

<script lang="ts">
import { defineComponent, ref, getCurrentInstance, computed, watch } from "vue";
import { HttpManager } from "@/api";
import useStore from "@/store";

export default defineComponent({
  setup() {
    const store = useStore();
    const divRef = ref<HTMLAudioElement>();
    const player = (el) => {
      divRef.value = el;
    };

    const songUrl = computed(() => store.song.songUrl); // 音乐链接
    const isPlay = computed(() => store.song.isPlay); // 播放状态
    const volume = computed(() => store.song.volume); // 音量
    const changeTime = computed(() => store.song.changeTime); // 指定播放时刻
    const autoNext = computed(() => store.song.autoNext); // 用于触发自动播放下一首
    const dialogVisible = ref(false)
    // 监听播放还是暂停
    watch(isPlay, () => togglePlay());
    // 跳到指定时刻播放
    watch(changeTime, () => (divRef.value.currentTime = changeTime.value));
    watch(volume, (value) => (divRef.value.volume = value));

    // 开始 / 暂停
    function togglePlay() {
      isPlay.value ? divRef.value.play() : divRef.value.pause();
    }
    setTimeout(() => {
      divRef.value.pause()
      divRef.value.load()
}, 10)
    // 获取歌曲链接后准备播放
    function canplay() {
      //  记录音乐时长
      store.song.setDuration(divRef.value.duration);
      //  开始播放
      divRef.value.play();
      store.song.setIsPlay(true);
    }
    // 音乐播放时记录音乐的播放位置
    function timeupdate() {
      store.song.setCurTime(divRef.value && divRef.value.currentTime ?divRef.value.currentTime: 0);
    }
    // 音乐播放结束时触发
    function ended() {
      store.song.setIsPlay(false);
      store.song.setCurTime(0);
      store.song.setAutoNext(!autoNext.value);
    }
    // window.onload = function(){
    //   dialogVisible.value = true
    // }
    return {
      songUrl,
      dialogVisible,
      player,
      canplay,
      timeupdate,
      ended,
      attachImageUrl: HttpManager.attachImageUrl,
    };
  },
});
</script>

<style scoped>
audio {
  display: none;
}
</style>
