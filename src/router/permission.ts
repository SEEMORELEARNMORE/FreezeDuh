import router from "./index";
import { HttpManager} from "@/api";
import {pinia} from '@/store'
import Cookie from 'js-cookie'
import useConfigureStore from "@/store/configure"
import useUserStore from "@/store/user"
const user = useUserStore(pinia)
const configure = useConfigureStore(pinia)
router.beforeEach(async (to, from,next) => {
  if(to.path === '/'){
    console.log(configure.token);
    if(!configure.token) {
          let tokenObj = localStorage.getItem('token')
          if(tokenObj){
          tokenObj = JSON.parse(tokenObj)
          if(new Date().getTime() - new Date(tokenObj['expires']).getTime()<0){
           console.log('token未过期哦');
           Cookie.set('token',tokenObj['token'],{expires:new Date(tokenObj['expires'])})
           const result = await HttpManager.getUserInfo()
            user.setUserId(result['data'].id);
            user.setUsername(result['data'].username);
            user.setUserPic(result['data'].avator);
            configure.setToken(true);
          }else{
           console.log('token已过期');
           localStorage.removeItem('token')
     
      }
    }
  }
  }
  next()
 })
 router.afterEach((to) => {  // 一定要再afterEach中判断而不是beforeEach，因为beforeEach在点击返回之后获取到的值不准确，每返回一次，会获取到延后一次的to、history
  if (window.history.state && window.history.state.forward) { // 或者判断 to.forward,window.history.state.forward是vue-router写入的，当返回或前进的时候才会有值
    to.meta.isBack = true;
  } else {
    to.meta.isBack = false;
  }
});