<template>
  <div class="search-song">
    <div v-if="!currentSongList.length" style="margin-top:140px;text-align:center">暂无相关歌曲</div>
    <song-list :songList="currentSongList" v-if="currentSongList.length"></song-list>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, watch, onMounted, getCurrentInstance } from "vue";
import SongList from "@/components/SongList.vue";
import { HttpManager } from "@/api";
import useStore from "@/store";

export default defineComponent({
  components: {
    SongList,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();

    const currentSongList = ref([]); // 存放的音乐
    const searchWord = computed(() => store.configure.searchWord);
    watch(searchWord, (value) => {
      searchSong(value);
    });

    // 搜索音乐
    async function searchSong(value) {
      if (!value) {
        currentSongList.value = [];
        return;
      }
      const result = (await HttpManager.getSongOfSingerSongName(value)) as ResponseBody;
      if (!result.data.length) {
        currentSongList.value = [];
      } else {
        currentSongList.value = result.data;
      }
    }

    onMounted(() => {
      searchSong(proxy.$route.query.keywords);
    });

    return {
      currentSongList,
    };
  },
});
</script>
