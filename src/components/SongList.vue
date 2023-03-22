<template>
  <div class="content">
    <el-table highlight-current-row :data="dataList" @row-click="handleClick">
      <el-table-column prop="songName" label="歌曲" />
      <el-table-column prop="singerName" label="歌手" />
      <el-table-column prop="introduction" label="专辑" />
      <el-table-column label="编辑" align="center" >
        <template #default="scope">
          <el-dropdown>
            <el-icon @click="handleEdit(scope.row)"><MoreFilled /></el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  :icon="Download"
                  @click="
                    downloadMusic({
                      songUrl: scope.row.url,
                      songName: scope.row.name,
                    })
                  "
                  >下载</el-dropdown-item
                >
                <el-dropdown-item :icon="Delete" v-if="show" @click="deleteCollection({ id: scope.row.id })">删除</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, toRefs, computed, reactive } from 'vue'
import mixin from '@/mixins/mixin'
import { MoreFilled, Delete, Download } from '@element-plus/icons-vue'
import { HttpManager } from '@/api'
import { Icon } from '@/enums'
import useStore from '@/store'

export default defineComponent({
  components: {
    MoreFilled,
  },
  props: {
    songList: Array,
    show: {
      default: false,
    }
  },
  emits: ['changeData'],
  setup(props) {
    const { getSongTitle, getSingerName, playMusic, checkStatus, downloadMusic } = mixin()
    const { proxy } = getCurrentInstance()
    const store = useStore()

    const { songList } = toRefs(props)

    const iconList = reactive({
      dislike: Icon.Dislike,
      like: Icon.Like,
    })

    const songUrl = computed(() => store.song.songUrl)
    const singerName = computed(() => store.song.singerName)
    const songTitle = computed(() => store.song.songTitle)
    const dataList = computed(() => {
      const list = []
      songList.value.forEach((item: any, index) => {
        item['songName'] = getSongTitle(item.name)
        item['singerName'] = getSingerName(item.name)
        item['index'] = index
        list.push(item)
      })
      console.log('歌曲列表：');
      console.log(list);
      return list
    })

    function handleClick(row) {
      playMusic({
        id: row.id,
        url: row.url,
        pic: row.pic,
        index: row.index,
        name: row.name,
        lyric: row.lyric,
        collectioncount: row.collectioncount,
        clickcount: row.clickcount,
        currentSongList: songList.value,
      })
    }

    function handleEdit(row) {
      console.log('row', row)
    }

    const userId = computed(() => store.user.userId)

    async function deleteCollection({ id }) {
      if (!checkStatus()) return

      const result = (await HttpManager.deleteCollection(userId.value, id)) as ResponseBody
      ;(proxy as any).$message({
        message: result.message,
        type: result.type,
      })

      if (result.data === false) proxy.$emit('changeData', result.data)
    }

    return {
      dataList,
      iconList,
      Delete,
      Download,
      songUrl,
      singerName,
      songTitle,
      handleClick,
      handleEdit,
      downloadMusic,
      deleteCollection,
    }
  },
})
</script>

<style lang="scss" scoped>
@import '@/assets/css/var.scss';
@import '@/assets/css/global.scss';

.content {
  background-color: $color-white;
  border-radius: $border-radius-songlist;
  padding: 10px;
}

.content:deep(.el-table__row.current-row) {
  color: $color-black;
  font-weight: bold;
}

.content:deep(.el-table__row) {
  cursor: pointer;
}

.icon {
  @include icon(1.2em, $color-black);
}
.el-table .warning-row {
  --el-table-tr-bg-color: #F5F5F5;
}
::v-deep .el-table__body tr.current-row > td {
    background: #eee !important;
    color:$royal-color!important;
}
</style>
