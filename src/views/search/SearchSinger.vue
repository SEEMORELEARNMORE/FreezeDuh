<template>
  <div class="play-list-container">
    <div v-if="!data.length" style="margin-top:140px;text-align:center">暂无相关歌手</div>

   <play-list :playList="data" path="singer-detail"></play-list>
    <el-pagination
      class="pagination"
      background
      layout="total, prev, pager, next"
      :current-page="currentPage"
      :page-size="pageSize"
      :total="allPlayList.length"
      @current-change="handleCurrentChange"
      v-if="data.length"
    >
    </el-pagination>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed,onMounted, getCurrentInstance } from "vue";
import PlayList from "@/components/PlayList.vue";
import { HttpManager } from "@/api";

const { proxy } = getCurrentInstance();
// data
const activeName = ref("全部歌手");
const pageSize = ref(15); // 页数
const currentPage = ref(1); // 当前页
const allPlayList = ref([]);
// computed
const data = computed(() => {
  return allPlayList.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});

// 获取所有歌手
async function searchSinger(keywords) {
  const result = (await HttpManager.getSinger(keywords)) as ResponseBody;
  currentPage.value = 1;
  allPlayList.value = result.data;
}

// 获取当前页
function handleCurrentChange(val) {
  currentPage.value = val;
}
onMounted(() => {
      searchSinger(proxy.$route.query.keywords);
});
</script>
