<template>
  <div class="sign-background">
      <div class="sign">
    <particles-background/>
    <div class="sign-head">
      <span style="color:#fff">帐号登录</span>
    </div>
    <el-form ref="signInForm" status-icon :model="registerForm" :rules="SignInRules">
      <el-form-item prop="username">
        <el-input placeholder="用户名" v-model="registerForm.username"></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input type="password" placeholder="密码" v-model="registerForm.password" @keyup.enter="handleLoginIn"></el-input>
      </el-form-item>
      <el-form-item class="sign-btn">
        <el-button @click="handleSignUp">注册</el-button>
        <el-button type="primary" @click="handleLoginIn">登录</el-button>
      </el-form-item>
    </el-form>
  </div>
  </div>

</template>

<script lang="ts">
import Cookies from 'js-cookie'
import { defineComponent, reactive, getCurrentInstance ,computed } from "vue";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";
import { NavName, RouterName, SignInRules } from "@/enums";
import ParticlesBackground from "@/components/ParticlesBackground.vue";
import useStore from '@/store';


export default defineComponent({
  components: {
    ParticlesBackground,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const { routerManager, changeIndex } = mixin();
    const store = useStore();
    // 登录用户名密码
    const registerForm = reactive({
      username: "",
      password: "",
    });

    async function handleLoginIn() {
      let canRun = true;
      (proxy.$refs["signInForm"] as any).validate((valid) => {
        if (!valid) return (canRun = false);
      });
      if (!canRun) return;


      try {
        const username = registerForm.username;
        const password = registerForm.password;
        const result = (await HttpManager.signIn({username,password})) as ResponseBody;
        (proxy as any).$message({
          message: result.message,
          type: result.type,
        });

        if (result.success) {
          console.log(result);
          store.user.setUserId(result.data.id);
          store.user.setUsername(result.data.username);
          store.user.setUserPic(result.data.avator);
          store.configure.setToken(true);
          const expires = new Date(JSON.parse(decodeURIComponent(escape(window.atob(result.token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/"))))).exp * 1000); 
          Cookies.set('token', result.token, {expires});
          const cookiesObj = {
            token:result.token,
            expires,
          }
          localStorage.setItem('token',JSON.stringify(cookiesObj))
          changeIndex(NavName.Home);
          routerManager(RouterName.Home, { path: RouterName.Home });
        }
      } catch (error) {
        console.error(error);
      }
    }

    function handleSignUp() {
      routerManager(RouterName.SignUp, { path: RouterName.SignUp });
    }

    return {
      registerForm,
      SignInRules,
      handleLoginIn,
      handleSignUp,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/sign.scss";
// .sign__particles{
//   background:url('@/assets/images/bg.jfif') no-repeat;
//   background-size:100% 100%;
// }
</style>
