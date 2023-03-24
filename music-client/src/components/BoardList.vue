<template>
  <div class="board-list" v-for="(val, key,i) in showData" :key="key">
    <div class="board-name" :style="{background:BOARDBACKGROUND[i]}">
      <span>{{BOARDCATEGORIES[key]}}</span>
      <span>{{date}}</span>
    </div>
    <div style="flex:1">
      <ul>
      <li v-for="(item,index) in val" :key="item['id']" @click="handleClick($event,item,val)">
        <span :class="{'ordinal':true,'first-three': index<3 ? true : false}">{{ index + 1 }}</span> 
        <span class="title" v-if="item['songName']">{{ item['songName'] }}</span>
        <span class="title" v-if="item['title']">{{ item['title'] }}</span>
        <span class="name"  v-if="item['singerName']">{{ item['singerName'] }}</span>
      </li>
    </ul>
    <div class="more" @click="allRank(key)">查看全部 ></div> 
    </div>
  </div>

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
const props = defineProps({ path: String })
const { path } = toRefs(props);
const store = useStore()
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

function handleClick(e,row,val) {
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
        currentSongList: val,
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
</style>