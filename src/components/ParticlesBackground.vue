<template>
  <!--引入粒子特效-->
  <div class="bkpro-login-canvas">
    <Particles id="tsparticles" class="sign__particles" :options="options" />
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, UnwrapRef, ref } from 'vue'

export default defineComponent({
  setup() {
    const options = {
      background: {
        color: {
          value: 'transparent', //粒子颜色
        },
      },
      fpsLimit: 60,
      interactivity: {
        events: {
          onClick: {
            enable: true,
            mode: 'push', //可用的click模式有: "push", "remove", "repulse", "bubble"。
          },
          onHover: {
            enable: true,
            mode: 'grab', //可用的hover模式有: "grab", "repulse", "bubble"。
          },
          resize: true,
        },
        modes: {
          bubble: {
            distance: 400,
            duration: 2,
            opacity: 0.8,
            size: 40,
          },
          push: {
            quantity: 4,
          },
          repulse: {
            distance: 200,
            duration: 0.4,
          },
        },
      },
      particles: {
        color: {
          value: '#ffffff',
        },
        links: {
          color: '#ffffff', //'#dedede'。线条颜色。
          distance: 150, //线条长度
          enable: true, //是否有线条
          opacity: 0.5, //线条透明度。
          width: 1, //线条宽度。
        },
        collisions: {
          enable: false,
        },
        move: {
          direction: 'none',
          enable: true,
          outMode: 'bounce',
          random: false,
          speed: 4, //粒子运动速度。
          straight: false,
        },
        number: {
          density: {
            enable: true,
            area: 800,
          },
          value: 20, //粒子数量。
        },
        opacity: {
          value: 0.5, //粒子透明度。
        },
        shape: {
          type: 'star', //可用的粒子外观类型有："circle","edge","triangle", "polygon","star"
        },
        size: {
          random: true,
          value: 5,
        },
      },
      detectRetina: true,
    }
    return {
      options,
    }
  },
})
</script>
<style lang="scss" scoped>
.sign__particles {
  height: 100%;
  width: 100%;
  background: url('@/assets/images/bg.jfif') no-repeat;
  background-size: 100% 100%;
}
</style>
