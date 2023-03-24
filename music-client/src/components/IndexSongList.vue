<template>
      <ul class="index_songlist">
        <li>
          <span>排名</span>
          <span v-if="songList[0]['songName']">歌曲</span>
          <span v-if="songList[0]['singerName']">歌手</span>
          <span v-if="songList[0]['lyric']">专辑</span>
          <span v-if="songList[0]['title']">歌单</span>
        </li>
      <li v-for="(item,index) in songList" :key="item['id']" @click="handleClick($event,item)" >
        <span >{{ index + 1 }}</span> 
        <span v-if="item['songName']">{{ item['songName'] }}</span>
        <span  v-if="item['title']">{{ item['title'] }}</span>
        <span  v-if="item['singerName']">{{ item['singerName'] }}</span>
        <span v-if="item['lyric']">{{ item['introduction'] }}</span>
        <span> </span>
      </li>
    </ul>

</template>

<script lang="ts" setup>
import { computed, ref, toRefs } from 'vue'
import cloneDeep from 'lodash/cloneDeep'
import mixin from '@/mixins/mixin'
import { BOARDCATEGORIES,BOARDBACKGROUND } from '@/enums/leaderboard'
import useStore from '@/store'
import { useRouter } from 'vue-router';
const router = useRouter()
const { playMusic,routerManager } = mixin()
const props = defineProps({ attrKey:String || Array,songList:Array })
const store = useStore()
const path=ref("song-sheet-detail")
const showData = computed(()=>{
  const allBoardList = cloneDeep(store.song.allBoardList)
  for(let i in allBoardList){
    allBoardList[i] = i =='songlistBoard' ? allBoardList[i].splice(0,8):allBoardList[i].splice(0,5)
  }
  console.log(store.song.allBoardList);
  console.log(allBoardList);
  return allBoardList
})
const date = ref(`${new Date().getMonth()+1}月${new Date().getDate()}日更新`)

function handleClick(e,row) {
  console.log(e.target.parentElement);
  
 const event =  e.target.tagName === 'SPAN'?e.target.parentElement:e.target;
  if(row.lyric){
    playMusic({
        id: row.id,
        url: row.url,
        pic: row.pic,
        index: row.index,
        name: row.name,
        lyric: row.lyric,
        collectioncount: row.collectioncount,
        clickcount: row.clickcount,
        currentSongList: props.songList,
      })
  }else{
    store.song.setSongDetails(row);
      routerManager(path.value, { path: `/${path.value}/${row.id}` });
  }

}

function allRank(key){
console.log(key);
router.push({name:'rank-detail',params: {key}})
}
</script>
<style lang="scss" scoped>
@import '@/assets/css/board.scss';
.index_songlist li{
 span{
    display: inline-block;
    width:24%;
    text-align: left;
  }
  &:nth-child(1){
    font-weight: 700;
  }
  
}
</style>