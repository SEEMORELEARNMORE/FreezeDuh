<template>
  <div class="play-list-container">
    <yin-nav :styleList="songStyle" :activeName="activeName" @click="handleChangeView"></yin-nav>
          <play-list :playList="datalist" path="song-sheet-detail"></play-list>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted,watch,onActivated } from "vue";
import YinNav from "@/components/layouts/YinNav.vue";
import PlayList from "@/components/PlayList.vue";
import { SONGSTYLE } from "@/enums";
import { HttpManager } from "@/api";
import { useRoute } from "vue-router";

export default defineComponent({
  components: {
    YinNav,
    PlayList,
  },
  setup() {
    const activeName = ref("全部歌单");
    const pageSize = ref(15); // 页数
    const currentPage = ref(1); // 当前页
    const songStyle = ref(SONGSTYLE); // 歌单导航栏类别
    const allPlayList = ref([]); // 歌单
    const data = computed(() => allPlayList.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value));
    const datalist = ref([])
    watch(data, (value) => {
      datalist.value = datalist.value.concat(value);
    });
    // 获取全部歌单
    async function getSongList() {
      datalist.value = []
      allPlayList.value = ((await HttpManager.getSongList()) as ResponseBody).data;
      console.log(allPlayList.value)
      currentPage.value = 1;
    }
    // 通过类别获取歌单
    async function getSongListOfStyle(style) {
      datalist.value = []
      allPlayList.value = ((await HttpManager.getSongListOfStyle(style)) as ResponseBody).data;
      currentPage.value = 1;
    }

    try {
      getSongList();
    } catch (error) {
      console.error(error);
    }

    // 获取歌单
    async function handleChangeView(item) {
      activeName.value = item.name;
      allPlayList.value = [];
      try {
        if (item.name === "全部歌单") {
          await getSongList();
        } else {
          await getSongListOfStyle(item.name);
        }
      } catch (error) {
        console.error(error);
      }
    }
    // 获取当前页
    function handleCurrentChange(val) {
      currentPage.value = val;
    }
    // 页面滚动监听方法
    function getScrollLoading(){

       //获取滚动距离
       let scrolllen = document.documentElement.scrollTop;
      //获取当前页数
      let page = currentPage.value
      //大于指定距离就请求数据（这个指定距离需要你自行调试的）
      if (scrolllen > 200 * page) {
        //增加页数计数
        currentPage.value = page + 1
      }
    }
    onMounted(()=>{
      window.addEventListener('scroll',getScrollLoading)
    })
    const isInit = ref(true);
    const routes = useRoute();
    onActivated(()=>{
      if (!routes.meta.isBack && !isInit.value) { // 如果不是返回还是需要请求更新数据的
        getSongList()
      }
isInit .value = false;
    })
    return {
      activeName,
      songStyle,
      pageSize,
      currentPage,
      allPlayList,
      data,
      datalist,
      handleChangeView,
      handleCurrentChange,
    };
  },
});
</script>
