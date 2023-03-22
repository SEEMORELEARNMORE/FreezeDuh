<template>
  <div class="play-list-container">
    <yin-nav :styleList="singerStyle" :activeName="activeName" @click="handleChangeView"></yin-nav>
         <play-list :playList="datalist" path="singer-detail"></play-list> 
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, watch,onMounted } from "vue";
import YinNav from "@/components/layouts/YinNav.vue";
import PlayList from "@/components/PlayList.vue";
import { singerStyle } from "@/enums";
import { HttpManager } from "@/api";

// data
const activeName = ref("全部歌手");
const pageSize = ref(15); // 页数
const currentPage = ref(1); // 当前页
const allPlayList = ref([]);
// computed
const data = computed(() => {
  return allPlayList.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});
const datalist = ref([])
    watch(data, (value) => {
      datalist.value = datalist.value.concat(value);
    });
// 获取所有歌手
async function getAllSinger() {
  datalist.value = []
  const result = (await HttpManager.getAllSinger()) as ResponseBody;
  currentPage.value = 1;
  allPlayList.value = result.data;
}

getAllSinger();
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
// 获取当前页
function handleCurrentChange(val) {
  currentPage.value = val;
}

function handleChangeView(item) {
  activeName.value = item.name;
  allPlayList.value = [];
  if (item.name === "全部歌手") {
    getAllSinger();
  } else {
    getSingerSex(item.type);
  }
}

// 通过性别对歌手分类
async function getSingerSex(sex) {
  datalist.value = []
  const result = (await HttpManager.getSingerOfSex(sex)) as ResponseBody;
  currentPage.value = 1;
  allPlayList.value = result.data;
}
</script>
