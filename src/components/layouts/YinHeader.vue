<!-- eslint-disable vue/no-parsing-error -->
<template>
  <div class="yin-header">
    <!--图标-->
    <div class="header-logo" @click="goPage()">
      <i class="iconfont icon-yinle" style="font-size:30px;color:#fff"></i>
      <span>{{ musicName }}</span>
    </div>
    <yin-header-nav class="yin-header-nav" :styleList="headerNavList" :activeName="activeNavName" @selectNav="goPage"></yin-header-nav>
    <!--搜索框-->
    <div class="header-search">
      <el-input placeholder="搜索" :prefix-icon="Search" v-model="keywords" @keyup.enter="goSearch()" @input="handleInput()" />
      <div :class="['recommended-lookup', isRecommended ? '' : 'recommend-none']">
        <div v-for="(item, index) in recommendContent" :key="item" style="margin-bottom: -30px">
          <div class="class-lookup" v-if="item.length">
            <i :class="['iconfont', 'icon-' + (index == 'recommendSong' ? 'dianyingzhiye-gequbang' : index == 'recommendSinger' ? 'geshou' : 'gedan')]"></i> <span style="height: 100%">{{ index == 'recommendSong' ? '单曲' : index == 'recommendSinger' ? '歌手' : '歌单' }}</span>
          </div>
          <ul>
            <li v-for="item2 in item" :key="item2.id" @click="goSearch($event,item2.name ? item2.name : item2.title)" v-html="item2.name ? item2.name : item2.title"></li>
          </ul>
        </div>
      </div>
      <div class="nodata" v-if="!hasRecommend">没有搜索到相关信息哦</div>
    </div>
    <!--设置-->
    <yin-header-nav v-if="!token" :styleList="signList" :activeName="activeNavName" @selectNav="goPage"></yin-header-nav>
    <el-dropdown class="user-wrap" v-if="token" trigger="click">
      <el-image class="user" fit="cover" :src="attachImageUrl(userPic)" />
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item v-for="(item, index) in menuList" :key="index" @click.stop="goMenuList(item.path)">{{ item.name }}</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script lang="ts">
import Cookies from 'js-cookie'
import { defineComponent, ref, getCurrentInstance, computed, reactive, watch } from 'vue'
import { Search } from '@element-plus/icons-vue'
import YinIcon from './YinIcon.vue'
import YinHeaderNav from './YinHeaderNav.vue'
import mixin from '@/mixins/mixin'
import { HEADERNAVLIST, SIGNLIST, MENULIST, Icon, MUSICNAME, RouterName, NavName } from '@/enums'
import { HttpManager } from '@/api'
import { IRespose } from '@/models/respose'
import useStore from '@/store'


export default defineComponent({
  components: {
    YinHeaderNav,
  },
  setup() {
    const { proxy } = getCurrentInstance()
    const store = useStore()
    const { changeIndex, routerManager, debounce, getSongTitle, getSingerName ,checkStatus} = mixin()

    const musicName = ref(MUSICNAME)
    const headerNavList = ref(HEADERNAVLIST) // 左侧导航栏
    const signList = ref(SIGNLIST) // 右侧导航栏
    const menuList = ref(MENULIST) // 用户下拉菜单项
    const iconList = reactive({
      ERJI: Icon.ERJI,
    })
    const keywords = ref('')
    // 根据input输入框判读是否显示推荐
    const isRecommended = ref(false)
    // 根据返回内容判断是否有推荐内容
    const hasRecommend = ref(true)
    const recommendContent = reactive({
      recommendSinger: [],
      recommendSong: [],
      recommendSongList: [],
    })
    const recommendIcon = ref(['icon-dianyingzhiye-gequbang', 'geshou', 'icon-gedan'])
    const activeNavName = computed(() => store.configure.activeNavName)
    const userPic = computed(() => store.user.userPic)
    const token = computed(() => store.configure.token)

    function goPage(path, name) {
      if (!path && !name) {
        changeIndex(NavName.Home)
        routerManager(RouterName.Home, { path: RouterName.Home })
      } else {
        changeIndex(name)
        routerManager(path, { path })
      }
    }

    function goMenuList(path) {
      if(!checkStatus()) return ;
      if (path == RouterName.SignOut) {
        // Cookies.remove('token')
       store.configure.SET_TOKEN(false)
        Cookies.remove('token')
        localStorage.removeItem('token')
        changeIndex(NavName.Home)
        routerManager(RouterName.Home, { path: RouterName.Home })
      } else {
        routerManager(path, { path })
      }
    }
    function goSearch(e,word) {
      if (keywords.value !== '') {
        isRecommended.value = false
        if(word){
          word = removeLight(word)
          if(word.includes('-')){
          word = word.split('-').reverse().join('-')
        }
        }
        store.configure.setSearchWord(word?word:keywords.value)
        const type = e.target.parentNode.previousSibling.children[1].innerText
        console.log(type);
        
        routerManager(RouterName.Search, { path: RouterName.Search, query: { keywords: keywords.value,type:type } })
      } else {
        (proxy as any).$message({
          message: '搜索内容不能为空',
          type: 'error',
        })
      }
    }
    function insertStr(soure, start, newStr){   
        return soure.slice(0, start) + newStr + soure.slice(start);
    }
    function removeLight(str){
      str = str.replaceAll('<span style="color:red">','')
      str = str.replaceAll('</span>','')
      return str
    }
    function highlightWords(data){
      const key = keywords.value;
      data.forEach((item)=>{
        let st = item.name?item.name:item.title;
      let pos = st.indexOf(key);
      if(pos !== -1){
            st = insertStr(st,pos + key.length,`</span>`)
            st = insertStr(st,pos,`<span style="color:red">`)
          }
      if(item.name) item.name = st
      if(item.title) item.title = st
      })
      return data;
    }
    async function handleInput() {  
      // 搜索词不为空 发送请求查找
      if (keywords.value !== '') {

        // let result = (await HttpManager.getSongOfSingerSongName(keywords.value)) as ResponseBody
        // result.data.forEach((item) => {
        //   item.name = item.name.split('-').reverse().join('-')
        // })
        // recommendContent.recommendSong = result.data
        // result = (await HttpManager.getSinger(keywords.value)) as ResponseBody
        // recommendContent.recommendSinger = result.data
        // result = (await HttpManager.getSongListOfLikeTitle(keywords.value)) as ResponseBody
        // recommendContent.recommendSongList = result.data

        let res = []
        const list = [HttpManager.getSongOfSingerSongName(keywords.value),HttpManager.getSinger(keywords.value), HttpManager.getSongListOfLikeTitle(keywords.value)]
        for await ( let r of list){
          res.push(r)
        }
        // let result = (await HttpManager.getSongOfSingerSongName(keywords.value)) as ResponseBody
        res[0].data.forEach((item) => {
          item.name = item.name.split('-').reverse().join('-')
        })
        recommendContent.recommendSong = highlightWords(res[0].data)
        recommendContent.recommendSinger = highlightWords(res[1].data)
        recommendContent.recommendSongList = highlightWords(res[2].data)
        // console.log(document.getElementsByTagName('li'));
        // Vue.nextTick(()=>{
        //   console.log("nextTick中的");
        //   console.log(document.getElementsByTagName('li'));
        // })
        if (recommendContent.recommendSinger.length || recommendContent.recommendSong.length || recommendContent.recommendSongList.length) {
          hasRecommend.value = true
        isRecommended.value = true
        } else {
          hasRecommend.value = false
          isRecommended.value = false
        }
      } else {
        isRecommended.value = false
        hasRecommend.value = true
      }
    }
    watch(keywords, () => {
      debounce(handleInput)
    })
    return {
      musicName,
      headerNavList,
      signList,
      menuList,
      iconList,
      keywords,
      activeNavName,
      userPic,
      token,
      isRecommended,
      recommendContent,
      recommendIcon,
      hasRecommend,
      Search,
      goPage,
      goMenuList,
      goSearch,
      handleInput,
      attachImageUrl: HttpManager.attachImageUrl,
    }
  },
})
</script>

