<template>
  <ul class="yin-header-nav">
    <li :class="{ active: item.name === activeName }" v-for="item in styleList" :key="item.path" @click="handleChangeView(item)">
      {{ item.name }}
    </li>
  </ul>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance } from "vue";

export default defineComponent({
  props: {
    styleList: Array,
    activeName: String,
  },
  emits: ["selectNav"],
  setup() {
    const { proxy } = getCurrentInstance();

    function handleChangeView(item) {
      proxy.$emit("selectNav", item.path, item.name);
    }
    return {
      handleChangeView,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

li {
  margin: $header-nav-margin;
  padding: $header-nav-padding;
  line-height: 3.3rem;
  color: $color-ddd;
  border-bottom: none;
  cursor: pointer;
}

li.active {
  color: #fff;
  font-weight: 700;
  font-weight: 600;
  border-bottom: 5px solid #fff;
}
</style>
