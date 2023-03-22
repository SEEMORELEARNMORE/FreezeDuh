import { defineStore } from 'pinia'

const useUserStore = defineStore({
  id:'user',
  state: () => {
    return {
      userId: "", // ID
      username: "", // 名字
      userPic: "", // 图片
    }
  },
  // getters: {
  //   userId: (state) => state.userId,
  //   username: (state) => state.username,
  //   userPic: (state) => state.userPic,
  // },
  actions: {
    setUserId(userId){
      this.userId = userId;
    },
    setUsername(username){
      this.username = username;
    },
    setUserPic(userPic){
      this.userPic = userPic;
    },
  },
})

export default useUserStore