<style lang="scss" scoped>
@import '@/assets/css/var.scss';
@import '@/assets/css/global.scss';

@media screen and (min-width: $sm) {
  .header-logo {
    margin: 0 1rem;
  }
}

@media screen and (max-width: $sm) {
  .header-logo {
    margin: 0 1rem;
    span {
      display: none;
    }
  }
  .header-search {
    display: none;
  }
}

.yin-header {
  position: fixed;
  width: 100%;
  height: $header-height;
  line-height: $header-height;
  padding: $header-padding;
  margin: $header-margin;
  background-color:$royal-color;
  box-shadow: $box-shadow;
  box-sizing: border-box;
  z-index: 100;
  display: flex;
  white-space: nowrap;
  flex-wrap: nowrap;
  color:#fff;
  font-weight: 700;
}

/* LOGO */
.header-logo {
  font-size: $font-size-logo;
  font-weight: bold;
  cursor: pointer;
  .icon {
    @include icon(1.9rem, $color-black);
    vertical-align: middle;
  }
  span {
    margin-left: 1rem;
  }
}

.yin-header-nav {
  flex: 1;
}

/*搜索输入框*/
.header-search {
  margin: 0 20px;
  width: 100%;
  position: relative;
  &::v-deep input {
    text-indent: 5px;
    max-width: $header-search-max-width;
    min-width: $header-search-min-width;
    border-radius: $header-search-radius;
    box-shadow: none;
    background-color: $color-light-grey;
    color: $color-black;
  }
  .recommended-lookup,.nodata {
    position: absolute;
    top: 50px;
    left: 0;
    display: flex;
    flex-direction: column;
    background-color: #fff;
    min-width: $header-search-min-width;
    max-width: $header-search-max-width;
    font-size: 15px;
    color: #999;
    line-height:48px;
    overflow-y: auto;
    overflow-x: hidden;
    width: 100%;
    max-height: 80vh;
    box-shadow: 0 12px 5px -10px rgba(0,0,0,0.1), 0 0 4px 0 rgba(0,0,0,0.1);
    -webkit-box-shadow: 0 12px 5px -10px rgba(0,0,0,0.1), 0 0 4px 0 rgba(0,0,0,0.1);
    border-radius: 5px;
    font-weight: normal;
  }
  .recommended-lookup,
  .class-lookup,
  ul {
    max-width: $header-search-max-width;
    min-width: $header-search-min-width;
  }
  // .class-lookup,ul,li{
  //   width: 100%;
  // }
  li {
    display: block;
    padding-left: 37px;
    width: inherit;
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
    font-size: 13px;

  }
  .class-lookup,
  li {
    height: 37px;
    cursor: pointer;
  }
}
.recommend-none {
  display: none !important;
}
/*用户*/
.user-wrap {
  position: relative;
  display: flex;
  align-items: center;

  .user {
    width: $header-user-width;
    height: $header-user-width;
    border-radius: $header-user-radius;
    margin-right: $header-user-margin;
    cursor: pointer;
  }
}


</style>
