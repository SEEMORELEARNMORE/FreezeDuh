<template>
  <div class="search">
    <yin-nav :styleList="searchNavList" :activeName="activeName" @click="handleChangeView"></yin-nav>
    <component class="search-list" :is="currentView"></component>
  </div>
</template>

<script lang="ts">
import { defineComponent,onMounted,getCurrentInstance, reactive , ref, warn, watch, computed} from "vue";
import YinNav from "@/components/layouts/YinNav.vue";
import SearchSinger from "./SearchSinger.vue";
import SearchSong from "./SearchSong.vue";
import SearchSongList from "./SearchSongList.vue";

export default defineComponent({
  setup(){
    const searchNavList =  reactive([
        {
          name: "歌曲",
          value: "SearchSong",
        },
        {
          name:'歌手',
          value:"SearchSinger"
        },
        {
          name: "歌单",
          value: "SearchSongList",
        },
      ])
    const activeName = ref("歌曲")
    const type = computed(() => proxy.$route.query.type)
    const currentView = ref("SearchSong")
    const { proxy } = getCurrentInstance();
    function  handleChangeView(item) {
      activeName.value = item.name;
      currentView.value = item.value;
     }
    function getType(){
      searchNavList.forEach((item)=>{
        if(item.name === type.value){
          handleChangeView(item)
        }
     })     
     }
     watch(type,()=>{getType()},{immediate: true })
  return {
      searchNavList,
      activeName,
      currentView,
      handleChangeView,
    };
  },
  components:{
    YinNav,
    SearchSinger,
    SearchSong,
    SearchSongList
  }


});
</script>

<style lang="scss" scoped>
@import '@/assets/css/var.scss';
@import '@/assets/css/global.scss';

.search {
  margin: auto;
  width: 900px;

  .search-list {
    min-height: 480px;
  }
}
</style>
