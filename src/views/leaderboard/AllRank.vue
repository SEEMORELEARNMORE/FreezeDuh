
<template>
<div class="board-list">
    <div class="info">
      <div class="board-name" :style="{background:BOARDBACKGROUND[0]}">
        <span>{{BOARDCATEGORIES[key]}}</span>
        <span>{{date}}</span>
      </div>
      <div style="padding-top:25px">
        <h2>{{BOARDCATEGORIES[key]}}</h2>
        <div class="about">最近更新：{{ date }}</div>
        <el-button :icon="CaretRight" :color="royal_color" plain round @click="playAll">播放全部</el-button>
        <el-button  :icon="FolderAdd" :color="royal_color" plain round @click="collectAll">收藏</el-button>
        <div class="about">简介：{{BOARDCATEGORIES[key]}}每天更新，欢迎关注</div>
      </div>
    </div>
    <div style="flex:1;margin: 0 20px">
      <!-- <ul>
      <li v-for="(item,index) in allBoardList" :key="item['id']" @click="handleClick(item)">
        <span :class="{'ordinal':true,'first-three': index<3 ? true : false}">{{ index + 1 }}</span> 
        <span class="title" v-if="item['songName']">{{ item['songName'] }}</span>
        <span class="title" v-if="item['title']">{{ item['title'] }}</span>
        <span class="name"  v-if="item['singerName']">{{ item['singerName'] }}</span>
      </li>
    </ul> -->
    <index-song-list :songList="dataList" :attrKey="key" ></index-song-list>
  </div>
</div>
</template>

<script lang="ts" setup>
import useStore from "@/store";
import { computed, getCurrentInstance, ref } from "vue";
import { useRoute } from 'vue-router';
import { BOARDCATEGORIES,BOARDBACKGROUND } from '@/enums/leaderboard'
import { CaretRight,FolderAdd } from '@element-plus/icons-vue'
import { HttpManager } from '@/api'
import IndexSongList from "@/components/IndexSongList.vue";
import mixin from "@/mixins/mixin";
const { checkStatus, playMusic, getSongTitle, getSingerName,} = mixin()
const { proxy } = getCurrentInstance()
const route = useRoute()
const store = useStore()
const key = route.params.key
const date = ref(`${new Date().getMonth()+1}月${new Date().getDate()}日更新`)
const allBoardList = computed(()=>store.song.allBoardList[key])
const dataList = computed(() => {
      const list = []
      if(key == 'songlistBoard') return allBoardList.value
      allBoardList.value.forEach((item: any, index) => {
        item['songName'] = getSongTitle(item.name)
        item['singerName'] = getSingerName(item.name)
        item['index'] = index
        list.push(item)
      })
      console.log('歌曲列表：');
      console.log(list);
      return list
})
const royal_color = ref("rgb(58,24,174)")
async function collectAll() {
      if (!checkStatus()) return
      let collectList = []
      allBoardList.value.forEach((item) => {
        const obj = {}
        obj['userId'] = store.user.userId
        obj['type'] = '0'
        obj['songId'] = item.id
        collectList.push(obj)
      })
      const result = await HttpManager.collectionAll(collectList)
      console.log(result)
      if (result['type'] === 'success') {
        (proxy as any).$message({
          message: '收藏成功',
          type: 'success',
        })
      }
}
function playAll(){
 const row  = allBoardList.value[0]
      playMusic({
        id: row.id,
        url: row.url,
        pic: row.pic,
        index: row.index,
        name: row.name,
        lyric: row.lyric,
        collectioncount: row.collectioncount,
        clickcount: row.clickcount,
        currentSongList: allBoardList.value,
      })
}
</script>
<style lang="scss" scoped>
@import '@/assets/css/board.scss';
.board-list{
  flex-direction: column;
  .board-name{
    margin:0 20px 20px 30px;
  }
  .info{
    display: flex;
  }
  .about{
    color:#666;
    font-size: 14px;
    margin: 15px 0;
  }
}
</style>