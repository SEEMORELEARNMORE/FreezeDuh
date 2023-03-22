<template>
 <div class="play-list-container">
    <yin-nav :styleList="BOARDTYPES" :activeName="activeName" @click="handleChangeView"></yin-nav>
    <board-list path="song-sheet-detail"></board-list>
  </div>
</template>
  
  <script lang="ts" setup>
  import YinNav from "@/components/layouts/YinNav.vue";
  import BoardList from "@/components/BoardList.vue";
  import { ref, reactive} from 'vue';
  import mixin from '@/mixins/mixin'
  import { BOARDTYPES } from '@/enums'
  import { HttpManager } from "@/api";
import useStore from "@/store";
  const { getSongTitle, getSingerName } = mixin()
  const activeName = ref("歌曲排行榜");
  let allBoardList = reactive({})
  const store = useStore()
  // 获取所有歌曲排行榜
  async function getAllSongBoard(){
    deleteAttribute(allBoardList)
    // 播放量
    let result = (await HttpManager.clickBoard()) as Response
    filterData(result['data'])
    allBoardList['clickBoard'] = result['data']
    // 收藏量
    result = (await HttpManager.collectionBoard()) as Response
    filterData(result['data'])
    allBoardList['collectionBoard'] = result['data']
    // 评论量
    result = (await HttpManager.commentBoard()) as Response
    filterData(result['data'])
    allBoardList['commentBoard'] = result['data']
    store.song.setAllBoardList(allBoardList)
  }
  function filterData(arr){
    arr.forEach((item: any, index) => {
        item['songName'] = getSongTitle(item.name)
        item['singerName'] = getSingerName(item.name).replace('+','、')
      })
  }
  function deleteAttribute(obj){
    for(let i in obj){
      delete obj[i]
    }
  }
  getAllSongBoard()
  
  // 获取歌单评分排行榜
  async function getSonglistBoard(){
    deleteAttribute(allBoardList)
    let result = (await HttpManager.songlistBoard()) as Response
    allBoardList['songlistBoard'] = result['data']
    store.song.setAllBoardList(allBoardList)
  }
  function handleChangeView(item) {
       activeName.value = item.name;
      // allPlayList.value = [];
       if (item.name === "歌曲排行榜") {
        getAllSongBoard()
       } else {
        getSonglistBoard()
       }
    }
  </script>
  