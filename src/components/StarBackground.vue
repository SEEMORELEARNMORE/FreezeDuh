<template>
<canvas id="star-canvas"></canvas>
</template>

<script lang="ts">
import { tsConstructorType } from "@babel/types";
import { defineComponent,onMounted} from "vue";


export default defineComponent({
 
  setup() {
  
            /** 
         * canvas 创建星空
         */

        // 定义变量
        let canvas,
            context,
            screenW,
            screenH,
            stars = [];

        // 定义常量
        const FPS = 50,
            numStars = 2000;

        const loadBg = () => {
                  // 星星构造函数
        class Star {
            x;
            y;
            length;
            opacity;
            factor;
            increment;
            constructor(x,y,length,opacity) {
            this.x = parseInt(x);
            this.y = parseInt(y);
            this.length = parseInt(length);
            this.opacity = opacity;
            this.factor = 1;
            this.increment = Math.random() * 0.03;
           }
           draw =  (context) => {
            context.rotate(Math.PI * 1 / 10);

            //save the context
            context.save();
            //move into the middle of the canvas,just make room
            context.translate(this.x, this.y);
            //change the opacity
            if (this.opacity > 1) {
                this.factor = -1;
            } else if (this.opacity <= 0) {
                this.factor = 1;

                // 更新一次星星位置
                this.x = Math.round(Math.random() * screenW);
                this.y = Math.round(Math.random() * screenH);
            }

            // factor 控制方面，淡入淡出，每次重绘，星星的透明度都在变化
            this.opacity += this.increment * this.factor;

            context.beginPath();
            //画线
            for (var i = 5; i > 0; i--) {
                context.lineTo(0, this.length);
                context.translate(0, this.length);
                context.rotate(Math.PI * 2 / 10);
                context.lineTo(0, -this.length);
                context.translate(0, -this.length);
                context.rotate(-(Math.PI * 6 / 10));
            }

            context.lineTo(0, this.length);
            context.closePath();

            context.fillStyle = 'rgba(255,255,200, ' + this.opacity + ')';
            context.shadowBlur = 5;
            context.shadowColor = '#ffff33';
            context.fill();

            context.restore();
        }
        }
            
            //获取canvas
            canvas = document.getElementById('star-canvas');
            context=canvas.getContext('2d')
            // 设置画布
            render();
            //获取canvas执行上下文
            // ===========组件应用层============
            //创建星星
            for (let i = 0; i < numStars; i++) {
                let x = Math.round(Math.random() * screenW);
                let y = Math.round(Math.random() * screenH);
                let length = 1 + Math.random() * 2;
                let opacity = Math.random();

                // 创建星星实例
                let star = new Star(x, y, length, opacity);
                stars.push(star);
            }

            // 星星闪动
            setInterval(animate, 1000 / FPS);
        }



        /**
         * 获取窗口大小信息
         */
        function getScreenInfo() {
          let winWidth,winHeight;
            //获取窗口宽度
            if (window.innerWidth) {
                winWidth = window.innerWidth;
            } else if ((document.body) && (document.body.clientWidth)) {
                winWidth = document.body.clientWidth;
            }

            //获取窗口高度
            winHeight = canvas.height

            //通过深入Document内部对body进行检测，获取窗口大小
            if (document.documentElement &&
                document.documentElement.clientHeight &&
                document.documentElement.clientWidth) {
                winHeight = document.documentElement.clientHeight;
                winWidth = document.documentElement.clientWidth;
            }

            // 将上述方法简化
            // screenW = window.innerWidth ||
            //     document.body.clientWidth ||
            //     document.documentElement.clientWidth;

            // screenH = window.innerHeight ||
            //     document.body.clientHeight ||
            //     document.documentElement.clientHeight;

            return {
                'winWidth': winWidth,
                'winHeight': winHeight
            }
        }

        /**
         * canvas设置，修复窗口变化，画布大小不变的问题
         */
        function render() {
            //获取屏幕大小
            screenW = getScreenInfo().winWidth;
            screenH = getScreenInfo().winHeight;

            // 设置canvas
            // canvas.setAttribute('width', screenW);
            // canvas.setAttribute('height', screenH);

            canvas.width = screenW;
            canvas.height = screenH;

            window.addEventListener('resize', render);
        }

        /**
         * 星星闪动函数
         */
        function animate() {
            context.clearRect(0, 0, screenW, screenH);
            for (let i = 0; i < stars.length; i++) {
                stars[i].draw(context);
            }
        }
        onMounted(()=>{
          loadBg()
        })
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
#star-canvas {
     position: absolute;
     left: 0;
     top: 60px;
     height: $header-height + 150px;
     width: 100%;
     background-color: #000;
 }
</style>
