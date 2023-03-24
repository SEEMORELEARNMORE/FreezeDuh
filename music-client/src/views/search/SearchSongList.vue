<template>
  <div class="search-song-list">
    <div v-if="!playList.length" style="margin-top:140px;text-align:center">暂无相关歌单</div>
    <play-list :playList="playList" path="song-sheet-detail"></play-list>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, watch, onMounted, getCurrentInstance } from "vue";
import PlayList from "@/components/PlayList.vue";
import { HttpManager } from "@/api";
import useStore from "@/store";

export default defineComponent({
  components: {
    PlayList,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();

    const playList = ref([]);
    const searchWord = computed(() => store.configure.searchWord);
    watch(searchWord, (value) => {
      getSearchList(value);
    });

    async function getSearchList(value) {
      if (!value) return;
      const result = (await HttpManager.getSongListOfLikeTitle(value)) as ResponseBody;
      if (result.data.length) 
        playList.value = result.data;
      
    }

    onMounted(() => {
      getSearchList(proxy.$route.query.keywords);
    });

    return {
      playList,
    };
  },
});
</script>
