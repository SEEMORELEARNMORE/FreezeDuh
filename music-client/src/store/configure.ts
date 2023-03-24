import { defineStore } from 'pinia'
import useUserStore from './user';

const useConfigureStore = defineStore({
  id:'configure',
  state: () => {
    return {
      token: false, // 用户是否登录
      showAside: false, // 是否显示侧边栏
      searchWord: "", // 搜索关键词
      activeNavName: "", // 导航栏名称
    }
  },
  // getters: {
  //   token: (state) => state.token,
  //   activeNavName: (state) => state.activeNavName,
  //   showAside: (state) => state.showAside,
  //   searchWord: (state) => state.searchWord,
  // },
  actions: {
    setToken(token){
      this.token = token;
    },
    setActiveNavName(activeNavName){
      this.activeNavName = activeNavName;
    },
    setShowAside(showAside){
      this.showAside = showAside;
    },
    setSearchWord(searchWord){
      this.searchWord = searchWord;
    },
    SET_TOKEN(token){
      this.setToken(token)
      if(!token){
        const user = useUserStore()
        user.setUserId('')
        user.setUsername('')
        user.setUserPic('')
      }
    }
  },
})

export default useConfigureStore
